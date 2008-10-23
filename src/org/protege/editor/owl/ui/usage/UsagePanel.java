package org.protege.editor.owl.ui.usage;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsagePanel extends JPanel {


    private UsageTree tree;

    private JCheckBox showAllCheckbox;
    private JCheckBox showDisjointsCheckbox;
    private JCheckBox showNamedSubSuperclassesCheckbox;

    private OWLEntity currentSelection;

    public UsagePanel(OWLEditorKit owlEditorKit) {
        setLayout(new BorderLayout());

        tree = new UsageTree(owlEditorKit);

        showAllCheckbox = new JCheckBox("this", !UsagePreferences.getInstance().isFilterActive(UsageFilter.filterSelf));
        showAllCheckbox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                UsagePreferences.getInstance().setFilterActive(UsageFilter.filterSelf, !showAllCheckbox.isSelected());
                setOWLEntity(currentSelection);
            }
        });

        showDisjointsCheckbox = new JCheckBox("disjoints", !UsagePreferences.getInstance().isFilterActive(UsageFilter.filterDisjoints));
        showDisjointsCheckbox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                UsagePreferences.getInstance().setFilterActive(UsageFilter.filterDisjoints, !showDisjointsCheckbox.isSelected());
                setOWLEntity(currentSelection);
            }
        });

        showNamedSubSuperclassesCheckbox = new JCheckBox("named sub/superclasses", !UsagePreferences.getInstance().isFilterActive(UsageFilter.filterNamedSubsSupers));
        showNamedSubSuperclassesCheckbox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                UsagePreferences.getInstance().setFilterActive(UsageFilter.filterNamedSubsSupers, !showNamedSubSuperclassesCheckbox.isSelected());                                
                setOWLEntity(currentSelection);
            }
        });

        Box box = new Box(BoxLayout.LINE_AXIS);
        box.add(new JLabel("Show: "));
        box.add(showAllCheckbox);
        box.add(showDisjointsCheckbox);
        box.add(showNamedSubSuperclassesCheckbox);

        add(box, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }


    public void setOWLEntity(OWLEntity entity) {
        currentSelection = entity;
        showNamedSubSuperclassesCheckbox.setVisible(entity != null && entity instanceof OWLClass);
        tree.setOWLEntity(entity);
    }
}
