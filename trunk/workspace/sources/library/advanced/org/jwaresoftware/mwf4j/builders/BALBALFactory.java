/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  java.util.concurrent.atomic.AtomicReference;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.bal.*;
import  org.jwaresoftware.mwf4j.behaviors.DeclarableEnabled;

/**
 * Standard implementation of {@linkplain BALFactory} that creates the default
 * implementation of all BAL components.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    impl,extras,helper
 **/

public class BALBALFactory implements BALFactory
{
    protected BALBALFactory() {
        this(false);
    }

    BALBALFactory(boolean locked) {
        myLockFlag=locked;
    }

    protected BALBALFactory newBALBALFactory() {
        return new BALBALFactory();//NOT locked ever!
    }
    
    public BALFactory newFactory() {
        BALBALFactory derived = newBALBALFactory();
        if (getCheckDeclarables()!=null) {
            derived.myDeclarablesFlag.set(isCheckDeclarables());
        }
        return derived;
    }

    public final void setCheckDeclarables(boolean flag) {
        if (!myLockFlag) myDeclarablesFlag.set(flag);
    }

    public boolean isCheckDeclarables() {
        Boolean forcedFlag = getCheckDeclarables();
        return (forcedFlag!=null) ? forcedFlag : DEFAULT_CHECK_DECLARABLES_SETTING;
    }

    public final Boolean getCheckDeclarables() {
        return myDeclarablesFlag.get();
    }

    protected final <T extends DeclarableEnabled> T finish(T action) {
        Boolean forcedFlag = getCheckDeclarables();
        if (forcedFlag!=null) {
            action.setCheckDeclarables(forcedFlag);
        }
        return action;
    }

    public Action newEmpty() {
        return finish(new EmptyAction());
    }

    public SequenceAction newSequence(String id) {
        SequenceAction out = id==null ? new SequenceAction() : new SequenceAction(id);
        return finish(out);
    }

    public SequenceAction newSequence() {
        return newSequence(null);
    }

    public IfAction newIf() {
        return finish(new IfAction());
    }

    public IfElseAction newIfElse() {
        return finish(new IfElseAction());
    }

    public <T> AssignAction<T> newSet() {
        return finish(new AssignAction<T>());
    }

    public <T> AssignAction<T> newSet(String toKey, StoreType toStoreType, T dataValue) {
        return finish(new AssignAction<T>(toKey, toStoreType, dataValue));
    }

    private final boolean myLockFlag;
    private AtomicReference<Boolean> myDeclarablesFlag= new AtomicReference<Boolean>();
}


/* end-of-BALBALFactory.java */
