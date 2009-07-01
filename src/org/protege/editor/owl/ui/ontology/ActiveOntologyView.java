package org.protege.editor.owl.ui.ontology;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ActiveOntologyView extends AbstractOWLViewComponent {

    private static final Logger logger = Logger.getLogger(ActiveOntologyView.class);


    private JComboBox ontologiesList;

    private OWLModelManagerListener owlModelManagerListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                ontologiesList.setSelectedItem(getOWLModelManager().getActiveOntology());
            }
        }
    };


    public void disposeOWLView() {
        getOWLModelManager().removeListener(owlModelManagerListener);
    }


    public void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        ontologiesList = new JComboBox();
        ontologiesList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OWLOntology ont = (OWLOntology) ontologiesList.getSelectedItem();
                if (ont != null) {
                    getOWLModelManager().setActiveOntology(ont);
                }
            }
        });
        add(ontologiesList);
        getOWLModelManager().addListener(owlModelManagerListener);
        rebuildList();
    }


    private void rebuildList() {
        try {
            ontologiesList.setModel(new DefaultComboBoxModel(getOWLModelManager().getOntologies().toArray()));
            ontologiesList.setSelectedItem(getOWLModelManager().getActiveOntology());
        }
        catch (Exception e) {
            logger.error(e);
        }
    }
}
