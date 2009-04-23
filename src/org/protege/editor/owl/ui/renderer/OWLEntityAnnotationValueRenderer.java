package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.AnnotationValueShortFormProvider;

import java.net.URI;
import java.util.List;


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
        provider = new AnnotationValueShortFormProvider(OWLRendererPreferences.getInstance().getAnnotationURIs(),
                                                        OWLRendererPreferences.getInstance().getAnnotationLangs(),
                                                        getOWLModelManager().getOWLOntologyManager());
    }


    public String render(OWLEntity entity) {
        String shortForm = provider.getShortForm(entity);
        return escape(shortForm);
    }


    protected void processChanges(List<? extends OWLOntologyChange> changes) {
        final List<URI> uris = provider.getAnnotationURIs();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange() && change.getAxiom().getAxiomType().equals(AxiomType.ANNOTATION_ASSERTION)) {
                OWLAnnotationAssertionAxiom ax = (OWLAnnotationAssertionAxiom) change.getAxiom();
                // @@TODO we need some way to determine whether the rendering really has changed due to these axioms
                // otherwise we're telling a whole load of things to update that don't need to
                if (uris.contains(ax.getProperty().getURI())){
                    OWLAnnotationSubject subject = ax.getSubject();
                    if (subject instanceof OWLEntity){
                        fireRenderingChanged((OWLEntity)subject);
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
}
