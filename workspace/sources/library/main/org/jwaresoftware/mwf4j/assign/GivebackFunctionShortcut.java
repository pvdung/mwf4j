/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.fixture.FunctionShortcuts;
import  org.jwaresoftware.gestalt.system.GetPropertyMethod;

/**
 * Function shortcut that will fetch named data from current harness's
 * variables. If a harness is not installed, or the named item does not
 * exist, this function shortcut returns <i>null</i> (default will be used
 * if caller specified one). Typically installed under scheme"$v:" or 
 * "$var:"; for example: {@code $var:defaults.ccy?JPY}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class GivebackFunctionShortcut extends FunctionShortcuts.PropertyFetcher
{
    public GivebackFunctionShortcut()
    {
        super();
    }

    protected String getProperty(String property, GetPropertyMethod fixture)
    {
        String string = null;
        Object object = GivebackVar.fromEvalOfOptional(property,Object.class).call();
        if (object!=null) {
            string = Strings.valueOf(object);
        }
        return string;
    }
}


/* end-of-GivebackFunctionShortcut.java */
