/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This file is heavily based on com.puppycrawl.tools.checkstyle.TreeWalker which was
 * published under the GNU Lesser General Public License. Following the copyright notice
 * of this file:
 */
////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////


package ch.tsphp.grammarconvention;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.puppycrawl.tools.checkstyle.DefaultContext;
import com.puppycrawl.tools.checkstyle.Defn;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Context;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.Utils;
import org.antlr.Tool;
import org.antlr.grammar.v3.ANTLRLexer;
import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.tool.Grammar;
import org.antlr.tool.GrammarAST;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Grammar walker which allows to define checks for grammar conventions.
 * <p/>
 * It is heavily based on com.puppycrawl.tools.checkstyle.TreeWalker.
 * The main difference lies in the fact that it uses ANTLRv3's GrammarAST as AST type and not
 * com.puppycrawl.tools.checkstyle.api.DetailAST (which is based on ANTLRv2). Therefore, one has to extend
 * AGrammarConventionCheck and not com.puppycrawl.tools.checkstyle.api.Check when writing an own grammar
 * convention check.
 */
public class GrammarWalker extends AbstractFileSetCheck
{
    private final Multimap<String, AGrammarConventionCheck> tokenToChecks = HashMultimap.create();
    private final Set<AGrammarConventionCheck> checks = new HashSet<>();
    private ClassLoader classLoader;
    private ModuleFactory moduleFactory;
    private Context childContext;

    public GrammarWalker() {
        setFileExtensions(new String[]{"g"});
    }

    //injected via dependency injection due to Contextualizable interface
    public void setClassLoader(final ClassLoader aClassLoader) {
        classLoader = aClassLoader;
    }

    //injected via dependency injection due to Contextualizable interface
    public void setModuleFactory(final ModuleFactory aModuleFactory) {
        moduleFactory = aModuleFactory;
    }

    @Override
    public void finishLocalSetup() {
        final DefaultContext checkContext = new DefaultContext();
        checkContext.add("classLoader", classLoader);
        checkContext.add("messages", getMessageCollector());
        checkContext.add("severity", getSeverity());
        childContext = checkContext;
    }

    @Override
    public void setupChild(final Configuration childConf) throws CheckstyleException {
        final String name = childConf.getName();
        final Object module = moduleFactory.createModule(name);
        if (!(module instanceof AGrammarConventionCheck)) {
            throw new CheckstyleException(getClass().getName() + " is not allowed as a parent of " + name);
        }
        final AGrammarConventionCheck check = (AGrammarConventionCheck) module;
        check.contextualize(childContext);
        check.configure(childConf);
        check.init();

        registerCheck(check);
    }

    private void registerCheck(final AGrammarConventionCheck check) throws CheckstyleException {
        final int[] tokens;
        final Set<String> checkTokens = check.getTokenNames();
        if (!checkTokens.isEmpty()) {
            tokens = check.getRequiredTokens();

            //register configured tokens
            final int[] acceptableTokens = check.getAcceptableTokens();
            Arrays.sort(acceptableTokens);
            for (String token : checkTokens) {
                try {
                    final int tokenId = TokenTypes.getTokenId(token);
                    if (Arrays.binarySearch(acceptableTokens, tokenId) >= 0) {
                        registerCheck(token, check);
                    }
                } catch (final IllegalArgumentException ex) {
                    throw new CheckstyleException("illegal token \"" + token + "\" in check " + check, ex);
                }
            }
        } else {
            tokens = check.getDefaultTokens();
        }
        for (int element : tokens) {
            registerCheck(element, check);
        }
        checks.add(check);
    }

    private void registerCheck(final int tokenId, final AGrammarConventionCheck check) {
        registerCheck(TokenTypes.getTokenName(tokenId), check);
    }

    private void registerCheck(final String token, final AGrammarConventionCheck check) {
        tokenToChecks.put(token, check);
    }

    @Override
    protected void processFiltered(final File file, final List<String> lines) {

        /* TODO rstoll CheckStyle uses a cache to avoid checking files multiple times.
          I am actually not sure if it is necessary, files should be processed only once anyway IMO.
          But I guess this is for versions -where the process is kept alive- (cache could be written in tmp folder)
          and re-checking is only done for files which
          have been changed. Makes perfect sense. Yet, since TSPHP is using it only in ant build so far it is not
          necessary to have a cache.
          */

        try {
            final FileText text = FileText.fromLines(file, lines);
            final FileContents contents = new FileContents(text);
            GrammarAST ast = getGrammarAST(file);
            walk(ast, contents);
        } catch (final Throwable err) {
            Utils.getExceptionLogger().debug("Throwable occurred.", err);
            getMessageCollector().add(
                    new LocalizedMessage(
                            0,
                            Defn.CHECKSTYLE_BUNDLE,
                            "general.exception",
                            new String[]{"" + err},
                            getId(),
                            this.getClass(), null));
        }
    }

    protected GrammarAST getGrammarAST(final File file) throws IOException, RecognitionException {
        String fileName = file.getName();
        ANTLRLexer lexer = new ANTLRLexer(new ANTLRReaderStream(new FileReader(file)));
        lexer.setFileName(fileName);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        ANTLRParser parser = ANTLRParser.createParser(tokenStream);
        parser.setFileName(fileName);
        Grammar grammar = new Grammar(new Tool());
        grammar.setFileName(fileName);
        return parser.grammar_(grammar).getTree();
    }

    private void walk(final GrammarAST ast, final FileContents contents) {
        getMessageCollector().reset();

        notifyBegin(ast, contents);
        processRec(ast);
        notifyEnd(ast);
    }

    /**
     * Notify interested checks that about to begin walking a tree.
     *
     * @param rootAst  the root of the tree
     * @param contents the contents of the file the AST was generated from
     */
    private void notifyBegin(final GrammarAST rootAst, final FileContents contents) {
        for (AGrammarConventionCheck check : checks) {
            check.setFileContents(contents);
            check.beginTree(rootAst);
        }
    }

    /**
     * Notify checks that finished walking a tree.
     *
     * @param rootAst the root of the tree
     */
    private void notifyEnd(final GrammarAST rootAst) {
        for (AGrammarConventionCheck check : checks) {
            check.finishTree(rootAst);
        }
    }

    private void processRec(final GrammarAST ast) {
        if (ast == null) {
            return;
        }

        notifyVisit(ast);

        if (ast.getChildCount() > 0) {
            processRec((GrammarAST) ast.getChild(0));
        }

        notifyLeave(ast);

        final GrammarAST parent = (GrammarAST) ast.getParent();
        if (parent != null) {
            final GrammarAST sibling = (GrammarAST) parent.getChild(ast.getChildIndex() + 1);
            if (sibling != null) {
                processRec(sibling);
            }
        }
    }

    /**
     * Notify interested checks that visiting a node.
     *
     * @param ast the node to notify for
     */
    private void notifyVisit(final GrammarAST ast) {
        final Collection<AGrammarConventionCheck> visitors =
                tokenToChecks.get(TokenTypes.getTokenName(ast.getType()));
        for (AGrammarConventionCheck check : visitors) {
            check.visitToken(ast);
        }
    }

    /**
     * Notify interested checks that leaving a node.
     *
     * @param ast the node to notify for
     */
    private void notifyLeave(final GrammarAST ast) {
        final Collection<AGrammarConventionCheck> visitors =
                tokenToChecks.get(TokenTypes.getTokenName(ast.getType()));
        for (AGrammarConventionCheck check : visitors) {
            check.leaveToken(ast);
        }
    }

    @Override
    public void destroy() {
        for (AGrammarConventionCheck check : checks) {
            check.destroy();
        }
        super.destroy();
    }
}
