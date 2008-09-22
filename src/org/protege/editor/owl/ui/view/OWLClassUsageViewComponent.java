package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.usage.UsageTree;
import org.semanticweb.owl.model.OWLClass;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLClassUsageViewComponent extends AbstractOWLClassViewComponent {

    private UsageTree tree;


    public void initialiseClassView() throws Exception {
        tree = new UsageTree(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(tree));
    }


    protected OWLClass updateView(OWLClass selectedClass) {
        tree.setOWLEntity(selectedClass);
        return selectedClass;
    }


    public void disposeView() {
    }
}
