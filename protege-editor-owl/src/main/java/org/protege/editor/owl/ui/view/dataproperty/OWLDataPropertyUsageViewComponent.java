package org.protege.editor.owl.ui.view.dataproperty;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLDataPropertyUsageViewComponent extends AbstractOWLDataPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -991724153835087772L;
    private UsagePanel usagePanel;


    protected OWLDataProperty updateView(OWLDataProperty property) {
        usagePanel.setOWLEntity(property);
        return property;
    }


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());
        usagePanel = new UsagePanel(getOWLEditorKit());
        add(usagePanel, BorderLayout.CENTER);
    }


    public void disposeView() {
    }
}
