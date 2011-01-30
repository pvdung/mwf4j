/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

/**
 * MWf4j specific service loggers.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public class Feedback extends org.jwaresoftware.gestalt.Feedback
{
    public static final String NSSUFFIX=".MWf4J";

    public static final String GROUPING_MWF4J = GROUPING_ROOT+NSSUFFIX;
    public static final Logger MWF4J = LoggerFactory.getLogger(GROUPING_MWF4J);

    public static final String GROUPING_CORE = GROUPING_SERVICES+NSSUFFIX;
    public static final Logger ForCore = LoggerFactory.getLogger(GROUPING_CORE);

    public static final String GROUPING_BAL = GROUPING_CORE+".BAL";
    public static final Logger ForBAL = LoggerFactory.getLogger(GROUPING_BAL);

    protected Feedback() {}
}


/* end-of-Feedback.java */
