package org.protege.editor.core.ui.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;

import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.menu.MenuBuilder;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.UIUtil;

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

    public static final int DEFAULT_WIDTH = 1024;

    public static final int DEFAULT_HEIGHT = 768;

    private static final int MINIMUM_WIDTH = 400;

    private static final int MINIMUM_HEIGHT = 300;

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
                workspace.handleActivated();
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
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        int w = prefs.getInt(SIZE_X, DEFAULT_WIDTH);
        int h = prefs.getInt(SIZE_Y, DEFAULT_HEIGHT);
        if(w < MINIMUM_WIDTH) {
            w = MINIMUM_WIDTH;
        }
        if(h < MINIMUM_HEIGHT) {
            h = MINIMUM_HEIGHT;
        }
        setSize(w, h);
        Point defLoc = getDefaultLocation();
        int x = prefs.getInt(LOC_X, defLoc.x);
        int y = prefs.getInt(LOC_Y, defLoc.y);
        Rectangle desiredRectangle = new Rectangle(x, y, w, h);
        if(UIUtil.isVisibleOnScreen(desiredRectangle)) {
            setLocation(x, y);
        }
        else {
            setLocation(defLoc.x, defLoc.y);
        }
    }


    private Point getDefaultLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = getSize();
        return new Point((screenDim.width - frameDim.width) / 2, (screenDim.height - frameDim.height) / 2);
    }


    protected void saveMetrics() {
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass().getName());
        Point location = getLocation();
        prefs.putInt(LOC_X, location.x);
        prefs.putInt(LOC_Y, location.y);
        prefs.putInt(SIZE_X, getSize().width);
        prefs.putInt(SIZE_Y, getSize().height);
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
        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
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

        Optional<JComponent> statusArea = workspace.getStatusArea();
        statusArea.ifPresent(sa -> contentPane.add(sa, BorderLayout.SOUTH));
        setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
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
