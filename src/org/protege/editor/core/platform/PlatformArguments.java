package org.protege.editor.core.platform;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/*
 * There currently does not seem to be any generic method for obtaining the command
 * line arguments in OSGi.  The following has code that checks for specific OSGi implementations
 * and uses their mechanism.
 * 
 * Felix needed to have its main routine wrapped.  Perhaps this is what we will do for all OSGi implementations.
 */
public class PlatformArguments {
    private static Logger log = Logger.getLogger(PlatformArguments.class);
    public static final String ARG_PROPERTY = "command.line.arg.";
    
    public static String[] getArguments(BundleContext context) {
        String[] args;
        if ((args = getWrappedArguments()) != null) {
            return args;
        }
        if ((args = getEquinoxArguments(context)) != null) {
            return args;
        }
        return new String[0];
    }
    
    public static String[] getWrappedArguments() {
        int count = 0;
        boolean argFound = false;
        do {
            argFound = false;
            Object arg = System.getProperty(ARG_PROPERTY + count);
            if (arg != null && arg instanceof String) {
                argFound = true;
                count++;
            }
        }
        while (argFound);
        if (count == 0) {
            return null;
        }
        String[] args = new String[count];
        for (int i = 0; i < count; i++) {
            args[i] = (String) System.getProperty(ARG_PROPERTY + i);
        }
        return args;
    }

    public static String[] getEquinoxArguments(BundleContext context) {
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
