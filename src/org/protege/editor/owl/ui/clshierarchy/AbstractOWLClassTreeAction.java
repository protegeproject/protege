package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 24, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @deprecated use <code>AbstractOWLTreeAction&lt;OWLEntity&gt;</code>
 */
public abstract class AbstractOWLClassTreeAction extends DisposableAction {

    private TreeSelectionModel selectionModel;

    private TreeSelectionListener selectionListener;


    public AbstractOWLClassTreeAction(String name, Icon icon, TreeSelectionModel selectionModel) {
        super(name, icon);
        this.selectionModel = selectionModel;
        this.selectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                reactToSelection();
            }
        };
        selectionModel.addTreeSelectionListener(selectionListener);
        setEnabled(canPerform(getSelectedOWLClass()));
    }


    // Called on a selection history to enable/disable
    // the action.
    private void reactToSelection() {
        // Ask subclasses if we should be enabled for the current
        // selection
        setEnabled(canPerform(getSelectedOWLClass()));
    }


    protected abstract boolean canPerform(OWLClass cls);


    protected TreePath getSelectionPath() {
        return selectionModel.getSelectionPath();
    }


    protected void setSelectionPath(TreePath path) {
        selectionModel.setSelectionPath(path);
    }


    /**
     * A convenience method that gets the first selected
     * <code>OWLClass</code>
     * @return The selected <code>OWLClass</code>, or
     *         <code>null</code> if no class is selected
     */
    protected OWLClass getSelectedOWLClass() {
        TreePath treePath = selectionModel.getSelectionPath();
        if (treePath == null) {
            return null;
        }
        return (OWLClass) ((OWLObjectTreeNode<OWLClass>) treePath.getLastPathComponent()).getUserObject();
    }


    public void dispose() {
        selectionModel.removeTreeSelectionListener(selectionListener);
    }


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialise() throws Exception {
    }
}
