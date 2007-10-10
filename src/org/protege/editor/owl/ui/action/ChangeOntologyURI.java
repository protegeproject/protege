package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLOntologyURIChanger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 07-Mar-2007<br><br>
 */
public class ChangeOntologyURI extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        try {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            String s = JOptionPane.showInputDialog("New ontology URI", ont.getURI());

            if (s == null) {
                return;
            }
            URI uri = new URI(s);
            OWLOntologyURIChanger changer = new OWLOntologyURIChanger(getOWLModelManager().getOWLOntologyManager());
            getOWLModelManager().applyChanges(changer.getChanges(ont, uri));
        }
        catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(getWorkspace(), ex.getMessage(), "Invalid URI", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
