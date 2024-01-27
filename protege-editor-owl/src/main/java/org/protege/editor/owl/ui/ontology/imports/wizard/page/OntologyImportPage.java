package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import javax.annotation.Nonnull;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;

public abstract class OntologyImportPage extends AbstractOWLWizardPanel {
    private static final long serialVersionUID = 489018744916640111L;
    private JCheckBox customizeImports;

    public OntologyImportPage(Object id, String title, OWLEditorKit owlEditorKit) {
        super(id, title, owlEditorKit);
    }

    @Nonnull
    public OntologyImportWizard getWizard() {
        return (OntologyImportWizard) super.getWizard();
    }
    
    @Override
    public void aboutToDisplayPanel() {
    	if (customizeImports != null) {
    		customizeImports.setSelected(getWizard().isCustomizeImports());
    	}
        super.aboutToDisplayPanel();
    }
    
    public JComponent createCustomizedImportsComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        customizeImports = new JCheckBox("Manually specify import declarations.");
        panel.add(customizeImports);
        customizeImports.setAlignmentX(LEFT_ALIGNMENT);
        customizeImports.addActionListener(e -> {
            getWizard().setCustomizeImports(customizeImports.isSelected());
        });
        JLabel label1 = new JLabel("This is generally not needed as Protege will choose a reasonable default.");
        label1.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(label1);
        return panel;
    }

}
