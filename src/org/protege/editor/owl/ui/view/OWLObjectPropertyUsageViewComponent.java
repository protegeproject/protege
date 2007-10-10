package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.UsageTree;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLObjectPropertyUsageViewComponent extends AbstractOWLObjectPropertyViewComponent {

    private UsageTree tree;


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
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
