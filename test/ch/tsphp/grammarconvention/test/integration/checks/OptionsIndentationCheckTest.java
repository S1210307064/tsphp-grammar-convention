/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.GrammarWalker;
import ch.tsphp.grammarconvention.checks.OptionsIndentationCheck;
import ch.tsphp.grammarconvention.test.integration.testutils.AGrammarWalkerTest;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;

public class OptionsIndentationCheckTest extends AGrammarWalkerTest
{
    @Test
    public void processFiltered_WithoutOptions_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        String moduleName = "OptionsIndentationCheck";
        ModuleFactory moduleFactory = createModuleFactory(moduleName, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(moduleName, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    //Tests for grammar options are covered in OptionsIndentationCheckGrammarTest
    //Tests for rule options are covered in OptionsIndentationCheckRuleTest

    protected OptionsIndentationCheck createCheck() {
        return new OptionsIndentationCheck();
    }

}
