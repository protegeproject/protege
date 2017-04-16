package org.protege.editor.owl.ui.explanation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class ExplanationPreferencesGeneralPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -3354987384223578780L;

	@Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);

        panel.addGroup("Installed explanation services");
        DefaultListModel<String> pluginModel = new DefaultListModel<>();
        ExplanationManager manager = new ExplanationManager(getOWLEditorKit());
        Collection<ExplanationService> services = manager.getExplainers();
        for (ExplanationService service : services)
            pluginModel.addElement(service.getName());
        JList<String> pluginList = new JList<>(pluginModel);
        pluginList.setToolTipText("Plugins that provide explanation facilities");
        JScrollPane pluginInfoScrollPane = new JScrollPane(pluginList);
        pluginInfoScrollPane.setPreferredSize(new Dimension(300, 100));
        panel.addGroupComponent(pluginInfoScrollPane);
    }

    @Override
    public void dispose() throws Exception {
    }

    @Override
    public void applyChanges() {
    }
}
