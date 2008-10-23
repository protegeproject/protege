package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owl.model.OWLIndividual;

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


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        usagePanel.setOWLEntity(selelectedIndividual);
        return selelectedIndividual;
    }


    public void disposeView() {
    }
}
