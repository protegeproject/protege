package org.protege.editor.owl.ui.renderer;

import org.apache.commons.lang.NotImplementedException;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityAnnotationValueRenderer extends AbstractOWLEntityRenderer {


    private AnnotationValueShortFormProvider provider;


    public void initialise() {
        final OWLDataFactory df = getOWLModelManager().getOWLDataFactory();

        // convert IRI -> lang map into annotation property -> lang map
        final List<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();
        Map<OWLAnnotationProperty, List<String>> propLangMap = new HashMap<OWLAnnotationProperty, List<String>>();

        final Map<IRI, List<String>> iriLangMap = OWLRendererPreferences.getInstance().getAnnotationLangs();
        for (IRI iri : OWLRendererPreferences.getInstance().getAnnotationIRIs()){
            final OWLAnnotationProperty ap = df.getOWLAnnotationProperty(iri);
            properties.add(ap);
            propLangMap.put(ap, iriLangMap.get(iri));
        }
        provider = new AnnotationValueShortFormProvider(properties,
                                                        propLangMap,
                                                        getOWLModelManager().getOWLOntologyManager());
    }


    public String render(IRI iri) {
        // doesn't matter what type of entity we choose - the same value is returned.
        OWLEntity entity = getOWLModelManager().getOWLDataFactory().getOWLClass(iri);
        String shortForm = provider.getShortForm(entity);
        return escape(shortForm);
    }


    protected void processChanges(List<? extends OWLOntologyChange> changes) {
        final List<OWLAnnotationProperty> properties = provider.getAnnotationProperties();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange() && change.getAxiom().getAxiomType().equals(AxiomType.ANNOTATION_ASSERTION)) {
                OWLAnnotationAssertionAxiom ax = (OWLAnnotationAssertionAxiom) change.getAxiom();
                // @@TODO we need some way to determine whether the rendering really has changed due to these axioms
                // otherwise we're telling a whole load of things to update that don't need to
                if (properties.contains(ax.getProperty())){
                    OWLAnnotationSubject subject = ax.getSubject();
                    if (subject instanceof IRI) {
                        IRI iri = (IRI) subject;
                        OWLDataFactory factory = getOWLModelManager().getOWLDataFactory();
                        fireRenderingChanged(factory.getOWLClass(iri));
                        fireRenderingChanged(factory.getOWLObjectProperty(iri));
                        fireRenderingChanged(factory.getOWLDataProperty(iri));
                        fireRenderingChanged(factory.getOWLAnnotationProperty(iri));
                        fireRenderingChanged(factory.getOWLNamedIndividual(iri));
                    }
                }
            }
        }
    }


    protected void disposeRenderer() {
        // do nothing
    }


    protected String escape(String rendering) {
        return RenderingEscapeUtils.getEscapedRendering(rendering);
    }


    protected AnnotationValueShortFormProvider getProvider(){
        return provider;
    }
    
    public boolean isConfigurable() {
    	return true;
    }

    public boolean configure(OWLEditorKit eKit) {
    	return AnnotationRendererPanel.showDialog(eKit);
    }
}
