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
import org.antlr.tool.GrammarAST;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OptionsIndentationCheckGrammarTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "OptionsIndentationCheck";
    private static final String INDENT = "    ";

    @Test
    public void processFiltered_NoIndentation_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add(INDENT + "language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_NoIndentationMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        String moduleName = "OptionsSpaceCheck";
        ModuleFactory moduleFactory = createModuleFactory(moduleName, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add(INDENT + "language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(moduleName, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 5));
    }

    @Test
    public void processFiltered_NotEnoughSpaces_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("  language=Java;");
        lines.add(INDENT + "language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3));
    }

    @Test
    public void processFiltered_NotEnoughSpacesMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add(" language = Java;");
        lines.add("  language= Java;");
        lines.add("   language = Java;");
        lines.add("    language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 4, 5));
    }

    @Test
    public void processFiltered_NoIndentationAndNotEnough_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add(INDENT + "language =Java;");
        lines.add("language = Java;");
        lines.add("  language= Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5));
    }

    @Test
    public void processFiltered_CorrectIndentation_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add(INDENT + "language =Java;");
        lines.add("language = Java;");
        lines.add("  language= Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5));
    }

    protected OptionsIndentationCheck createCheck() {
        return new OptionsIndentationCheck();
    }

}
