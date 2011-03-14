/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.concurrent.ExecutorService;
import  java.util.concurrent.Executors;

import  org.jwaresoftware.gestalt.ProblemHandler;
import  org.jwaresoftware.gestalt.ServiceProviderException;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.bootstrap.Fixture;
import  org.jwaresoftware.gestalt.fixture.StringResolver;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.helpers.VariablesHashMap;

/**
 * Straightforward implementation of the {@linkplain Harness harness}
 * interface and the default for MWf4J components. Actions, statements,
 * conditions and other components must use a harness to access various
 * services like the continuation and unwind queues during execution. We
 * rely on the supplied fixture service provider lookup mechanism to
 * retrieve all application-supplied overrides for our services. For
 * instance, if the application wants to supply its own variables map then
 * it should install a map (or a map provider) under the name
 * {@linkplain MWf4J.ServiceIds#VARIABLES} of a type compatible with a 
 * concurrent map. Unwindables must self-[un]register as part of their
 * execution.
 * <p/>
 * <b>Usage note 1:</b> most of the getter methods (like getVariables) are
 * expected to be <em>available immediately after construction</em>. So 
 * it's important that the harness creator pre-populate the incoming
 * fixture with all service provider overrides and configuration BEFORE
 * creating the harness. Also, any pre-installed continuations and unwinds
 * are <em>CLEARED</em> on entry to {@linkplain #run()}! Only unwinds and
 * continuations added during execution are processed.
 * <p/>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (single while constructed, guarded during run)
 * @.group    impl,helper
 **/

public class SimpleHarness extends HarnessSkeleton
{
    public SimpleHarness(Activity activity, Fixture.Implementation fixture)
    {
        super(fixture);
        Validate.notNull(activity,What.ACTIVITY);
        myOwner = activity;
        myData = findVariables();
        myExectorService = findExecutorService();
        myStringResolver = findResolverService();
    }



    public Activity getOwner()
    {
        return myOwner;
    }

    public Variables getVariables()
    {
        return myData;
    }

    public ExecutorService getExecutorService()
    {
        return myExectorService;
    }

    public String typeCN()
    {
        return "main";
    }



    public String interpolate(String inputString, ProblemHandler issueHandler)
    {
        String output = inputString;
        if (myStringResolver!=null) {
            output = myStringResolver.interpolate(inputString,issueHandler);
        }
        return output;
    }



    protected ControlFlowStatement firstStatement()
    {
        return getOwner().firstStatement(staticView());
    }



    private <T> T findService(String id, Class<T> ofType, String what) {
        T service=null;
        try {
            service = getServiceInstanceOrNull(id,ofType,getIssueHandler());
        } catch(ServiceProviderException spiX) {
            String typeName = ofType.getSimpleName();
            Diagnostics.ForCore.warn("Error looking for "+what+" as "+typeName+".class type",spiX);
        }
        return service;
    }

    private Variables findVariables()
    {
        Variables map;
        map = findService(MWf4J.ServiceIds.VARIABLES,Variables.class,"dataMap");
        if (map==null) {
            map = new VariablesHashMap(23,0.8f);
        }
        return map;
    }

    private ExecutorService findExecutorService()
    {
        ExecutorService service;
        service = findService(MWf4J.ServiceIds.EXECUTOR,ExecutorService.class,"executor");
        if (service==null) {
            service = Executors.newCachedThreadPool();
        }
        return service;
    }

    private StringResolver findResolverService()
    {
        StringResolver service;
        service = findService(MWf4J.ServiceIds.STRING_RESOLVER,StringResolver.class,"string-resolver");
        if (service==null) {
            if (getTarget() instanceof StringResolver) {
                service = StringResolver.class.cast(getTarget());
            }
        }
        return service;
    }


    private final Activity myOwner;
    private final Variables myData;
    private final ExecutorService myExectorService;
    private final StringResolver myStringResolver;
}


/* end-of-SimpleHarness.java */
