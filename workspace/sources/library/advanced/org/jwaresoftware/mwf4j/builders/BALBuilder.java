/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import java.util.Collection;
import java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.bal.*;
import  org.jwaresoftware.mwf4j.scope.ByNameRewindpointMatcher;

/**
 * Builder for BAL specific actions. Subclasses are expected to provide the
 * final public "action" or "activity" static factory method. See BAL programmer
 * test suites for examples. Note that once a builder's {@linkplain #build}
 * method is called it is no longer usable (for now: one output per builder 
 * instance in its lifetime).
 * <p/>
 * <b>Implementation note:</b> due to the comprehensive "touches all of BAL"
 * nature of this class, in addition to making building BAL-based flows 
 * simpler for real applications, it's designed to make comprehensive unit
 * and integration testing of MWf4J itself possible.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras
 * @see       BALFactory
 **/

@SuppressWarnings("unchecked")
public abstract class BALBuilder<BB extends BALBuilder<BB>> extends BuilderSkeleton
{
    public Action build()
    {
        Validate.stateNotNull(myDefinition,What.ACTION);
        Validate.stateIsTrue(getFinishers().isFlat(),"is outermost builder");
        Action theOutput = myDefinition;
        uninitThis();
        return theOutput;
    }

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
        return add(BAL().newEmpty());
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for AssignAction: set("a","value"), set("b",true), nil("c")
//  ---------------------------------------------------------------------------------------

    public BB set(String flag)
    {
        Validate.notNull(flag,What.VARIABLE_NAME);
        return add(BAL().newSet(flag,VARIABLE,Boolean.TRUE));
    }

    public <T> BB set(String variable, T value)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(BAL().newSet(variable,VARIABLE,value));
    }

    public final <T> BB set(Variable variable, T value)
    {
        validateNotNull(variable,What.VARIABLE_NAME);
        return set(variable.value(),value);
    }

    public <T> BB set(Property property, T value)
    {
        validateNotNull(property,What.PROPERTY_NAME);
        return add(BAL().newSet(property.value(),PROPERTY,value));
    }

    public <T> BB set(MDCID threadvar, T value)
    {
        validateNotNull(threadvar,What.VARIABLE_NAME);
        return add(BAL().newSet(threadvar.value(),ONTHREAD,value));
    }

    public BB set(String variable, Reference from)
    {
        Validate.neitherNull(variable,What.VARIABLE_NAME,from,What.REFERENCE);
        AssignAction<Object> set = BAL().newSet();
        set.setFrom(from,Object.class);
        set.setTo(variable);
        return add(set);
    }

    public BB set(String variable, Variable from)
    {
        Validate.notNull(from,What.REFERENCE);
        return set(variable, new Reference(from.value()));
    }

    public BB nil(String variable)
    {
        Validate.notNull(variable,What.VARIABLE_NAME);
        return add(BAL().newSet(variable,VARIABLE,null));
    }

    public BB nil(Property property)
    {
        Validate.notNull(property,What.PROPERTY_NAME);
        return add(BAL().newSet(property.value(),PROPERTY,null));
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for SequenceAction: block(), block("init",TRYEACH), end()
//  ---------------------------------------------------------------------------------------

    protected final void validateNotBuildingInner()
    {
        Validate.stateIsNull(myInner,What.INNERBLOCK);
    }

    protected final void validateIsInner()
    {
        Validate.stateNotNull(myOuter,What.OUTERBLOCK);
    }

    protected final Finisher getFinisher()
    {
        return myFinisher;
    }

    protected void open(BB outer)
    {
        myOuter = outer;
    }

    protected BB close(BB trigger)
    {
        Validate.stateIsTrue(trigger!=null && myOuter==trigger,"leaving builder is my outerblock");
        Validate.stateNotNull(myDefinition,What.ACTION);
        validateNotBuildingInner();
        BALBuilder<?> next = getFinisher().finish(myOuter,myDefinition);//Can trigger outer.close
        uninitThis();
        Validate.responseNotNull(next,What.BUILDER);
        return (BB)next;
    }

    protected final BB enterInner(BB inner)
    {
        inner.open((BB)this);
        getFinishers().push(inner.getFinisher());
        myInner = inner;
        return myInner;
    }

    protected final BB leaveInner(BB trigger, String id)
    {
        Validate.stateIsTrue(trigger!=null && myInner==trigger,"leaving builder is my innerblock");
        Validate.stateIsTrue(id==null || Strings.equal(id,myInner.getId()),"innerblock.id is "+id);
        trigger = myInner;//Shutup Findbugs
        myInner = null;//Do here in case of loop back to my 'close' from inner.close
        getFinishers().popIfTop(trigger.getFinisher());
        return trigger.close((BB)this);
    }

    protected static void configureBlock(SequenceAction body, Flag flag)
    {
        if (DECLARABLES==flag || NO_DECLARABLES==flag) {
            body.setCheckDeclarables(flag.value());
        } else if (TRYEACH==flag) {
            body.setTryEach(TRYEACH.on());
        } else if (PROTECTED==flag) {
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
        validateNotBuildingInner();
        return enterInner(newChildBuilder(new BALFinishers.ForBlock()));
    }

    public BB block(String id)
    {
        validateNotBuildingInner();
        BB bb = enterInner(newChildBuilder(BAL(),BAL().newSequence(id),new BALFinishers.ForBlock()));
        bb.setId(id);
        return bb;
    }

    public BB block(String id, String cursorformat)
    {
        Validate.notBlank(cursorformat,"cursorformat");
        BB bb = block(id);
        SequenceAction.class.cast(bb.myDefinition).setCursorFormat(cursorformat);
        return bb;
    }

    public BB block(Flag...flags)
    {
        validateNotBuildingInner();
        BALFactory balFactory = newBALFactory(BAL(),flags);
        SequenceAction body = newSequence(null,balFactory,flags);
        return enterInner(newChildBuilder(balFactory, body, new BALFinishers.ForBlock()));
    }

    protected BB autoblock(Finisher wrapUp)
    {
        validateNotBuildingInner();
        return enterInner(newChildBuilder(wrapUp));
    }

    public final BB end()
    {
        validateIsInner();
        return myOuter.leaveInner((BB)this,null);
    }

    public final BB end(String id)
    {
        validateIsInner();
        return myOuter.leaveInner((BB)this,id);
        
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for If[Else]Action: iff(YES), iff("order.category is NEW")
//  ---------------------------------------------------------------------------------------

    public final BB iff(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        IfAction branch = BAL().newIf();
        branch.setTest(test);
        return autoblock(new BALFinishers.ForIf(branch));
    }

    public final BB endif()
    {
        return end();
    }

    public final BB ife(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        IfElseAction branch = BAL().newIfElse();
        branch.setTest(test);
        return autoblock(new BALFinishers.ForIf(branch));
    }

    public final BB otherwise()
    {
        IfElseAction branch = getFinishers().getUnderConstruction(IfElseAction.class);
        Validate.stateNotNull(branch,"inner-ifelse");
        return autoblock(new BALFinishers.ForIfOtherwise(branch));
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for RewindAction: rewind("getOrder@10"), rewind("getOrder@1",2,HALTIFMAX)
//  ---------------------------------------------------------------------------------------

    public final BB rewind(String mark, int limit, Flag flag)
    {
        Validate.notBlank(mark,What.CURSOR_NAME);
        RewindAction rewind = BAL().newRewind();
        rewind.setRewindpointMatcher(new ByNameRewindpointMatcher(mark));
        if (limit>0 && limit!=Integer.MAX_VALUE) {
            rewind.setMaxIterations(limit);
            rewind.setCallCounter(mark+".__callnum");
        }
        if (flag==HALTIFMAX || flag==NO_HALTIFMAX) {
            rewind.setHaltIfMax(flag.value());
        }
        return add(rewind);
    }

    public final BB rewind(String mark)
    {
        return rewind(mark,-1,null);
    }

//  ---------------------------------------------------------------------------------------
//  Fluent API for ForEachAction: foreach("i",in(0,10)), foreach("file",fileset("/tmp/*.in"))
//  ---------------------------------------------------------------------------------------

    public final BB foreach(String cursor, Collection<?> dataset)
    {
        Validate.notBlank(cursor,What.CURSOR_NAME);
        Validate.notNull(dataset,What.DATA);
        ForEachAction foreach = BAL().newForEach();
        foreach.setCursorKey(cursor);
        foreach.setDataset(dataset);
        return autoblock(new BALFinishers.ForEach(foreach));
    }

    public final BB foreach(String cursor, Callable<? extends Collection<?>> incoll)
    {
        Validate.notBlank(cursor,What.CURSOR_NAME);
        Validate.notNull(incoll,What.DATA);
        ForEachAction foreach = BAL().newForEach();
        foreach.setCursorKey(cursor);
        foreach.setDataset(incoll);//cast to shutup IDE
        return autoblock(new BALFinishers.ForEach(foreach));
    }


//  ---------------------------------------------------------------------------------------
//  Constructor methods: available only via concrete subclass's APIs
//  ---------------------------------------------------------------------------------------

    public final static BALFactory getDefaultFactory()
    {
        return BALFactory.Standard;
    }

    private static BALFactory newBALFactory(BALFactory fallbackFactory, Flag... flags)
    {
        BALFactory factory = fallbackFactory;
        for (Flag flag:flags) {
            if (DECLARABLES==flag || NO_DECLARABLES==flag) {
                factory = fallbackFactory.newFactory();
                factory.setCheckDeclarables(flag.value());
                break;
            }
        }
        return factory;
    }

    protected final BALFactory BAL()
    {
        return myFactory;
    }

    protected final Finishers getFinishers()
    {
        return myFinisherStack;
    }

    private void init(BALFactory newHelper, Sequence newBody, Finishers finisherStack, Finisher finisher)
    {
        myFactory = newHelper;
        myDefinition = newBody;
        myFinisherStack = finisherStack;
        myFinisher = finisher;
        if (finisherStack.isEmpty() && (finisher instanceof RootFinisher)) {
            finisherStack.push(finisher);
        }
    }

    protected BALBuilder(BALFactory newHelper, Sequence newBody, Finishers finisherStack, Finisher finisher)//3
    {
        Validate.noneNull(newHelper,What.FACTORY,newBody,What.BODY,finisherStack,"finishers",finisher,What.FINISHER);
        init(newHelper,newBody,finisherStack,finisher);
    }

    protected BALBuilder(BALFactory newHelper, Finishers finisherStack, Finisher finisher)//1
    {
        Validate.noneNull(newHelper,What.FACTORY,finisherStack,"finishers",finisher,What.FINISHER);
        init(newHelper,newHelper.newSequence(),finisherStack,finisher);
    }

    protected BALBuilder(String id, BALFactory newHelper, Finishers finisherStack, Finisher finisher)//2
    {
        super(id);
        Validate.noneNull(newHelper,What.FACTORY,finisherStack,"finishers",finisher,What.FINISHER);
        init(newHelper,newHelper.newSequence(id),finisherStack,finisher);
    }

    protected BALBuilder()
    {
        this(getDefaultFactory(),new Finishers(),new BALFinishers.ForRoot());//1
    }

    protected BALBuilder(String id)
    {
        this(id,getDefaultFactory(),new Finishers(),new BALFinishers.ForRoot());//2
    }

    protected BALBuilder(String id, Flag...flags)
    {
        super(id);
        BALFactory factory = newBALFactory(getDefaultFactory(),flags);
        init(factory,newSequence(id,factory,flags),new Finishers(),new BALFinishers.ForRoot());
    }

    protected abstract BB newChildBuilder(BALFactory childHelper, Sequence childBody, Finisher childFinisher);

    protected final BB newChildBuilder(Finisher childFinisher)
    {
        return newChildBuilder(BAL(),BAL().newSequence(),childFinisher);
    }

    private void uninitThis()
    {
        myDefinition = null;
        myOuter = null;
        myInner = null;
        myFinisher = null;
        myFinisherStack = null;
        myFactory = null;
    }

    private Sequence myDefinition;
    private BB myOuter;
    private BB myInner;
    private Finishers myFinisherStack;//NB: created by root builder ONLY; otherwise passed via ctor!
    private Finisher myFinisher;//my finisher for myDefinition
    private BALFactory myFactory;
}


/* end-of-BALBuilder.java */
