/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.Collection;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.bal.BAL;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Adapter that converts a simple delimited list of strings values into
 * a collection of strings. Use as a get-method for a looping statement. Safe
 * for concurrent use from multiple threads once {@linkplain #freeze frozen}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after frozen)
 * @.group    impl,extras,helper
 **/

public final class DelimitedStringToCollectionCallback extends CollectionCallbackSkeleton<Collection<String>>
{
    public DelimitedStringToCollectionCallback(String delimited)
    {
        Validate.notNull(delimited,What.CRITERIA);
        myList = Strings.trim(delimited);
    }

    public DelimitedStringToCollectionCallback(String delimited, String delimiters)
    {
        Validate.neitherNull(delimited,What.CRITERIA,delimiters,"delimiters");
        myList = Strings.trim(delimited);
        myDelims = delimiters;
    }

    protected Collection<String> callInner()
    {
        String delims = myDelims;
        if (delims==null) {
            delims = BAL.getListsDelimiter(harness.staticView());
        }
        return Strings.lsplit(myList,delims);
    }

    public void freeze(Fixture environ)
    {
        myList   = Declarables.freeze(environ,myList);
        myDelims = Declarables.freeze(environ,myDelims);
    }

    private String myList;
    private String myDelims;
}


/* end-of-DelimitedStringToCollectionCallback.java */
