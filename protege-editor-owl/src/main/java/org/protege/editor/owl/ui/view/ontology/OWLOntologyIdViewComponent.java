package org.protege.editor.owl.ui.view.ontology;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 06/02/2012
 */
public class OWLOntologyIdViewComponent extends AbstractOWLViewComponent {

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        JPanel ontologyIRIPanel = new JPanel(new GridBagLayout());
        add(ontologyIRIPanel, BorderLayout.NORTH);
        Insets insets = new Insets(0, 0, 0, 0);
        ontologyIRIPanel.add(new JLabel("Ontology IRI"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets(), 0, 0));
        ontologyIRIPanel.add(new JTextField(), new GridBagConstraints(1, 0, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));



        ontologyIRIPanel.add(new JLabel("Version IRI"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets(), 0, 0));
        ontologyIRIPanel.add(new JTextField(), new GridBagConstraints(1, 1, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));


    }

    @Override
    protected void disposeOWLView() {
    }
}
