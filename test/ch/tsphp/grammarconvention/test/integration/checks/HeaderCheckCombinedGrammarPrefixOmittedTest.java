/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration.checks;

import ch.tsphp.grammarconvention.checks.HeaderCheck;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class HeaderCheckCombinedGrammarPrefixOmittedTest extends AGrammarWalkerTest
{
    private static final String MODULE_NAME = "HeaderCheck";

    protected String getGrammarLine() {
        return "grammar test;";
    }
    protected String getHeaderIdentifier() {
        return "@header";
    }


    @Test
    public void processFiltered_WithoutHeaderSections_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    @Test
    public void processFiltered_WitMemberSectionsButNotHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add("@members{}");
        lines.add("@parser::members{}");
        lines.add("@lexer::members{}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    @Test
    public void processFiltered_WitRuleHeaderButWithoutHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add("@members{}");
        lines.add("rule");
        lines.add("@header{}");
        lines.add(": EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verifyVisitAndLeaveTokenNotCalled(check);
    }
    
    @Test
    public void processFiltered_WithoutNotice_CheckIsPerformedAndLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add(getHeaderIdentifier() + "{}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check).logIt(captor.capture(), anyString());
        assertThat(captor.getValue(), is(2));
    }


    @Test
    public void processFiltered_HeaderNoticeHalfDone_CheckIsPerformedAndLogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add(getHeaderIdentifier() + "{");
        lines.add("/*");
        lines.add(" */");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/*", " * copyright by Robert Stoll", " */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 1);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(check).logIt(captor.capture(), anyString());
        assertThat(captor.getValue(), is(4));
    }

    @Test
    public void processFiltered_HeaderWithFullNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add(getHeaderIdentifier() + "{");
        lines.add("/*");
        lines.add(" * copyright by Robert Stoll");
        lines.add(" */");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/*", " * copyright by Robert Stoll", " */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_HeaderInOneLineNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add(getHeaderIdentifier() + "{/* copyright by Robert Stoll */}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    @Test
    public void processFiltered_HeaderLongerThanNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add(getGrammarLine());
        lines.add(getHeaderIdentifier() + "{");
        lines.add("/* copyright by Robert Stoll */");
        lines.add("//just a superfluous comment");
        lines.add("//just a superfluous comment");
        lines.add("//just a superfluous comment");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verify(check).visitToken(any(GrammarAST.class), any(TokenStream.class));
        verifyLogItNotCalled(check);
    }

    protected HeaderCheck createCheck() {
        return new HeaderCheck();
    }

    private String[][] getHeaderFileAttribute(File file) {
        return new String[][]{{"headerFile", file.getPath()}};
    }
}
