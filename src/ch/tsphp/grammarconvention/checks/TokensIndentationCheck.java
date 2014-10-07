/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.runtime.TokenStream;
import org.antlr.tool.GrammarAST;

public class TokensIndentationCheck extends OptionsIndentationCheck
{

    private static final int NUMBER_OF_SPACES = 4;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.TOKENS};
    }

    @Override
    public void visitToken(final GrammarAST ast, final TokenStream tokenStream) {
        final int count = ast.getChildCount();
        for (int i = 0; i < count; ++i) {
            final GrammarAST equalSign = (GrammarAST) ast.getChild(i);
            if (equalSign.getChildCount() == 2) {
                checkTokenPair(equalSign);
            } else {
                checkImaginaryToken(equalSign);
            }
        }
    }

    private void checkImaginaryToken(final GrammarAST imaginaryToken) {
        int equalSignPositionInLine = imaginaryToken.getToken().getCharPositionInLine();
        if (equalSignPositionInLine != NUMBER_OF_SPACES) {
            logIt(imaginaryToken.getLine(), "imaginary token should be intended by "
                    + NUMBER_OF_SPACES * 2 + " spaces but was intended by " + equalSignPositionInLine);
        }
    }

    private void checkTokenPair(final GrammarAST equalSign) {
        final GrammarAST lhs = (GrammarAST) equalSign.getChild(0);
        final GrammarAST rhs = (GrammarAST) equalSign.getChild(1);
        int lhsPositionInLine = lhs.getToken().getCharPositionInLine();
        if (lhsPositionInLine != NUMBER_OF_SPACES) {
            logIt(lhs.getLine(), "token pair should be intended by "
                    + NUMBER_OF_SPACES + " spaces but was intended by " + lhsPositionInLine);
        }
        int equalSignPositionInLine = equalSign.getToken().getCharPositionInLine();
        if (!isOnSameLine(lhs, equalSign) && equalSignPositionInLine != NUMBER_OF_SPACES * 2) {
            logIt(equalSign.getLine(), "= sign of a token pair should be intended by "
                    + NUMBER_OF_SPACES * 2 + " spaces but was intended by " + equalSignPositionInLine);
        }
        int rhsPositionInLine = rhs.getToken().getCharPositionInLine();
        if (!isOnSameLine(equalSign, rhs) && rhsPositionInLine != NUMBER_OF_SPACES * 2) {
            logIt(rhs.getLine(), "right hand side of a token pair should be intended by "
                    + NUMBER_OF_SPACES * 2 + " spaces but was intended by " + rhsPositionInLine);
        }
    }
}
