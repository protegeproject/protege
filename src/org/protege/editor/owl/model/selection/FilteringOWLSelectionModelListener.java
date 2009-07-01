package org.protege.editor.owl.model.selection;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 08-Feb-2007<br><br>
 */
public class FilteringOWLSelectionModelListener implements OWLSelectionModelListener, OWLEntityVisitor {

    private OWLEditorKit owlEditorKit;


    public FilteringOWLSelectionModelListener(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    public void selectionChanged() {
        OWLObject owlObject = owlEditorKit.getWorkspace().getOWLSelectionModel().getSelectedObject();
        if (owlObject != null && owlObject instanceof OWLEntity) {
            ((OWLEntity)owlObject).accept(this);
        }
    }


    public void visit(OWLClass cls) {
    }


    public void visit(OWLObjectProperty property) {
    }


    public void visit(OWLDataProperty property) {
    }


    public void visit(OWLAnnotationProperty property) {
    }


    public void visit(OWLNamedIndividual individual) {
    }


    public void visit(OWLDatatype dataType) {
    }
}
