package org.protege.editor.owl.ui.metrics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLAxiomTypeFramePanel;
import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.AxiomCountMetric;
import org.semanticweb.owlapi.metrics.AxiomTypeMetric;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.metrics.OWLMetric;
import org.semanticweb.owlapi.metrics.OWLMetricManager;
import org.semanticweb.owlapi.metrics.ReferencedClassCount;
import org.semanticweb.owlapi.metrics.ReferencedDataPropertyCount;
import org.semanticweb.owlapi.metrics.ReferencedIndividualCount;
import org.semanticweb.owlapi.metrics.ReferencedObjectPropertyCount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

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
 * Date: 29-Oct-2007<br><br>
 */
public class MetricsPanel extends JPanel {

    private Map<String, OWLMetricManager> metricManagerMap;

    private Map<OWLMetricManager, MetricsTableModel> tableModelMap;

    private OWLEditorKit owlEditorKit;

    private JPopupMenu popupMenu;

    private AxiomCountMetric lastMetric;

    public MetricsPanel(OWLEditorKit editorKit) {
        owlEditorKit = editorKit;
        initialiseOWLView();
        createPopupMenu();
    }


    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("Show axioms") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAxiomTypeDialog();

            }
        });
    }


    private void showAxiomTypeDialog() {
        Set<? extends OWLAxiom> axioms = lastMetric.getAxioms();
        final OWLAxiomTypeFramePanel panel = new OWLAxiomTypeFramePanel(owlEditorKit);
        Set<OWLAxiom> axs = new HashSet<OWLAxiom>(axioms);
        panel.setRoot(axs);
        panel.setPreferredSize(new Dimension(800, 300));
        JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = op.createDialog(this, lastMetric.getName());
        dlg.setResizable(true);
        dlg.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                panel.dispose();
            }
        });
        dlg.setModal(false);
        dlg.setVisible(true);
    }


    protected void initialiseOWLView() {
        metricManagerMap = new LinkedHashMap<String, OWLMetricManager>();
        tableModelMap = new HashMap<OWLMetricManager, MetricsTableModel>();
        createBasicMetrics();
        createClassAxiomMetrics();
        createObjectPropertyAxiomMetrics();
        createDataPropertyAxiomMetrics();
        createIndividualAxiomMetrics();
        createAnnotationAxiomMetrics();
        createUI();
        updateView(owlEditorKit.getModelManager().getActiveOntology());
        for(OWLMetricManager man : metricManagerMap.values()) {
            for(OWLMetric m : man.getMetrics()) {
                m.setImportsClosureUsed(true);
                m.setOntology(owlEditorKit.getModelManager().getActiveOntology());
            }
        }
    }


    private void createUI() {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        for (String metricsSet : metricManagerMap.keySet()) {
            MetricsTableModel tableModel = new MetricsTableModel(metricManagerMap.get(metricsSet));
            tableModelMap.put(metricManagerMap.get(metricsSet), tableModel);
            final JTable table = new JTable(tableModel);
            table.setGridColor(Color.LIGHT_GRAY);
            table.setRowHeight(table.getRowHeight() + 4);
            table.setShowGrid(true);
            table.getColumnModel().getColumn(1).setMaxWidth(150);
            table.getColumnModel().setColumnMargin(2);
            table.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        handleTablePopupRequest(table, e);
                    }
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        handleTablePopupRequest(table, e);
                    }
                }

                private void handleTablePopupRequest(JTable table, MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if(row == -1 || col == -1) {
                        return;
                    }
                    MetricsTableModel model = (MetricsTableModel) table.getModel();
                    for(OWLMetricManager man : tableModelMap.keySet()) {
                        if(tableModelMap.get(man).equals(model)) {
                            OWLMetric metric = man.getMetrics().get(row);
                            if(metric instanceof AxiomCountMetric) {
                                lastMetric = (AxiomCountMetric) metric;
                                popupMenu.show(table, e.getX(), e.getY());
                            }
                            break;
                        }
                    }

                }
            });

            final JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }


                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }

                private void showMenu(MouseEvent e) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new AbstractAction("Copy metrics to clipboard") {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            exportCSV();
                        }
                    });
                    menu.show(tablePanel, e.getX(), e.getY());
                }
            });
            tablePanel.add(table);
            tablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 14, 2),
                                                                    ComponentFactory.createTitledBorder(metricsSet)));
            table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            box.add(tablePanel);
        }
        JScrollPane sp = new JScrollPane(box);
        sp.setOpaque(false);
        add(sp);
    }




    private void createBasicMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomCount(getOWLModelManager().getActiveOntology()));
        metrics.add(new LogicalAxiomCount(getOWLModelManager()
                .getActiveOntology()));
        metrics.add(new ReferencedClassCount(getOWLModelManager()
                .getActiveOntology()));
        metrics.add(new ReferencedObjectPropertyCount(getOWLModelManager()
                .getActiveOntology()));
        metrics.add(new ReferencedDataPropertyCount(getOWLModelManager()
                .getActiveOntology()));
        metrics.add(new ReferencedIndividualCount(getOWLModelManager()
                .getActiveOntology()));
        metrics.add(new DLExpressivity(getOWLModelManager().getActiveOntology()));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Metrics", metricManager);
    }


    private void createClassAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.SUBCLASS_OF));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.EQUIVALENT_CLASSES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.DISJOINT_CLASSES));
        metrics.add(new GCICount(getOWLModelManager().getActiveOntology()));
        metrics.add(new HiddenGCICount(getOWLModelManager().getActiveOntology()));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Class axioms", metricManager);
    }


    private void createObjectPropertyAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.SUB_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.EQUIVALENT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.INVERSE_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.DISJOINT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.TRANSITIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.SYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.ASYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.REFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.OBJECT_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.OBJECT_PROPERTY_RANGE));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.SUB_PROPERTY_CHAIN_OF));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Object property axioms", metricManager);
    }


    private void createDataPropertyAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.SUB_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.EQUIVALENT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.DISJOINT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.FUNCTIONAL_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.DATA_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.DATA_PROPERTY_RANGE));        
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Data property axioms", metricManager);
    }


    private void createIndividualAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.CLASS_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(),
                                        AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.SAME_INDIVIDUAL));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.DIFFERENT_INDIVIDUALS));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Individual axioms", metricManager);
    }


    private void createAnnotationAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<OWLMetric<?>>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.ANNOTATION_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.ANNOTATION_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetric(getOWLModelManager()
                .getActiveOntology(), AxiomType.ANNOTATION_PROPERTY_RANGE));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Annotation axioms", metricManager);
    }
    

    public void updateView(OWLOntology activeOntology) {
        for (OWLMetricManager man : metricManagerMap.values()) {
            man.setOntology(activeOntology);
        }
        repaint();
    }

    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }

    private void exportCSV() {
        StringBuilder sb = new StringBuilder();
        for(OWLMetricManager man : metricManagerMap.values()) {
            sb.append(man.toString());
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(sb.toString()), null);
    }
}
