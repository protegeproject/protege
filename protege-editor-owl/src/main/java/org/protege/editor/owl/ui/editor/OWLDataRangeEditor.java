package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDataRangeChecker;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public class OWLDataRangeEditor extends AbstractOWLObjectEditor<OWLDataRange> {

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
        expressionEditor = new ExpressionEditor<>(owlEditorKit, new OWLDataRangeChecker(owlEditorKit.getOWLModelManager()));
        expressionScroller = new JScrollPane(expressionEditor);
        tabbedPane.add("Data range expression", expressionScroller);
        tabbedPane.setPreferredSize(new Dimension(400, 600));
    }


    public boolean setEditedObject(OWLDataRange dataRange){
        expressionEditor.setExpressionObject(dataRange);
        if (dataRange != null && dataRange.isDatatype()){
            datatypeList.setSelection((OWLDatatype)dataRange);
            tabbedPane.setSelectedComponent(datatypeList);
        }
        else{
            datatypeList.setSelection((OWLDatatype)null);
            tabbedPane.setSelectedComponent(expressionScroller);
        }
        return true;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Data Range";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLDataRange;
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return editorPanel;
    }


    @Nullable
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


    public void dispose() {
        datatypeList.dispose();
    }
}
