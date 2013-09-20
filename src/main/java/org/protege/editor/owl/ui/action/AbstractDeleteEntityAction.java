package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public abstract class AbstractDeleteEntityAction<E extends OWLEntity> extends OWLSelectionViewAction {

    private OWLObjectHierarchyDeleter<E> deleter;

    private OWLEntitySetProvider<E> entitySetProvider;


    protected AbstractDeleteEntityAction(String name, Icon icon, OWLEditorKit owlEditorKit,
                                         OWLObjectHierarchyProvider<E> hp,
                                         OWLEntitySetProvider<E> entitySetProvider) {
        super(name, icon);
        this.entitySetProvider = entitySetProvider;
        this.deleter = new OWLObjectHierarchyDeleter<E>(owlEditorKit,
                                                        hp,
                                                        entitySetProvider,
                                                        getPluralDescription());
    }


    public void updateState() {
        setEnabled(!entitySetProvider.getEntities().isEmpty());
    }


    public void dispose() {
        deleter.dispose();
        deleter = null;
    }


    public void actionPerformed(ActionEvent e) {
        deleter.performDeletion();
    }


    protected void notifySelectionChange() {
        updateState();
    }


    /**
     * Returns the plural name of a set of entities e.g.
     * classes, properties, individuals. This is used in
     * the UI e.g. "Delete selected classes"
     */
    protected abstract String getPluralDescription();
}
