package org.protege.editor.core.ui.workspace;

import java.net.URL;

import javax.swing.Icon;

import org.protege.editor.core.plugin.ProtegePlugin;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>ProtegePlugin</code> that creates <code>WorkspaceTab</code>s.
 * The has a reference to the <code>TabbedWorkspace</code> that it is
 * creating the tabs for.
 */
public interface WorkspaceTabPlugin extends ProtegePlugin<WorkspaceTab> {

    /**
     * Gets the workspace that this plugin creates
     * tabs for.
     *
     * @return A <code>TabbedWorkspace</code> that the tabs instantiated
     *         by this plugin belong to.
     */
    public TabbedWorkspace getWorkspace();


    /**
     * Gets the label that is shown on the tab
     *
     * @return A <code>String</code> that represents the label.  The
     *         return value must not be <code>null</code>.
     */
    public String getLabel();


    /**
     * Gets the icon shown on the tab.
     *
     * @return An <code>Icon</code> to be shown on the tab.
     *         The return value may be <code>null</code>.
     */
    public Icon getIcon();

    /**
     * Gets the default tab index.  This can be used to
     * order the tabs.
     *
     * @return A <code>String</code> that represents the index.
     */
    public String getIndex();

    public URL getDefaultViewConfigFile();
}
