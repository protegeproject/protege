package org.protege.editor.core.plugin;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.osgi.framework.Bundle;
import org.protege.editor.core.ProtegeApplication;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginUtilities {

    private static PluginUtilities instance;

    private ProtegeApplication protegeApplication;


    private PluginUtilities() {

    }


    /**
     * Gets the one and only instance of <code>PluginUtilities</code>.
     */
    public static synchronized PluginUtilities getInstance() {
        if (instance == null) {
            instance = new PluginUtilities();
        }
        return instance;
    }


    /**
     * This method is called by the system to initialise the
     * plugin utilities.  Users should <b>not</b> call this method.
     */
    public void initialise(ProtegeApplication protegeApplication) {
        this.protegeApplication = protegeApplication;
    }
    
    public static Bundle getBundle(IExtension extension) {
        IContributor contributor = extension.getContributor();
        String name = contributor.getName();
        return OSGIUtils.getDefault().getBundle(name);
    }
    
    public static Map<String, String> getAttributes(IExtension ext) {
        Map<String, String> attributes = new HashMap<String, String>();
        for (IConfigurationElement config : ext.getConfigurationElements()) {
            String id = config.getAttribute(PluginProperties.PLUGIN_XML_ID);
            String value = config.getAttribute(PluginProperties.PLUGIN_XML_VALUE);
            attributes.put(id, value);
        }
        return attributes;
    }
    
    public static String getAttribute(IExtension ext, String key) {
        return getAttributes(ext).get(key);
    }
    
    public static Object getExtensionObject(IExtension ext, String property) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Bundle b = getBundle(ext);
        return b.loadClass(getAttribute(ext, property)).newInstance();
        // return config.createExecutableExtension(property);
    }
    
}

