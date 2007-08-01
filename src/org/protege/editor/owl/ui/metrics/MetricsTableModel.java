package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owl.metrics.OWLMetric;
import org.semanticweb.owl.metrics.OWLMetricManager;

import javax.swing.table.AbstractTableModel;
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


    public void updateMetrics() {
        boolean updated = false;
        for (OWLMetric metric : metricManager.getMetrics()) {
//            if(metric.isDirty()) {
            metric.recomputeMetric();
            updated = true;
//            }
        }
        if (updated) {
            fireTableDataChanged();
        }
    }
}
