package org.protege.editor.owl.ui.inference;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.*;

/*
 * WARNING - This code was liberally stolen from PreferencesDialogPanel
 */

public class TabbedReasonerPreferencesPanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 5167874018417496809L;
    public static final String LABEL = "Reasoner";

    private final Logger logger = LoggerFactory.getLogger(TabbedReasonerPreferencesPanel.class);

    private List<OWLPreferencesPanel> panels = new ArrayList<OWLPreferencesPanel>();

    public void initialise() throws Exception {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JTabbedPane tabbedPane = new JTabbedPane();
        InferencePreferencePluginLoader loader = new InferencePreferencePluginLoader(getOWLEditorKit());
        Set<InferencePreferencePlugin> plugins = new TreeSet<InferencePreferencePlugin>(new Comparator<InferencePreferencePlugin>() {
            public int compare(InferencePreferencePlugin o1, InferencePreferencePlugin o2) {
                String s1 = o1.getLabel();
                String s2 = o2.getLabel();
                return s1.compareTo(s2);
            }
        });
        plugins.addAll(loader.getPlugins());
        
        JComponent defaultComponent = null;
        for (InferencePreferencePlugin plugin : plugins) {
            try {
                OWLPreferencesPanel panel = plugin.newInstance();
                panel.initialise();
                final String label = plugin.getLabel();
                final JScrollPane scroller = new JScrollPane(panel);
                scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
                panels.add(panel);
                tabbedPane.addTab(label, scroller);
                if (DisplayedInferencesPreferencePanel.LABEL.equals(label)) {
                	defaultComponent = scroller;
                }
            }
            catch (Throwable e) {
                logger.warn("An error occurred whilst instantiating the preferences panel: {}", e);
            }
        }
        if (defaultComponent != null) { // avoid having the precompute preferences as the default.
        	tabbedPane.setSelectedComponent(defaultComponent);
        }
        add(tabbedPane);
    }

    public void dispose() throws Exception {
        for (OWLPreferencesPanel panel : panels) {
            try {
                panel.dispose();
            }
            catch (Throwable t) {
                logger.warn("An error occurred during disposal of the preferences panel: {}", t);
            }
        }
    }

    @Override
    public void applyChanges() {
        for (OWLPreferencesPanel panel : panels) {
            try {
                panel.applyChanges();
            }
            catch (Throwable t) {
                logger.warn("An error occurred whilst applying changes in the preferences panel {}: {}", panel.getLabel(), t);
            }
        }
    }

}
