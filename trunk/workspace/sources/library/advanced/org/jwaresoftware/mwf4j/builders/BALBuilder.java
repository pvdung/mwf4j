/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.bal.*;

/**
 * Builder for BAL specific actions. Subclasses are expected to provide the
 * final public "action" or "activity" static factory method. See BAL programmer
 * test suites for examples. Note that once a builder's {@linkplain #build}
 * method is called it is no longer active (one output per builder for now).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 * @see       BALFactory
 **/

public abstract class BALBuilder<BB extends BALBuilder<BB>> extends BuilderSkeleton
{
    public Action build()
    {
        Validate.stateNotNull(myDefinition,What.ACTION);
        Action mydef = myDefinition;
        myDefinition = null;
        return mydef;
    }


    @SuppressWarnings("unchecked")
    protected final BB add(Action step)
    {
        myDefinition.add(step);
        return (BB)this;
    }

    public BB run(Action step)
    {
        Validate.notNull(step,What.ACTION);
        return add(step);
    }

    public final BB empty()
    {
        return run(BAL().newEmpty());
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for AssignAction: set("a","value"), set("b",true), nil("c")
//  ---------------------------------------------------------------------------------------


    public BB set(String variable, String value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<String>(variable,VARIABLE,value));
    }

    public BB set(String variable, boolean value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<Boolean>(variable,VARIABLE,value));
    }

    public BB set(String variable, Object value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<Object>(variable,VARIABLE,value));
    }

    public BB set(String variable, int value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<Integer>(variable,VARIABLE,value));
    }

    public BB set(String variable, long value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<Long>(variable,VARIABLE,value));
    }

    public BB set(String variable, double value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(new AssignAction<Double>(variable,VARIABLE,value));
    }

    public final BB set(Variable variable, Object value)
    {
        validateNotNull(variable,What.VARIABLE_NAME);
        return set(variable.value(),value);
    }

    public BB set(Property property, String value)
    {
        validateNotNull(property,What.PROPERTY_NAME);
        return add(new AssignAction<String>(property.value(),PROPERTY,value));
    }

    public BB set(Property property, boolean value)
    {
        validateNotNull(property,What.PROPERTY_NAME);
        return add(new AssignAction<Boolean>(property.value(),PROPERTY,value));
    }

    public BB set(Property property, int value)
    {
        validateNotNull(property,What.PROPERTY_NAME);
        return add(new AssignAction<Integer>(property.value(),PROPERTY,value));
    }

    public BB set(String variable, Reference from)
    {
        Validate.neitherNull(variable,What.VARIABLE_NAME,from,What.REFERENCE);
        AssignAction<Object> set = new AssignAction<Object>();
        set.setFrom(from,Object.class);
        set.setTo(variable);
        return add(set);
    }

    public BB set(String variable, Variable from)
    {
        Validate.notNull(from,What.REFERENCE);
        return set(variable, new Reference(from.value()));
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for SequenceAction: block(), block("init",TRYEACH), endblock()
//  ---------------------------------------------------------------------------------------

    protected final void validateNotBuildingBlock()
    {
        Validate.stateIsNull(myInner,What.INNERBLOCK);
    }

    protected final void setOuterBlock(BB parent)
    {
        myOuter = parent;
    }

    @SuppressWarnings("unchecked")
    protected final BB enterBlock(BB child)
    {
        child.setOuterBlock((BB)this);
        myInner = child;
        return myInner;
    }

    @SuppressWarnings("unchecked")
    protected BB leaveBlock(BB child, String id)
    {
        Validate.stateIsTrue(child!=null && myInner==child,"end child is top");
        Validate.stateIsTrue(id==null || Strings.equal(id,myInner.getId()),"child.id is "+id);
        myDefinition.add(myInner.build());
        myInner.setOuterBlock(null);
        myInner = null;
        return (BB)this;
    }

    protected static void configureBlock(SequenceAction body, Flag flag)
    {
        if (TRYEACH==flag) {
            body.setTryEach(TRYEACH.on());
        } else if (PROTECT==flag) {
            body.setHaltIfError(false);
        } else if (MULTIUSE==flag) {
            body.setMode(SequenceAction.Mode.MULTIPLE);
        }
    }

    protected static SequenceAction newSequence(String id, BALFactory balFactory, Flag...flags)
    {
        SequenceAction body = (id==null) ? balFactory.newSequence() : balFactory.newSequence(id);
        if (flags!=null && flags.length>0) {
            for (Flag flag:flags) {
                configureBlock(body,flag);
            }
        }
        return body;
    }

    public BB block()
    {
        Validate.stateIsNull(myInner,What.INNERBLOCK);
        return enterBlock(newChildBuilder(BAL().newSequence()));
    }

    public BB block(Flag...flags)
    {
        Validate.stateIsNull(myInner,What.INNERBLOCK);
        SequenceAction body = newSequence(null,BAL(),flags);
        return enterBlock(newChildBuilder(body));
    }

    @SuppressWarnings("unchecked")
    public final BB end()
    {
        Validate.stateNotNull(myOuter,What.OUTERBLOCK);
        return myOuter.leaveBlock((BB)this,null);
    }

    @SuppressWarnings("unchecked")
    public final BB end(String id)
    {
        Validate.stateNotNull(myOuter,What.OUTERBLOCK);
        return myOuter.leaveBlock((BB)this,id);
        
    }

//  ---------------------------------------------------------------------------------------
//  Constructor methods: available only via concrete subclass's APIs
//  ---------------------------------------------------------------------------------------

    public final static BALFactory getDefaultFactory()
    {
        return BALFactory.Standard;
    }

    protected final BALFactory BAL()
    {
        return myFactory;
    }

    private void init(BALFactory newHelper, Sequence newBody)
    {
        Validate.neitherNull(newHelper,What.FACTORY,newBody,What.BODY);
        myFactory = newHelper;
        myDefinition = newBody;
    }

    protected BALBuilder(BALFactory newHelper, Sequence newBody)
    {
        init(newHelper,newBody);
    }

    protected BALBuilder(BALFactory newHelper)
    {
        Validate.notNull(newHelper,What.FACTORY);
        init(newHelper,newHelper.newSequence());
    }

    protected BALBuilder(String id, BALFactory newHelper)
    {
        super(id);
        Validate.notNull(newHelper,What.FACTORY);
        init(newHelper,newHelper.newSequence(id));
    }

    protected BALBuilder()
    {
        this(getDefaultFactory());
    }

    protected BALBuilder(String id)
    {
        this(id,getDefaultFactory());
    }

    protected BALBuilder(String id, Flag...flags)
    {
        super(id);
        init(getDefaultFactory(),newSequence(id,getDefaultFactory(),flags));
    }

    protected abstract BB newChildBuilder(BALFactory newHelper, Sequence newBody);

    protected final BB newChildBuilder(Sequence newBody)
    {
        return newChildBuilder(BAL(),newBody);
    }


    private Sequence myDefinition;
    private BB myOuter;
    private BB myInner;
    private BALFactory myFactory;
}


/* end-of-ActivityBuilder.java */
