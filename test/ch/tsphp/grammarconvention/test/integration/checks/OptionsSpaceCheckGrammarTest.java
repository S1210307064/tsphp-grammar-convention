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
import org.antlr.runtime.TokenStream;
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

public class OptionsSpaceCheckGrammarTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "OptionsSpaceCheck";

    protected String getOptionsLine() {
        return "options{";
    }

    protected String getRuleLine() {
        return "rule : EOF;";
    }


    @Test
    public void processFiltered_SpaceRequiredNoneThere_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3));
    }

    @Test
    public void processFiltered_SpaceRequiredNoneThereMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        String moduleName = "OptionsSpaceCheck";
        ModuleFactory moduleFactory = createModuleFactory(moduleName, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(moduleName, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(4)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3, 4, 4));
    }

    @Test
    public void processFiltered_SpaceRequiredLeftMissing_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_SpaceRequiredLeftMissingMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 6));
    }

    @Test
    public void processFiltered_SpaceRequiredRightMissing_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_SpaceRequiredRightMissingMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language =Java;");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 6));
    }

    @Test
    public void processFiltered_SpaceRequiredOneLeftAndOneRightMissing_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language= Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5));
    }

    @Test
    public void processFiltered_SpaceNotRequiredButThere_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3));
    }

    @Test
    public void processFiltered_SpaceNotRequiredButThereMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(4)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 4, 5, 5));
    }

    @Test
    public void processFiltered_SpaceNotRequiredLeftHas_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_SpaceNotRequiredLeftHasMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language =Java;");
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 5));
    }

    @Test
    public void processFiltered_SpaceNotRequiredRightHas_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_SpaceNotRequiredRightHasMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language= Java;");
        lines.add("language= Java;");
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 4));
    }

    @Test
    public void processFiltered_SpaceNotRequiredOneHasLeftAndOneHasRight_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language =Java;");
        lines.add("language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5));
    }

    @Test
    public void processFiltered_SpaceRequiredRightNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language =");
        lines.add("Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceRequiredLeftNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language");
        lines.add("= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceRequiredBothNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language");
        lines.add("=");
        lines.add("Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceNotRequiredRightNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language=");
        lines.add("Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceNotRequiredLeftNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language");
        lines.add("=Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceRequiredAndSpacesGiven_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("true"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceNotRequiredNoneGiven_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("language=Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_SpaceNotRequiredBothNotOnSameLine_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsSpaceCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add("language");
        lines.add("=");
        lines.add("Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, getWithSpaceAttribute("false"));

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    private String[][] getWithSpaceAttribute(String value) {
        return new String[][]{{"withSpacesAroundEqual", value}};
    }

    protected OptionsSpaceCheck createCheck() {
        return new OptionsSpaceCheck();
    }

}
