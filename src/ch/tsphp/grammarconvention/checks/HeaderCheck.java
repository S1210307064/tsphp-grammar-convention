/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.runtime.TokenStream;
import org.antlr.tool.GrammarAST;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HeaderCheck extends AGrammarConventionCheck
{
    private String headerFile;
    private List<String> licenceNotice;

    public void setHeaderFile(final String headerFilePath) {
        headerFile = headerFilePath;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.AMPERSAND};
    }

    @Override
    public void init() {
        if (headerFile == null || headerFile.isEmpty()) {
            throw new IllegalStateException("The property 'headerFile' needs to be specified "
                    + "in order that HeaderCheck works");
        }
        try {
            licenceNotice = Files.readAllLines(Paths.get(headerFile), Charset.defaultCharset());
            if (licenceNotice.size() <= 1 && licenceNotice.get(0).equals("")) {
                throw new IllegalStateException("headerFile did not contain any content. "
                        + "Did you forgot to save the content?.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the headerFile: " + headerFile);
        }
    }

    @Override
    public void visitToken(final GrammarAST ast, final TokenStream tokenStream) {
        if (isNotGrammarAction(ast)) {
            return;
        }

        String id;
        String action;
        if (ast.getChildCount() == 2) {
            // (@ id ACTION)
            id = ast.getChild(0).getText();
            action = ast.getChild(1).getText();
        } else {
            // (@ scopeName id ACTION){
            id = ast.getChild(1).getText();
            action = ast.getChild(2).getText();
        }
        if (id.equals("header")) {
            String[] lines = action.split("\\r?\\n");
            //line 0 is empty means that one starts to write the notice on the next line after @header{
            //which is most probably the normal case.
            int start = lines[0].equals("") ? 1 : 0;
            int numberOfLines = lines.length;
            if (numberOfLines - start == 0) {
                logIt(ast.getLine(), "License notice is missing.");
            } else {
                for (int i = start; i < numberOfLines; ++i) {
                    if (!lines[i].equals(licenceNotice.get(i - start))) {
                        logIt(ast.getLine() + i, "License missing or wrong. Mismatch found!\n"
                                + "excepted: " + licenceNotice.get(i) + "\n"
                                + "found: " + lines[i]);
                        break;
                    }
                }
            }
        }
    }

    private boolean isNotGrammarAction(GrammarAST ast) {
        int parentType = ast.getParent().getType();
        return parentType != ANTLRParser.LEXER_GRAMMAR
                && parentType != ANTLRParser.PARSER_GRAMMAR
                && parentType != ANTLRParser.TREE_GRAMMAR
                && parentType != ANTLRParser.COMBINED_GRAMMAR;

    }
}
