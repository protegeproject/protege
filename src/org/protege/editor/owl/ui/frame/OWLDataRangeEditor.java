package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.selector.DataRangeSelectionPanel;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLException;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class OWLDataRangeEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDataRange> {

    private JPanel editorPanel;

    private JTabbedPane tabbedPane;

    private DataRangeSelectionPanel datatypeListScrollPane;

    private ExpressionEditor<OWLDataRange> expressionEditor;


    public OWLDataRangeEditor(OWLEditorKit owlEditorKit) {
        editorPanel = new JPanel(new BorderLayout(7, 7));

        tabbedPane = new JTabbedPane();
        editorPanel.add(tabbedPane);
        tabbedPane.add("Built in datatypes", datatypeListScrollPane = new DataRangeSelectionPanel(owlEditorKit));
//        expressionEditor = new ExpressionEditor<OWLDataRange>(owlEditorKit, new OWLExpressionChecker<OWLDataRange>() {
//            public void check(String text) throws OWLExpressionParserException, OWLException {
//                editorKit.getModelManager().getOWLDescriptionParser().
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


    public void setEditedObject(OWLDataRange dataRange){
        if (dataRange.isDataType()){
            datatypeListScrollPane.setSelectedObject((OWLDataType)dataRange);
        }
    }


    public JComponent getEditorComponent() {
        return editorPanel;
    }


    public OWLDataRange getEditedObject() {
        if (tabbedPane.getSelectedComponent() == datatypeListScrollPane) {
            return datatypeListScrollPane.getSelectedObject();
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
        datatypeListScrollPane.setSelectedObject(null);
    }


    public void dispose() {
    }
}
