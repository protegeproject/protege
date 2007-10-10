package org.protege.editor.owl.model.selection;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


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
        OWLEntity entity = owlEditorKit.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (entity != null) {
            entity.accept(this);
        }
    }


    public void visit(OWLClass cls) {
    }


    public void visit(OWLObjectProperty property) {
    }


    public void visit(OWLDataProperty property) {
    }


    public void visit(OWLIndividual individual) {
    }


    public void visit(OWLDataType dataType) {
    }
}
