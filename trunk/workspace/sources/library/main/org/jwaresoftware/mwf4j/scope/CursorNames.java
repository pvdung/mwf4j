/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Collection of naming utility methods for rewind cursors. Defaults are written
 * with {@linkplain NumberRewindCursor} in mind. To add your own naming
 * algorithms, extend this class. Note that the {@code nameFrom} methods will
 * always return a string; whereas the {@code *OrNull} variants can return
 * <i>null</i> depending on the inputs.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,infra,helper
 **/

public class CursorNames
{
    /** String format template used for integer-indices: {@value}.
     *  Example outputs: {@code handleOrder@12}, {@code Sequence@08}.
     */
    public static final String INDEX_TEMPLATE = "%1$s@%2$02d";


    public final static String nameFrom(String template, String basename, int index)
    {
        String name;
        if (template==null) {
            template = INDEX_TEMPLATE;
        }
        if (basename!=null) {
            name = String.format(template,basename,index);
        } else {
            name = "@"+index;
        }
        return name;
    }


    public final static String nameFrom(String basename, int index)
    {
        return nameFrom(null,basename,index);
    }


    public final static String nameFromOrNull(ControlFlowStatement owner)
    {
        String output=null;
        if (owner!=null) {
            output = What.typeCN(owner,"Statement");//eg 'Sequence','TryEachSequence'
        }
        return output;
    }


    public final static String nameFrom(ControlFlowStatement owner, int index)
    {
        String basename = nameFromOrNull(owner);
        return nameFrom(basename,index);
    }


    public final static String nameFromOrNull(ControlFlowStatement owner, Number index)
    {
        String output = null;
        if (index!=null) {
            output = nameFrom(owner,index.intValue());
        }
        return output;
    }



    /**
     * Permit extension via subclassing for application-specific names.
     **/
    protected CursorNames()
    {
        super();
    }
}


/* end-of-CursorNames.java */
