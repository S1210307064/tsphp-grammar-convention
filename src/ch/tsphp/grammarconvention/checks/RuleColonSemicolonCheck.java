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

public class RuleColonSemicolonCheck extends AGrammarConventionCheck
{

    private static final int NUMBER_OF_SPACES = 4;

    @Override
    public int[] getDefaultTokens() {
        return new int[]{ANTLRParser.RULE};
    }

    @Override
    public void visitToken(final GrammarAST ruleAst, final TokenStream tokenStream) {
        GrammarAST colon = getColonAst(ruleAst);
        tokenStream.seek(colon.getToken().getTokenIndex());
        GrammarAST tokenBeforeColon = new GrammarAST(tokenStream.LT(-1));
        GrammarAST semicolon = (GrammarAST) ruleAst.getChild(ruleAst.getChildCount() - 1);
        tokenStream.seek(semicolon.getToken().getTokenIndex());
        GrammarAST tokenBeforeSemicolon = new GrammarAST(tokenStream.LT(-1));

        if (isOnSameLine(tokenBeforeColon, colon)) {
            logIt(colon.getLine(), ": of a rule needs to be on its own line.");
        } else {
            int colonPositionInLine = colon.getToken().getCharPositionInLine();
            if (colonPositionInLine != NUMBER_OF_SPACES) {
                logIt(colon.getLine(), ": of a rule should be intended by " + NUMBER_OF_SPACES
                        + " spaces but was intended by " + colonPositionInLine);
            }
        }

        if (isOnSameLine(tokenBeforeSemicolon, semicolon)) {
            logIt(semicolon.getLine(), "; of a rule needs to be on its own line.");
        } else {
            int semicolonPositionInLine = semicolon.getToken().getCharPositionInLine();
            if (semicolonPositionInLine != NUMBER_OF_SPACES) {
                logIt(semicolon.getLine(), "; of a rule should be intended by " + NUMBER_OF_SPACES
                        + " spaces but was intended by " + semicolonPositionInLine);
            }
        }
    }

    private GrammarAST getColonAst(GrammarAST ruleAst) {
        GrammarAST alternatives = (GrammarAST) ruleAst.getChild(ruleAst.getChildCount() - 2);
        // catch block is optional, if it is there then the alternatives are located at -3
        if (alternatives.getType() == ANTLRParser.CATCH) {
            alternatives = (GrammarAST) ruleAst.getChild(ruleAst.getChildCount() - 3);
        }

        // since the alternatives use an imaginary token BLOCK as root, which in turn is is built by the token before
        // the alternatives (the COLON), it corresponds to the colon AST
        return alternatives;
    }
}
