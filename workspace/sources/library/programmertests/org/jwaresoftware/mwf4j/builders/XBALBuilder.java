/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.bal.ThrowAction;
import  org.jwaresoftware.mwf4j.starters.EchoAction;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Local extension of a {@linkplain BALBuilder} that enables testing for
 * extensibility and test-specific DSL features.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper,test
 **/

public final class XBALBuilder extends BALBuilder<XBALBuilder>
{
    public static final XBALBuilder action()
    {
        return new XBALBuilder();
    }

    public static final XBALBuilder action(String id)
    {
        return new XBALBuilder(id);
    }

    public static final XBALBuilder action(Flag...flags)
    {
        return new XBALBuilder(null,flags);
    }

    public static final XBALBuilder action(String id, Flag...flags)
    {
        return new XBALBuilder(id,flags);
    }

 

    public XBALBuilder touch(String id)
    {
        TouchAction touch = new TouchAction(id);
        if (BAL().isCheckDeclarables())
            touch.setCheckDeclarables(true);
        return run(touch);
    }

    public XBALBuilder error(String id)
    {
        return run(new ThrowAction(id));
    }

    public XBALBuilder never()
    {
        return run(new EpicFail());
    }

    public XBALBuilder echocursor(String key)
    {
        EchoAction echo = new EchoAction("echo",key);
        if (BAL().isCheckDeclarables())
            echo.setCheckDeclarables(true);
        return add(echo);
    }


    XBALBuilder(BALFactory newHelper,Sequence newBody,Finishers finisherStack,Finisher newFinisher)
    {
        super(newHelper,newBody,finisherStack,newFinisher);
    }

    XBALBuilder(String id) //for action(id)
    {
        super(id);
    }

    XBALBuilder() //for action()
    {
        super();
    }

    XBALBuilder(String id, Flag...flags) //for action(flags)|action(id,flags)
    {
        super(id,flags);
        
    }

    protected XBALBuilder newChildBuilder(BALFactory childHelper, Sequence childBody, Finisher childFinisher)
    {
        return new XBALBuilder(childHelper,childBody,getFinishers(),childFinisher);
    }
}


/* end-of-XBALBuilder.java */
