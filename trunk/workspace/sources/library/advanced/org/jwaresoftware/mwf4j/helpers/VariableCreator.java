/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.messages.catalog.OJGMessages;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.CalledClosureSkeleton;

/**
 * Callback that will auto-initialize a harness variable to a new instance
 * if one was not predefined. You must supply both the key for lookup (and
 * storage if needed), and the class of the variable object. Note that it's
 * possible for the constructor to be used more than once if this creator
 * is triggered by multiple threads (against one harness) concurrently.
 * However, only ONE instance is ever stored and (re)used.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public class VariableCreator<T> extends CalledClosureSkeleton<T>
{
    public VariableCreator(String key, Class<T> ofType)
    {
        Validate.neitherNull(key,What.KEY,ofType,What.CLASS_TYPE);
        findConstructor(ofType);
        varClass = ofType;
        varKey = key;
    }

    protected T callInner() throws Exception
    {
        T value = vars.get(varKey,varClass);
        if (value==null) {
            try {
                value = varClass.newInstance();
                Object replaced = vars.putIfAbsent(varKey,value);
                if (replaced!=null) {//another thread got there first!
                    value = varClass.cast(replaced);
                }
            } catch(Exception newX) {
                String message = OJGMessages.NewInstanceFailed(varClass,newX);
                throw new ClosureException(message,newX);
            }
        }
        return value;
    }

    private void findConstructor(Class<T> ofType)
    {
        try {
            ofType.getConstructor();
        } catch(Throwable noVoidX) {
            String message = OJGMessages.IncompatibleFactoryClass(ofType.getName());
            throw new IllegalArgumentException(message);
        }
    }

    private final String varKey;
    private final Class<T> varClass;
}


/* end-of-VariableCreator.java */
