package org.protege.editor.owl.ui.view.datatype;

import org.protege.editor.owl.ui.usage.UsagePanel;
import org.semanticweb.owl.model.OWLDatatype;

import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLDataTypeUsageViewComponent extends AbstractOWLDataTypeViewComponent {

    private UsagePanel usagePanel;


    protected OWLDatatype updateView(OWLDatatype property) {
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