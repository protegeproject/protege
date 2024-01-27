package org.protege.editor.owl.ui.renderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 8, 2008<br><br>
 */
public class AnnotationRendererPanel extends JPanel {
	private static final long serialVersionUID = -4986709925668660394L;
	private JTable table;
    private JToolBar toolbar;

    private DefaultTableModel model;
    private JTextField languageField;

    private boolean dirty = false;

    private OWLEditorKit eKit;


    private Action addAction = new AbstractAction("Add Annotation", OWLIcons.getIcon("property.annotation.add.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleAddAnnotation();
        }
    };

    private Action removeAction = new AbstractAction("Remove Annotation", OWLIcons.getIcon("property.annotation.remove.png")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleRemoveAnnotation();
        }
    };

    private Action upAction = new AbstractAction("Move Up", Icons.getIcon("object.move_up.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveUp();
        }
    };

    private Action downAction = new AbstractAction("Move Down", Icons.getIcon("object.move_down.gif")){
        public void actionPerformed(ActionEvent actionEvent) {
            handleMoveDown();
        }

    };


    public AnnotationRendererPanel(OWLEditorKit owlEditorKit) {

        this.eKit = owlEditorKit;

        setLayout(new BorderLayout());

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        addToolbarAction(addAction);
        addToolbarAction(removeAction);
        toolbar.addSeparator(new Dimension(6, 6));
        addToolbarAction(upAction);
        addToolbarAction(downAction);

        add(toolbar, BorderLayout.NORTH);

        model = new DefaultTableModel(){
            public boolean isCellEditable(int row, int col) {
                return col > 0;
            }
        };
        model.addTableModelListener(tableModelEvent -> {
            dirty = true;
        });
        model.addColumn("Annotation IRI");

        table = new JTable(model);
        table.setShowVerticalLines(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setWidth(200);
        final JScrollPane scroller = new JScrollPane(table);
        add(scroller, BorderLayout.CENTER);
        
        JPanel languagePanel = new JPanel();
        languagePanel.setLayout(new BoxLayout(languagePanel, BoxLayout.X_AXIS));
        languagePanel.add(new JLabel("Set Language: "));
        languagePanel.add(languageField = new JTextField());
        add(languagePanel,BorderLayout.SOUTH);
        
        load();
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }

    public static boolean showDialog(OWLEditorKit owlEditorKit) {
        AnnotationRendererPanel panel = new AnnotationRendererPanel(owlEditorKit);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                                  "Annotation Renderer",
                                                  panel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  panel);
        if(ret == JOptionPane.OK_OPTION) {
            panel.applyChanges();
            return true;
        }
        return false;
    }

    protected void load(){
        List<IRI> rows = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        List<String> languages = OWLRendererPreferences.getInstance().getAnnotationLangs();
        for (IRI row : rows){
            Object[] rowData = new Object[1];
            rowData[0] = row;
            model.addRow(rowData);
        }
        StringBuilder langsAsString = new StringBuilder();
        for (String lang : languages) {
            if (langsAsString.length() != 0) {
                langsAsString.append(", ");
            }
            if (lang.equals(OWLRendererPreferences.NO_LANGUAGE_SET)) {
                lang = OWLRendererPreferences.NO_LANGUAGE_SET_USER_TOKEN;
            }
            langsAsString.append(lang);
        }
        if (langsAsString.length() > 1){
            languageField.setText(langsAsString.toString());
        }
    }

    protected void applyChanges() {
        if (dirty){
            // @@TODO change this to get annotation properties
            java.util.List<IRI> iris = new ArrayList<>();
            for (int i=0; i<model.getRowCount(); i++){
                IRI iri = (IRI)model.getValueAt(i, 0);
                iris.add(iri);
            }
            String langsAsString = languageField.getText();
            java.util.List<String> langs = new ArrayList<>();
            if (langsAsString != null){
                for (String token : langsAsString.split(",")){
                    token = token.trim();
                    if (token.equals(OWLRendererPreferences.NO_LANGUAGE_SET_USER_TOKEN)){
                        token = OWLRendererPreferences.NO_LANGUAGE_SET;
                    }
                    langs.add(token);
                }
            }
            OWLRendererPreferences.getInstance().setAnnotations(iris);
            OWLRendererPreferences.getInstance().setAnnotationLanguages(langs);
            dirty = false;
        }
    }


    private void addToolbarAction(Action action) {
        JButton button = new JButton(action);
        button.setToolTipText((String)action.getValue(Action.NAME));
        button.setText(null);
        button.setBorder(new EmptyBorder(4, 4, 4, 4));
        toolbar.add(button);
    }


    public void dispose() throws Exception {
        // do nothing
    }

    private void handleAddAnnotation() {
        OWLAnnotationProperty p = new UIHelper(eKit).pickAnnotationProperty();
        if (p != null){
            Object[] rowData = new Object[]{p.getIRI(), null};
            model.addRow(rowData);
            table.getSelectionModel().setSelectionInterval(model.getRowCount()-1, model.getRowCount()-1);
        }
    }


    private void handleRemoveAnnotation() {
        final int row = table.getSelectedRow();
        if (row != -1){
            model.removeRow(row);
            if (row < model.getRowCount()){
                table.getSelectionModel().setSelectionInterval(row, row);
            }
            else if (row-1 > 0){
                table.getSelectionModel().setSelectionInterval(row-1, row-1);
            }
        }
    }

    private void handleMoveUp() {
        final int row = table.getSelectedRow();
        if (row > 0){
            model.moveRow(row, row, row-1);
            table.getSelectionModel().setSelectionInterval(row-1, row-1);
        }
    }

    private void handleMoveDown() {
        final int row = table.getSelectedRow();
        if (row < model.getRowCount()-1){
            model.moveRow(row, row, row+1);
            table.getSelectionModel().setSelectionInterval(row+1, row+1);
        }
    }
}
