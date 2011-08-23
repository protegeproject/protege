package org.protege.editor.owl.ui.editor;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

public class OWLAnnotationPropertyDomainEditor extends AbstractOWLObjectEditor<IRI> {
    private OWLEditorKit editorKit;
    private JTabbedPane tabs;
    private OWLClassSelectorWrapper classSelectionEditor;
    private IRITextEditor iriTextEditor;
    
    public OWLAnnotationPropertyDomainEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        
        classSelectionEditor = new OWLClassSelectorWrapper();
        classSelectionEditor.setup("class.hierarchy", "Class Hierarchy Selection Editor", editorKit);
        classSelectionEditor.initialise();
        iriTextEditor = new IRITextEditor(editorKit);
        tabs = new JTabbedPane();
        tabs.add("Select Class", classSelectionEditor.getComponent());
        tabs.add("Edit raw IRI", iriTextEditor.getEditorComponent());
    }

    public String getEditorTypeName() {
        return "Annotation Property domain Editor";
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLAnnotationProperty;
    }

    public JComponent getEditorComponent() {
        return tabs;
    }

    public IRI getEditedObject() {
        Component component = tabs.getSelectedComponent();
        if (component == classSelectionEditor.getComponent()) {
            for (OWLClassExpression ce : classSelectionEditor.getClassExpressions()) {
                if (!ce.isAnonymous()) {
                    return ce.asOWLClass().getIRI();
                }
            }
        }
        else if (component == iriTextEditor.getEditorComponent()) {
            return iriTextEditor.getEditedObject();
        }
        return null;
    }

    public boolean setEditedObject(IRI editedObject) {
        if (editedObject == null) {
            return false;
        }
        boolean classFound = false;
        OWLDataFactory factory = editorKit.getOWLModelManager().getOWLDataFactory();
        OWLClass cls = factory.getOWLClass(editedObject);
        for (OWLOntology o : editorKit.getOWLModelManager().getActiveOntologies()) {
            if (o.containsEntityInSignature(cls)) {
                classFound = true;
                break;
            }
        }
        if (classFound) {
            classSelectionEditor.setDescription(cls);
        }
        iriTextEditor.setEditedObject(editedObject);
        return true;
    }

    public void dispose() {
        classSelectionEditor.dispose();
        iriTextEditor.dispose();
        classSelectionEditor = null;
        iriTextEditor = null;
    }

}
