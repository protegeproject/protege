package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.OWLSelectionViewAction;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.util.OWLEntityRemover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public abstract class AbstractDeleteEntityAction<E extends OWLEntity> extends OWLSelectionViewAction {

    private OWLEditorKit owlEditorKit;


    protected AbstractDeleteEntityAction(String name, Icon icon, OWLEditorKit owlEditorKit) {
        super(name, icon);
        this.owlEditorKit = owlEditorKit;
    }


    public void updateState() {
        setEnabled(!getSelectedEntities().isEmpty());
    }


    public void dispose() {
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void actionPerformed(ActionEvent e) {
        Set<E> selents = getSelectedEntities();
        String name;
        if (selents.size() == 1) {
            name = owlEditorKit.getOWLModelManager().getRendering(selents.iterator().next());
        }
        else {
            name = getPluralDescription();
        }
        JRadioButton onlySelectedEntityRadioButton = new JRadioButton("Delete " + name + " only");
        JRadioButton decendantsRadioButton = new JRadioButton("Delete " + name + " and asserted descendant " + getPluralDescription());
        JPanel panel = new JPanel(new BorderLayout(2, 2));
        panel.add(onlySelectedEntityRadioButton, BorderLayout.NORTH);
        panel.add(decendantsRadioButton, BorderLayout.SOUTH);
        ButtonGroup bg = new ButtonGroup();
        bg.add(onlySelectedEntityRadioButton);
        bg.add(decendantsRadioButton);
        onlySelectedEntityRadioButton.setSelected(true);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        int ret = JOptionPane.showConfirmDialog(owlEditorKit.getOWLWorkspace(),
                                                panel,
                                                "Delete",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret != JOptionPane.OK_OPTION) {
            return;
        }
        if (onlySelectedEntityRadioButton.isSelected()) {
            delete(selents);
        }
        else {
            Set<E> ents = new HashSet<E>();
            for (E ent : selents) {
                ents.add(ent);
                ents.addAll(getHierarchyProvider().getDescendants(ent));
            }
            delete(ents);
        }
    }


    private void delete(Set<E> ents) {
        OWLEntityRemover remover = new OWLEntityRemover(owlEditorKit.getOWLModelManager().getOWLOntologyManager(),
                                                        owlEditorKit.getOWLModelManager().getOntologies());
        for (E ent : ents) {
            ent.accept(remover);
        }
        owlEditorKit.getOWLModelManager().applyChanges(remover.getChanges());
    }


    protected abstract Set<E> getSelectedEntities();


    protected abstract OWLObjectHierarchyProvider<E> getHierarchyProvider();


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
