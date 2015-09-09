package org.protege.editor.owl.ui.ontology.wizard.move;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKit;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 */
public class MoveAxiomsKitPluginImpl implements MoveAxiomsKitPlugin {

    private IExtension extension;

    private OWLEditorKit editorKit;

    public MoveAxiomsKitPluginImpl(OWLEditorKit editorKit, IExtension extension) {
        this.extension = extension;
        this.editorKit = editorKit;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public MoveAxiomsKit newInstance() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        ExtensionInstantiator<MoveAxiomsKit> instantiator = new ExtensionInstantiator<MoveAxiomsKit>(extension);
        MoveAxiomsKit kit =  instantiator.instantiate();
        kit.setup(extension.getUniqueIdentifier(), PluginUtilities.getAttribute(extension, "name"), editorKit);
        return kit;
    }
}
