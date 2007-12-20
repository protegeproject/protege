package org.protege.editor.owl.ui.explanation;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.frame.ExplanationFrame;
import org.protege.editor.owl.ui.frame.ExplanationFrameSectionRow;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.OWLEditorKit;
import com.clarkparsia.explanation.util.ExplanationProgressMonitor;
/*
 * Copyright (C) 2007, University of Manchester
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Nov-2007<br><br>
 */
public class ExplanationGeneratorPanel extends JPanel implements Copyable {

    private OWLEditorKit owlEditorKit;

    private Set<Set<OWLAxiom>> explanations;

    private OWLAxiom axiom;

    private Action cancelAction = new AbstractAction("Cancel") {

        public void actionPerformed(ActionEvent e) {
            handleCancel();
        }
    };

    private boolean cancelled;

    private ExplanationProgressMonitor progressMonitor = new ExplanationProgressMonitor() {

        public boolean isCancelled() {
            return cancelled;
        }


        public void foundExplanation(Set<OWLAxiom> set) {
            handleFoundExplanation(set);
        }


        public void foundAllExplanations() {
            cancelAction.setEnabled(false);
        }

    };

    private OWLFrameList2<OWLAxiom> frameList;

    private ExplanationFrame explanationFrame;

    public ExplanationGeneratorPanel(OWLAxiom axiom, OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        this.axiom = axiom;
        explanationFrame = new ExplanationFrame(owlEditorKit);
        frameList = new OWLFrameList2<OWLAxiom>(owlEditorKit, explanationFrame);
        explanationFrame.setRootObject(null);
        frameList.setWrap(false);
        setLayout(new BorderLayout(7, 7));
        add(new JScrollPane(frameList));
        explanations = new HashSet<Set<OWLAxiom>>();
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JButton(cancelAction), BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        final JCheckBox hightlightUnsatisfiableClasses = new JCheckBox("Highlight unsatisfiable classes");
        hightlightUnsatisfiableClasses.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                frameList.setHighlightUnsatifiableClasses(hightlightUnsatisfiableClasses.isSelected());
                explanationFrame.refill();
            }
        });
        add(hightlightUnsatisfiableClasses, BorderLayout.NORTH);
        add(new JButton(new AbstractAction("Copy to clipboard") {

            public void actionPerformed(ActionEvent e) {
                handleCopy();
            }
        }), BorderLayout.NORTH);



    }

    private void handleCancel() {
        cancelAction.setEnabled(false);
        cancelled = true;
    }


    public Dimension getPreferredSize() {
        return new Dimension(500, 400);
    }


    private void handleFoundExplanation(Set<OWLAxiom> set) {
        explanations.add(set);
        refill();
    }

    private void refill() {
        Runnable r = new Runnable() {

            public void run() {
//                TreeSet<Set<OWLAxiom>> ts = new TreeSet<Set<OWLAxiom>>(explanations);
                explanationFrame.setExplanation(axiom, explanations);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    public ExplanationProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }


    public void addChangeListener(ChangeListener changeListener) {
    }


    public boolean canCopy() {
        return true;
    }


    public List<OWLObject> getObjectsToCopy() {
        List<OWLObject> obj = new ArrayList<OWLObject>();
        for(Object o : frameList.getSelectedValues()) {
            if(o instanceof ExplanationFrameSectionRow) {
                ExplanationFrameSectionRow row = (ExplanationFrameSectionRow) o;
                obj.add(row.getAxiom());
            }
        }
        return obj;
    }


    public void removeChangeListener(ChangeListener changeListener) {
    }

    private void handleCopy() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(Set<OWLAxiom> axs : explanations) {
            count++;
            sb.append("Explanation: ");
            sb.append(count);
            sb.append("  -------------------------------------------------------------\n");
            int axCount = 0;
            for(OWLAxiom ax : axs) {
                axCount++;
                sb.append(axCount);
                sb.append(") ");
                String ren = owlEditorKit.getOWLModelManager().getRendering(ax);
                ren = ren.replace("\n", " ");
                ren = ren.replace("\t", " ");
                ren = ren.replaceAll("\\s+", " ");
                sb.append(ren);
                sb.append("\n");
            }
            sb.append("\n");
            sb.append("\n");
        }
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(new StringSelection(sb.toString()), null);
    }
}
