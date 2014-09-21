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
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessages;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class GrammarWalkerErrorTest
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

    private class DummyGrammarWalker extends GrammarWalker
    {
        public void processFiltered(File file, List<String> lines) {
            super.processFiltered(file, lines);
        }

        public LocalizedMessages getMessageCollectorPublic() {
            return super.getMessageCollector();
        }
    }


    @Test
    public void processFiltered_FileDoesNotExist_ReportError() throws CheckstyleException, IOException {
        AGrammarConventionCheck check = spy(new DummyCheck());
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = mock(Configuration.class);
        when(config.getAttributeNames()).thenReturn(new String[]{});
        when(config.getChildren()).thenReturn(new Configuration[]{});

        List<String> lines = new ArrayList<>();
        lines.add("grammar test; //no rules defined");
        File file = new File("nonExistingFile");

        DummyGrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.processFiltered(file, lines);

        LocalizedMessages messages = walker.getMessageCollectorPublic();
        assertThat(messages.size(), is(1));
        LocalizedMessage localizedMessage = messages.getMessages().first();
        assertThat(localizedMessage.getKey(), is("general.exception"));
        assertThat(localizedMessage.getSeverityLevel(), is(SeverityLevel.ERROR));
        assertThat(localizedMessage.getMessage(), is("Got an exception - "
                + "java.io.FileNotFoundException: nonExistingFile (The system cannot find the file specified)"));
    }


    protected DummyGrammarWalker createGrammarWalker(ModuleFactory moduleFactory) {
        DummyGrammarWalker walker = new DummyGrammarWalker();
        walker.setModuleFactory(moduleFactory);
        return walker;
    }
}
