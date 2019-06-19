package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLAnnotationPropertyHierarchyProvider;
import org.protege.editor.owl.ui.selector.OWLAnnotationPropertySelectorPanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class OWLAnnotationEditor extends AbstractOWLObjectEditor<OWLAnnotation> implements VerifiedInputEditor {


    private static String lastEditorName = "";



    private final OWLEditorKit owlEditorKit;

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JPanel mainPanel = new VerifiedInputJPanel();

    private final OWLAnnotationPropertySelectorPanel annotationPropertySelector;

    private final List<OWLObjectEditor<? extends OWLAnnotationValue>> editors = new ArrayList<>();

    private final List<InputVerificationStatusChangedListener> verificationListeners = new ArrayList<>();

    private final ChangeListener changeListener = event -> verify();

    private final InputVerificationStatusChangedListener mergedVerificationListener = newState -> {
        for (InputVerificationStatusChangedListener listener : verificationListeners) {
            listener.verifiedStatusChanged(newState);
        }
    };


    private OWLAnnotationProperty lastSelectedProperty;

    private boolean status = false;

    private OWLConstantEditor constantEditor;


    public OWLAnnotationEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        mainPanel.setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPanel.add(splitPane);

        annotationPropertySelector = createAnnotationPropertySelector();

        splitPane.setLeftComponent(annotationPropertySelector);
        splitPane.setRightComponent(tabbedPane);
        splitPane.setBorder(null);
        loadEditors();
        initialiseLastSelectedProperty();

        annotationPropertySelector.addSelectionListener(event -> verify());

        tabbedPane.addChangeListener(changeListener);
    }

    protected final void initialiseLastSelectedProperty() {
    	assert lastSelectedProperty == null; 
        lastSelectedProperty = getDefaultAnnotationProperty(); 
    }

	protected OWLAnnotationProperty getDefaultAnnotationProperty() {
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
		return mngr.getOWLDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());		
	}

    protected OWLAnnotationPropertySelectorPanel createAnnotationPropertySelector() {
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        final OWLAnnotationPropertyHierarchyProvider hp =
                mngr.getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
        OWLAnnotationPropertySelectorPanel selectorPanel = new OWLAnnotationPropertySelectorPanel(owlEditorKit, true, hp);
        selectorPanel.addSelectionListener(e -> {
         updateOverridingDatatype(selectorPanel.getSelectedObject());
        });
        return selectorPanel;
	}


	private void loadEditors() {
        editors.addAll(createEditors());
        int selIndex = 0;
        int tabCount = 0;
        for (OWLObjectEditor<? extends OWLAnnotationValue> editor : editors) {
            String editorTypeName = editor.getEditorTypeName();
            tabbedPane.add(editorTypeName, editor.getEditorComponent());
            if(lastEditorName != null && editorTypeName != null && lastEditorName.equals(editorTypeName)) {
                selIndex = tabCount;
            }
            tabCount++;
        }
        tabbedPane.setSelectedIndex(selIndex);
    }


    protected List<OWLObjectEditor<? extends OWLAnnotationValue>> createEditors() {

        constantEditor = new OWLConstantEditor(owlEditorKit);

        final IRITextEditor textEditor = new IRITextEditor(owlEditorKit);
        textEditor.addStatusChangedListener(mergedVerificationListener);

        final IRIFromEntityEditor iriEditor = new IRIFromEntityEditor(owlEditorKit);
        iriEditor.addSelectionListener(changeListener);

        final OWLAnonymousIndividualAnnotationValueEditor anonIndividualEditor = new OWLAnonymousIndividualAnnotationValueEditor(owlEditorKit);

        List<OWLObjectEditor<? extends OWLAnnotationValue>> result = new ArrayList<>();
        result.add(constantEditor);
        result.add(iriEditor);
        result.add(textEditor);
        result.add(anonIndividualEditor);
		return result;
	}


	protected OWLObjectEditor<? extends OWLAnnotationValue> getSelectedEditor() {
        return editors.get(tabbedPane.getSelectedIndex());
    }

    public boolean setEditedObject(OWLAnnotation annotation) {
        int tabIndex = -1;
        if (annotation != null) {
            annotationPropertySelector.setSelection(annotation.getProperty());
            for (int i = 0; i < editors.size(); i++) {
                OWLObjectEditor<OWLAnnotationValue> editor = getEditorAt(i);
                // because we don't know the type of the editor we need to test
                if (editor.canEdit(annotation.getValue())) {
                    editor.setEditedObject(annotation.getValue());
                    if (tabIndex == -1) {
                        tabIndex = i;
                    }
                }
                else {
                    editor.clear();
                    editor.setEditedObject(null);
                }
            }
            updateOverridingDatatype(annotation.getProperty());
        }
        else {
            annotationPropertySelector.setSelection(lastSelectedProperty);
            for (int i = 0; i < editors.size(); i++) {
                OWLObjectEditor<? extends OWLAnnotationValue> editor = editors.get(i);
                editor.setEditedObject(null);
                editor.clear();
                if(lastEditorName.equals(editor.getEditorTypeName())) {
                    tabIndex = i;
                }
            }
        }
        tabbedPane.setSelectedIndex(tabIndex == -1 ? 0 : tabIndex);
        return true;
    }

    private void updateOverridingDatatype(@Nullable OWLAnnotationProperty property) {
        constantEditor.clearOverridingDatatype();
        if(property == null) {
            return;
        }
        EntitySearcher.getRanges(property,
                                 owlEditorKit.getOWLModelManager().getActiveOntology())
                .stream()
                .filter(OWL2Datatype::isBuiltIn)
                .findFirst()
                .map(OWL2Datatype::getDatatype)
                .map(rng -> rng.getDatatype(owlEditorKit.getOWLModelManager().getOWLDataFactory()))
                .ifPresent(rng -> constantEditor.setOverridingDatatype(rng));
    }

    @SuppressWarnings("unchecked")
    private OWLObjectEditor<OWLAnnotationValue> getEditorAt(int index) {
        return (OWLObjectEditor<OWLAnnotationValue>)editors.get(index);
    }


    public OWLAnnotation getAnnotation() {
        OWLAnnotationProperty property = annotationPropertySelector.getSelectedObject();
        if (property != null){
            lastSelectedProperty = property;
            lastEditorName = getSelectedEditor().getEditorTypeName();

            OWLDataFactory dataFactory = owlEditorKit.getModelManager().getOWLDataFactory();

            OWLAnnotationValue obj = getSelectedEditor().getEditedObject();

            if (obj != null) {
            	return dataFactory.getOWLAnnotation(property, obj);
            }
        }
        return null;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "OWL Annotation";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLAnnotation;
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return mainPanel;
    }

    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    @Nullable
    public OWLAnnotation getEditedObject() {
        return getAnnotation();
    }


    public void dispose() {
        annotationPropertySelector.dispose();
        for (OWLObjectEditor<? extends OWLAnnotationValue> editor : editors) {
            editor.dispose();
        }
    }


    private void verify() {
        if (status != isValid()){
            status = isValid();
            for (InputVerificationStatusChangedListener l : verificationListeners){
                l.verifiedStatusChanged(status);
            }
        }
    }


    private boolean isValid() {
        return annotationPropertySelector.getSelectedObject() != null && getSelectedEditor().getEditedObject() != null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verificationListeners.add(listener);
        listener.verifiedStatusChanged(isValid());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        verificationListeners.remove(listener);
    }
    
    private class VerifiedInputJPanel extends JPanel implements VerifiedInputEditor {
		private static final long serialVersionUID = -6537871629287844213L;

		public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
			OWLAnnotationEditor.this.addStatusChangedListener(listener);
		}

		public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
			OWLAnnotationEditor.this.removeStatusChangedListener(listener);
		}
    	
    }
    
    protected final OWLAnnotationProperty getLastSelectedProperty() {
		return lastSelectedProperty;
	}
    
    protected final OWLAnnotationPropertySelectorPanel getAnnotationPropertySelector() {
		return annotationPropertySelector;
	}
}
