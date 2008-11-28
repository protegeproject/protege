package org.protege.editor.owl.ui.ontology.wizard.move.common;

import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;
import org.protege.editor.owl.ui.list.OWLObjectList;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.core.ui.util.CheckList;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLAxiom;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public class SignatureDependentSelectionPreviewPanel extends MoveAxiomsKitConfigurationPanel {

    private SignatureSelection signatureSelection;

    private OWLObjectList previewList;

    private JLabel previewLabel;

    private RemovableObjectList<OWLEntity> signatureList;

    private CheckList list;

    private Timer previewTimer;


    public SignatureDependentSelectionPreviewPanel(SignatureSelection signatureSelection) {
        this.signatureSelection = signatureSelection;
        previewLabel = new JLabel("Axioms: ");
    }


    public void initialise() {
        setLayout(new BorderLayout(7, 7));


        JPanel previewPanel = new JPanel(new BorderLayout(3, 3));
        previewLabel = new JLabel("Axioms: Computing... ");
        previewPanel.add(previewLabel, BorderLayout.NORTH);
        previewList = new OWLObjectList(getEditorKit());
        previewPanel.add(new JScrollPane(previewList));
        previewPanel.setBorder(ComponentFactory.createTitledBorder("Preview"));

        add(previewPanel);
        OWLCellRenderer cellRenderer = new OWLCellRenderer(getEditorKit());
        cellRenderer.setWrap(false);
        cellRenderer.setHighlightKeywords(true);
        previewList.setCellRenderer(cellRenderer);


        signatureList = new RemovableObjectList<OWLEntity>();
        signatureList.setCellRenderer(cellRenderer);

        signatureList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                }
            }
        });
        signatureList.setPreferredSize(new Dimension(300, 300));
        JPanel signatureListPanel = new JPanel(new BorderLayout());
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
        signatureListPanel.setBorder(ComponentFactory.createTitledBorder("Signature"));
        add(signatureListPanel, BorderLayout.WEST);

        signatureList.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                updateSignature();
            }
        });

        previewTimer = new Timer(1200, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doPreviewUpdate();
            }
        });
        previewTimer.setRepeats(false);
    }


    public void updateSignature() {
        Set<OWLEntity> sig = signatureSelection.getSignature();
        Set<OWLEntity> newSig = new HashSet<OWLEntity>(getCheckedEntities());
        if (!sig.equals(newSig)) {
            signatureSelection.setSignature(newSig);
            updatePreview();
        }
    }


    public String getInstructions() {
        return "Confirm the signature for computing the module";
    }


    public void updatePreview() {
        // Slight delay
        previewTimer.restart();
    }


    private void doPreviewUpdate() {
        previewLabel.setText("Axioms: Computing... ");
        previewLabel.repaint();
        previewList.setListData(new Object[0]);
        final Set<OWLEntity> entities = getCheckedEntities();

        final Set<OWLOntology> sourceOntologies = getModel().getSourceOntologies();

        Runnable runnable = new Runnable() {
            public void run() {
                final Set<OWLAxiom> axioms = signatureSelection.getAxioms(sourceOntologies, entities);
                final java.util.List<OWLAxiom> axs = new ArrayList<OWLAxiom>(new TreeSet<OWLAxiom>(axioms));
                final int upperBound = 500 > axs.size() ? axs.size() : 500;
                System.out.println("Updating for sig: " + entities);
                Runnable runnable = new Runnable() {
                    public void run() {
                        System.out.println("Filling list");
                        previewLabel.setText("Axioms (showing " + upperBound + " out of " + axioms.size() + " in module)");
                    }
                };


                SwingUtilities.invokeLater(runnable);

                System.out.println(axioms.size());

                Runnable runnable2 = new Runnable() {
                    public void run() {

                        previewList.setListData(axs.subList(0, upperBound).toArray());
                        System.out.println("Done");
                    }
                };
                SwingUtilities.invokeLater(runnable2);
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }


    private Set<OWLEntity> getCheckedEntities() {
        final Set<OWLEntity> entities = new HashSet<OWLEntity>();
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