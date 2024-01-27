package org.protege.editor.core.ui.preferences;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesDialogPanel extends JPanel implements Disposable {

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_TITLE = "Reset preferences?";

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_MESSAGE = "Are you sure you want to reset all preferences to their default settings";

    public static final int DIALOG_DEFAULT_WIDTH = 850;

    public static final int DIALOG_DEFAULT_HEIGHT = 600;

    private final Map<String, PreferencesPanel> map = new HashMap<>();

    private final Map<String, JComponent> componentMap = new HashMap<>();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private static final String PREFS_HISTORY_PANEL_KEY = "prefs.history.panel";

    // A bit messy
    private boolean wasReset = false;

    private final Logger logger = LoggerFactory.getLogger(PreferencesDialogPanel.class);

    public PreferencesDialogPanel(EditorKit editorKit) {
        setLayout(new BorderLayout());

        PreferencesPanelPluginLoader loader = new PreferencesPanelPluginLoader(editorKit);
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
                final String label = plugin.getLabel();
                final JScrollPane sp = new JScrollPane(panel);
                sp.setBorder(new EmptyBorder(0, 0, 0, 0));
                map.put(label, panel);
                componentMap.put(label, sp);
                tabbedPane.addTab(label, sp);
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst trying to instantiate the preferences panel plugin '{}': {}", plugin.getLabel(), e);
            }
        }
        add(tabbedPane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int margin = 300;
        int prefWidth = Math.min(screenSize.width - margin, DIALOG_DEFAULT_WIDTH);
        int prefHeight = Math.min(screenSize.height - margin, DIALOG_DEFAULT_HEIGHT);
        setPreferredSize(new Dimension(prefWidth, prefHeight));

    }

    public void dispose() {
        for (PreferencesPanel panel : new ArrayList<>(map.values())) {
            try {
                panel.dispose();
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst disposing of the preferences panel plugin '{}': {}", panel.getLabel(), e);
            }
        }
        map.clear();
    }

    public void applyPreferences() {
        for (PreferencesPanel panel : new ArrayList<>(map.values())) {
            try {
                panel.applyChanges();
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst trying to save the preferences for the preferences panel '{}': {}", panel.getLabel(), e);
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

    public static void showPreferencesDialog(String selectedPanel, EditorKit editorKit) {
        final PreferencesDialogPanel preferencesPanel = new PreferencesDialogPanel(editorKit);
        
        JPanel holder = new JPanel(new BorderLayout(4, 4));
        holder.add(preferencesPanel);
        
        final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(PreferencesDialogPanel.class);
        if (selectedPanel == null){
            selectedPanel = prefs.getString(PREFS_HISTORY_PANEL_KEY, null);
        }
        Component c = preferencesPanel.componentMap.get(selectedPanel);
        if (c != null) {
            preferencesPanel.tabbedPane.setSelectedComponent(c);
        }

        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton resetPreferencesButton = new JButton(new AbstractAction("Reset preferences...") {
            public void actionPerformed(ActionEvent e) {
                handleResetPreferences(preferencesPanel);
            }
        });
        resetPanel.add(resetPreferencesButton);
        holder.add(resetPanel, BorderLayout.SOUTH);

        JOptionPane op = new JOptionPane(holder, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = op.createDialog(editorKit.getWorkspace(), "Preferences");
        dlg.setResizable(true);
        dlg.setVisible(true);
        if (!preferencesPanel.wasReset) {
            Object o = op.getValue();
            if (o != null){
                int ret = (Integer)o;
                if (ret == JOptionPane.OK_OPTION) {
                    preferencesPanel.applyPreferences();
                }
            }
            prefs.putString(PREFS_HISTORY_PANEL_KEY, preferencesPanel.getSelectedPanel());
        }

        preferencesPanel.dispose();
    }
    
    private static void handleResetPreferences(PreferencesDialogPanel panel) {
        int ret = JOptionPane.showConfirmDialog(panel, RESET_PREFERENCES_CONFIRMATION_DIALOG_MESSAGE, RESET_PREFERENCES_CONFIRMATION_DIALOG_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(ret == JOptionPane.OK_OPTION) {
            PreferencesManager.getInstance().resetPreferencesToFactorySettings();
            panel.wasReset = true;
        }
    }
}
