package org.protege.editor.owl.model.library.auto;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class RepositoryPreferencesPanel extends OWLPreferencesPanel {
    private JCheckBox useXmlBaseBox;
    private JCheckBox useOntologyNameOrVersionBox;
    
    public void initialise() {
        setLayout(new PreferencesPanelLayoutManager(this));
        add(useXmlBaseBox = new JCheckBox(), "Use XML Base (Recommended)");
        add(useOntologyNameOrVersionBox = new JCheckBox(), "Use Ontology Name/Version");
        useXmlBaseBox.setEnabled(true);
        useOntologyNameOrVersionBox.setEnabled(true);
    }

    @Override
    public void applyChanges() {
        
    }



    public void dispose() {
        // TODO Auto-generated method stub

    }

}
