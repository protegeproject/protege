package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.vocab.XSDVocabulary;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class OWLDataRangeEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDataRange> {

    private OWLEditorKit editorKit;

    private JPanel editorPanel;

    private JTabbedPane tabbedPane;

    private JList datatypeList;

    private JScrollPane datatypeListScrollPane;

    private ExpressionEditor<OWLDataRange> expressionEditor;


    public OWLDataRangeEditor(OWLEditorKit owlEditorKit) {
        editorPanel = new JPanel(new BorderLayout(7, 7));
        datatypeList = new JList();
        this.editorKit = owlEditorKit;
        fillDatatypeList();
        datatypeList.setCellRenderer(new OWLCellRenderer(owlEditorKit));

        tabbedPane = new JTabbedPane();
        editorPanel.add(tabbedPane);
        tabbedPane.add("Built in datatypes", datatypeListScrollPane = new JScrollPane(datatypeList));
//        expressionEditor = new ExpressionEditor<OWLDataRange>(owlEditorKit, new OWLExpressionChecker<OWLDataRange>() {
//            public void check(String text) throws OWLExpressionParserException, OWLException {
//                editorKit.getOWLModelManager().getOWLDescriptionParser().
//            }
//
//
//            public OWLDataRange createObject(String text) throws OWLExpressionParserException, OWLException {
//                return null;
//            }
//        });
//        tabbedPane.add("Data range expression", expressionEditor);
        tabbedPane.setPreferredSize(new Dimension(400, 600));
    }


    private void fillDatatypeList() {
        List<OWLDataType> datatypes = new ArrayList<OWLDataType>();
        for (URI uri : new TreeSet<URI>(XSDVocabulary.ALL_DATATYPES)) {
            datatypes.add(editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory().getOWLDataType(uri));
        }
        datatypeList.setListData(datatypes.toArray());
        datatypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setEditedObject(OWLDataRange dataRange){
        if (dataRange == null){
            datatypeList.clearSelection();
        }
        else if (dataRange.isDataType() &&
            XSDVocabulary.ALL_DATATYPES.contains(((OWLDataType)dataRange).getURI())){
            datatypeList.setSelectedValue(dataRange, true);
        }
    }

    public JComponent getEditorComponent() {
        return editorPanel;
    }


    public OWLDataRange getEditedObject() {
        if (tabbedPane.getSelectedComponent() == datatypeListScrollPane) {
            return (OWLDataType) datatypeList.getSelectedValue();
        }
        else {
            if (expressionEditor.isWellFormed()) {
                try {
                    return expressionEditor.createObject();
                }
                catch (OWLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                return null;
            }
        }
    }


    public void clear() {
        datatypeList.clearSelection();
    }


    public void dispose() {
    }
}
