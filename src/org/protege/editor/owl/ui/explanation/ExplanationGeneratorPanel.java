package org.protege.editor.owl.ui.explanation;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.net.URI;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.util.InferredAxiomGenerator;
import org.semanticweb.owl.util.InferredSubClassAxiomGenerator;
import org.protege.editor.owl.ui.framelist.OWLFrameList2;
import org.protege.editor.owl.ui.framelist.ExplanationFrameList;
import org.protege.editor.owl.ui.framelist.ConfigurableFrameListExplanationHandler;
import org.protege.editor.owl.ui.frame.*;
import org.protege.editor.owl.ui.view.Copyable;
import org.protege.editor.owl.ui.list.OWLAxiomList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
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

    private String [] subClassAxiomSymbolChoices = new String [] {"\u2192", "subClassOf", "\u2291"};

    private String [] equivalentClassesAxiomSymbolChoices = new String [] {"\u2194", "equivalentTo", "\u2263"};

    private String [] disjointClassesAxiomSymbolChoices = new String [] {"disjointWith", "\u2192 not", "\u2291 \u00AC"};

    private Action cancelAction = new AbstractAction("Stop") {

        public void actionPerformed(ActionEvent e) {
            handleCancel();
        }
    };

    private List<Action> frameSectionRowActions;

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

    private ExplanationFrameList frameList;

    private ExplanationFrame explanationFrame;

    private JComboBox subClassAxiomSymbolCombo;

    private JComboBox equivClassesAxiomSymbolCombo;

    private JCheckBox obfuscateNamesCheckBox;

    public ExplanationGeneratorPanel(OWLAxiom axiom, OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        this.axiom = axiom;
        explanationFrame = new ExplanationFrame(owlEditorKit);
        frameList = new ExplanationFrameList(owlEditorKit);
        explanationFrame = frameList.getFrame();
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
        obfuscateNamesCheckBox = new JCheckBox("Obfuscate entity names", explanationFrame.isObfuscateNames());
        obfuscateNamesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explanationFrame.setObfuscateNames(obfuscateNamesCheckBox.isSelected());
            }
        });
        explanationFrame.setObfuscateNames(obfuscateNamesCheckBox.isSelected());

        JPanel formattingPanel = new JPanel();
        formattingPanel.setLayout(new PreferencesPanelLayoutManager(formattingPanel));
        formattingPanel.add(hightlightUnsatisfiableClasses, null);
        subClassAxiomSymbolCombo = new JComboBox(subClassAxiomSymbolChoices);
        formattingPanel.add(subClassAxiomSymbolCombo, "Subclass axiom symbol");
        subClassAxiomSymbolCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explanationFrame.setSubClassAxiomSymbol(subClassAxiomSymbolCombo.getSelectedItem().toString());
            }
        });
        explanationFrame.setSubClassAxiomSymbol(subClassAxiomSymbolCombo.getSelectedItem().toString());

        equivClassesAxiomSymbolCombo = new JComboBox(equivalentClassesAxiomSymbolChoices);
        formattingPanel.add(equivClassesAxiomSymbolCombo, "Equivalent classes axiom symbol");
        equivClassesAxiomSymbolCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explanationFrame.setEquivalentClassesAxiomSymbol(equivClassesAxiomSymbolCombo.getSelectedItem().toString());
            }
        });
        explanationFrame.setEquivalentClassesAxiomSymbol(equivClassesAxiomSymbolCombo.getSelectedItem().toString());

        final JComboBox disjointClassesAxiomSymbolCombo = new JComboBox(disjointClassesAxiomSymbolChoices);
        formattingPanel.add(disjointClassesAxiomSymbolCombo, "Disjoint classes axiom symbol");
        disjointClassesAxiomSymbolCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                explanationFrame.setDisjointClassesAxiomSymbol(disjointClassesAxiomSymbolCombo.getSelectedItem().toString());                
            }
        });

        formattingPanel.add(obfuscateNamesCheckBox, null);
        final JCheckBox useOrderingCheckBox = new JCheckBox("Use ordering", explanationFrame.isUseOrdering());
        useOrderingCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                explanationFrame.setUseOrdering(useOrderingCheckBox.isSelected());
            }
        });
        formattingPanel.add(useOrderingCheckBox, null);

        JPanel northPanel = new JPanel(new BorderLayout(7, 7));
        add(northPanel, BorderLayout.NORTH);

        northPanel.add(formattingPanel, BorderLayout.WEST);
        JPanel actionPanelHolder = new JPanel(new BorderLayout());
        northPanel.add(actionPanelHolder, BorderLayout.EAST);
        JPanel actionPanel = new JPanel(new GridLayout(2, 1));
        actionPanelHolder.add(actionPanel, BorderLayout.SOUTH);


        Action extractOntologyAction = new AbstractAction("Extract ontology...") {
            public void actionPerformed(ActionEvent e) {
                extractOntology();
            }
        };
        frameList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    updateFrameSectionActions();
                }
            }
        });
        frameSectionRowActions = new ArrayList<Action>();
        frameSectionRowActions.add(extractOntologyAction);

        Action viewJustificationEntailments = new AbstractAction("View justification entailments...") {

            public void actionPerformed(ActionEvent e) {
                showAtomicEntailments();
            }
        };

        actionPanel.add(new JButton(viewJustificationEntailments));
        frameSectionRowActions.add(viewJustificationEntailments);
        updateFrameSectionActions();
        actionPanel.add(new JButton(extractOntologyAction));
    }

    private void showAtomicEntailments() {
        Object selVal = frameList.getSelectedValue();
        if(selVal == null) {
            return;
        }
        ExplanationFrameSection section = null;
        if(selVal instanceof ExplanationFrameSection) {
            section = (ExplanationFrameSection) selVal;
        }
        else if(selVal instanceof ExplanationFrameSectionRow) {
            section = ((ExplanationFrameSectionRow) selVal).getFrameSection();
        }
        if(section == null) {
            return;
        }
        try {
            Set<OWLAxiom> axioms = section.getAxioms();
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ont = manager.createOntology(URI.create("http://another.com/ont"));
            for(OWLAxiom ax : axioms) {
                manager.applyChange(new AddAxiom(ont, ax));
            }
            OWLReasoner reasoner = owlEditorKit.getOWLModelManager().getOWLReasonerManager().createReasoner(manager);
//            AtomicEntailmentsCountMetric metric = new AtomicEntailmentsCountMetric(owlEditorKit.getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory());
           
            AxiomListFrame listFrame = new AxiomListFrame(owlEditorKit);
            OWLFrameList2<Set<OWLAxiom>> frameList2 = new OWLFrameList2<Set<OWLAxiom>>(owlEditorKit, listFrame);
//            Set<OWLAxiom> entailedAxioms = metric.getEntailments(axioms);

//            entailedAxioms.remove(axiom);
//            frameList2.setRootObject(entailedAxioms);
            frameList2.setExplanationHandler(new ConfigurableFrameListExplanationHandler(owlEditorKit, reasoner, ont));
            JScrollPane sp = new JScrollPane(frameList2);
            sp.setPreferredSize(new Dimension(600, 400));
            JOptionPane op = new JOptionPane(sp, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog dlg = op.createDialog(this, "Justification entailments");
            dlg.setModal(false);
            dlg.setResizable(true);
            dlg.setVisible(true);
        }
//        catch(ExplanationMetricException e) {
//            e.printStackTrace();
//        }
        catch(OWLOntologyChangeException e) {
            e.printStackTrace();
        }
        catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    private void extractOntology() {
        Object selVal = frameList.getSelectedValue();
        if(selVal == null) {
            return;
        }
        ExplanationFrameSection section = null;
        if(selVal instanceof ExplanationFrameSection) {
            section = (ExplanationFrameSection) selVal;
        }
        else if(selVal instanceof ExplanationFrameSectionRow) {
            section = ((ExplanationFrameSectionRow) selVal).getFrameSection();
        }
        if(section == null) {
            return;
        }
        section.extractOntology();
    }

    private void updateFrameSectionActions() {
        Object selVal = frameList.getSelectedValue();
        boolean valid = selVal instanceof OWLFrameSection ||
                selVal instanceof OWLFrameSectionRow;
        for(Action action : frameSectionRowActions) {
            action.setEnabled(valid);
        }
    }

    private void handleCancel() {
        cancelAction.setEnabled(false);
        cancelled = true;
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
