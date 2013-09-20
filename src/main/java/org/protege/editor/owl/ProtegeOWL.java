package org.protege.editor.owl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



/**
 * This class is minimal for the moment.  We can refactor or maybe more stuff (an
 * Activator?) will come here.
 * @author tredmond
 *
 */
public class ProtegeOWL implements BundleActivator {

    public static final String ID = "org.protege.editor.owl";
    
    private static BundleContext context;

    public void start(BundleContext context) throws Exception {
        ProtegeOWL.context = context;
    }

    public void stop(BundleContext context) throws Exception {
        ProtegeOWL.context = null;
    }
    
    public static BundleContext getBundleContext() {
        return context;
    }

}
