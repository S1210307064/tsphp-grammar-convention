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

public class OptionsSpaceCheck extends AGrammarConventionCheck
{
    private boolean withSpacesAroundEqual = true;
    protected String type = "option";

    public void setWithSpacesAroundEqual(final boolean withSpaces) {
        withSpacesAroundEqual = withSpaces;
    }

    public boolean needsSpacesAroundEqual() {
        return withSpacesAroundEqual;
    }


    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.OPTIONS};
    }

    @Override
    public void visitToken(final GrammarAST ast, final TokenStream tokenStream) {
        final int count = ast.getChildCount();
        for (int i = 0; i < count; ++i) {
            final GrammarAST equalSign = (GrammarAST) ast.getChild(i);
            final GrammarAST lhs = (GrammarAST) equalSign.getChild(0);
            final GrammarAST rhs = (GrammarAST) equalSign.getChild(1);

            if (isOnSameLine(equalSign, lhs)) {
                boolean hasSpaceBeforeEqual = isSpaceBetween(lhs, equalSign);
                if (withSpacesAroundEqual && !hasSpaceBeforeEqual) {
                    logIt(equalSign.getLine(), type + " pair needs spaces around = and there was no space before =");
                } else if (!withSpacesAroundEqual && hasSpaceBeforeEqual) {
                    logIt(equalSign.getLine(), type + " pair should not have spaces around = and space found before =");
                }
            }
            if (isOnSameLine(equalSign, rhs)) {
                boolean hasSpaceAfterEqual = isSpaceBetween(equalSign, rhs);
                if (withSpacesAroundEqual && !hasSpaceAfterEqual) {
                    logIt(equalSign.getLine(), type + " pair needs spaces around = and there was no space after =");
                } else if (!withSpacesAroundEqual && hasSpaceAfterEqual) {
                    logIt(equalSign.getLine(), type + " pair should not have spaces around = and space found after =");
                }
            }
        }
    }

    private boolean isSpaceBetween(final GrammarAST left, final GrammarAST right) {
        return left.getCharPositionInLine() + left.getText().length() + 1 <= right.getCharPositionInLine();
    }
}
