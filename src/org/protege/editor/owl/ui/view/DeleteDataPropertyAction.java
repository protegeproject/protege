package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteDataPropertyAction extends AbstractDeleteEntityAction<OWLDataProperty> {


    public DeleteDataPropertyAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLDataProperty> propSetProvider) {
        super("Delete selected properties",
              OWLIcons.getIcon("property.Data.delete.png"),
              owlEditorKit,
              owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider(),
              propSetProvider);
    }


    protected String getPluralDescription() {
        return "properties";
    }
}

