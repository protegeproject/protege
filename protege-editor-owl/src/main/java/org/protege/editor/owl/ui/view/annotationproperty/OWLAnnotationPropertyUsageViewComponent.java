package org.protege.editor.owl.ui.view.annotationproperty;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLAnnotationPropertyUsageViewComponent extends AbstractOWLAnnotationPropertyViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -1722772786122689207L;
    private UsagePanel usagePanel;


    protected OWLAnnotationProperty updateView(OWLAnnotationProperty property) {
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