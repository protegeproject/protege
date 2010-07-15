package org.protege.editor.owl.ui.inference;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

/*
 * WARNING - This code was liberally stolen from PreferencesDialogPanel
 */

public class TabbedReasonerPreferencesPanel extends OWLPreferencesPanel {
    private static final long serialVersionUID = 5167874018417496809L;
    public static final String LABEL = "Reasoner";
    
    private List<OWLPreferencesPanel> panels = new ArrayList<OWLPreferencesPanel>();

    public void initialise() throws Exception {
        setLayout(new BorderLayout());
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
        for (InferencePreferencePlugin plugin : plugins) {
            try {
                OWLPreferencesPanel panel = plugin.newInstance();
                panel.initialise();
                final String label = plugin.getLabel();
                final JScrollPane scroller = new JScrollPane(panel);
                scroller.setBorder(new EmptyBorder(0, 0, 0, 0));
                panels.add(panel);
                tabbedPane.addTab(label, scroller);
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void dispose() throws Exception {
        for (OWLPreferencesPanel panel : panels) {
            try {
                panel.dispose();
            }
            catch (Throwable t) {
                ProtegeApplication.getErrorLog().logError(t);
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
                ProtegeApplication.getErrorLog().logError(t);
            }
        }
    }

}
