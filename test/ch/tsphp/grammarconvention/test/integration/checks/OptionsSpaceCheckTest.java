/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.GrammarWalker;
import ch.tsphp.grammarconvention.checks.OptionsSpaceCheck;
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

public class OptionsSpaceCheckTest extends AGrammarWalkerTest
{
    @Test
    public void processFiltered_WithoutOptions_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", new String[][]{});

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredNoneThere_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredNoneThereMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck",getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(4)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3, 4, 4));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredLeftMissing_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

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
    public void processFiltered_GrammarOptionsSpaceRequiredLeftMissingMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 6));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredRightMissing_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredRightMissingMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language =Java;");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 6));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredOneLeftAndOneRightMissing_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

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
    public void processFiltered_GrammarOptionsSpaceNotRequiredButThere_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredButThereMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck",getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(4)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 4,5,5));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredLeftHas_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

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
    public void processFiltered_GrammarOptionsSpaceNotRequiredLeftHasMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language =Java;");
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

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
    public void processFiltered_GrammarOptionsSpaceNotRequiredRightHas_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredRightHasMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language= Java;");
        lines.add("language= Java;");
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 4));
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredOneHasLeftAndOneHasRight_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

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
    public void processFiltered_GrammarOptionsSpaceRequiredRightNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language =");
        lines.add("Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredLeftNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language");
        lines.add("= Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceRequiredBothNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("language");
        lines.add("=");
        lines.add("Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredRightNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language=");
        lines.add("Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredLeftNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language");
        lines.add("=Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarOptionsSpaceNotRequiredBothNotOnSameLine_LogNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language=Java;");
        lines.add("language");
        lines.add("=");
        lines.add("Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration("OptionsSpaceCheck", getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verifyLogItNotCalled(check);
    }

    private String[][] getWithSpaceAttribute(String value) {
        return new String[][]{{"withSpacesAroundEqual", value}};
    }

    protected OptionsSpaceCheck createCheck() {
        return new OptionsSpaceCheck();
    }

}
