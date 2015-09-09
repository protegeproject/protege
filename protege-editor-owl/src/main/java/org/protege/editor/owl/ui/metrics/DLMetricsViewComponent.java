package org.protege.editor.owl.ui.metrics;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.DLExpressivityChecker;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLMetricsViewComponent extends AbstractOWLViewComponent {

    private static final Logger logger = Logger.getLogger(DLMetricsViewComponent.class);


    private boolean changed;

    private OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            handleChanges(changes);
        }
    };

    private OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ONTOLOGY_RELOADED)) {
                changed = true;
                refresh();
            }
        }
    };

    private HierarchyListener hierarchyListener = new HierarchyListener() {
        public void hierarchyChanged(HierarchyEvent e) {
            changed = true;
            refresh();
        }
    };

    private DLNamePanel namePanel;


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        DLExpressivityChecker expressivityChecker = new DLExpressivityChecker(getOWLModelManager().getActiveOntologies());
        namePanel = new DLNamePanel();
        namePanel.setBorder(ComponentFactory.createTitledBorder("DL Expressivity"));
        add(namePanel, BorderLayout.NORTH);
        DLNameKeyPanel keyPanel = new DLNameKeyPanel();
        keyPanel.setBorder(ComponentFactory.createTitledBorder("Symbol key"));
        add(keyPanel, BorderLayout.CENTER);
        setName();
        getOWLModelManager().addOntologyChangeListener(listener);
        getOWLModelManager().addListener(modelManagerListener);
        addHierarchyListener(hierarchyListener);
    }


    protected void disposeOWLView() {
        getOWLModelManager().removeOntologyChangeListener(listener);
        getOWLModelManager().removeListener(modelManagerListener);
        removeHierarchyListener(hierarchyListener);
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                if (change.getAxiom().isLogicalAxiom()) {
                    changed = true;
                    refresh();
                    break;
                }
            }
        }
    }


    private void setName() {
        DLExpressivityChecker checker = new DLExpressivityChecker(getOWLModelManager().getActiveOntologies());
        namePanel.setConstructs(checker.getConstructs());
    }


    private void refresh() {
        if (isShowing()) {
            if (changed) {
                setName();
                changed = false;
            }
        }
        else {
            changed = true;
        }
    }
}
