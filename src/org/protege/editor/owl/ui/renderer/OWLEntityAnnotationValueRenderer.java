package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.prefix.PrefixMapper;
import org.protege.editor.owl.ui.prefix.PrefixMapperManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntologyChange;
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
        if (OWLRendererPreferences.getInstance().isRenderPrefixes()){
            final String uriStr = entity.getURI().toString();

            PrefixMapper mapper = PrefixMapperManager.getInstance().getMapper();
            for (String base : mapper.getValues()){
                if (uriStr.startsWith(base)){
                    return escape(mapper.getPrefix(base) + ":" + shortForm);
                }
            }
        }
        return escape(shortForm);
    }


    protected void processChanges(List<? extends OWLOntologyChange> changes) {
        final List<URI> uris = provider.getAnnotationURIs();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange() && change.getAxiom() instanceof OWLEntityAnnotationAxiom) {
                OWLEntityAnnotationAxiom axiom = (OWLEntityAnnotationAxiom) change.getAxiom();
                if (uris.contains(axiom.getAnnotation().getAnnotationURI())){
                    OWLEntity ent = axiom.getSubject();
                    // @@TODO we need some way to determine whether the rendering really has changed due to these axioms
                    // otherwise we're telling a whole load of things to update that don't need to
                    fireRenderingChanged(ent);
                }
            }
        }
    }


    protected void disposeRenderer() {
        // do nothing
    }


    private String escape(String rendering) {
        return RenderingEscapeUtils.getEscapedRendering(rendering);
    }
}
