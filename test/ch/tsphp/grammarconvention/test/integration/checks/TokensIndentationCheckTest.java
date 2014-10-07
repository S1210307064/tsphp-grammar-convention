/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.checks.TokensIndentationCheck;
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

public class TokensIndentationCheckTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "TokensIndentationCheck";
    private static final String INDENT = "    ";

    @Test
    public void processFiltered_WithoutTokens_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    @Test
    public void processFiltered_NoIndentation_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus='+';");
        lines.add("Plus = '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        String moduleName = "OptionsSpaceCheck";
        ModuleFactory moduleFactory = createModuleFactory(moduleName, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("Plus='+';");
        lines.add(INDENT + "Plus='+';");
        lines.add("Plus = '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("  Plus='+';");
        lines.add(INDENT + "Plus= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(" Plus = '+';");
        lines.add("  Plus= '+';");
        lines.add("   Plus = '+';");
        lines.add("    Plus= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add("Plus = '+';");
        lines.add("  Plus= '+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus");
        lines.add("= '+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus");
        lines.add("= '+';");
        lines.add(INDENT + "Plus");
        lines.add("= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus");
        //would need two INDENT
        lines.add(INDENT + "= '+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus");
        lines.add(" = '+';");
        lines.add(INDENT + "Plus");
        lines.add("  = '+';");
        lines.add(INDENT + "Plus");
        lines.add("   = '+';");
        lines.add(INDENT + "Plus");
        lines.add("    = '+';");
        lines.add(INDENT + "Plus");
        lines.add("     = '+';");
        lines.add(INDENT + "Plus");
        lines.add("      = '+';");
        lines.add(INDENT + "Plus");
        lines.add("       = '+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus=");
        lines.add("'+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus =");
        lines.add("'+';");
        lines.add(INDENT + "Plus= ");
        lines.add("'+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus=");
        //would need two INDENT
        lines.add(INDENT + "'+';");
        lines.add(INDENT + "Plus=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus=");
        lines.add(" '+';");
        lines.add(INDENT + "Plus=");
        lines.add("  '+';");
        lines.add(INDENT + "Plus=");
        lines.add("   '+';");
        lines.add(INDENT + "Plus= ");
        lines.add("    '+';");
        lines.add(INDENT + "Plus =");
        lines.add("     '+';");
        lines.add(INDENT + "Plus =");
        lines.add("      '+';");
        lines.add(INDENT + "Plus = ");
        lines.add("       '+';");
        lines.add(INDENT + "Plus=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
    public void processFiltered_ImaginaryTokenNotIntended_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("A;");
        lines.add(INDENT + "B;");
        lines.add("}");
        lines.add("rule : EOF;");
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
    public void processFiltered_ImaginaryTokenNotIntendedMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("A;");
        lines.add(INDENT + "B;");
        lines.add("C;");
        lines.add("D;");
        lines.add(INDENT + "E;");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 5, 6));
    }

    @Test
    public void processFiltered_ImaginaryTokenWrongIntended_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(" A;");
        lines.add(INDENT + "B;");
        lines.add("}");
        lines.add("rule : EOF;");
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
    public void processFiltered_ImaginaryTokenWrongIntendedMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(" A;");
        lines.add(INDENT + "B;");
        lines.add("  C;");
        lines.add("   D;");
        lines.add(INDENT + "E;");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 5, 6));
    }

    @Test
    public void processFiltered_CorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add(INDENT + "Plus ='+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "='+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "='+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "='+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "= '+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus =");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus =");
        lines.add(INDENT + INDENT + "'+';");
        lines.add("}");
        lines.add("rule : EOF;");
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
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "= ");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "= ");
        lines.add(INDENT + INDENT + "'+';");
        lines.add(INDENT + "Plus");
        lines.add(INDENT + INDENT + "=");
        lines.add(INDENT + INDENT + "'+';");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_ImaginaryTokenCorrectIndentation_LogItNotCalled()
            throws CheckstyleException, IOException {
        TokensIndentationCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add(INDENT + "BLOCK;");
        lines.add(INDENT + "BLOCK;");
        lines.add(INDENT + "BLOCK;");
        lines.add(INDENT + "BLOCK;");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    protected TokensIndentationCheck createCheck() {
        return new TokensIndentationCheck();
    }

}
