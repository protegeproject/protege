package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.DeleteEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLObjectPropertyIcon;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteObjectPropertyAction extends AbstractDeleteEntityAction<OWLObjectProperty> {


    public DeleteObjectPropertyAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLObjectProperty> propSetProvider) {
        super("Delete selected properties",
              new DeleteEntityIcon(new OWLObjectPropertyIcon()),
              owlEditorKit,
              owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider(),
              propSetProvider);
    }


    protected String getPluralDescription() {
        return "properties";
    }
}
