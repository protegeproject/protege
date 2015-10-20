package org.protege.editor.owl.ui.view;

import org.slf4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.semanticweb.owlapi.model.*;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLPropertyTreeDropHandler<N extends OWLPropertyExpression> implements OWLTreeDragAndDropHandler<N> {

    private OWLModelManager owlModelManager;


    public OWLPropertyTreeDropHandler(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public void move(N child, N fromParent, N toParent) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
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
