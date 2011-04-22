/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.reveal.Identified;
import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.bal.*;

/**
 * Collection of BAL-specific {@linkplain Finisher finishers} for use with
 * {@linkplain BALBuilder}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,extras,helper
 **/

public final class BALFinishers
{
    public static final Action unwrap(Action body) 
    {
        if (body instanceof SequenceAction) {
            SequenceAction seq = (SequenceAction)body;
            if (seq.canFlatten())
                body = seq.lastAdded();
        }
        return body;
    }

    public abstract static class Skeleton implements Finisher, Identified
    {
        protected Skeleton() {
        }
    }

    public static final class ForRoot implements Finisher, RootFinisher
    {
        public ForRoot() {
            super();
        }
        public String getId() {
            return "";
        }
        public BALBuilder<?> finish(BALBuilder<?> nulll, Sequence main) {
            return nulll;
        }
        public <T> T getUnderConstruction(Class<T> ofType) {
            return null;
        }
    }

    public static final class ForBlock extends Skeleton
    {
        public ForBlock() {
            super();
        }
        public String getId() {
            return "block";
        }
        public BALBuilder<?> finish(BALBuilder<?> outer, Sequence collected) {
            return outer.add(collected);
        }
        public <T> T getUnderConstruction(Class<T> ofType) {
            return null;
        }
    }

    public static final class ForIf extends Skeleton
    {
        private final IfAction myBranch;
        public ForIf(IfAction step) {
            myBranch = step;
        }
        public String getId() {
            return "if";
        }
        public BALBuilder<?> finish(BALBuilder<?> outer, Sequence collected) {
            myBranch.setThen(unwrap(collected));
            return outer.add(myBranch);
        }
        public <T> T getUnderConstruction(Class<T> ofType) {
            return ofType.cast(myBranch);
        }
    }

    public static final class ForIfOtherwise extends Skeleton
    {
        private final IfElseAction myBranch;
        public ForIfOtherwise(IfElseAction step) {
            myBranch = step;
        }
        public String getId() {
            return "else";
        }
        public BALBuilder<?> finish(BALBuilder<?> forIf, Sequence collected) {
            myBranch.setElse(unwrap(collected));
            return forIf.end();
        }
        public <T> T getUnderConstruction(Class<T> ofType) {
            return ofType.cast(myBranch);
        }
    }
}


/* end-of-BALFinishers.java */
