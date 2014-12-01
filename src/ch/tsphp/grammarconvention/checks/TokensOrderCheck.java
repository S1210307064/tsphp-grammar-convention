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

public class TokensOrderCheck extends AGrammarConventionCheck
{

    private boolean isImaginary;
    private boolean haveNotReportedMixed;
    private boolean haveNotReportedOrderImaginary;
    private boolean haveNotReportedOrderNonImaginary;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.TOKENS};
    }

    @Override
    public void visitToken(final GrammarAST tokensAst, final TokenStream tokenStream) {
        final int count = tokensAst.getChildCount();
        //only check if there are at least 2 tokens
        if (count > 1) {
            check(tokensAst);
        }
    }

    private void check(final GrammarAST tokensAst) {
        isImaginary = false;
        haveNotReportedMixed = true;
        haveNotReportedOrderImaginary = true;
        haveNotReportedOrderNonImaginary = true;

        String tokenName;
        GrammarAST ast = (GrammarAST) tokensAst.getChild(0);
        if (ast.getChildCount() == 2) {
            tokenName = ast.getChild(0).getText();
        } else {
            tokenName = ast.getText();
            isImaginary = true;
        }

        final int count = tokensAst.getChildCount();
        for (int i = 1; i < count; ++i) {
            final GrammarAST equalSign = (GrammarAST) tokensAst.getChild(i);
            if (equalSign.getChildCount() == 2) {
                tokenName = checkTokenPair(tokenName, equalSign);

            } else {
                tokenName = checkImaginaryToken(tokenName, equalSign);
            }
        }
    }

    private String checkImaginaryToken(final String tokenName1, final GrammarAST equalSign) {
        String tokenName2 = equalSign.getText();
        if (!isImaginary) {
            isImaginary = true;
        } else if (haveNotReportedOrderImaginary && tokenName2.compareTo(tokenName1) < 0) {
            haveNotReportedOrderImaginary = false;
            logWrongOrder(tokenName1, tokenName2, equalSign);
        }
        return tokenName2;
    }

    private String checkTokenPair(final String tokenName1, final GrammarAST equalSign) {
        final GrammarAST lhs = (GrammarAST) equalSign.getChild(0);
        String tokenName2 = lhs.getText();
        if (isImaginary) {
            isImaginary = false;
            if (haveNotReportedMixed) {
                haveNotReportedMixed = false;
                logIt(lhs.getLine(), "imaginary tokens and non-imaginary tokens should not be mixed,"
                        + " whereas non-imaginary tokens should be first followed by the imaginary ones.");
            }
        } else if (haveNotReportedOrderNonImaginary && tokenName2.compareTo(tokenName1) < 0) {
            haveNotReportedOrderNonImaginary = false;
            logWrongOrder(tokenName1, tokenName2, lhs);
        }
        return tokenName2;
    }

    private void logWrongOrder(final String tokenName1, final String tokenName2, final GrammarAST lhs) {
        logIt(lhs.getLine(), "tokens are not in alphabetical order, spotted first occurrence. "
                + tokenName1 + " and " + tokenName2 + " have to be switched at least "
                + "(maybe there are more errors).");
    }
}
