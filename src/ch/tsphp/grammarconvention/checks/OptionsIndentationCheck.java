/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.tool.GrammarAST;

public class OptionsIndentationCheck extends AGrammarConventionCheck
{

    private static final int NUMBER_OF_SPACES = 4;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.OPTIONS};
    }

    @Override
    public void visitToken(final GrammarAST ast, final TokenStream tokenStream) {
        if (isGrammarOrRule(ast.getParent())) {
            check(ast);
        }
    }

    private boolean isGrammarOrRule(Tree parent) {
        int parentType = parent.getType();
        return parentType == ANTLRParser.COMBINED_GRAMMAR
                || parentType == ANTLRParser.TREE_GRAMMAR
                || parentType == ANTLRParser.PARSER_GRAMMAR
                || parentType == ANTLRParser.LEXER_GRAMMAR
                || parentType == ANTLRParser.RULE;
    }

    private void check(final GrammarAST ast) {
        final int count = ast.getChildCount();
        for (int i = 0; i < count; ++i) {
            final GrammarAST equalSign = (GrammarAST) ast.getChild(i);
            final GrammarAST lhs = (GrammarAST) equalSign.getChild(0);
            final GrammarAST rhs = (GrammarAST) equalSign.getChild(1);
            if (lhs.getToken().getCharPositionInLine() != NUMBER_OF_SPACES) {
                logIt(lhs.getLine(), "option pair should be intended by "
                        + NUMBER_OF_SPACES + " spaces but was intended by " + lhs.getCharPositionInLine());
            }
            int equalSignPositionInLine = equalSign.getToken().getCharPositionInLine();
            if (!isOnSameLine(lhs, equalSign) && equalSignPositionInLine != NUMBER_OF_SPACES * 2) {
                logIt(equalSign.getLine(), "= sign of an option pair should be intended by "
                        + NUMBER_OF_SPACES * 2 + " spaces but was intended by " + equalSignPositionInLine);
            }
            int rhsPositionInLine = rhs.getToken().getCharPositionInLine();
            if (!isOnSameLine(equalSign, rhs) && rhsPositionInLine != NUMBER_OF_SPACES * 2) {
                logIt(rhs.getLine(), "right hand side of an option pair should be intended by "
                        + NUMBER_OF_SPACES * 2 + " spaces but was intended by " + rhsPositionInLine);
            }
        }
    }

}
