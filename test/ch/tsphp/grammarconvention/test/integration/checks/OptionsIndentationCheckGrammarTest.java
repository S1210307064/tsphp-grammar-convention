/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.checks.OptionsIndentationCheck;
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

public class OptionsIndentationCheckGrammarTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "OptionsIndentationCheck";
    private static final String INDENT = "    ";

    protected String getOptionsLine() {
        return "options{";
    }

    protected String getRuleLine() {
        return "rule : EOF;";
    }

    @Test
    public void processFiltered_NoIndentation_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
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
        lines.add(getOptionsLine());
        lines.add("language=Java;");
        lines.add(INDENT + "language=Java;");
        lines.add("language = Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(moduleName, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
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
        lines.add(getOptionsLine());
        lines.add("  language=Java;");
        lines.add(INDENT + "language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
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
        lines.add(getOptionsLine());
        lines.add(" language = Java;");
        lines.add("  language= Java;");
        lines.add("   language = Java;");
        lines.add("    language= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
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
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add("language = Java;");
        lines.add("  language= Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5));
    }

    @Test
    public void processFiltered_EqualOnNewLineWithoutIndentation_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language");
        lines.add("= Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_EqualOnNewLineWithoutIndentationMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language");
        lines.add("= Java;");
        lines.add(INDENT + "language");
        lines.add("= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5, 7));
    }

    @Test
    public void processFiltered_EqualOnNewLineWrongIndentation_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language");
        //would need two INDENT
        lines.add(INDENT + "= Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_EqualOnNewLineWrongIndentationMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language");
        lines.add(" = Java;");
        lines.add(INDENT + "language");
        lines.add("  = Java;");
        lines.add(INDENT + "language");
        lines.add("   = Java;");
        lines.add(INDENT + "language");
        lines.add("    = Java;");
        lines.add(INDENT + "language");
        lines.add("     = Java;");
        lines.add(INDENT + "language");
        lines.add("      = Java;");
        lines.add(INDENT + "language");
        lines.add("       = Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 7);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(7)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5, 7, 9, 11, 13, 15, 17));
    }

    @Test
    public void processFiltered_RhsOnNewLineWithoutIndentation_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language=");
        lines.add("Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_RhsOnNewLineWithoutIndentationMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language =");
        lines.add("Java;");
        lines.add(INDENT + "language= ");
        lines.add("Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5, 7));
    }

    @Test
    public void processFiltered_RhsOnNewLineWrongIndentation_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language=");
        //would need two INDENT
        lines.add(INDENT + "Java;");
        lines.add(INDENT + "language=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5));
    }

    @Test
    public void processFiltered_RhsOnNewLineWrongIndentationMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language=");
        lines.add(" Java;");
        lines.add(INDENT + "language=");
        lines.add("  Java;");
        lines.add(INDENT + "language=");
        lines.add("   Java;");
        lines.add(INDENT + "language= ");
        lines.add("    Java;");
        lines.add(INDENT + "language =");
        lines.add("     Java;");
        lines.add(INDENT + "language =");
        lines.add("      Java;");
        lines.add(INDENT + "language = ");
        lines.add("       Java;");
        lines.add(INDENT + "language=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 7);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(7)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(5, 7, 9, 11, 13, 15, 17));
    }


    @Test
    public void processFiltered_CorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language =Java;");
        lines.add(INDENT + "language =Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);


        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_EqualOnNewLineCorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "=Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "=Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "=Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "= Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_RhsOnNewLineCorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language =");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language =");
        lines.add(INDENT + INDENT + "Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_EqualSignAndRhsOnNewLineCorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        OptionsIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add(getOptionsLine());
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "= ");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "= ");
        lines.add(INDENT + INDENT + "Java;");
        lines.add(INDENT + "language");
        lines.add(INDENT + INDENT + "=");
        lines.add(INDENT + INDENT + "Java;");
        lines.add("}");
        lines.add(getRuleLine());
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    protected OptionsIndentationCheck createCheck() {
        return new OptionsIndentationCheck();
    }

}
