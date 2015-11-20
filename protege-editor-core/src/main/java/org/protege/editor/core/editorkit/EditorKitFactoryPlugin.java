package org.protege.editor.core.editorkit;

import org.eclipse.core.runtime.IExtension;
import org.osgi.framework.Bundle;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.PluginUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * <p/>
 * An <code>EditorKitFactoryPlugin</code> encapsulates
 * details of an <code>EditorKitFactory</code> plugin.
 */
public class EditorKitFactoryPlugin {

    private final Logger logger = LoggerFactory.getLogger(EditorKitFactoryPlugin.class);


    private IExtension extension;

    public static final String LABEL_PARAM = "label";


    public EditorKitFactoryPlugin(IExtension extension) {
        this.extension = extension;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    /**
     * Gets the <code>EditorKitFactory</code> label.  This is
     * typically used for UI menu items etc.
     */
    public String getLabel() {
        String param = PluginUtilities.getAttribute(extension, LABEL_PARAM);
        if (param == null) {
            return "<Error: Label not defined!> " + extension;
        }
        return param;
    }


    public EditorKitFactory newInstance() {
        try {
            Bundle b = PluginUtilities.getInstance().getBundle(extension);
            b.start();
            ExtensionInstantiator<EditorKitFactory> instantiator = new ExtensionInstantiator<EditorKitFactory>(extension);
            return instantiator.instantiate();
        }
        catch (Exception e) {
            logger.error("An error occurred during editor kit instantiation.", e);
        } 
        return null;
    }
}
