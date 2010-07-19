/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

/**
 * The types of join points a forked action can use for its branches. 
 * Note that {@linkplain JoinType#NONE} is the equivalent of send-and-forget;
 * there is no "join" effectively.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,api,helper
 **/

public enum JoinType
{
    ALL, ANY, NONE;
}


/* end-of-JoinType.java */
