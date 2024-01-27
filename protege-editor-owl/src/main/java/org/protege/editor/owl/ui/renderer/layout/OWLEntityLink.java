package org.protege.editor.owl.ui.renderer.layout;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/11/2011
 */
public class OWLEntityLink extends Link {

    private OWLEditorKit editorKit;

    private OWLEntity entity;

    public OWLEntityLink(OWLEditorKit editorKit, OWLEntity entity) {
        super(editorKit.getOWLModelManager().getOWLEntityRenderer().getShortForm(entity));
        this.editorKit = editorKit;
        this.entity = entity;

    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }

    @Override
    public void activate(Component component, MouseEvent event) {
        editorKit.getWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
        editorKit.getWorkspace().displayOWLEntity(entity);
    }
}
