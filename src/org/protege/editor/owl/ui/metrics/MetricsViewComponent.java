package org.protege.editor.owl.ui.metrics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MetricsViewComponent extends AbstractOWLViewComponent {

    private List<Metric> metrics = new ArrayList<Metric>();

    private Map<Metric, MetricsTreeNode> nodeMap;

    private DefaultMutableTreeNode rootNode;

    private JTree tree;


    protected void initialiseOWLView() throws Exception {

        nodeMap = new IdentityHashMap<Metric, MetricsTreeNode>();
        update();
        rootNode = new DefaultMutableTreeNode();
        tree = new JTree(rootNode);
        setLayout(new BorderLayout());

        Metric parMetric;
        addMetric(parMetric = new ClassCountMetric(), null);
        addMetric(new ImportsClosureClassCountMetric(), parMetric);
        addMetric(parMetric = new ObjectPropertyCount(), null);
        addMetric(new ImportsClosureObjectPropertyCountMetric(), parMetric);
        addMetric(parMetric = new DataPropertyCountMetric(), null);
        addMetric(new ImportsClosureDataPropertyCountMetric(), parMetric);
        addMetric(parMetric = new IndividualCountMetric(), null);
        addMetric(new ImportsClosureIndividualCountMetric(), parMetric);
        tree.expandPath(new TreePath(rootNode.getPath()));
        tree.setRootVisible(false);
        tree.setCellRenderer(new MetricTreeCellRenderer());
        update();
        add(new JScrollPane(tree));
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.setShowsRootHandles(true);
    }


    private void addMetric(Metric metric, Metric parentMetric) {
        metric.setOWLModelManager(getOWLModelManager());
        DefaultMutableTreeNode parentNode;
        if (parentMetric != null) {
            parentNode = nodeMap.get(parentMetric);
        }
        else {
            parentNode = rootNode;
        }
        MetricsTreeNode node = new MetricsTreeNode(metric);
        parentNode.add(node);
        nodeMap.put(metric, node);
    }


    protected void update() {
        for (Metric metric : nodeMap.keySet()) {
            metric.updateMetric();
        }
    }


    protected void disposeOWLView() {
    }


    private class MetricTreeCellRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Object obj = ((DefaultMutableTreeNode) value).getUserObject();
            if (obj instanceof Metric) {
                Metric metric = (Metric) obj;
                String rendering = "<html><body>";
                rendering += metric.getName();
                rendering += ":  <b>";
                rendering += metric.getMetric();
                rendering += "</b>";
                rendering += "</body></html>";
                label.setText(rendering);
                label.setIcon(null);
            }
            return label;
        }
    }
}
