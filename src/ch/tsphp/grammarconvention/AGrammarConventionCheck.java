/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention;

import com.puppycrawl.tools.checkstyle.api.Check;
import org.antlr.tool.GrammarAST;

/**
 * Helper class for grammar convention checks.
 */
public abstract class AGrammarConventionCheck extends Check
{
    /**
     * Override this method in the sub-class to get notices before the tree is processed.
     *
     * @param rootAst The root ast of the tree
     */
    public void beginTree(final GrammarAST rootAst) {
        //override in sub-class if suitable
    }

    /**
     * Override this method in the sub-class to get notices after the tree was processed.
     *
     * @param rootAst The root ast of the tree
     */
    public void finishTree(final GrammarAST rootAst) {
        //override in sub-class if suitable
    }

    /**
     * Override this method in the sub-class to get notices before a token is processed.
     * <p/>
     * Define in getDefaultTokens() which tokens you want to be notified of.
     *
     * @param ast The ast of the corresponding token.
     */
    public void visitToken(final GrammarAST ast) {
        //override in sub-class if suitable
    }

    /**
     * Override this method in the sub-class to get notices after token was processed.
     * <p/>
     * Define in getDefaultTokens() which tokens you want to be notified of.
     *
     * @param ast The ast of the corresponding token.
     */
    public void leaveToken(final GrammarAST ast) {
        //override in sub-class if suitable
    }

    //CheckStyle made log final and thus cannot be used in verification of tests.
    public void logIt(final int line, final String key) {
        log(line, key);
    }

    protected boolean isOnSameLine(final GrammarAST left, final GrammarAST right) {
        return left.getLine() == right.getLine();
    }
}
