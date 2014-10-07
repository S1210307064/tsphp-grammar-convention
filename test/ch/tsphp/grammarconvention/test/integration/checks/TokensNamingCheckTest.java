/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.checks.TokensNamingCheck;
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

public class TokensNamingCheckTest extends AGrammarWalkerTest
{

    private static final String MODULE_NAME = "TokenNamingCheckTest";

    @Test
    public void processFiltered_WithoutTokens_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        TokensNamingCheck check = spy(createCheck());

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
    public void processFiltered_NotEverythingInUpperCase_LogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        TokensNamingCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("BLOCK;");
        lines.add("Aa;");
        lines.add("METHOD_CALL;");
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
    public void processFiltered_NotEverythingInUpperCaseMultipleTimes_LogCalledForAppropriateLines()
            throws CheckstyleException, IOException {
        TokensNamingCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("BLOCK;");
        lines.add("Aa;");
        lines.add("B_a;");
        lines.add("METHOD_CALL;");
        lines.add("A_a_As;");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 3);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check, times(3)).logIt(captor.capture(), anyString());
        assertThat(captor.getAllValues(), contains(4, 5, 7));
    }


    @Test
    public void processFiltered_NonImaginaryTokens_LogItNotCalled()
            throws CheckstyleException, IOException {
        TokensNamingCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("A = 'a';");
        lines.add("B = 'b';");
        lines.add("C = 'c';");
        lines.add("D = 'd';");
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
    public void processFiltered_CorrectNaming_LogItNotCalled()
            throws CheckstyleException, IOException {
        TokensNamingCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("tokens{");
        lines.add("A = 'a';");
        lines.add("BLOCK;");
        lines.add("METHOD_CALL;");
        lines.add("}");
        lines.add("rule : EOF;");
        File file = createFile("test.g", lines);

        Configuration config = createChildConfiguration(MODULE_NAME, new String[][]{});

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    protected TokensNamingCheck createCheck() {
        return new TokensNamingCheck();
    }

}
