package org.protege.editor.core.ui.workspace;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.TabbedPaneUI;

import org.coode.mdock.UIComponentFactory;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.core.ui.tabbedpane.CloseableTabbedPaneUI;
import org.protege.editor.core.ui.tabbedpane.NullTabCloseHandler;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * Represents a tab in a <code>TabbedWorkspace</code>.

 * This is a core plugin type.
 */
public abstract class WorkspaceTab extends JComponent implements ProtegePluginInstance {

    static  {
        UIComponentFactory.setInstance(new UIComponentFactory() {
            public TabbedPaneUI createComponentNodeTabbedPaneUI() {
                return new CloseableTabbedPaneUI(CloseableTabbedPaneUI.TabClosability.NOT_CLOSEABLE, new NullTabCloseHandler());
            }
            @Override
            public Border createComponentNodeBorder() {
                return BorderFactory.createEmptyBorder();
            }
        });
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8243398072263923882L;

    /**
     * The ID of this particular plugin extension.
     */
    private String id;

    private String label;

    private Icon icon;

    private TabbedWorkspace workspace;

    private URL defaultViewConfigurationFile;


    public void setup(WorkspaceTabPlugin plugin) {
        this.id = plugin.getId();
        this.label = plugin.getLabel();
        this.icon = plugin.getIcon();
        this.workspace = plugin.getWorkspace();
        this.defaultViewConfigurationFile = plugin.getDefaultViewConfigFile();
    }


    public String getId() {
        return id;
    }


    public URL getDefaultViewConfigurationFile() {
        return defaultViewConfigurationFile;
    }


    /**
     * Gets this tabs label.  This label will be used in the
     * <code>Workspace</code> UI, for displaying the name of the tab.
     * @return A <code>String</code> representing the label.
     */
    public String getLabel() {
        return label;
    }


    /**
     * Gets this tabs icon.
     * @return An <code>Icon</code> or <code>null</code> if the tab
     *         does not have an icon.
     */
    public Icon getIcon() {
        return icon;
    }


    /**
     * Gets this tab's <code>Workspace</code>, which can be used to
     * access the <code>EditorKit</code>.
     */
    public TabbedWorkspace getWorkspace() {
        return workspace;
    }


    public void requestSelection() {
        workspace.setSelectedTab(this);
    }


    public void save() {
        // Does nothing by default
    }
}
