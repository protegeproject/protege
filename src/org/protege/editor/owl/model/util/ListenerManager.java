package org.protege.editor.owl.model.util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 05-Mar-2007<br><br>
 */
public class ListenerManager<L extends Object> {

    private Map<L, StackTraceElement []> listenerMap;


    public ListenerManager() {
        listenerMap = new HashMap<L, StackTraceElement []>();
    }


    public void recordListenerAdded(L listener) {
        StackTraceElement []  stackTrace = Thread.currentThread().getStackTrace();
        listenerMap.put(listener, stackTrace);
    }


    public void recordListenerRemoved(L listener) {
        listenerMap.remove(listener);
    }


    public void dumpWarningForListener(L listener, PrintStream ps, String cleanupMessage) {
        if (listenerMap.containsKey(listener)) {
            ps.println("*** WARNING BADLY BEHAVING LISTENER: " + listener.getClass().getName() + " ***");
            StackTraceElement [] trace = listenerMap.get(listener);
            if (trace != null) {
                ps.println("    Possible culprit (trace from when listener was added): ");
                for (int i = 3; i < trace.length; i++) {
                    String s = trace[i].toString();
                    if (i == 0) {
                        ps.println("        ->" + s);
                    }
                    else {
                        ps.println("          " + s);
                    }
                    if (i == 5) {
                        break;
                    }
                }
            }
            ps.println("    " + cleanupMessage);
        }
    }


    public void dumpWarningForAllListeners(PrintStream ps, String cleanupMessage) {
        for (L listener : listenerMap.keySet()) {
            dumpWarningForListener(listener, ps, cleanupMessage);
        }
    }
}
