package org.protege.editor.core.ui.view;

import javax.swing.JComponent;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.SelectionProvider;
import org.protege.editor.core.ui.workspace.Workspace;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>ViewComponent</code> is the plugin part to a
 * <code>View</code>.  <code>TabViewPlugins</code> should
 * subclass this class and create their view UI in the
 * <code>initialise</code> method.
 */
public abstract class ViewComponent extends JComponent implements ProtegePluginInstance {

    private Workspace workspace;

    private View view;


    public void setup(ViewComponentPlugin plugin) {
        this.workspace = plugin.getWorkspace();
    }


    public void setView(View view) {
        this.view = view;
    }


    public View getView() {
        return view;
    }


    public Workspace getWorkspace() {
        return workspace;
    }


    protected void setHeaderText(String text) {
        if (view != null) {
            view.setHeaderText(text);
        }
    }


    protected void addAction(ProtegeAction action, String group, String groupIndex) {
        if (view != null) {
            view.addAction(action, group, groupIndex);
        }
    }


    protected void addAction(DisposableAction action, String group, String groupIndex) {
        addAction(new ViewActionAdapter(action), group, groupIndex);
    }


    protected boolean isPinned() {
        if (view != null) {
            return view.isPinned();
        }
        return false;
    }


    protected SelectionProvider getSelectionProvider() {
        return null;
    }
}
