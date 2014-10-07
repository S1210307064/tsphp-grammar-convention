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

    @Test
    public void processFiltered_GrammarWithoutHeaderSections_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile("test.g", lines);

        File headerFile = createFile("licenseHeader.txt", new String[]{"/* copyright by Robert Stoll */"});
        Configuration config = createChildConfiguration(MODULE_NAME, getHeaderFileAttribute(headerFile));

        //act
        processAndCheckNoAdditionalErrorOccurred(moduleFactory, lines, file, config, 0);

        verifyVisitAndLeaveTokenNotCalled(check);
    }

    @Test
    public void processFiltered_GrammarWitMemberSectionsButNotHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
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
    public void processFiltered_GrammarWitRuleHeaderButWithoutHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
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
    public void processFiltered_LexerGrammarWitRuleHeaderButWithoutHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("lexer grammar test;");
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
    public void processFiltered_ParserGrammarWitRuleHeaderButWithoutHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("parser grammar test;");
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
    public void processFiltered_TreeGrammarWitRuleHeaderButWithoutHeader_CheckIsNeverCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("tree grammar test;");
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
    public void processFiltered_GrammarWithHeaderWithoutNotice_CheckIsPerformedAndLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@header{}");
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
    public void processFiltered_GrammarWithParserHeaderWithoutNotice_CheckIsPerformedAndLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@parser::header{}");
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
    public void processFiltered_GrammarWithLexerHeaderWithoutNotice_CheckIsPerformedAndLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@lexer::header{}");
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
    public void processFiltered_GrammarWithHeaderNoticeHalfDone_CheckIsPerformedAndLogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@header{");
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
    public void processFiltered_GrammarWithParserHeaderNoticeHalfDone_CheckIsPerformedAndLogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@parser::header{");
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
    public void processFiltered_GrammarWithLexerHeaderNoticeHalfDone_CheckIsPerformedAndLogCalledForAppropriateLine()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@lexer::header{");
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
    public void processFiltered_GrammarWithHeaderWithFullNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@header{");
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
    public void processFiltered_GrammarWithParserHeaderWithFullNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@parser::header{");
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
    public void processFiltered_GrammarWithLexerHeaderWithFullNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@lexer::header{");
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
    public void processFiltered_GrammarWithHeaderInOneLineNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@header{/* copyright by Robert Stoll */}");
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
    public void processFiltered_GrammarWithParserHeaderInOneLineNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@parser::header{/* copyright by Robert Stoll */}");
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
    public void processFiltered_GrammarWithLexerHeaderInOneLineNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());
        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@lexer::header{/* copyright by Robert Stoll */}");
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
    public void processFiltered_GrammarWithHeaderLongerThanNotice_CheckIsPerformedAndNoLogCalled()
            throws CheckstyleException, IOException {
        HeaderCheck check = spy(createCheck());

        ModuleFactory moduleFactory = createModuleFactory(MODULE_NAME, check);

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("@header{");
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
