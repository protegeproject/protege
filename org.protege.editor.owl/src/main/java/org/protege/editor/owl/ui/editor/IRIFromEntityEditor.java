package org.protege.editor.owl.ui.editor;

import java.util.Collections;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Aug-2007<br><br>
 */
public class IRIFromEntityEditor implements OWLObjectEditor<IRI> {

    private OWLEntitySelectorPanel entitySelectorPanel;

    private OWLEditorKit eKit;

    private OWLObjectEditorHandler<IRI> handler;


    public IRIFromEntityEditor(OWLEditorKit owlEditorKit) {
        eKit = owlEditorKit;
        entitySelectorPanel = new OWLEntitySelectorPanel(owlEditorKit, false);
    }


    @Override
    public boolean canEdit(Object object) {
		boolean contained = false;
    	if (object instanceof IRI) {
    		for (OWLOntology ontology : eKit.getModelManager().getActiveOntologies()) {
                if (ontology.containsEntityInSignature((IRI) object,
                        Imports.EXCLUDED)) {
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


    @Override
    public void setHandler(OWLObjectEditorHandler<IRI> iriEditorHandler) {
        handler = iriEditorHandler;
    }


    @Override
    public OWLObjectEditorHandler<IRI> getHandler() {
        return handler;
    }


    @Override
    public JComponent getEditorComponent() {
        return entitySelectorPanel;
    }


    @Override
    public IRI getEditedObject() {
        final OWLEntity entity = entitySelectorPanel.getSelectedObject();
        return entity != null ? entity.getIRI() : null;
    }


    @Override
    public Set<IRI> getEditedObjects() {
        IRI selObj = getEditedObject();
        if (selObj != null) {
        	return Collections.singleton(selObj);
        } else {
        	return Collections.emptySet();
        }
    }


    @Override
    public boolean isMultiEditSupported() {
        return false;
    }


    @Override
    public void clear() {
        setEditedObject(null);
    }


    @Override
    public boolean setEditedObject(IRI object) {
        if (object != null){
            final OWLDataFactory df = eKit.getOWLModelManager().getOWLDataFactory();
            for (OWLOntology ont : eKit.getOWLModelManager().getActiveOntologies()){
                if (ont.containsClassInSignature(object, Imports.EXCLUDED)) {
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLClass(object)));
                    break;
                }
 else if (ont.containsObjectPropertyInSignature(object,
                        Imports.EXCLUDED)) {
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLObjectProperty(object)));
                    break;
                }
 else if (ont.containsDataPropertyInSignature(object,
                        Imports.EXCLUDED)) {
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLDataProperty(object)));
                    break;
                }
 else if (ont.containsIndividualInSignature(object,
                        Imports.EXCLUDED)) {
                    entitySelectorPanel.setSelection(Collections.singleton(df.getOWLNamedIndividual(object)));
                    break;
                }
 else if (ont.containsAnnotationPropertyInSignature(object,
                        Imports.EXCLUDED)) {
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


    @Override
    public String getEditorTypeName() {
        return "Entity IRI";
    }


    public JComponent getComponent() {
        return entitySelectorPanel;
    }


    @Override
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
