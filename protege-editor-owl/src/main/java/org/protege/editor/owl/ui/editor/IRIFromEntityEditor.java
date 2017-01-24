package org.protege.editor.owl.ui.editor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel2;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class IRIFromEntityEditor implements OWLObjectEditor<IRI> {

    private OWLEntitySelectorPanel2 entitySelectorPanel;

    private OWLEditorKit eKit;

    private OWLObjectEditorHandler<IRI> handler;


    public IRIFromEntityEditor(OWLEditorKit owlEditorKit) {
        this.eKit = owlEditorKit;
        entitySelectorPanel = new OWLEntitySelectorPanel2(owlEditorKit, false);
    }


    public boolean canEdit(Object object) {
		boolean contained = false;
    	if (object instanceof IRI) {
    		for (OWLOntology ontology : eKit.getModelManager().getActiveOntologies()) {
    			if (ontology.containsEntityInSignature((IRI) object)) {
    				contained = true;
    				break;
    			}
    		}
    	}
        return contained;
    }


    public boolean isPreferred(Object object) {
        return false;
    }


    public void setHandler(OWLObjectEditorHandler<IRI> iriEditorHandler) {
        this.handler = iriEditorHandler;
    }


    public OWLObjectEditorHandler<IRI> getHandler() {
        return handler;
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return entitySelectorPanel;
    }


    @Nullable
    public IRI getEditedObject() {
        final OWLEntity entity = entitySelectorPanel.getSelectedObject();
        return entity != null ? entity.getIRI() : null;
    }


    public Set<IRI> getEditedObjects() {
        IRI selObj = getEditedObject();
        if (selObj != null) {
        	return Collections.singleton(selObj);
        } else {
        	return Collections.emptySet();
        }
    }


    public boolean isMultiEditSupported() {
        return false;
    }


    public void clear() {
        setEditedObject(null);
    }


    public boolean setEditedObject(IRI object) {
        if (object != null){
            final OWLDataFactory df = eKit.getOWLModelManager().getOWLDataFactory();
            for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
                if (ont.containsClassInSignature(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLClass(object)));
                    break;
                }
                else if (ont.containsObjectPropertyInSignature(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLObjectProperty(object)));
                    break;
                }
                else if (ont.containsDataPropertyInSignature(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLDataProperty(object)));
                    break;
                }
                else if (ont.containsIndividualInSignature(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLNamedIndividual(object)));
                    break;
                }
                else if (ont.containsAnnotationPropertyInSignature(object)){
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLAnnotationProperty(object)));
                    break;
                }
            }
        }
        else{
            entitySelectorPanel.setSelection((OWLEntity)null);
        }
        return true;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Entity IRI";
    }


    public JComponent getComponent() {
        return entitySelectorPanel;
    }


    public void dispose() {
        entitySelectorPanel.dispose();
    }


    public void addSelectionListener(ChangeListener listener) {
        entitySelectorPanel.addSelectionListener(listener);
    }


    public void removeSelectionListener(ChangeListener listener) {
        entitySelectorPanel.addSelectionListener(listener);
    }
}
