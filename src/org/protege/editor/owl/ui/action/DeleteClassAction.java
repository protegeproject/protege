package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DeleteClassAction extends AbstractDeleteEntityAction<OWLClass> {

    public DeleteClassAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLClass> clsSetProvider) {
        super("Delete selected classes",
              OWLIcons.getIcon("class.delete.png"),
              owlEditorKit,
              owlEditorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider(),
              clsSetProvider);
    }


    protected String getPluralDescription() {
        return "classes";
    }


    protected String getResultsViewId() {
        return "OWLClassUsageView";
    }
}
