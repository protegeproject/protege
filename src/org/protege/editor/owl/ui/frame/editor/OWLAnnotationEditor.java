package org.protege.editor.owl.ui.frame.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLAnnotationPropertyHierarchyProvider;
import org.protege.editor.owl.ui.selector.OWLAnnotationPropertySelectorPanel;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLAnnotationValue;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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

    private OWLAnnotationPropertySelectorPanel annotationPropertySelector;

    private List<OWLAnnotationValueEditor> editors;

    private OWLAnnotationProperty lastSelectedProperty;

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

        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        final OWLAnnotationPropertyHierarchyProvider hp =
                mngr.getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
        annotationPropertySelector = new OWLAnnotationPropertySelectorPanel(owlEditorKit, true, hp);
        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.add(annotationPropertySelector);
        listHolder.setPreferredSize(new Dimension(200, 300));

        splitPane.setLeftComponent(listHolder);
        splitPane.setRightComponent(tabbedPane);
        splitPane.setBorder(null);
        loadEditors();
        lastSelectedProperty = mngr.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getURI());

        annotationPropertySelector.addSelectionListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event) {
                verify();
            }
        });

        tabbedPane.addChangeListener(changeListener);
    }


    private void loadEditors() {
        final IRIAnnotationValueEditor iriEditor = new IRIAnnotationValueEditor(owlEditorKit);
        iriEditor.addSelectionListener(changeListener);

        final OWLConstantEditor constantEditor = new OWLConstantEditor(owlEditorKit);
        // @@TODO add change listener

        final OWLAnonymousIndividualAnnotationValueEditor anonIndividualEditor = new OWLAnonymousIndividualAnnotationValueEditor(owlEditorKit);
        // @@TODO add change listener

        editors = new ArrayList<OWLAnnotationValueEditor>();
        editors.add(constantEditor);
        editors.add(iriEditor);
        editors.add(anonIndividualEditor);
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
        int tabIndex = -1;
        boolean preferred = false;
        if (annotation != null) {
            annotationPropertySelector.setSelection(annotation.getProperty());
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
            annotationPropertySelector.setSelection(lastSelectedProperty);
            for (OWLAnnotationValueEditor editor : editors) {
                editor.setEditedObject(null);
            }
        }
        tabbedPane.setSelectedIndex(tabIndex == -1 ? 0 : tabIndex);
    }


    public OWLAnnotation getAnnotation() {
        OWLAnnotationProperty property = annotationPropertySelector.getSelectedObject();
        if (property != null){
            lastSelectedProperty = property;

            OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();

            OWLAnnotationValue obj = getSelectedEditor().getEditedObject();

            return dataFactory.getOWLAnnotation(property, obj);
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
        return annotationPropertySelector.getSelectedObject() != null && getSelectedEditor().getEditedObject() != null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verifierListeners.add(listener);
        listener.verifiedStatusChanged(isValid());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verifierListeners.remove(listener);
    }
}
