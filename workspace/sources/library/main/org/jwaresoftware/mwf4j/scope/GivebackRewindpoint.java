/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.util.Set;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Giveback;
import  org.jwaresoftware.mwf4j.starters.CalledClosureSkeleton;

/**
 * Giveback that will check the set of installed rewind points against an
 * application-supplied matcher. If a match is found it's given back; otherwise
 * will give back <i>null</i>. Caller gets to decide if <i>null</i> is a
 * valid response or not.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class GivebackRewindpoint extends CalledClosureSkeleton<Rewindpoint>
    implements Giveback<Rewindpoint>
{
    public GivebackRewindpoint(Rewindpoint matcher)
    {
        Validate.notNull(matcher,What.CALLBACK);
        myMarkMatcher = matcher;
    }

    @Override
    protected Rewindpoint callInner() throws Exception
    {
        Rewindpoint mark=null;
        for (Scope block:Scopes.copyOf(harness,false)) {//latest first!
            Set<Rewindpoint> cursors = block.copyOfRewindpoints();
            for (Rewindpoint next:cursors) {
                if (myMarkMatcher.matches(next)) {
                    mark=next;
                    break;
                }
            }
        }
        return mark;
    }

    private final Rewindpoint myMarkMatcher;
}


/* end-of-GivebackRewindpoint.java */
