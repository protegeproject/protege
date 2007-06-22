package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.util.AnnotationValueShortFormProvider;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
