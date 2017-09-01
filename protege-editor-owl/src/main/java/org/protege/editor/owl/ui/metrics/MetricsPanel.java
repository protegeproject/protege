package org.protege.editor.owl.ui.metrics;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLAxiomTypeFramePanel;
import org.semanticweb.owlapi.metrics.*;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Oct-2007<br><br>
 */
public class MetricsPanel extends JPanel implements Disposable {

    private final Map<String, OWLMetricManager> metricManagerMap = new LinkedHashMap<>();

    private final Map<OWLMetricManager, MetricsTableModel> tableModelMap = new HashMap<>();

    private final OWLOntologyChangeListener ontologyChangeListener = changes -> invalidateMetrics();

    private OWLEditorKit owlEditorKit;

    private final JPopupMenu popupMenu = new JPopupMenu();

    private AxiomCountMetric lastMetric;

    public MetricsPanel(OWLEditorKit editorKit) {
        this.owlEditorKit = editorKit;
        setBackground(Color.WHITE);
        setOpaque(true);
        initialiseOWLView();
        createPopupMenu();
        editorKit.getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
    }

    @Override
    public void dispose() throws Exception {
        owlEditorKit.getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
    }

    private void invalidateMetrics() {
        tableModelMap.values().forEach(MetricsTableModel::invalidate);
    }

    private void createPopupMenu() {
        JMenuItem showAxioms = new JMenuItem("Show axioms");
        popupMenu.add(showAxioms);
        showAxioms.addActionListener(e -> showAxiomTypeDialog());
    }


    private void showAxiomTypeDialog() {
        Set<? extends OWLAxiom> axioms = lastMetric.getAxioms();
        final OWLAxiomTypeFramePanel panel = new OWLAxiomTypeFramePanel(owlEditorKit);
        Set<OWLAxiom> axs = new HashSet<>(axioms);
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
        box.setBackground(Color.WHITE);
        for (String metricsSet : metricManagerMap.keySet()) {
            MetricsTableModel tableModel = new MetricsTableModel(metricManagerMap.get(metricsSet));
            tableModelMap.put(metricManagerMap.get(metricsSet), tableModel);
            final JTable table = new JTable(tableModel);
            table.setGridColor(new Color(240, 240, 240));
            FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
            table.setRowHeight((fontMetrics.getLeading() * 2)
                    + fontMetrics.getMaxAscent()
                    + fontMetrics.getMaxDescent()
                    + 4);
            table.setShowGrid(true);
            table.getColumnModel().getColumn(1).setMaxWidth(150);
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
                            OWLMetric<?> metric = man.getMetrics().get(row);
                            if(metric instanceof AxiomCountMetric) {
                                lastMetric = (AxiomCountMetric) metric;
                                popupMenu.show(table, e.getX(), e.getY());
                            }
                            break;
                        }
                    }

                }
            });
            DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBorder(null);
                    Object metricValue = table.getModel().getValueAt(row, 1);
                    if (metricValue instanceof Integer) {
                        Integer number = (Integer) metricValue;
                        if (number > 0) {
                            if (column == 1) {
                                label.setFont(label.getFont().deriveFont(Font.BOLD));
                            }
                            else {
                                label.setFont(label.getFont().deriveFont(Font.PLAIN));
                            }
                            if (!isSelected) {
                                label.setForeground(Color.BLACK);
                            }
                        }
                        else {
                            label.setFont(label.getFont().deriveFont(Font.PLAIN));
                            label.setForeground(Color.LIGHT_GRAY);
                        }
                    }
                    else {
                        if (!isSelected) {
                            label.setForeground(Color.BLACK);
                        }
                    }
                    return label;
                }
            };
            table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
            table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);

            final JPanel tablePanel = new JPanel(new BorderLayout(3, 3));
            tablePanel.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }


                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
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
            JLabel titleLabel = new JLabel(metricsSet);
            titleLabel.setOpaque(false);
            tablePanel.add(titleLabel, BorderLayout.NORTH);
            tablePanel.setOpaque(false);
            JPanel tableHolder = new JPanel(new BorderLayout());
            tableHolder.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
            tableHolder.add(table);
            tableHolder.setOpaque(false);
            tablePanel.add(tableHolder);
            table.setShowVerticalLines(false);
            table.setShowHorizontalLines(true);
            table.setBackground(Color.WHITE);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(2, 7, 14, 7));
            tablePanel.setOpaque(false);
            box.add(tablePanel);
            box.setOpaque(false);
        }
        JScrollPane sp = new JScrollPane(box);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(Color.WHITE);
        JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
        verticalScrollBar.setBlockIncrement(50);
        verticalScrollBar.setUnitIncrement(50);
        add(sp);
    }

    private OWLOntology getOntology() {
        return owlEditorKit.getModelManager().getActiveOntology();
    }



    private void createBasicMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomCount(getOntology()));
        metrics.add(new LogicalAxiomCount(getOntology()));
        metrics.add(new AxiomTypeMetric(getOntology(), AxiomType.DECLARATION));
        metrics.add(new ReferencedClassCount(getOntology()));
        metrics.add(new ReferencedObjectPropertyCount(getOntology()));
        metrics.add(new ReferencedDataPropertyCount(getOntology()));
        metrics.add(new ReferencedIndividualCount(getOntology()));
        metrics.add(new ReferencedAnnotationPropertyCount(getOntology()));
        metrics.add(new DLExpressivity(getOntology()));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
        metricManagerMap.put("Metrics", metricManager);
    }


    private void createClassAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.SUBCLASS_OF));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.EQUIVALENT_CLASSES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.DISJOINT_CLASSES));
        metrics.add(new GCICount(getOntology()));
        metrics.add(new HiddenGCICount(getOntology()));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
        metricManagerMap.put("Class axioms", metricManager);
    }


    private void createObjectPropertyAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.SUB_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.EQUIVALENT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.INVERSE_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.DISJOINT_OBJECT_PROPERTIES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.TRANSITIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.SYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.ASYMMETRIC_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.REFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.IRREFLEXIVE_OBJECT_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.OBJECT_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.OBJECT_PROPERTY_RANGE));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.SUB_PROPERTY_CHAIN_OF));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
        metricManagerMap.put("Object property axioms", metricManager);
    }


    private void createDataPropertyAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.SUB_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.EQUIVALENT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.DISJOINT_DATA_PROPERTIES));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.FUNCTIONAL_DATA_PROPERTY));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.DATA_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.DATA_PROPERTY_RANGE));        
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
        metricManagerMap.put("Data property axioms", metricManager);
    }


    private void createIndividualAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.CLASS_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(),
                                        AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.SAME_INDIVIDUAL));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.DIFFERENT_INDIVIDUALS));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
        metricManagerMap.put("Individual axioms", metricManager);
    }


    private void createAnnotationAxiomMetrics() {
        List<OWLMetric<?>> metrics = new ArrayList<>();
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.ANNOTATION_ASSERTION));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.ANNOTATION_PROPERTY_DOMAIN));
        metrics.add(new AxiomTypeMetricWrapper(getOntology(), AxiomType.ANNOTATION_PROPERTY_RANGE));
    	/*
    	 * Degenericized to be compatible with changing OWLAPI interfaces
    	 */
        OWLMetricManager metricManager = new OWLMetricManager((List) metrics);
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

    private static class AxiomTypeMetricWrapper extends AxiomTypeMetric {

        private AxiomType<?> type;

        public AxiomTypeMetricWrapper(OWLOntology o, AxiomType<?> axiomType) {
            super(o, axiomType);
            this.type = axiomType;
        }

        @Nonnull
        @Override
        public String getName() {
            return type.getName();
        }
    }
}
