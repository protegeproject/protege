package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.DeleteEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLDataPropertyIcon;
import org.protege.editor.owl.ui.renderer.OWLEntityIcon;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteDataPropertyAction extends AbstractDeleteEntityAction<OWLDataProperty> {


    public DeleteDataPropertyAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLDataProperty> propSetProvider) {
        super("Delete selected properties",
              new DeleteEntityIcon(new OWLDataPropertyIcon(OWLEntityIcon.FillType.HOLLOW)),
              owlEditorKit,
              owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider(),
              propSetProvider);
    }


    protected String getPluralDescription() {
        return "properties";
    }
}

