/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.checks.RuleColonSemicolonCheck;
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

public class RuleColonSemicolonCheckTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "RuleColonSemicolonCheck";
    private static final String INDENT = "    ";

    @Test
    public void processFiltered_ColonNotOnOwnLine_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA : ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(2));
    }

    @Test
    public void processFiltered_ColonNotIndented_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(": ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3));
    }

    @Test
    public void processFiltered_ColonNotIndentedMultipleTimes_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(": ruleC");
        lines.add(INDENT + ";");
        lines.add("ruleC");
        lines.add(": ruleD");
        lines.add(INDENT + ";");
        lines.add("ruleD");
        lines.add(": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check, times(4)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(6, 9, 12));
    }

    @Test
    public void processFiltered_ColonWrongIndented_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(" : ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3));
    }

    @Test
    public void processFiltered_ColonWrongIndentedMultipleTimes_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(" : ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add("  : ruleC");
        lines.add(INDENT + ";");
        lines.add("ruleC");
        lines.add("   : ruleD");
        lines.add(INDENT + ";");
        lines.add("ruleD");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check, times(4)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 6, 9));
    }

    @Test
    public void processFiltered_SemicolonNotOnOwnLine_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB ;");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3));
    }

    @Test
    public void processFiltered_SemicolonNotIndented_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(";");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_SemicolonNotIndentedMultipleTimes_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(INDENT + ": ruleC");
        lines.add(";");
        lines.add("ruleC");
        lines.add(INDENT + ": ruleD");
        lines.add(";");
        lines.add("ruleD");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check, times(4)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(7, 10));
    }

    @Test
    public void processFiltered_SemicolonWrongIndented_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(" ;");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(1)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4));
    }

    @Test
    public void processFiltered_SemicolonWrongIndentedMultipleTimes_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(" ;");
        lines.add("ruleB");
        lines.add(INDENT + ": ruleC");
        lines.add("  ;");
        lines.add("ruleC");
        lines.add(INDENT + ": ruleD");
        lines.add("   ;");
        lines.add("ruleD");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check, times(4)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 7, 10));
    }

    @Test
    public void processFiltered_ColonAndSemicolonNotOnOwnLine_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA : ruleB;");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(2, 2));
    }

    @Test
    public void processFiltered_ColonNotIntendedAndSemicolonNotOnOwnLine_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA : ruleB;");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(2, 2));
    }

    @Test
    public void processFiltered_ColonWrongIntendedAndSemicolonNotOnOwnLine_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add("  : ruleB;");
        lines.add("ruleB");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 2);

        verify(check, times(2)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(2)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(3, 3));
    }

    @Test
    public void processFiltered_Correct_LogNotCalled()
            throws CheckstyleException, IOException {
        RuleColonSemicolonCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("ruleA");
        lines.add(INDENT + ": ruleB");
        lines.add(INDENT + ";");
        lines.add("ruleB");
        lines.add(INDENT + ": ruleC");
        lines.add(INDENT + ";");
        lines.add("ruleC");
        lines.add(INDENT + ": ruleD");
        lines.add(INDENT + ";");
        lines.add("ruleD");
        lines.add(INDENT + ": EOF");
        lines.add(INDENT + ";");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check, times(4)).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    protected RuleColonSemicolonCheck createCheck() {
        return new RuleColonSemicolonCheck();
    }

}
