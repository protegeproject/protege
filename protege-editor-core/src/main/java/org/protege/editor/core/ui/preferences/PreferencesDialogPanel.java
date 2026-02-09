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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesDialogPanel extends JPanel implements Disposable {

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_TITLE = "Reset preferences?";

    public static final String RESET_PREFERENCES_CONFIRMATION_DIALOG_MESSAGE = "Are you sure you want to reset ALL preferences to their default settings?";

    public static final int DIALOG_DEFAULT_WIDTH = 850;

    public static final int DIALOG_DEFAULT_HEIGHT = 600;

    private final Map<String, PreferencesPanel> map = new HashMap<>();

    private final Map<String, JComponent> componentMap = new HashMap<>();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private static final String PREFS_HISTORY_PANEL_KEY = "prefs.history.panel";

    private final Logger logger = LoggerFactory.getLogger(PreferencesDialogPanel.class);
    
    private final EditorKit editorKit;

    public PreferencesDialogPanel(EditorKit editorKit) {
    	this.editorKit = editorKit;
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background"));
        setOpaque(true);
        add(tabbedPane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int margin = 300;
        int prefWidth = Math.min(screenSize.width - margin, DIALOG_DEFAULT_WIDTH);
        int prefHeight = Math.min(screenSize.height - margin, DIALOG_DEFAULT_HEIGHT);
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        reload();
    }
    
    private void reload() {
    	dispose();
    	tabbedPane.removeAll();
    	tabbedPane.setBackground(UIManager.getColor("Panel.background"));
    	tabbedPane.setOpaque(true);
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
                sp.setBackground(UIManager.getColor("Panel.background"));
                sp.setOpaque(true);
                sp.getViewport().setBackground(UIManager.getColor("Panel.background"));
                sp.getViewport().setOpaque(true);
                map.put(label, panel);
                componentMap.put(label, sp);
                tabbedPane.addTab(label, sp);
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst trying to instantiate the preferences panel plugin '{}': {}", plugin.getLabel(), e);
            }
        }
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
    
    protected void setSelectedPanel(String tabName) {
    	Component c = componentMap.get(tabName);
        if (c != null) {
            tabbedPane.setSelectedComponent(c);
        }
    }

    public static void showPreferencesDialog(String selectedPanel, EditorKit editorKit) {
        final PreferencesDialogPanel preferencesPanel = new PreferencesDialogPanel(editorKit);
        
        JPanel holder = new JPanel(new BorderLayout(4, 4));
        holder.setBackground(UIManager.getColor("Panel.background"));
        holder.setOpaque(true);
        holder.add(preferencesPanel);
                
        if (selectedPanel == null){
        	Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(PreferencesDialogPanel.class);
            selectedPanel = prefs.getString(PREFS_HISTORY_PANEL_KEY, null);
        }
        preferencesPanel.setSelectedPanel(selectedPanel);

        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resetPanel.setBackground(UIManager.getColor("Panel.background"));
        resetPanel.setOpaque(true);
        JButton resetPreferencesButton = new JButton(new AbstractAction("Reset preferences...") {
            public void actionPerformed(ActionEvent e) {
                handleResetPreferences(preferencesPanel);
            }
        });
        resetPanel.add(resetPreferencesButton);
        holder.add(resetPanel, BorderLayout.SOUTH);

        JOptionPane op = new JOptionPane(holder, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        op.setBackground(UIManager.getColor("Panel.background"));
        op.setOpaque(true);
        JDialog dlg = op.createDialog(editorKit.getWorkspace(), "Preferences");
        dlg.getContentPane().setBackground(UIManager.getColor("Panel.background"));
        
        // Recursively set background colors on all components in the dialog
        setBackgroundRecursive(dlg, UIManager.getColor("Panel.background"));
        
        dlg.setResizable(true);
        dlg.setVisible(true);
        Object o = op.getValue();
        if (o != null){
            int ret = (Integer)o;
            if (ret == JOptionPane.OK_OPTION) {
                preferencesPanel.applyPreferences();
            }
        }
        
		PreferencesManager.getInstance()
				.getApplicationPreferences(PreferencesDialogPanel.class)
				.putString(PREFS_HISTORY_PANEL_KEY, preferencesPanel.getSelectedPanel());

        preferencesPanel.dispose();
    }
    
    private static void handleResetPreferences(PreferencesDialogPanel panel) {
    	String selectedPanel = panel.getSelectedPanel();
        int ret = JOptionPane.showConfirmDialog(panel, RESET_PREFERENCES_CONFIRMATION_DIALOG_MESSAGE, RESET_PREFERENCES_CONFIRMATION_DIALOG_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(ret == JOptionPane.OK_OPTION) {
            PreferencesManager.getInstance().resetPreferencesToFactorySettings();
        }        
        panel.reload();
        panel.setSelectedPanel(selectedPanel);
    }
    
    /**
     * Recursively sets the background color on all components in a container.
     * This ensures that all nested components use the correct theme background.
     */
    private static void setBackgroundRecursive(Container container, Color background) {
        if (container == null || background == null) return;
        
        // Set background on the container itself
        container.setBackground(background);
        if (container instanceof JComponent) {
            ((JComponent) container).setOpaque(true);
        }
        
        // Recursively set background on all child components
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                setBackgroundRecursive((Container) component, background);
            } else {
                component.setBackground(background);
                if (component instanceof JComponent) {
                    ((JComponent) component).setOpaque(true);
                }
            }
        }
    }
}
