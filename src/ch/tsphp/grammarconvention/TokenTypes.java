/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention;

import org.antlr.grammar.v3.ANTLRParser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class TokenTypes
{

    private static final Map<String, Integer> TOKEN_NAMES_TO_IDS = new HashMap<>();
    private static final Map<Integer, String> TOKEN_IDS_TO_NAMES = new HashMap<>();

    static {
        String[] tokenNames = ANTLRParser.tokenNames;
        for (String tokenName : tokenNames) {
            if (!tokenName.startsWith("<")) {
                try {
                    Field field = ANTLRParser.class.getField(tokenName);
                    int tokenId = field.getInt(null);
                    TOKEN_NAMES_TO_IDS.put(tokenName, tokenId);
                    TOKEN_IDS_TO_NAMES.put(tokenId, tokenName);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    //should never happen
                }
            }
        }
    }

    private TokenTypes() {
    }

    public static int getTokenId(String tokenName) {
        return TOKEN_NAMES_TO_IDS.get(tokenName);
    }

    public static String getTokenName(int tokenId) {
        return TOKEN_IDS_TO_NAMES.get(tokenId);
    }
}
