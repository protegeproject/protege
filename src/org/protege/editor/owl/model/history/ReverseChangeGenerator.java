package org.protege.editor.owl.model.history;

import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeVisitor;
import org.semanticweb.owl.model.RemoveAxiom;
import org.semanticweb.owl.model.SetOntologyURI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Jan-2007<br><br>
 */
public class ReverseChangeGenerator implements OWLOntologyChangeVisitor {

    private OWLOntologyChange reverseChange;


    public OWLOntologyChange getReverseChange() {
        return reverseChange;
    }


    public void visit(AddAxiom change) {
        reverseChange = new RemoveAxiom(change.getOntology(), change.getAxiom());
    }


    public void visit(RemoveAxiom change) {
        reverseChange = new AddAxiom(change.getOntology(), change.getAxiom());
    }


    public void visit(SetOntologyURI change) {
        reverseChange = new SetOntologyURI(change.getOntology(), change.getOriginalURI());
    }
}
