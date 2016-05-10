package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.menu.MenuBuilder;
import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 15, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * The <code>WorkspaceFrame</code> is a holder for a <code>Workspace</code>.
 * The frame menu bar and toolbar are constructed for the particular workspace.
 */
public class WorkspaceFrame extends JFrame {

    private static final long serialVersionUID = -8568184212386766789L;

    public static final int DEFAULT_WIDTH = 1024;

    public static final int DEFAULT_HEIGHT = 768;

    private Workspace workspace;

    public static final String LOC_X = "LOC_X";

    public static final String LOC_Y = "LOC_Y";

    public static final String SIZE_X = "SIZE_X";

    public static final String SIZE_Y = "SIZE_Y";

    private Set<ProtegeAction> menuActions;


    public WorkspaceFrame(Workspace workspace) {
        this.workspace = workspace;
        menuActions = new HashSet<>();
        createUI();
        restoreMetrics();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (OSUtils.isOSX()){
                    ProtegeAppleApplication.getInstance().setEditorKit(null);
                }
                saveMetrics();
                removeWindowListener(this);
            }

            public void windowActivated(WindowEvent event) {
                if (OSUtils.isOSX()){
                    ProtegeAppleApplication.getInstance().setEditorKit(WorkspaceFrame.this.workspace.getEditorKit());
                }
            }
        });
    }


    public void dispose() {
        super.dispose();

        for (ProtegeAction action : menuActions) {
            try {
                action.dispose();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void restoreMetrics() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        int w = p.getInt(SIZE_X, DEFAULT_WIDTH);
        int h = p.getInt(SIZE_Y, DEFAULT_HEIGHT);
        setSize(w, h);
        Point defLoc = getDefaultLocation();
        int x = p.getInt(LOC_X, defLoc.x);
        int y = p.getInt(LOC_Y, defLoc.y);
        setLocation(x, y);
        setSize(w, h);
    }


    private Point getDefaultLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = getSize();
        return new Point((screenDim.width - frameDim.width) / 2, (screenDim.height - frameDim.height) / 2);
    }


    protected void saveMetrics() {
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        p.putInt(LOC_X, getLocation().x);
        p.putInt(LOC_Y, getLocation().y);
        p.putInt(SIZE_X, getSize().width);
        p.putInt(SIZE_Y, getSize().height);
    }


    public JMenu getMenu(String name) {
        for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
            JMenu menu = getJMenuBar().getMenu(i);
            if (menu.getText() != null) {
                if (menu.getText().equals(name)) {
                    return menu;
                }
            }
        }
        JMenu menu = new JMenu(name);
        getJMenuBar().add(menu);
        return menu;
    }


    private void createUI() {
        JPanel contentPane = new JPanel(new BorderLayout(7, 7));
        setContentPane(contentPane);
        // Menu bar
        createMenuBar();

        // Add the workspace to the frame
        contentPane.add(workspace);
        workspace.initialiseExtraMenuItems(getJMenuBar());
        String title = workspace.getTitle();
        if (title != null) {
            setTitle(title);
        }
        setIconImage(((ImageIcon) Icons.getIcon("logo32.gif")).getImage());

        JComponent statusArea = workspace.getStatusArea();
        if (statusArea != null) {
            contentPane.add(statusArea, BorderLayout.SOUTH);
        }
    }


    public void updateTitle() {
        String title = workspace.getTitle();
        if (title != null) {
            setTitle(title);
        }
    }


    private void createMenuBar() {
        // Delegate to the menu builder, which will create the
        // menus based on installed plugins.
        MenuBuilder menuBuilder = new MenuBuilder(workspace.getEditorKit());
        setJMenuBar(menuBuilder.buildMenu());
        menuActions.addAll(menuBuilder.getActions());
    }
    

}
