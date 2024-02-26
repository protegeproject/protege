package org.protege.editor.owl.model.history;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;


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


    public void visit(SetOntologyID change) {
        reverseChange = new SetOntologyID(change.getOntology(), change.getOriginalOntologyID());
    }


    public void visit(AddImport addImport) {
        reverseChange = new RemoveImport(addImport.getOntology(), addImport.getImportDeclaration());
    }


    public void visit(RemoveImport removeImport) {
        reverseChange = new AddImport(removeImport.getOntology(), removeImport.getImportDeclaration());
    }


    public void visit(AddOntologyAnnotation addOntologyAnnotation) {
        reverseChange = new RemoveOntologyAnnotation(addOntologyAnnotation.getOntology(), addOntologyAnnotation.getAnnotation());
    }


    public void visit(RemoveOntologyAnnotation removeOntologyAnnotation) {
        reverseChange = new AddOntologyAnnotation(removeOntologyAnnotation.getOntology(), removeOntologyAnnotation.getAnnotation());
    }
}
