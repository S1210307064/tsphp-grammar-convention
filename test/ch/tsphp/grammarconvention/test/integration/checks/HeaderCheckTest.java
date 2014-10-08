/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.GrammarWalker;
import ch.tsphp.grammarconvention.checks.HeaderCheck;
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

public class HeaderCheckTest extends AGrammarWalkerTest
{
    private static final String MODULE_NAME = "HeaderCheck";

    @Test(expected = IllegalStateException.class)
    public void processFiltered_HeaderFilePropertyNotSpecified_ThrowsIllegalStateException()
            throws CheckstyleException, IOException {
        HeaderCheck check = createCheck();
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);
        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        //assert in @Test
    }

    @Test(expected = IllegalStateException.class)
    public void processFiltered_HeaderFilePropertyEmpty_ThrowsIllegalStateException()
            throws CheckstyleException, IOException {
        HeaderCheck check = createCheck();
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);
        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{{"headerFile", ""}});

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        //assert in @Test
    }

    @Test(expected = IllegalStateException.class)
    public void processFiltered_HeaderFileNotFound_ThrowsIllegalStateException()
            throws CheckstyleException, IOException {
        HeaderCheck check = createCheck();
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);
        File file = new File("nonExistingFile");
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(file));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, new ArrayList<String>());

        //assert in @Test
    }

    @Test(expected = IllegalStateException.class)
    public void processFiltered_HeaderFileEmpty_ThrowsIllegalStateException()
            throws CheckstyleException, IOException {
        HeaderCheck check = createCheck();
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);
        File file = createFile("licenseHeader.txt", new String[]{""});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(file));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, new ArrayList<String>());

        //assert in @Test
    }

    protected HeaderCheck createCheck() {
        return new HeaderCheck();
    }

    private String[][] getHeaderFileAttribute(File file) {
        return new String[][]{{"headerFile", file.getPath()}};
    }
}
