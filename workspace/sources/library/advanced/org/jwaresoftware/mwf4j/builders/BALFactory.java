/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.bal.*;
import  org.jwaresoftware.mwf4j.behaviors.DeclarableEnabled;

/**
 * Factory of BAL-related components. You can customize the BALFactory that
 * our standard {@linkplain BALBuilder} uses or just use our default 
 * implementation {@linkplain BALFactory#Standard}. Note that the factory
 * methods signatures are in terms of concrete BAL implementation classes;
 * this guarantees certain assumptions that the factory and builder will
 * make.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    api,extras,helper
 **/

public interface BALFactory extends DeclarableEnabled
{
    BALFactory newFactory();
    SequenceAction newSequence();
    SequenceAction newSequence(String id);
    Action newEmpty();
    <T> AssignAction<T> newSet();
    <T> AssignAction<T> newSet(String toKey, StoreType toStoreType, T dataValue);
    IfAction newIf();
    IfElseAction newIfElse();
    RewindAction newRewind();
    ForEachAction newForEach();

    public final static BALFactory Standard = new BALBALFactory(true);
}


/* end-of-BALFactory.java */
