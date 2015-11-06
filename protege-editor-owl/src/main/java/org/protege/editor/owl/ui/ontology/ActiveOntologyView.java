package org.protege.editor.owl.ui.ontology;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


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

    private final Logger logger = LoggerFactory.getLogger(ActiveOntologyView.class);


    private JComboBox ontologiesList;

    private OWLModelManagerListener owlModelManagerListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                updateList();
            }
        }
    };


    private final OWLOntologyChangeListener ontologyChangedListener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            handleOntologyChanges(owlOntologyChanges);
        }
    };

    private void updateList() {
        ontologiesList.setSelectedItem(getOWLModelManager().getActiveOntology());
        ontologiesList.setRenderer(ontologiesList.getRenderer());
    }




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
        getOWLModelManager().addOntologyChangeListener(ontologyChangedListener);
        rebuildList();
    }

    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for(OWLOntologyChange change : changes) {
            System.out.println("UPDATE");
            if(change instanceof SetOntologyID) {

                updateList();
                break;
            }
        }
    }

    private void rebuildList() {
        try {
            ontologiesList.setModel(new DefaultComboBoxModel(getOWLModelManager().getOntologies().toArray()));
            updateList();
        }
        catch (Exception e) {
            logger.error("An error occurred when rebuilding the list of active ontologies.", e);
        }
    }
}
