/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.unit;

import ch.tsphp.grammarconvention.AGrammarConventionCheck;
import ch.tsphp.grammarconvention.GrammarWalker;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrammarWalkerTest
{

    private class DummyCheck extends AGrammarConventionCheck
    {
        @Override
        public int[] getDefaultTokens() {
            return new int[0];
        }
    }

    @Test(expected = CheckstyleException.class)
    public void setupChild_IsNotAGrammarConventionCheck_ThrowsCheckstyleException() throws CheckstyleException {
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        String notAGrammarConvention = "muaha";
        when(moduleFactory.createModule(anyString())).thenReturn(notAGrammarConvention);
        Configuration config = mock(Configuration.class);

        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.setupChild(config);
    }

    @Test(expected = CheckstyleException.class)
    public void setupChild_IsCheck_ThrowsCheckstyleException() throws CheckstyleException {
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        Check check = mock(Check.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = mock(Configuration.class);

        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.setupChild(config);
    }

    @Test
    public void setupChild_Standard_CallsInitOnCheck() throws CheckstyleException {
        AGrammarConventionCheck check = spy(new DummyCheck());
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = mock(Configuration.class);
        when(config.getAttributeNames()).thenReturn(new String[]{});
        when(config.getChildren()).thenReturn(new Configuration[]{});

        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);

        verify(check).init();
    }

    @Test
    public void destroy_Standard_CallsDestroyOnCheck() throws CheckstyleException {
        AGrammarConventionCheck check = spy(new DummyCheck());
        ModuleFactory moduleFactory = mock(ModuleFactory.class);
        when(moduleFactory.createModule(anyString())).thenReturn(check);
        Configuration config = mock(Configuration.class);
        when(config.getAttributeNames()).thenReturn(new String[]{});
        when(config.getChildren()).thenReturn(new Configuration[]{});

        GrammarWalker walker = createGrammarWalker(moduleFactory);
        walker.finishLocalSetup();
        walker.setupChild(config);
        walker.destroy();

        verify(check).destroy();
    }

    protected GrammarWalker createGrammarWalker(ModuleFactory moduleFactory) {
        GrammarWalker walker = new GrammarWalker();
        walker.setModuleFactory(moduleFactory);
        return walker;
    }
}
