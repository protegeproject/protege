package org.protege.editor.core.ui.explanationpreferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanelPlugin;
import org.protege.editor.core.ui.preferences.PreferencesPanelPluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplanationPreferencesPanel extends PreferencesPanel implements Disposable {

    private final Map<String, PreferencesPanel> map = new HashMap<>();

    private final Map<String, JComponent> componentMap = new HashMap<>();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final Logger logger = LoggerFactory.getLogger(ExplanationPreferencesPanel.class);

    private static final String EXPL_PREFS_HISTORY_PANEL_KEY = "expl.prefs.history.panel";

    @Override
    public void dispose() throws Exception {
        final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ExplanationPreferencesPanel.class);
        prefs.putString(EXPL_PREFS_HISTORY_PANEL_KEY, getSelectedPanel());
        for (PreferencesPanel panel : new ArrayList<>(map.values())) {
            try {
                panel.dispose();
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst disposing of the explanation preferences panel plugin '{}': {}", panel.getLabel(), e);
            }
        }
        map.clear();
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

    public void updatePanelSelection(String selectedPanel) {
    	if (selectedPanel == null){
            final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(ExplanationPreferencesPanel.class);
            selectedPanel = prefs.getString(EXPL_PREFS_HISTORY_PANEL_KEY, null);
        }
        Component c = componentMap.get(selectedPanel);
        if (c != null) {
            tabbedPane.setSelectedComponent(c);
        }
    }

    @Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());

        ExplanationPreferencesPanelPluginLoader loader = new ExplanationPreferencesPanelPluginLoader(getEditorKit());
        Set<PreferencesPanelPlugin> plugins = new TreeSet<>((o1, o2) -> {
                String s1 = o1.getLabel();
                String s2 = o2.getLabel();
                return s1.compareTo(s2);
        });
        plugins.addAll(loader.getPlugins());

        for (PreferencesPanelPlugin plugin : plugins) {
            try {
                PreferencesPanel panel = plugin.newInstance();
                panel.initialise();
                String label = plugin.getLabel();
                final JScrollPane sp = new JScrollPane(panel);
                sp.setBorder(new EmptyBorder(0, 0, 0, 0));
                map.put(label, panel);
                componentMap.put(label, sp);
                tabbedPane.addTab(label, sp);
            } catch (Throwable e) {
                logger.warn("An error occurred whilst trying to instantiate the explanation preferences panel plugin '{}': {}", plugin.getLabel(), e);
            }
        }

        add(tabbedPane);

        updatePanelSelection(null);
    }

	@Override
	public void applyChanges() {
        for (PreferencesPanel panel : new ArrayList<>(map.values())) {
            try {
                panel.applyChanges();
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst trying to save the preferences for the explanation preferences panel '{}': {}", panel.getLabel(), e);
            }
        }
	}
}
