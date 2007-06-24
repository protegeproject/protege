package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.protege.editor.owl.ui.UsageTree;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLIndividualUsageViewComponent extends AbstractOWLIndividualViewComponent {

    private UsageTree tree;


    public void initialiseIndividualsView() throws Exception {
        tree = new UsageTree(getOWLEditorKit());
        setLayout(new BorderLayout());
        add(new JScrollPane(tree));
    }


    protected OWLIndividual updateView(OWLIndividual selelectedIndividual) {
        tree.setOWLEntity(selelectedIndividual);
        return selelectedIndividual;
    }


    public void disposeView() {
    }
}
