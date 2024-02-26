package org.protege.editor.owl.ui.view.objectproperty;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyUsageViewComponent extends AbstractOWLObjectPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -2146681987525184036L;
    private UsagePanel usagePanel;


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
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
