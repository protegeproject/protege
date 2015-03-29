package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.ui.ontology.OntologyIDJDialog;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.SetOntologyID;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 07-Mar-2007<br><br>
 */
public class ChangeOntologyIRI extends ProtegeOWLAction {
	private static final long serialVersionUID = -6080240335045735182L;


	public void actionPerformed(ActionEvent e) {
		OWLOntology ont = getOWLModelManager().getActiveOntology();
		OWLOntologyID id = OntologyIDJDialog.showDialog(getOWLEditorKit(), ont.getOntologyID());
		if (id != null) {
			getOWLModelManager().applyChanges(getChanges(ont, id));
		}
	}
	
    private List<OWLOntologyChange> getChanges(OWLOntology ontology, OWLOntologyID id) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = owlOntologyManager.getOWLDataFactory();
        OWLOntologyID oldId = ontology.getOntologyID();
        changes.add(new SetOntologyID(ontology, id));
        if (!id.isAnonymous() && !id.equals(oldId)) {
        	for (OWLOntology ont : owlOntologyManager.getOntologies()) {
        		for (OWLImportsDeclaration decl : ont.getImportsDeclarations()) {
        			if (decl.getIRI().equals(oldId.getVersionIRI())) {
        				changes.add(new RemoveImport(ont, decl));
        				changes.add(new AddImport(ont, factory.getOWLImportsDeclaration(id.getDefaultDocumentIRI())));
        			}
        			else if (decl.getIRI().equals(oldId.getOntologyIRI())) {
        				changes.add(new RemoveImport(ont, decl));
        				changes.add(new AddImport(ont, factory.getOWLImportsDeclaration(id.getOntologyIRI())));
        			}
        		}
        	}
        }
        return changes;
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
