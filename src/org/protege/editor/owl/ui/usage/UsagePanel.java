package org.protege.editor.owl.ui.usage;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsagePanel extends JPanel {


    private UsageTree tree;


    public UsagePanel(OWLEditorKit owlEditorKit) {
        setLayout(new BorderLayout());
        tree = new UsageTree(owlEditorKit);
        add(new JScrollPane(tree));
    }


    public void setOWLEntity(OWLEntity entity) {
        tree.setOWLEntity(entity);
    }
}
