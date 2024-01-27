package org.protege.editor.owl.ui.hierarchy.creation;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PickRootClassPanel extends AbstractOWLWizardPanel {
	private static final long serialVersionUID = 3010893357248469815L;

	public static final String ID = "PickRootClassPanel";

    private OWLModelManagerTree<OWLClass> tree;


    public PickRootClassPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Pick root class", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please select the root class");
        tree = new OWLModelManagerTree<>(getOWLEditorKit(),
                                                 getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider());
        tree.setSelectedOWLObject(getOWLEditorKit().getWorkspace().getOWLSelectionModel().getLastSelectedClass());
        parent.add(ComponentFactory.createScrollPane(tree));
    }


    public void displayingPanel() {
        tree.requestFocus();
    }


    public OWLClass getRootClass() {
        OWLClass cls = tree.getSelectedOWLObject();
        if (cls == null) {
            return getOWLModelManager().getOWLDataFactory().getOWLThing();
        }
        else {
            return tree.getSelectedOWLObject();
        }
    }


    public Object getNextPanelDescriptor() {
        return TabIndentedHierarchyPanel.ID;
    }


    public void dispose() {
        tree.dispose();
    }
}
