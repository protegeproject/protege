package org.protege.editor.core.ui.preferences;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

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

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_TITLE = "Reset preferences?";

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_MESSAGE = "Are you sure you want to reset all preferences to their default settings";
    public static final int DIALOG_DEFAULT_WIDTH = 850;
    public static final int DIALOG_DEFAULT_HEIGHT = 725;

    private Map<String, PreferencesPanel> map;

    private Map<String, JComponent> scrollerMap;

    private JTabbedPane tabbedPane;

    private static final String PREFS_HISTORY_PANEL_KEY = "prefs.history.panel";

    // A bit messy
    private boolean wasReset = false;

    private final Logger logger = LoggerFactory.getLogger(PreferencesDialogPanel.class);

    public PreferencesDialogPanel(EditorKit editorKit) {
        map = new HashMap<>();
        scrollerMap = new HashMap<>();
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        PreferencesPanelPluginLoader loader = new PreferencesPanelPluginLoader(editorKit);
        Set<PreferencesPanelPlugin> plugins = new TreeSet<>(new Comparator<PreferencesPanelPlugin>() {
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
                logger.warn("An error occurred whilst trying to instantiate the preferences panel plugin '{}': {}", plugin.getLabel(), e);
            }
        }
        add(tabbedPane);
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

    public Dimension getPreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(screenSize.width - 100, DIALOG_DEFAULT_WIDTH);
        int height = Math.min(screenSize.height - 100, DIALOG_DEFAULT_HEIGHT);
        return new Dimension(width, height);
    }

    

    public static void showPreferencesDialog(String selectedPanel, EditorKit editorKit) {
        final PreferencesDialogPanel preferencesPanel = new PreferencesDialogPanel(editorKit);
        
        JPanel holder = new JPanel(new BorderLayout(4, 4));
        holder.add(preferencesPanel);
        
        final Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(PreferencesDialogPanel.class);
        if (selectedPanel == null){
            selectedPanel = prefs.getString(PREFS_HISTORY_PANEL_KEY, null);
        }
        Component c = preferencesPanel.scrollerMap.get(selectedPanel);
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
