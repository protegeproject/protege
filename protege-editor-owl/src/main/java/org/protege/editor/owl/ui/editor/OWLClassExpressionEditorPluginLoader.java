package org.protege.editor.owl.ui.editor;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ProtegeOWL;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Feb 26, 2009<br><br>
 */
public class OWLClassExpressionEditorPluginLoader extends AbstractPluginLoader<OWLClassExpressionEditorPlugin> {

    private OWLEditorKit editorKit;

    public OWLClassExpressionEditorPluginLoader(OWLEditorKit editorKit) {
        super(ProtegeOWL.ID, OWLClassExpressionEditorPlugin.ID);
        this.editorKit = editorKit;
    }


    protected OWLClassExpressionEditorPlugin createInstance(IExtension extension) {
        return new OWLClassExpressionEditorPluginImpl(editorKit, extension);
    }
}
