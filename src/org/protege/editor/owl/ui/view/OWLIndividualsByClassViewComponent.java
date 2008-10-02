package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.IndividualsByTypeHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
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
public class OWLIndividualsByClassViewComponent extends AbstractOWLIndividualViewComponent {

    private OWLObjectHierarchyProvider<OWLObject> provider;

    private OWLObjectTree<OWLObject> tree;

    private TreeSelectionListener listener;

    private Set<EventType> updateEvents = new HashSet<EventType>();

    private OWLModelManagerListener managerListener = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (updateEvents.contains(event.getType())){
                provider.setOntologies(getOWLModelManager().getActiveOntologies()); // forces refresh
            }
        }
    };


    private void transmitSelection() {
        OWLObject obj = tree.getSelectedOWLObject();
        if (obj instanceof OWLEntity) {
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) obj);
        }
    }


    public void initialiseIndividualsView() throws Exception {
        setLayout(new BorderLayout());

        updateEvents.add(EventType.ACTIVE_ONTOLOGY_CHANGED);
        updateEvents.add(EventType.ONTOLOGY_LOADED);
        updateEvents.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        updateEvents.add(EventType.ENTITY_RENDERER_CHANGED);

        getOWLModelManager().addListener(managerListener);

        provider = new IndividualsByTypeHierarchyProvider(getOWLModelManager().getOWLOntologyManager());
        tree = new OWLModelManagerTree<OWLObject>(getOWLEditorKit(), provider);
        add(new JScrollPane(tree));
        provider.setOntologies(getOWLModelManager().getActiveOntologies());
        listener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                transmitSelection();
            }
        };
        tree.addTreeSelectionListener(listener);
    }


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
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
