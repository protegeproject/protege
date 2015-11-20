package org.protege.editor.core.platform;

import org.osgi.framework.BundleContext;

/*
 * There currently does not seem to be any generic method for obtaining the command
 * line arguments in OSGi.  The following has code that checks for specific OSGi implementations
 * and uses their mechanism.
 * 
 * Felix needed to have its main routine wrapped.  Perhaps this is what we will do for all OSGi implementations.
 */
public class PlatformArguments {

    public static final String ARG_PROPERTY = "command.line.arg.";
    
    public static String[] getArguments(BundleContext context) {
        String[] args;
        if ((args = getWrappedArguments()) != null) {
            return args;
        }
        return new String[0];
    }
    
    public static String[] getWrappedArguments() {
        int count = 0;
        boolean argFound = false;
        do {
            argFound = false;
            String arg = System.getProperty(ARG_PROPERTY + count);
            if (arg != null) {
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

}
