package org.protege.editor.owl.model.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


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


    public void dumpWarningForListener(L listener, Logger log, Level level, String cleanupMessage) {
        if (listenerMap.containsKey(listener)) {
            log.log(level, "*** WARNING BADLY BEHAVING LISTENER: " + listener.getClass().getName() + " ***");
            StackTraceElement [] trace = listenerMap.get(listener);
            if (trace != null) {
                log.log(level, "    Possible culprit (trace from when listener was added): ");
                for (int i = 3; i < trace.length; i++) {
                    String s = trace[i].toString();
                    if (i == 0) {
                        log.log(level, "        ->" + s);
                    }
                    else {
                        log.log(level, "          " + s);
                    }
                    if (i == 5) {
                        break;
                    }
                }
            }
            log.log(level, "    " + cleanupMessage);
        }
    }


    public void dumpWarningForAllListeners(Logger log, Level level, String cleanupMessage) {
        for (L listener : listenerMap.keySet()) {
            dumpWarningForListener(listener, log, level, cleanupMessage);
        }
    }
}
