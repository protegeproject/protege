package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.IndividualsByInferredTypeHierarchyProvider;
import org.protege.editor.owl.ui.tree.CountingOWLObjectTreeCellRenderer;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class OWLIndividualsByInferredTypeViewComponent extends AbstractOWLIndividualViewComponent {

    private IndividualsByInferredTypeHierarchyProvider provider;

    private OWLObjectTree<OWLObject> tree;

    private TreeSelectionListener listener;

    private Set<EventType> updateEvents = new HashSet<>();

    private OWLModelManagerListener managerListener = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (updateEvents.contains(event.getType())){
                provider.setReasoner(getOWLModelManager().getReasoner()); // forces refresh
            }
        }
    };


    private void transmitSelection() {
        if (isSynchronizing()){
            OWLObject obj = tree.getSelectedOWLObject();
            if (obj instanceof OWLEntity) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) obj);
            }
        }
    }


    public void initialiseIndividualsView() throws Exception {
        setLayout(new BorderLayout());

        updateEvents.add(EventType.ONTOLOGY_CLASSIFIED);
        updateEvents.add(EventType.REASONER_CHANGED);

        getOWLModelManager().addListener(managerListener);

        provider = new IndividualsByInferredTypeHierarchyProvider(getOWLModelManager().getOWLOntologyManager());
        tree = new OWLModelManagerTree<>(getOWLEditorKit(), provider);
        tree.setCellRenderer(new CountingOWLObjectTreeCellRenderer<>(getOWLEditorKit(), tree));

        add(new JScrollPane(tree));

        provider.setReasoner(getOWLModelManager().getReasoner());
        listener = e -> transmitSelection();
        tree.addTreeSelectionListener(listener);
    }


    public OWLNamedIndividual updateView(OWLNamedIndividual selelectedIndividual) {
        OWLObject selObj = tree.getSelectedOWLObject();
        if (selelectedIndividual != null && selObj != null) {
            if (selelectedIndividual.equals(selObj)) {
                return selelectedIndividual;
            }
        }
        tree.setSelectedOWLObject(selelectedIndividual);
        return selelectedIndividual;
    }


    public void disposeView() {
        tree.dispose();
        getOWLModelManager().removeListener(managerListener);
    }
}