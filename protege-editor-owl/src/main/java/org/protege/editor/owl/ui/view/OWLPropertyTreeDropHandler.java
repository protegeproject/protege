package org.protege.editor.owl.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLPropertyTreeDropHandler<N extends OWLPropertyExpression> implements OWLTreeDragAndDropHandler<N> {

    private OWLModelManager owlModelManager;


    public OWLPropertyTreeDropHandler(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public void move(N child, N fromParent, N toParent) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        changes.add(new AddAxiom(owlModelManager.getActiveOntology(), getAxiom(df, child, toParent)));

        if (fromParent != null) {
            changes.add(new RemoveAxiom(owlModelManager.getActiveOntology(), getAxiom(df, child, fromParent)));
        }
        owlModelManager.applyChanges(changes);
    }


    public void add(N child, N parent) {
        OWLDataFactory df = owlModelManager.getOWLDataFactory();
        owlModelManager.applyChange(new AddAxiom(owlModelManager.getActiveOntology(), getAxiom(df, child, parent)));
    }


    protected abstract OWLAxiom getAxiom(OWLDataFactory df, N child, N parent);
}
