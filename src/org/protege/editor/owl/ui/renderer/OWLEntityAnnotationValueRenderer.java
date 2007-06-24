package org.protege.editor.owl.ui.renderer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.util.AnnotationValueShortFormProvider;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 18-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityAnnotationValueRenderer extends OWLEntityRendererImpl {

    private static final Logger logger = Logger.getLogger(OWLEntityAnnotationValueRenderer.class);

    private OWLModelManager owlModelManager;

    private List<URI> uris = new ArrayList<URI>();

    private AnnotationValueShortFormProvider provider;


    public OWLEntityAnnotationValueRenderer() {
        uris.add(OWLRDFVocabulary.RDFS_LABEL.getURI());
        uris.add(URI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
    }


    private void checkForChange(OWLEntity entity) {
        fireRenderingChanged(entity);
    }


    public void setup(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public void initialise() {
        super.initialise();
        provider = new AnnotationValueShortFormProvider(uris,
                                                        new HashMap<URI, List<String>>(),
                                                        owlModelManager.getOWLOntologyManager());
    }


    public String render(OWLEntity entity) {
        // We should check the cache!
        return provider.getShortForm(entity);
//
//        if (!uris.isEmpty()) {
//            // We prefer renderings from the active ontology
//            List<OWLAnnotation> candidateAnnotations = new ArrayList<OWLAnnotation>();
//            for (OWLAnnotation anno : entity.getAnnotations(owlModelManager.getActiveOntology())) {
//                candidateAnnotations.add(anno);
//            }
//            for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
//                if (!ont.equals(owlModelManager.getActiveOntology())) {
//                    for (OWLAnnotation anno : entity.getAnnotations(ont)) {
//                        candidateAnnotations.add(anno);
//                    }
//                }
//            }
//
//            int curIndex = uris.size();
//            OWLAnnotation curCandidate = null;
//            for (OWLAnnotation instance : candidateAnnotations) {
//                int index = uris.indexOf(instance.getAnnotationURI());
//                if (index != -1 && index < curIndex) {
//                    curCandidate = instance;
//                    curIndex = index;
//                }
//            }
//            if (curCandidate != null) {
//                OWLObject content = curCandidate.getAnnotationValue();
//                if (content instanceof OWLConstant) {
//                    return escape(((OWLConstant) content).getLiteral());
//                }
//            }
//        }
////        // Return default rendering
//        return super.render(entity);
    }


    private String escape(String rendering) {
        return RenderingEscapeUtils.getEscapedRendering(rendering);
    }


    public void disposeRenderer() {
    }
}
