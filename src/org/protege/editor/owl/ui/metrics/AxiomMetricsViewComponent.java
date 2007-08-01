package org.protege.editor.owl.ui.metrics;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.metrics.*;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
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
 * Date: 30-Jul-2007<br><br>
 */
public class AxiomMetricsViewComponent extends AbstractOWLViewComponent {

    private Map<String, OWLMetricManager> metricManagerMap;

    private Map<OWLMetricManager, MetricsTableModel> tableModelMap;

    private boolean update;

    private OWLModelManagerListener listener = new OWLModelManagerListener() {

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                try {
                    updateView(getOWLModelManager().getActiveOntology());
                }
                catch (Exception e) {
                    ProtegeApplication.getErrorLog().logError(e);
                }
            }
        }
    };

    private OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {

        public void ontologiesChanged(List<? extends OWLOntologyChange> list) throws OWLException {
            handleChanges();
        }
    };


    private HierarchyListener hierarchyListener = new HierarchyListener() {

        public void hierarchyChanged(HierarchyEvent e) {
            if (update) {
                updateMetrics();
            }
        }
    };


    protected void initialiseOWLView() throws Exception {
        metricManagerMap = new LinkedHashMap<String, OWLMetricManager>();
        tableModelMap = new HashMap<OWLMetricManager, MetricsTableModel>();
        createBasicMetrics();
        createClassAxiomMetrics();
        createObjectPropertyAxiomMetrics();
        createDataPropertyAxiomMetrics();
        createIndividualAxiomMetrics();
        createAnnotationAxiomMetrics();
        createUI();
        updateMetrics();
        getOWLModelManager().addListener(listener);
        getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
        addHierarchyListener(hierarchyListener);
    }


    private void handleChanges() {
        if (isShowing()) {
            updateMetrics();
        }
        else {
            update = true;
        }
    }


    private void updateMetrics() {
        update = false;
        for (OWLMetricManager man : metricManagerMap.values()) {
            man.setOntology(getOWLModelManager().getActiveOntology());
        }
        repaint();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        for (String metricsSet : metricManagerMap.keySet()) {
            MetricsTableModel tableModel = new MetricsTableModel(metricManagerMap.get(metricsSet));
            tableModelMap.put(metricManagerMap.get(metricsSet), tableModel);
            JTable table = new JTable(tableModel);
            table.setGridColor(Color.LIGHT_GRAY);
            table.setRowHeight(table.getRowHeight() + 4);
            table.setShowGrid(true);
            table.getColumnModel().getColumn(1).setMaxWidth(150);
            table.getColumnModel().setColumnMargin(2);
            JPanel tablePanel = new JPanel(new BorderLayout());
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


    protected void disposeOWLView() {
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
        removeHierarchyListener(hierarchyListener);
    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        for (OWLMetricManager man : metricManagerMap.values()) {
            man.setOntology(activeOntology);
            tableModelMap.get(man).updateMetrics();
        }
    }
}
