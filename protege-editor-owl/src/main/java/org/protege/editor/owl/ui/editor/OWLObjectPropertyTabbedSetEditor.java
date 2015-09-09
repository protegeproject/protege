package org.protege.editor.owl.ui.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Apr 6, 2009<br><br>
 */
public class OWLObjectPropertyTabbedSetEditor extends AbstractOWLObjectEditor<Set<OWLObjectPropertyExpression>> implements VerifiedInputEditor {
	public static final String DESCRIPTION_EDITOR_TITLE="Property Expression Editor";
	public static final String HIERARCHY_EDITOR_TITLE = "Property Hierarchy";
	
	private JTabbedPane tabbedPane;
	
	private OWLObjectPropertySetEditor descriptionEditor;
    private OWLObjectPropertySelectorPanel propertySelectorPanel;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();
    
    private InputVerificationStatusChangedListener inputListener = new InputVerificationStatusChangedListener(){
        public void verifiedStatusChanged(boolean newState) {
            handleVerifyEditorContents();
        }
    };
    
    public OWLObjectPropertyTabbedSetEditor(OWLEditorKit owlEditorKit) {
        tabbedPane = new JTabbedPane();
        
        propertySelectorPanel = new OWLObjectPropertySelectorPanel(owlEditorKit);
        propertySelectorPanel.addStatusChangedListener(inputListener);
        
        descriptionEditor = new OWLObjectPropertySetEditor(owlEditorKit);
        descriptionEditor.addStatusChangedListener(inputListener);
        
        tabbedPane.addTab(HIERARCHY_EDITOR_TITLE, propertySelectorPanel);
        tabbedPane.addTab(DESCRIPTION_EDITOR_TITLE, descriptionEditor.getEditorComponent());
    }


    public Set<OWLObjectPropertyExpression> getEditedObject() {
    	if (tabbedPane.getSelectedComponent() == propertySelectorPanel) {
    		return (Set) propertySelectorPanel.getSelectedObjects();
    	}
    	else {
    		return descriptionEditor.getEditedObject();
    	}
    }


    public boolean setEditedObject(Set<OWLObjectPropertyExpression> properties){
    	if (properties == null) {
    		properties = Collections.emptySet();
    	}
    	if (canConvertToObjectPropertyList(properties)) {
    		propertySelectorPanel.setSelection(convertToObjectPropertyList(properties));
    	}
    	descriptionEditor.setEditedObject(properties);
        return true;
    }
    
    private boolean canConvertToObjectPropertyList(Set<OWLObjectPropertyExpression> properties) {
    	for (OWLObjectPropertyExpression property : properties) {
    		if (property.isAnonymous()) {
    			return false;
    		}
    	}
    	return true;
    }

    private Set<OWLObjectProperty> convertToObjectPropertyList(Set<OWLObjectPropertyExpression> properties) {
    	return (Set) properties;
    }
    
    public String getEditorTypeName() {
        return "Set of Object Properties";
    }


    public boolean canEdit(Object object) {
        return checkSet(object, OWLObjectProperty.class);
    }


    public JComponent getEditorComponent() {
        return tabbedPane;
    }
    
    private void handleVerifyEditorContents() {
    	if (tabbedPane.getSelectedComponent() == propertySelectorPanel) {
    		for (InputVerificationStatusChangedListener l : listeners){
    			l.verifiedStatusChanged(true);
    		}
    	}
    }
    
    public void addStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.add(l);
        descriptionEditor.addStatusChangedListener(l);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener l) {
        listeners.remove(l);
        descriptionEditor.removeStatusChangedListener(l);
    }


    public void dispose() {
        propertySelectorPanel.dispose();
        descriptionEditor.dispose();
    }
}