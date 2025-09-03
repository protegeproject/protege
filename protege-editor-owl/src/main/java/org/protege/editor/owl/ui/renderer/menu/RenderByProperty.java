package org.protege.editor.owl.ui.renderer.menu;

import org.protege.editor.core.ui.action.ProtegeDynamicAction;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLEntityAnnotationValueRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class RenderByProperty extends ProtegeDynamicAction {

	private static final long serialVersionUID = 8119262495644333132L;
	
	private static final OWLAnnotationProperty LABEL = OWLManager.getOWLDataFactory().getRDFSLabel();
	
	private JMenu menu;	
	private OWLModelManagerListener listener;

	public void actionPerformed(ActionEvent e) {}

	@Override
	public void rebuildChildMenuItems(JMenu thisMenuItem) {
		Set<OWLAnnotationProperty> annotationProperties = getAnnotationProperties();
		List<OWLAnnotationProperty> annotationPropertiesList = new ArrayList<>(annotationProperties);
		Collections.sort(annotationPropertiesList, getComparator());		
		
		for (OWLAnnotationProperty prop : annotationPropertiesList) {
			JMenuItem item = new PropertyMenuItem(prop);
			thisMenuItem.add(item);
		}
	}
	
	
	private Set<OWLAnnotationProperty> getAnnotationProperties() {
		Set<OWLAnnotationProperty> annotationProperties = new HashSet<>();
		Set<OWLOntology> ontologies = getOWLModelManager().getActiveOntologies();
		for (OWLOntology ontology : ontologies) {
			annotationProperties.addAll(ontology.getAnnotationPropertiesInSignature());
		}
		return annotationProperties;
	}
	
	
	public OWLModelManager getOWLModelManager() {
		return ((OWLModelManager)getEditorKit().getModelManager());
	}

	private Comparator<OWLAnnotationProperty> getComparator() {
		return (p1, p2) -> getOWLModelManager().getRendering(p1).compareTo(getOWLModelManager().getRendering(p2));
	}	

	public void setMenu(JMenu menu) {
		this.menu = menu;		
		updateCheckedStatus();
	}

	
	/*
	 * Code below is copied from AbstractByRendererMenu 
	 */
	

	public void initialise() throws Exception {
		listener = event -> {
            if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
                updateCheckedStatus();
            }
        };
		getOWLModelManager().addListener(listener);		
	}
	
	private void updateCheckedStatus() {
		if (menu != null) {
			RendererPlugin plugin = OWLRendererPreferences.getInstance().getRendererPlugin();
			markCheckedMenu(isMyRendererPlugin(plugin) && isConfigured(plugin));
		}		
	}

	private void markCheckedMenu(boolean marked) {
		if (marked) {
			JCheckBoxMenuItem it = new JCheckBoxMenuItem();
			it.setSelected(true);
			/*
			 * This is unfortunate. We cannot easily get the checkbox
			 * icon from the L&F. Actually we get it, and then it 
			 * throws a class cast exception when it tries to paint it,
			 * because it expects the component to be a JCheckBox. 
			 */
			menu.setIcon(Icons.getIcon("hierarchy.collapsed.gif"));
			
		} else {
			menu.setIcon(null);
		}		
	}
	
	public void dispose() throws Exception {
		getOWLModelManager().removeListener(listener);
	}
	
	/*
	 * End copied code
	 */
	
	protected boolean isMyRendererPlugin(RendererPlugin plugin) {
		return plugin.getRendererClassName().equals(OWLEntityAnnotationValueRenderer.class.getName());
	}
	
	protected boolean isConfigured(RendererPlugin plugin) {
		List<IRI> annotations = OWLRendererPreferences.getInstance().getAnnotationIRIs();
		return annotations.size() == 1 && !annotations.iterator().next().equals(LABEL.getIRI());
	}

	protected void configure(OWLAnnotationProperty prop) {
		OWLRendererPreferences.getInstance().setAnnotations(Collections.singletonList(prop.getIRI()));
	}
	
	private void setRendering(OWLAnnotationProperty prop) {
		OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
		for (RendererPlugin plugin : preferences.getRendererPlugins()) {
			if (isMyRendererPlugin(plugin)) {
				preferences.setRendererPlugin(plugin);
				configure(prop);
				getOWLModelManager().refreshRenderer();				
				break;
			}
		}		
	}
	
	private void resetRendering() {
		OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
		preferences.setRendererPlugin(preferences.getRendererPluginByClassName(OWLEntityRendererImpl.class.getName()));		
		getOWLModelManager().refreshRenderer();
	}
	
	
	class PropertyMenuItem extends JCheckBoxMenuItem {

		private OWLAnnotationProperty property;
		
		public PropertyMenuItem(OWLAnnotationProperty prop) {			
			super(getOWLModelManager().getRendering(prop));
			property = prop;
			setToolTipText(property.getIRI().toString());			
			addActionListener(arg0 -> {
                onStateChanged();
            });
			setSelected(isRenderingProperty());
		}
		
		private boolean isRenderingProperty() {
			if (!isMyRendererPlugin(OWLRendererPreferences.getInstance().getRendererPlugin())) { return false; }
			
			List<IRI> annotations = OWLRendererPreferences.getInstance().getAnnotationIRIs();
			for (IRI iri : annotations) {
				if (iri.equals(property.getIRI())) {
					return true;
				}
			}
			return false;
		}

		private void onStateChanged() {
			if (isSelected()) {
				setRendering(property);
			} else {
				resetRendering();
			}
		}		
	}		
	
}
