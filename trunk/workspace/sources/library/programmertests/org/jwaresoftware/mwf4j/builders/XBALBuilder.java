/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.bal.ThrowAction;
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

    public static final XBALBuilder action(String id, Flag...flags)
    {
        return new XBALBuilder(id,flags);
    }

    public XBALBuilder touch(String id)
    {
        return run(new TouchAction(id));
    }

    public XBALBuilder block(String id)
    {
        validateNotBuildingBlock();
        XBALBuilder block = enterBlock(newChildBuilder(BAL().newSequence(id)));
        block.setId(id);
        return block;
    }

    public XBALBuilder error(String id)
    {
        return run(new ThrowAction(id));
    }

    public XBALBuilder never()
    {
        return run(new EpicFail());
    }

    XBALBuilder(BALFactory newHelper, Sequence newBody) //for block(...)
    {
        super(newHelper,newBody);
    }

    XBALBuilder(String id) //for action()
    {
        super(id);
    }

    XBALBuilder() //for action(id)
    {
        super();
    }

    XBALBuilder(String id, Flag...flags)
    {
        super(id,flags);
        
    }

    protected XBALBuilder newChildBuilder(BALFactory newHelper, Sequence newBody)
    {
        return new XBALBuilder(newHelper,newBody);
    }
}


/* end-of-XBALBuilder.java */
