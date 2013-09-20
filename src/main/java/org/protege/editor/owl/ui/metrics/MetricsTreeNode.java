package org.protege.editor.owl.ui.metrics;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Feb-2007<br><br>
 */
public class MetricsTreeNode extends DefaultMutableTreeNode {

    protected MetricsTreeNode(Metric metric) {
        super(metric);
    }
}
