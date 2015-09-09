package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owlapi.metrics.OWLMetric;
import org.semanticweb.owlapi.metrics.OWLMetricManager;

import javax.swing.table.AbstractTableModel;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jul-2007<br><br>
 */
public class MetricsTableModel extends AbstractTableModel {

    private OWLMetricManager metricManager;


    public MetricsTableModel(OWLMetricManager metricManager) {
        this.metricManager = metricManager;
    }


    public int getRowCount() {
        return metricManager.getMetrics().size();
    }


    public int getColumnCount() {
        return 2;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        OWLMetric metric = metricManager.getMetrics().get(rowIndex);
        if (columnIndex == 0) {
            return metric.getName();
        }
        else {
            return metric.getValue().toString();
        }
    }


    public String getColumnName(int column) {
        if (column == 0) {
            return "Metric";
        }
        else {
            return "Value";
        }
    }


    public void includeImports(boolean b) {
        for (OWLMetric metric : metricManager.getMetrics()) {
            metric.setImportsClosureUsed(b);
        }
        fireTableDataChanged();
    }
}
