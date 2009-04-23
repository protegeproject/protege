package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 10-Feb-2007<br><br>
 */
public class OWLAnnotationEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLAnnotation> implements VerifiedInputEditor {


    private OWLEditorKit owlEditorKit;

    private JTabbedPane tabbedPane;

    private JPanel mainPanel;

    private AnnotationURIList uriList;

    private List<OWLAnnotationValueEditor> editors;

    private URI lastSelectedURI;

    private List<InputVerificationStatusChangedListener> verifierListeners = new ArrayList<InputVerificationStatusChangedListener>();

    private boolean status = false;

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            verify();
        }
    };


    public OWLAnnotationEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        tabbedPane = new JTabbedPane();
        mainPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPanel.add(splitPane);

        uriList = new AnnotationURIList(owlEditorKit);
        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.add(new JScrollPane(uriList));
        listHolder.setPreferredSize(new Dimension(200, 300));

        splitPane.setLeftComponent(listHolder);
        splitPane.setRightComponent(tabbedPane);
        splitPane.setBorder(null);
        loadEditors();
        lastSelectedURI = OWLRDFVocabulary.RDFS_COMMENT.getURI();

        uriList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                verify();
            }
        });

        tabbedPane.addChangeListener(changeListener);
    }


    private void loadEditors() {
        final OWLIndividualAnnotationValueEditor individualValueEditor = new OWLIndividualAnnotationValueEditor(owlEditorKit);
        individualValueEditor.addSelectionListener(changeListener);

        editors = new ArrayList<OWLAnnotationValueEditor>();
        editors.add(new OWLConstantEditor(owlEditorKit));
        editors.add(individualValueEditor);
// @@TODO v3 port
//        editors.add(new OWLAnonymousIndividualAnnotationValueEditor(owlEditorKit));
        for (OWLAnnotationValueEditor editor : editors) {
            tabbedPane.add(editor.getEditorTypeName(), editor.getComponent());
        }
    }


    private OWLAnnotationValueEditor getSelectedEditor() {
        return editors.get(tabbedPane.getSelectedIndex());
    }


    public void clear() {
        setAnnotation(null);
    }


    public void setAnnotation(OWLAnnotation annotation) {
        uriList.rebuildAnnotationURIList();
        int tabIndex = -1;
        boolean preferred = false;
        if (annotation != null) {
            uriList.setSelectedURI(annotation.getProperty().getURI());
            for (int i = 0; i < editors.size(); i++) {
                OWLAnnotationValueEditor editor = editors.get(i);
                if (editor.canEdit(annotation.getValue())) {
                    editor.setEditedObject(annotation.getValue());
                    if (tabIndex == -1) {
                        tabIndex = i;
                    }
                    else if (preferred == false) {
                        tabIndex = i;
                    }
                }
                else {
                    editor.setEditedObject(null);
                }
            }
        }
        else {
            uriList.setSelectedURI(lastSelectedURI);
            for (int i = 0; i < editors.size(); i++) {
                OWLAnnotationValueEditor editor = editors.get(i);
                editor.setEditedObject(null);
            }
        }
        tabbedPane.setSelectedIndex(tabIndex == -1 ? 0 : tabIndex);
    }


    public OWLAnnotation getAnnotation() {
        URI uri = uriList.getSelectedURI();
        if (uri != null){
            lastSelectedURI = uriList.getSelectedURI();
            if (lastSelectedURI == null) {
                lastSelectedURI = OWLRDFVocabulary.RDFS_COMMENT.getURI();
            }
            OWLAnnotationValueEditor editor = getSelectedEditor();
            Object obj = editor.getEditedObject();
// @@TODO v3 port
//            if (obj instanceof OWLLiteral) {
//                OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
//                return dataFactory.getRDFOWLConstantAnnotation(uri, (OWLLiteral) obj);
//            }
//            else if (obj instanceof OWLIndividual) {
//                OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
//                return dataFactory.getOWLObjectAnnotation(uri, (OWLIndividual) obj);
//            }
//            else {
//                OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();
//                return dataFactory.getOWLConstantAnnotation(uri, dataFactory.getRDFTextLiteral(obj.toString()));
//            }
        }
        return null;
    }


    public JComponent getEditorComponent() {
        return mainPanel;
    }


    public JComponent getInlineEditorComponent() {
        return getEditorComponent();
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLAnnotation getEditedObject() {
        return getAnnotation();
    }


    public void dispose() {
        for (OWLAnnotationValueEditor editor : editors) {
            editor.dispose();
        }
    }


    private void verify() {
        if (status != isValid()){
            status = isValid();
            for (InputVerificationStatusChangedListener l : verifierListeners){
                l.verifiedStatusChanged(status);
            }
        }
    }


    private boolean isValid() {
        return uriList.getSelectedURI() != null && getSelectedEditor().getEditedObject() != null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verifierListeners.add(listener);
        listener.verifiedStatusChanged(isValid());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verifierListeners.remove(listener);
    }
}
