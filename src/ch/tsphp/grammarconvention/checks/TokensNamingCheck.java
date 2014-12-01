/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.runtime.TokenStream;
import org.antlr.tool.GrammarAST;

public class TokensNamingCheck extends OptionsSpaceCheck
{

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.TOKENS};
    }

    @Override
    public void visitToken(final GrammarAST ast, final TokenStream tokenStream) {
        final int count = ast.getChildCount();
        for (int i = 0; i < count; ++i) {

            final GrammarAST imaginaryToken = (GrammarAST) ast.getChild(i);
            //only naming of imaginary tokens is checked
            if (isImaginaryTokenAndNotUpperCase(imaginaryToken)) {
                logIt(imaginaryToken.getLine(), "imaginary tokens have to be in upper case.");
            }
        }
    }

    private boolean isImaginaryTokenAndNotUpperCase(GrammarAST imaginaryToken) {
        return imaginaryToken.getChildCount() == 0
                && !imaginaryToken.getText().equals(imaginaryToken.getText().toUpperCase());
    }
}
