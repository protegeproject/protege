package org.protege.editor.owl.ui.metrics;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLAxiomTypeFramePanel;
import org.semanticweb.owl.metrics.*;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
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
        this.owlEditorKit = editorKit;
        initialiseOWLView();
        createPopupMenu();
    }


    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(new AbstractAction("Show axioms") {
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
        updateView(owlEditorKit.getOWLModelManager().getActiveOntology());
        for(OWLMetricManager man : metricManagerMap.values()) {
            for(OWLMetric m : man.getMetrics()) {
                m.setImportsClosureUsed(true);
                m.setOntology(owlEditorKit.getOWLModelManager().getActiveOntology());
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

                public void mousePressed(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        handleTablePopupRequest(table, e);
                    }
                }


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

                public void mousePressed(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }


                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }

                private void showMenu(MouseEvent e) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new AbstractAction("Copy metrics to clipboard") {

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
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new ReferencedClassCount(getOWLModelManager().getOWLOntologyManager()));
        metrics.add(new ReferencedObjectPropertyCount(getOWLModelManager().getOWLOntologyManager()));
        metrics.add(new ReferencedDataPropertyCount(getOWLModelManager().getOWLOntologyManager()));
        metrics.add(new ReferencedIndividualCount(getOWLModelManager().getOWLOntologyManager()));
        metrics.add(new DLExpressivity(getOWLModelManager().getOWLOntologyManager()));
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Metrics", metricManager);
    }


    private void createClassAxiomMetrics() {
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.SUBCLASS));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.EQUIVALENT_CLASSES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.DISJOINT_CLASSES));
        metrics.add(new GCICount(getOWLModelManager().getOWLOntologyManager()));
        metrics.add(new HiddenGCICount(getOWLModelManager().getOWLOntologyManager()));
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Class axioms", metricManager);
    }


    private void createObjectPropertyAxiomMetrics() {
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.SUB_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.EQUIVALENT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.INVERSE_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.DISJOINT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.TRANSITIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.SYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.ANTI_SYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.REFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.OBJECT_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.OBJECT_PROPERTY_RANGE));
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Object property axioms", metricManager);
    }


    private void createDataPropertyAxiomMetrics() {
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.SUB_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.EQUIVALENT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.DISJOINT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.FUNCTIONAL_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.DATA_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.DATA_PROPERTY_RANGE));        
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Data property axioms", metricManager);
    }


    private void createIndividualAxiomMetrics() {
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.CLASS_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(),
                                        AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.SAME_INDIVIDUAL));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.DIFFERENT_INDIVIDUALS));
        OWLMetricManager metricManager = new OWLMetricManager(metrics);
        metricManagerMap.put("Individual axioms", metricManager);
    }


    private void createAnnotationAxiomMetrics() {
        List<OWLMetric> metrics = new ArrayList<OWLMetric>();
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.ENTITY_ANNOTATION));
        metrics.add(new AxiomTypeMetric(getOWLModelManager().getOWLOntologyManager(), AxiomType.AXIOM_ANNOTATION));
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
        return owlEditorKit.getOWLModelManager();
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
