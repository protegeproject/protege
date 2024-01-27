package org.protege.editor.owl.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.classexpression.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 24, 2008<br><br>
 */
public class CreateDefinedClassPanel extends JPanel implements VerifiedInputEditor {

    private final OWLEntityCreationPanel<OWLClass> entityCreatePanel;

    private final JRadioButton anonymousButton;

    private final JRadioButton namedButton;

    private List<InputVerificationStatusChangedListener> listeners = new ArrayList<>();


    public CreateDefinedClassPanel(OWLEditorKit eKit) {
        setLayout(new BorderLayout());

        anonymousButton = new JRadioButton("Anonymous class", true);
        anonymousButton.setAlignmentX(0.0f);
        namedButton = new JRadioButton("Named class", !anonymousButton.isSelected());
        namedButton.setAlignmentY(0.0f);


        entityCreatePanel = new OWLEntityCreationPanel<>(eKit, OWLClass.class);
        entityCreatePanel.setEnabled(namedButton.isSelected());
        entityCreatePanel.setAlignmentY(0.0f);
        entityCreatePanel.setBorder(new EmptyBorder(0, 20, 0, 0));


        ActionListener buttonListener = (event) -> {
            if (namedButton.isSelected()) {
                entityCreatePanel.setEnabled(true);
                for (InputVerificationStatusChangedListener l : listeners) {
                    entityCreatePanel.addStatusChangedListener(l);
                }
            }
            else {
                entityCreatePanel.setEnabled(false);
                for (InputVerificationStatusChangedListener l : listeners) {
                    entityCreatePanel.removeStatusChangedListener(l);
                    l.verifiedStatusChanged(true);
                }
            }
        };
        anonymousButton.addActionListener(buttonListener);
        namedButton.addActionListener(buttonListener);


        Box namedPanel = new Box(BoxLayout.LINE_AXIS);
        namedPanel.setAlignmentX(0.0f);
        namedPanel.add(namedButton);
        namedPanel.add(entityCreatePanel);

        ButtonGroup bg = new ButtonGroup();
        bg.add(namedButton);
        bg.add(anonymousButton);

        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.add(anonymousButton);
        box.add(namedPanel);

        add(box, BorderLayout.CENTER);
    }


    private JComponent getDefaultFocusedComponent() {
        return anonymousButton;
    }


    public OWLEntityCreationSet<OWLClass> getEntityCreationSet() {
        if (anonymousButton.isSelected()) {
            return null;
        }
        else {
            return entityCreatePanel.getOWLEntityCreationSet();
        }
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    public static OWLEntityCreationSet<OWLClass> showDialog(OWLClassExpression desc, OWLEditorKit eKit) {
        OWLEntityCreationSet<OWLClass> creationSet = null;

        AnonymousDefinedClassManager adcManager = eKit.getOWLModelManager().get(AnonymousDefinedClassManager.ID);
        if (adcManager != null) {
            CreateDefinedClassPanel panel = new CreateDefinedClassPanel(eKit);
            int ret = JOptionPaneEx.showValidatingConfirmDialog(eKit.getOWLWorkspace().getRootPane(), "Create defined class", panel,
                    JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
                    panel.getDefaultFocusedComponent());

            if (ret == JOptionPane.OK_OPTION) {
                creationSet = panel.getEntityCreationSet();
                if (creationSet == null) {
                    creationSet = adcManager.createAnonymousClass(eKit.getOWLModelManager().getActiveOntology(), desc);
                }
                else {
                    creationSet = appendDefinitionToCreationSet(creationSet, desc, eKit);
                }
            }
        }
        else {
            creationSet = OWLEntityCreationPanel.showDialog(eKit, OWLClass.class);
            if (creationSet != null) {
                appendDefinitionToCreationSet(creationSet, desc, eKit);
            }
        }
        return creationSet;
    }


    private static OWLEntityCreationSet<OWLClass> appendDefinitionToCreationSet(OWLEntityCreationSet<OWLClass> creationSet, OWLClassExpression desc, OWLEditorKit eKit) {
        final OWLClass owlEntity = creationSet.getOWLEntity();
        final OWLAxiom ax = eKit.getOWLModelManager().getOWLDataFactory().getOWLEquivalentClassesAxiom(owlEntity, desc);

        final List<OWLOntologyChange> changes = new ArrayList<>(creationSet.getOntologyChanges());
        changes.add(new AddAxiom(eKit.getOWLModelManager().getActiveOntology(), ax));

        return new OWLEntityCreationSet<>(owlEntity, changes);
    }
}
