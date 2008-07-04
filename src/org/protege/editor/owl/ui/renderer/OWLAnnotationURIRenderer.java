package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 4, 2008<br><br>
 *
 * Finds renderings for the annotation URIs when an annotation value renderer is being used.
 * Effectively, it should work just like the <code>AnnotationValueShortFormProvider</code>
 * A temporary hack required until annotation properties are reimplemented.
 * This only works for RDF/XML ontologies.
 *
 * Also, as we are incapable of editing them, they should cache well.
 * Only needs to be updated if the active ontologies change or the renderer prefs are updated
 */
public class OWLAnnotationURIRenderer {

    private OWLModelManager mngr;

    private List<URI> registeredURIs;

    private Map<URI, List<String>> preferredLanguageMap;

    private Map<URI, String> renderingMap = new HashMap<URI, String>();


    public OWLAnnotationURIRenderer(OWLModelManager mngr) {
        this.mngr = mngr;
        rebuild(mngr.getActiveOntologies());
    }

    
    public String getRendering(URI uri) {
        return renderingMap.get(uri);
    }


    private void rebuild(Set<OWLOntology> onts){

        registeredURIs = OWLRendererPreferences.getInstance().getAnnotationURIs();
        preferredLanguageMap = OWLRendererPreferences.getInstance().getAnnotationLangs();

        for (OWLOntology ont : onts){
            OWLOntologyFormat format = mngr.getOWLOntologyManager().getOntologyFormat(ont);
            if (format instanceof RDFXMLOntologyFormat) {
                RDFXMLOntologyFormat rdfxmlOntologyFormat = (RDFXMLOntologyFormat) format;
                final Map<URI, Set<OWLAnnotation>> annotationURIAnnotations = rdfxmlOntologyFormat.getAnnotationURIAnnotations();
                for (URI uri : ont.getAnnotationURIs()){
                    final Set<OWLAnnotation> potentialLabels = annotationURIAnnotations.get(uri);
                    if (potentialLabels != null){
                        String label = findMostAppropriateLabel(potentialLabels);
                        if (label != null){
                            renderingMap.put(uri, label);
                        }
                    }
                }
            }
        }
    }

    // basically stolen from AnnotationValueShortFormProvider
    private String findMostAppropriateLabel(Set<OWLAnnotation> potentialLabels){
        int lastURIMatchIndex = Integer.MAX_VALUE;
        int lastLangMatchIndex = Integer.MAX_VALUE;

        OWLObject candidateValue = null;

        for (OWLAnnotation anno : potentialLabels){
            int index = registeredURIs.indexOf(anno.getAnnotationURI());
            if (index == -1) {
                continue;
            }
            if (index == lastURIMatchIndex) {
                // Different annotation but same URI, as previous candidate
                // look at lang tag for that URI and see if we take priority over the previous one
                OWLObject annoVal = anno.getAnnotationValue();
                if (annoVal instanceof OWLUntypedConstant) {
                    OWLUntypedConstant untypedConstantVal = (OWLUntypedConstant) annoVal;
                    List<String> langList = preferredLanguageMap.get(anno.getAnnotationURI());
                    if (langList != null) {
                        // There is no need to check if lang is null.  It may well be that no
                        // lang is preferred over any other lang.
                        int langIndex = langList.indexOf(untypedConstantVal.getLang());
                        if (langIndex != -1 && langIndex < lastLangMatchIndex) {
                            lastLangMatchIndex = langIndex;
                            candidateValue = annoVal;
                        }
                    }
                }
            }
            else if (index < lastURIMatchIndex) {
                // Better match than previous URI - wipe out previous match!
                lastURIMatchIndex = index;
                candidateValue = anno.getAnnotationValue();

                // wipe out the language indexing and recalculate
                lastLangMatchIndex = Integer.MAX_VALUE;
                if (candidateValue instanceof OWLUntypedConstant) {
                    OWLUntypedConstant untypedConstantVal = (OWLUntypedConstant) candidateValue;
                    List<String> langList = preferredLanguageMap.get(anno.getAnnotationURI());
                    if (langList != null) {
                        // There is no need to check if lang is null.  It may well be that no
                        // lang is preferred over any other lang.
                        int langIndex = langList.indexOf(untypedConstantVal.getLang());
                        if (langIndex != -1) {
                            lastLangMatchIndex = langIndex;
                        }
                    }
                }
            }
        }
        if (candidateValue != null){
            if (candidateValue instanceof OWLConstant){
                return ((OWLConstant)candidateValue).getLiteral();
            }
            else{
                return mngr.getRendering(candidateValue);
            }
        }
        return null;
    }
}