/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.slf4j.Logger;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Traceable;

/**
 * Helper that emits potentially high-volume trace information around statement
 * and other component execution. Default is customized for BAL and ECA type fine
 * tracing, but can be customized for other components via Traceable link. Uses
 * the {@linkplain MDC} indentation utilities to give slightly more sane looking
 * output for nested statements. Easily plugged in as implementation support
 * for {@linkplain org.jwaresoftware.mwf4j.behaviors.Executable Executables}.
 * <p/>
 * <b>Usage note 1:</b> the result of {@linkplain #isEnabled()} should
 * NOT change during the lifetime of a single trace support instance. If this
 * attribute flips back-n-forth, the enter/leave pairs (default trace markers)
 * can get out of sequence. This method was not made final to allow extensions
 * to add other UNCHANGING criteria for the "enabled" state (like checking 
 * additional criteria beside the Logger's enabled flag).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public class TraceSupport
{
    public TraceSupport(final Traceable link)
    {
        verifyLink(link);
        myLink = link;
        myIndentMarker= MWf4J.NS+"."+link.typeCN()+".trace.indentlevel";
    }


    public final Logger logger()
    {
        return myLink.logger();
    }


    public boolean isEnabled()
    {
        return logger().isTraceEnabled();
    }


    public final void write(String format, Object...args)
    {
        if (isEnabled()) {
            Object[] finalargs;
            if (args!=null && args.length>0) {
                finalargs = new Object[args.length+1];
                finalargs[0]= currentIndent();
                System.arraycopy(args, 0, finalargs, 1, args.length);
            } else {
                finalargs = new Object[]{currentIndent()};
            }
            logger().trace("{} >"+format,finalargs);
        }
    }


    protected final void caughtThis(Throwable detected)
    {
        Object[] args= new Object[]{currentIndent(),myLink.getId(),Throwables.getTypedMessage(detected)};
        logger().warn("{} >Detected exception in '{}': {}",args);
    }


    public final void caught(Throwable detected)
    {
        if (isEnabled()) {
            caughtThis(detected);
        }
    }


    public final void throwing(Throwable generated)
    {
        if (isEnabled()) {
            Object[] finalargs= new Object[]{currentIndent(),myLink.getId(),Throwables.getTypedMessage(generated)};
            logger().warn("{} >Throw from '{}': {}",finalargs);
        }
    }


    public final void signaling(String message)
    {
        if (isEnabled()) {
            Object[] finalargs= new Object[]{currentIndent(),myLink.getId(),message};
            logger().error("{} >Signal from '{}': {}",finalargs);
        }
    }


    protected final String indentMarker()
    {
        return myIndentMarker;
    }


    public final String currentIndent()
    {
        return MDC.currentIndent(indentMarker());
    }


    protected final void enteringThis()
    {
        Object[] args = new Object[]{MDC.enterIndent(indentMarker()),myLink.typeCN(),myLink.toString()};
        logger().trace("{}Enter {} {}",args);
    }


    public void doEnter(Harness h)
    {
        if (isEnabled()) {
            enteringThis();
        }
    }


    protected final void leavingThis()
    {
        Object[] args = new Object[]{MDC.leaveIndent(indentMarker()),myLink.typeCN(),myLink.toString()};
        logger().trace("{}Leave {} {}",args);
    }


    public void doLeave(Harness h)
    {
        if (isEnabled()) {
            leavingThis();
        }
    }


    public void doNexxt(Object next, Harness h)
    {
        if (isEnabled()) {
            logger().trace("{} >NEXT: {}",currentIndent(),next);
        }
    }


    public void doUnwind(Harness h)
    {
        if (isEnabled()) {
            Object[] args = new Object[]{currentIndent(),myLink.typeCN(),myLink.toString()};
            logger().trace("{}Unwind {} {}",args);
        }
    }


    public void doError(Harness h, Throwable issue)
    {
        if (isEnabled()) {
            caughtThis(issue);
        }
    }


    private void verifyLink(Traceable link)
    {
        Validate.notNull(link,What.CALLBACK);
        Validate.notNull(link.logger(),"logger");
        Validate.notBlank(link.typeCN(),"typeid");
    }


    protected final Traceable myLink;
    private final String myIndentMarker;
}


/* end-of-TraceSupport.java */
