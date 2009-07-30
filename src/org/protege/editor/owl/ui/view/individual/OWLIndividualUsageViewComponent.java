package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLIndividualUsageViewComponent extends AbstractOWLIndividualViewComponent {

    private UsagePanel usagePanel;


    public void initialiseIndividualsView() throws Exception {
        setLayout(new BorderLayout());
        usagePanel = new UsagePanel(getOWLEditorKit());
        add(usagePanel, BorderLayout.CENTER);
    }


    public OWLNamedIndividual updateView(OWLNamedIndividual individual) {
        usagePanel.setOWLEntity(individual);
        return individual;
    }


    public void disposeView() {
    }
}
