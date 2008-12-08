package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDataRangeChecker;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
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

    private OWLDataTypeSelectorPanel datatypeList;

    private ExpressionEditor<OWLDataRange> expressionEditor;

    private JScrollPane expressionScroller;


    public OWLDataRangeEditor(OWLEditorKit owlEditorKit) {
        editorPanel = new JPanel(new BorderLayout(7, 7));

        tabbedPane = new JTabbedPane();
        editorPanel.add(tabbedPane);
        tabbedPane.add("Built in datatypes", datatypeList = new OWLDataTypeSelectorPanel(owlEditorKit));
        expressionEditor = new ExpressionEditor<OWLDataRange>(owlEditorKit, new OWLDataRangeChecker(owlEditorKit.getOWLModelManager()));
        expressionScroller = new JScrollPane(expressionEditor);
        tabbedPane.add("Data range expression", expressionScroller);
        tabbedPane.setPreferredSize(new Dimension(400, 600));
    }


    public void setEditedObject(OWLDataRange dataRange){
        expressionEditor.setExpressionObject(dataRange);
        if (dataRange.isDataType()){
            datatypeList.setSelection((OWLDataType)dataRange);
            tabbedPane.setSelectedComponent(datatypeList);
        }
        else{
            datatypeList.setSelection((OWLDataType)null);
            tabbedPane.setSelectedComponent(expressionScroller);
        }
    }


    public JComponent getEditorComponent() {
        return editorPanel;
    }


    public OWLDataRange getEditedObject() {
        if (tabbedPane.getSelectedComponent() == datatypeList) {
            return datatypeList.getSelectedObject();
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
        datatypeList.setSelection((OWLDataType)null);
        expressionEditor.setExpressionObject(null);
    }


    public void dispose() {
    }
}
