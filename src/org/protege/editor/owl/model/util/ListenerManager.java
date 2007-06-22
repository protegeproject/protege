package org.protege.editor.owl.model.util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
