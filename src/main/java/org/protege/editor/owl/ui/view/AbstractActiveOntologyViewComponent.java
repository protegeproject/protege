package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public abstract class AbstractActiveOntologyViewComponent extends AbstractOWLViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 4137940424821355598L;

    private OWLModelManagerListener owlModelManagerListener;

    private OWLOntologyChangeListener owlOntologyChangeListener;

    private HierarchyListener hierarchyListener;

    private boolean updateView;


    final protected void initialiseOWLView() throws Exception {
        owlModelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                    updateView = true;
                    markForUpdate();
                }
            }
        };
        getOWLModelManager().addListener(owlModelManagerListener);

        owlOntologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                for (OWLOntologyChange chg : changes) {
                    if (chg.getOntology().equals(getOWLModelManager().getActiveOntology())) {
                        updateView = true;
                        markForUpdate();
                        break;
                    }
                }
            }
        };
        getOWLModelManager().addOntologyChangeListener(owlOntologyChangeListener);

        hierarchyListener = new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                markForUpdate();
            }
        };

        addHierarchyListener(hierarchyListener);
        initialiseOntologyView();
    }


    protected abstract void initialiseOntologyView() throws Exception;


    final protected void disposeOWLView() {
        getOWLModelManager().removeListener(owlModelManagerListener);
        getOWLModelManager().removeOntologyChangeListener(owlOntologyChangeListener);
        disposeOntologyView();
    }


    protected abstract void disposeOntologyView();


    private void markForUpdate() {
        if (isShowing() && updateView) {
            try {
                if (isSynchronizing()) {
                    updateView(getOWLModelManager().getActiveOntology());
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            updateView = false;
        }
        else {
            updateView = true;
        }
    }


    protected abstract void updateView(OWLOntology activeOntology) throws Exception;
}
