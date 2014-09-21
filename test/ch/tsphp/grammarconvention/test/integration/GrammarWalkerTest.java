/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.integration;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import ch.tsphp.grammarconvention.GrammarWalker;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.antlr.grammar.v3.ANTLRParser;
import org.antlr.tool.GrammarAST;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrammarWalkerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private class DummyCheck extends AGrammarConventionCheck
    {
        @Override
        public int[] getDefaultTokens() {
            return new int[0];
        }
    }

    @Test
    public void processFiltered_Standard_CallsBeginTreeAndFinishTreeOnCheck() throws CheckstyleException, IOException {
        DummyCheck check = spy(new DummyCheck());
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = createDummyConfiguration();

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("rule: EOF;");
        File file = createFile(folder, lines);

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).beginTree(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.COMBINED_GRAMMAR));
        captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).finishTree(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.COMBINED_GRAMMAR));

    }

    @Test
    public void processFiltered_TokenNamesEmptyDefaultOptions_CheckOnlyCalledForOptions() throws CheckstyleException,
            IOException {
        DummyCheck check = spy(new DummyCheck());
        when(check.getDefaultTokens()).thenReturn(new int[]{ANTLRParser.OPTIONS});
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = createDummyConfiguration();

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile(folder, lines);

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).visitToken(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.OPTIONS));
        captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).leaveToken(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.OPTIONS));
    }

    @Test
    public void processFiltered_TokenNamesContainOptionsDefaultAsWell_CheckOnlyCalledForOptions()
            throws CheckstyleException, IOException {
        DummyCheck check = spy(new DummyCheck());
        check.setTokens(new String[]{"OPTIONS"});
        when(check.getDefaultTokens()).thenReturn(new int[]{ANTLRParser.OPTIONS});
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = createDummyConfiguration();

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile(folder, lines);

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).visitToken(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.OPTIONS));
        captor = ArgumentCaptor.forClass(GrammarAST.class);
        verify(check).leaveToken(captor.capture());
        assertThat(captor.getAllValues().size(), is(1));
        assertThat(captor.getValue().getType(), is(ANTLRParser.OPTIONS));
    }

    @Test
    public void processFiltered_TokenNamesContainOptionsDefaultDoesNot_NothingCalled()
            throws CheckstyleException, IOException {
        DummyCheck check = spy(new DummyCheck());
        check.setTokens(new String[]{"OPTIONS"});
        when(check.getDefaultTokens()).thenReturn(new int[]{ANTLRParser.RULE});
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = createDummyConfiguration();

        List<String> lines = new ArrayList<>();
        lines.add("grammar test;");
        lines.add("options{");
        lines.add("language = Java;");
        lines.add("}");
        lines.add("rule: EOF;");
        File file = createFile(folder, lines);

        //act
        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.process(file, lines);

        try {
            ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
            verify(check).visitToken(captor.capture());
            fail("visitToken was called " + captor.getAllValues().size() + " time(s)");
        } catch (MockitoAssertionError e) {
            //should get exception since visitToken should not have been called
        }
        try {
            ArgumentCaptor<GrammarAST> captor = ArgumentCaptor.forClass(GrammarAST.class);
            verify(check).leaveToken(captor.capture());
            fail("leaveToken was called " + captor.getAllValues().size() + " time(s)");
        } catch (MockitoAssertionError e) {
            //should get exception since leaveToken should not have been called
        }
    }

    protected GrammarWalker createGrammarWalker(ModuleFactory moduleFactory) {
        GrammarWalker walker = new GrammarWalker();
        walker.setModuleFactory(moduleFactory);
        return walker;
    }

    public static File createFile(TemporaryFolder folder, List<String> lines) throws IOException {
        File file = folder.newFile("test.g");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (String string : lines) {
                writer.write(string);
                writer.write("\n");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return file;
    }

    private Configuration createDummyConfiguration() {
        Configuration config = mock(Configuration.class);
        when(config.getAttributeNames()).thenReturn(new String[]{});
        when(config.getChildren()).thenReturn(new Configuration[]{});
        return config;
    }
}
