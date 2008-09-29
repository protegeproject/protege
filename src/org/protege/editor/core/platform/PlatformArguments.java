package org.protege.editor.core.platform;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/*
 * There currently does not seem to be any generic method for obtaining the command
 * line arguments in OSGi.  The following has code that checks for specific OSGi implementations
 * and uses their mechanism.
 */
public class PlatformArguments {
    private static Logger log = Logger.getLogger(PlatformArguments.class);

    public static String[] getArguments(BundleContext context) {
        try {
            ServiceReference ref = context.getServiceReference("org.eclipse.osgi.service.environment.EnvironmentInfo");
            if (ref != null) { // OSGi based on eclipse equinox
                Object o = context.getService(ref);
                if (o != null) {
                    try {
                        Method m = o.getClass().getMethod("getCommandLineArgs", new Class[0]);
                        return (String[]) m.invoke(o);
                    }
                    finally {
                        context.ungetService(ref);
                    }
                }
            }
        } catch (Throwable t) { // it is not worth throwing an exception here continue processing.
            log.warn("Error retrieving command line arguments" + t);
        }
        return null;
    }

}
