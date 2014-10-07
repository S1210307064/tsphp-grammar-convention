/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.tool.GrammarAST;

public class OptionsIndentationCheck extends AGrammarConventionCheck
{

    private static final int NUMBER_OF_SPACES = 4;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.OPTIONS};
    }

    @Override
    public void visitToken(final GrammarAST ast) {
        final int count = ast.getChildCount();
        for (int i = 0; i < count; ++i) {
            final GrammarAST equalSign = (GrammarAST) ast.getChild(i);
            final GrammarAST lhs = (GrammarAST) equalSign.getChild(0);
            final GrammarAST rhs = (GrammarAST) equalSign.getChild(1);
            if (lhs.getCharPositionInLine() != NUMBER_OF_SPACES) {
                logIt(lhs.getLine(), "should be intended by " + NUMBER_OF_SPACES + " spaces but was intended by "
                        + lhs.getCharPositionInLine());
            }
            if (!isOnSameLine(lhs, equalSign) && equalSign.getCharPositionInLine() != NUMBER_OF_SPACES) {
                logIt(lhs.getLine(), "should be intended by " + NUMBER_OF_SPACES + " spaces but was intended by "
                        + lhs.getCharPositionInLine());
            }
            if (!isOnSameLine(equalSign, rhs) && rhs.getCharPositionInLine() != NUMBER_OF_SPACES) {
                logIt(lhs.getLine(), "should be intended by " + NUMBER_OF_SPACES + " spaces but was intended by "
                        + lhs.getCharPositionInLine());
            }
        }
    }

}
