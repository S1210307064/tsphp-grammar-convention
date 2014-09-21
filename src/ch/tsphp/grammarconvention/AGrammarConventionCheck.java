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
    public void beginTree(GrammarAST rootAst) {
    }

    public void finishTree(GrammarAST rootAst) {
    }

    public void visitToken(GrammarAST ast) {
    }

    public void leaveToken(GrammarAST ast) {
    }
}
