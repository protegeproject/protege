package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.protege.editor.owl.model.hierarchy.IndividualsByTypeHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;


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


    private void transmitSelection() {
        OWLObject obj = tree.getSelectedOWLObject();
        if (obj instanceof OWLEntity) {
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) obj);
        }
    }


    public void initialiseIndividualsView() throws Exception {
        setLayout(new BorderLayout());
        tree = new OWLModelManagerTree<OWLObject>(getOWLEditorKit(),
                                                  provider = new IndividualsByTypeHierarchyProvider(getOWLModelManager().getOWLOntologyManager()));
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
    }
}
