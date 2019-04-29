package org.protege.editor.owl.ui.ontology.wizard.move.common;

import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.core.ui.util.CheckList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.FormLabel;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public class SignatureDependentSelectionPreviewPanel extends MoveAxiomsKitConfigurationPanel {

    private SignatureSelection signatureSelection;

    private OWLObjectList<OWLAxiom> previewList;

    private FormLabel previewLabel;

    private RemovableObjectList<OWLEntity> signatureList;

    private CheckList list;

    private Timer previewTimer;


    public SignatureDependentSelectionPreviewPanel(SignatureSelection signatureSelection) {
        this.signatureSelection = signatureSelection;
        previewLabel = new FormLabel("Axioms: ");
    }


    public void initialise() {
        setLayout(new BorderLayout(7, 7));


        JPanel previewPanel = new JPanel(new BorderLayout(3, 3));
        previewLabel = new FormLabel("Axioms: Computing... ");
        previewPanel.add(previewLabel, BorderLayout.NORTH);
        previewList = new OWLObjectList<>(getEditorKit());
        previewPanel.add(new JScrollPane(previewList));

        add(previewPanel);
        OWLCellRenderer cellRenderer = new OWLCellRenderer(getEditorKit());
        cellRenderer.setWrap(false);
        cellRenderer.setHighlightKeywords(true);
        previewList.setCellRenderer(cellRenderer);


        signatureList = new RemovableObjectList<>();
        signatureList.setCellRenderer(cellRenderer);

        signatureList.setPreferredSize(new Dimension(300, 300));
        JPanel signatureListPanel = new JPanel(new BorderLayout(3, 3));
        list = new CheckList(signatureList);
        list.addCheckListListener(new CheckList.CheckListListener() {

            public void itemChecked(Object item) {
                updatePreview();
            }


            public void itemUnchecked(Object item) {
                updatePreview();
            }
        });
        signatureListPanel.add(new JScrollPane(list));
        signatureListPanel.add(new FormLabel("Signature"), BorderLayout.NORTH);
        add(signatureListPanel, BorderLayout.WEST);

        signatureList.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                updateSignature();
            }
        });

        previewTimer = new Timer(500, e -> {
            doPreviewUpdate();
        });
        previewTimer.setRepeats(false);
    }


    public void updateSignature() {
        Set<OWLEntity> sig = signatureSelection.getSignature();
        Set<OWLEntity> newSig = new HashSet<>(getCheckedEntities());
        if (!sig.equals(newSig)) {
            signatureSelection.setSignature(newSig);
            updatePreview();
        }
    }


    public String getInstructions() {
        return "Confirm the signature";
    }


    public void updatePreview() {
        // Slight delay
        previewTimer.restart();
    }


    private void doPreviewUpdate() {
        previewLabel.setText("Axioms: Computing... ");
        previewLabel.repaint();
        previewList.setListData(new OWLAxiom[0]);
        final Set<OWLEntity> entities = getCheckedEntities();

        final Set<OWLOntology> sourceOntologies = getModel().getSourceOntologies();

        Runnable runnable = () -> {
            final Set<OWLAxiom> axioms = signatureSelection.getAxioms(sourceOntologies, entities);
            final java.util.List<OWLAxiom> axs = new ArrayList<>(new TreeSet<>(axioms));
            final int upperBound = 500 > axs.size() ? axs.size() : 500;


            SwingUtilities.invokeLater(() -> previewLabel.setText("Axioms (showing " + upperBound + " out of " + axioms.size() + " axioms)"));

            SwingUtilities.invokeLater(() -> previewList.setListData(axs.subList(0, upperBound).toArray(new OWLAxiom[axs.size()])));
        };
        Thread t = new Thread(runnable);
        t.start();
    }


    private Set<OWLEntity> getCheckedEntities() {
        final Set<OWLEntity> entities = new HashSet<>();
        for (Object o : list.getCheckedItems()) {
            RemovableObjectList<OWLEntity>.RemovableObjectListItem item = (RemovableObjectList.RemovableObjectListItem) o;
            entities.add(item.getObject());
        }
        return entities;
    }


    public void dispose() {
    }


    public String getID() {
        return "modularity.selectlocalitytype";
    }


    public String getTitle() {
        return "Module preview page";
    }


    public void update() {
        Set<OWLEntity> sig = signatureSelection.getSignature();
        signatureList.setListData(sig.toArray());
        updatePreview();
    }


    public void commit() {
        signatureSelection.setSignature(getCheckedEntities());
    }
}
