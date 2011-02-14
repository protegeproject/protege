package org.protege.editor.core.ui.workspace.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;

public class LoadedTabsSelector extends JPanel {
    private static final long serialVersionUID = 4063978799949163657L;
    private CheckTable<WorkspaceTabPlugin> table;

    public LoadedTabsSelector(TabbedWorkspace workspace) {
        super(new BorderLayout());
        table = new CheckTable<WorkspaceTabPlugin>("Custom tabs");
        table.setDefaultRenderer(new DefaultTableCellRenderer(){
            /**
             * 
             */
            private static final long serialVersionUID = -7161202195746696063L;

            public Component getTableCellRendererComponent(JTable jTable, Object o, boolean b, boolean b1, int i, int i1) {
                if (o instanceof WorkspaceTabPlugin){
                    o = ((WorkspaceTabPlugin)o).getLabel();
                }
                return super.getTableCellRendererComponent(jTable, o, b, b1, i, i1);
            }
        });
        table.getModel().setData(workspace.getCustomTabsManager().getCustomTabPlugins(workspace), false);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public List<WorkspaceTabPlugin> getSelectedTabs(){
        return table.getFilteredValues();
    }
}
