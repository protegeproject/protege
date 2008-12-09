package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.description.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddSiblingClassAction extends OWLSelectionViewAction {

    private OWLObjectTree<OWLClass> tree;

    private OWLEditorKit owlEditorKit;


    public void dispose() {
    }


    public AddSiblingClassAction(OWLEditorKit owlEditorKit, OWLObjectTree<OWLClass> tree) {
        super("Add sibling class", OWLIcons.getIcon("class.add.sib.png"));
        putValue(AbstractAction.ACCELERATOR_KEY, OWLWorkspace.getCreateSibKeyStroke());
        this.owlEditorKit = owlEditorKit;
        this.tree = tree;
    }


    public void updateState() {
        if (tree.getSelectedOWLObject() == null) {
            setEnabled(false);
            return;
        }
        OWLClass thing = owlEditorKit.getModelManager().getOWLDataFactory().getOWLThing();
        setEnabled(!tree.getSelectedOWLObject().equals(thing));
        return;
    }


    protected boolean canPerform(OWLClass cls) {
        return cls != null;
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLClass cls = tree.getSelectedOWLObject();
        if (cls == null) {
            // Shouldn't really get here, because the
            // action should be disabled
            return;
        }
        // We need to apply the changes in the active ontology
        OWLEntityCreationSet<OWLClass> creationSet = owlEditorKit.getWorkspace().createOWLClass();
        if (creationSet == null) {
            return;
        }

        OWLModelManager mngr = owlEditorKit.getModelManager();
        OWLDataFactory df = mngr.getOWLDataFactory();

        AnonymousDefinedClassManager adcManager = mngr.get(AnonymousDefinedClassManager.ID);

        // Combine the changes that are required to create the OWLClass, with the
        // changes that are required to make it a sibling class.
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(creationSet.getOntologyChanges());
        for (OWLDescription par : mngr.getOWLHierarchyManager().getOWLClassHierarchyProvider().getParents(cls)) {
            if (adcManager != null && adcManager.isAnonymous(par.asOWLClass())){
                par = adcManager.getExpression(par.asOWLClass());
            }
            OWLAxiom ax = df.getOWLSubClassAxiom(creationSet.getOWLEntity(), par);
            changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
        }
        mngr.applyChanges(changes);
        // Select the new class
        tree.setSelectedOWLObject(creationSet.getOWLEntity());
    }
}
