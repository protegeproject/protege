package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.UsageTree;
import org.semanticweb.owl.model.OWLDataProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLDataPropertyUsageViewComponent extends AbstractOWLDataPropertyViewComponent {

    private UsageTree tree;


    protected OWLDataProperty updateView(OWLDataProperty property) {
        tree.setOWLEntity(property);
        return property;
    }


    public void initialiseView() throws Exception {
        tree = new UsageTree(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(tree));
    }


    public void disposeView() {
    }
}
