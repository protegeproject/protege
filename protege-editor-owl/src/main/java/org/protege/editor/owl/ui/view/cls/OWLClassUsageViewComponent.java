package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owlapi.model.OWLClass;

import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLClassUsageViewComponent extends AbstractOWLClassViewComponent {

        private UsagePanel usagePanel;


    public void initialiseClassView() throws Exception {
        setLayout(new BorderLayout());
        usagePanel = new UsagePanel(getOWLEditorKit());
        add(usagePanel, BorderLayout.CENTER);
    }


    protected OWLClass updateView(OWLClass selectedClass) {
        usagePanel.setOWLEntity(selectedClass);
        return selectedClass;
    }


    public void disposeView() {
    }
}
