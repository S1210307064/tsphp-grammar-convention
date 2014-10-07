/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.checks;

import org.antlr.grammar.v3.ANTLRParser;

public class TokensSpaceCheck extends OptionsSpaceCheck
{

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.TOKENS};
    }

}
