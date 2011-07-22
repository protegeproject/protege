package org.protege.editor.core.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesDialogPanel extends JPanel implements Disposable {

    private static final long serialVersionUID = 6338996558666619642L;

    private Map<String, PreferencesPanel> map;

    private Map<String, JComponent> scrollerMap;

    private JTabbedPane tabbedPane;

    private static final String PREFS_HISTORY_PANEL_KEY = "prefs.history.panel";

    public PreferencesDialogPanel(EditorKit editorKit) {
        map = new HashMap<String, PreferencesPanel>();
        scrollerMap = new HashMap<String, JComponent>();
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        PreferencesPanelPluginLoader loader = new PreferencesPanelPluginLoader(editorKit);
        Set<PreferencesPanelPlugin> plugins = new TreeSet<PreferencesPanelPlugin>(new Comparator<PreferencesPanelPlugin>() {
            public int compare(PreferencesPanelPlugin o1, PreferencesPanelPlugin o2) {
                String s1 = o1.getLabel();
                String s2 = o2.getLabel();
                return s1.compareTo(s2);
            }
        });
        plugins.addAll(loader.getPlugins());
        for (PreferencesPanelPlugin plugin : plugins) {
            try {
                PreferencesPanel panel = plugin.newInstance();
                panel.initialise();
                final String label = plugin.getLabel();
                final JScrollPane scroller = new JScrollPane(panel);
                scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
                map.put(label, panel);
                scrollerMap.put(label, scroller);
                tabbedPane.addTab(label, scroller);
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        add(tabbedPane);
    }

    public void dispose() {
        for (PreferencesPanel panel : new ArrayList<PreferencesPanel>(map.values())) {
            try {
                panel.dispose();
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        map.clear();
    }

    public void applyPreferences() {
        for (PreferencesPanel panel : new ArrayList<PreferencesPanel>(map.values())) {
            try {
                panel.applyChanges();
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }

    protected String getSelectedPanel() {
        Component c = tabbedPane.getSelectedComponent();
        if (c instanceof JScrollPane){
            c = ((JScrollPane)c).getViewport().getView();
        }
        for (String tabName : map.keySet()){
            if (c.equals(map.get(tabName))){
                return tabName;
            }
        }
        return null;
    }

    public Dimension getPreferredSize() {
        return new Dimension(850, 675);
    }

    public static void showPreferencesDialog(String selectedPanel, EditorKit editorKit) {
        PreferencesDialogPanel panel = new PreferencesDialogPanel(editorKit);
        final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(PreferencesDialogPanel.class);
        if (selectedPanel == null){
            selectedPanel = prefs.getString(PREFS_HISTORY_PANEL_KEY, null);
        }
        Component c = panel.scrollerMap.get(selectedPanel);
        if (c != null) {
            panel.tabbedPane.setSelectedComponent(c);
        }
        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = op.createDialog(editorKit.getWorkspace(), "Preferences");
        dlg.setResizable(true);
        dlg.setVisible(true);
        Object o = op.getValue();
        if (o != null){
            int ret = (Integer)o;
            if (ret == JOptionPane.OK_OPTION) {
                panel.applyPreferences();
            }
        }

        prefs.putString(PREFS_HISTORY_PANEL_KEY, panel.getSelectedPanel());
        panel.dispose();
    }
}
