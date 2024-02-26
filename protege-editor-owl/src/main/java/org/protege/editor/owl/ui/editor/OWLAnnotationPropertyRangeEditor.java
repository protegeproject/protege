package org.protege.editor.owl.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;

public class OWLAnnotationPropertyRangeEditor extends AbstractOWLObjectEditor<IRI> {
    private OWLEditorKit editorKit;
    private JComponent editingComponent;
    private JTabbedPane tabs;
    private OWLClassSelectorWrapper classSelectionEditor;
    private OWLDatatypeSelectionEditor datatypeSelectionEditor;
    private IRITextEditor iriTextEditor;
    
    public OWLAnnotationPropertyRangeEditor(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        
        classSelectionEditor = new OWLClassSelectorWrapper();
        classSelectionEditor.setup("class.hierarchy", "Class Hierarchy Selection Editor", editorKit);
        classSelectionEditor.initialise();
        
        datatypeSelectionEditor = new OWLDatatypeSelectionEditor(editorKit);
        
        iriTextEditor = new IRITextEditor(editorKit);
        
        tabs = new JTabbedPane();
        tabs.addTab("Select Class", classSelectionEditor.getComponent());
        tabs.addTab("Select datatype", datatypeSelectionEditor.getEditorComponent());
        tabs.addTab("Edit raw IRI", iriTextEditor.getEditorComponent());
        
        editingComponent = new JPanel(new BorderLayout());
        editingComponent.setPreferredSize(new Dimension(500, 500));
        editingComponent.add(tabs);
    }

    @Nonnull
    public String getEditorTypeName() {
        return "Annotation Property domain Editor";
    }

    public boolean canEdit(Object object) {
        return object instanceof OWLAnnotationProperty;
    }

    @Nonnull
    public JComponent getEditorComponent() {
        return editingComponent;
    }

    @Nullable
    public IRI getEditedObject() {
        Component component = tabs.getSelectedComponent();
        if (component == classSelectionEditor.getComponent()) {
            for (OWLClassExpression ce : classSelectionEditor.getClassExpressions()) {
                if (!ce.isAnonymous()) {
                    return ce.asOWLClass().getIRI();
                }
            }
        }
        else if (component == datatypeSelectionEditor.getEditorComponent()) {
            OWLDatatype dt = datatypeSelectionEditor.getEditedObject();
            return dt != null ? dt.getIRI() : null;
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
        OWLDataFactory factory = editorKit.getOWLModelManager().getOWLDataFactory();
        OWLClass cls = factory.getOWLClass(editedObject);
        OWLDatatype dt = factory.getOWLDatatype(editedObject);
        
        boolean classFound = cls.isBuiltIn();
        boolean datatypeFound = dt.isBuiltIn();
        
        for (OWLOntology o : editorKit.getOWLModelManager().getActiveOntologies()) {
            if (o.containsEntityInSignature(cls)) {
                classFound = true;
            }
            if (o.containsEntityInSignature(dt)) {
                datatypeFound = true;
            }
            if (classFound && datatypeFound) {
                break;
            }
        }
        if (classFound) {
            classSelectionEditor.setDescription(cls);
        }
        if (datatypeFound) {
            datatypeSelectionEditor.setEditedObject(dt);
        }
        iriTextEditor.setEditedObject(editedObject);
        return true;
    }

    public void dispose() {
        if (classSelectionEditor != null) { // protect against a double dispose!
            classSelectionEditor.dispose();
            iriTextEditor.dispose();
            datatypeSelectionEditor.dispose();
            classSelectionEditor = null;
            iriTextEditor = null;
            datatypeSelectionEditor = null;
        }
    }

}
