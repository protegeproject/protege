package org.protege.editor.owl.ui.action;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLOntologyURIChanger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 07-Mar-2007<br><br>
 */
public class ChangeOntologyIRI extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        try {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            String s = JOptionPane.showInputDialog("New ontology IRI", ont.getOntologyID().getOntologyIRI());

            if (s == null) {
                return;
            }
            IRI iri = IRI.create(new URI(s));
            OWLOntologyURIChanger changer = new OWLOntologyURIChanger(getOWLModelManager().getOWLOntologyManager());
            getOWLModelManager().applyChanges(changer.getChanges(ont, iri));
        }
        catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(getWorkspace(), ex.getMessage(), "Invalid IRI", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
