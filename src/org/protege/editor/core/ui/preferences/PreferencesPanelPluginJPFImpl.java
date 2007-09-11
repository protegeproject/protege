package org.protege.editor.core.ui.preferences;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginUtilities;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesPanelPluginJPFImpl implements PreferencesPanelPlugin {

    public static final String ID = "preferencespanel";

    public static final String LABEL_PARAM = "label";

    private IExtension extension;

    private EditorKit editorKit;


    public PreferencesPanelPluginJPFImpl(IExtension extension, EditorKit editorKit) {
        this.extension = extension;
        this.editorKit = editorKit;
    }


    public String getLabel() {
        return PluginUtilities.getAttribute(extension, LABEL_PARAM);
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public PreferencesPanel newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                 InstantiationException {
        ExtensionInstantiator<PreferencesPanel> instantiator = new ExtensionInstantiator<PreferencesPanel>(extension);
        PreferencesPanel preferencesPanel = instantiator.instantiate();
        preferencesPanel.setup(getLabel(), editorKit);
        return preferencesPanel;
    }
}
