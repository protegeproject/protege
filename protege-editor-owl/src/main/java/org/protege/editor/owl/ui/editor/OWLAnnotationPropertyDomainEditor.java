package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;

public class OWLAnnotationPropertyDomainEditor extends AbstractOWLObjectEditor<IRI> {
	
    private IRITextEditor iriTextEditor;

    private JComponent editingComponent;
    
	private JTabbedPane tabs;
	
	private OWLEditorKit editorKit;
    
    private OWLClassSelectorWrapper classSelectionEditor;
    
    public OWLAnnotationPropertyDomainEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        
        classSelectionEditor = new OWLClassSelectorWrapper();
        classSelectionEditor.setup("class.hierarchy", "Class Hierarchy Selection Editor", editorKit);
        classSelectionEditor.initialise();
        
        iriTextEditor = new IRITextEditor(editorKit);
        
        tabs = new JTabbedPane();
        tabs.addTab("Select Class", classSelectionEditor.getComponent());
        tabs.addTab("Edit raw IRI", iriTextEditor.getEditorComponent());
        
        editingComponent = new JPanel(new BorderLayout());
        editingComponent.setPreferredSize(new Dimension(500, 500));
        editingComponent.add(tabs);
    }
    
    public String getEditorTypeName() {
        return "Annotation Property domain Editor";
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLAnnotationProperty;
    }

    public JComponent getEditorComponent() {
        return editingComponent;
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
