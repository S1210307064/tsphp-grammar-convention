/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.grammarconvention.test.unit.checks;

import ch.tsphp.grammarconvention.checks.OptionsSpaceCheck;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OptionsSpaceCheckTest
{
    @Test
    public void construct_Standard_SpaceRequiredIsTrue(){
        //no arrange necessary

        OptionsSpaceCheck check = new OptionsSpaceCheck();

        assertThat(check.getWithSpacesAroundEqual(), is(true));
    }
}
