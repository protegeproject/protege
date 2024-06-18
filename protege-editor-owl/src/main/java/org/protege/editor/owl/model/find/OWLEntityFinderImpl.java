package org.protege.editor.owl.model.find;

import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.renderer.RenderingEscapeUtils;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEntityFinderImpl implements OWLEntityFinder {

    private final Logger logger = LoggerFactory.getLogger(OWLEntityFinderImpl.class);

    private OWLEntityRenderingCache renderingCache;

    private OWLModelManagerImpl mngr;

    private static final String WILDCARD = "*";


    public OWLEntityFinderImpl(OWLModelManagerImpl mngr, OWLEntityRenderingCache renderingCache) {
        this.mngr = mngr;
        this.renderingCache = renderingCache;
    }

    private String stripAndEscapeRendering(String rendering) {
        if (rendering.startsWith("'") && rendering.endsWith("'") && rendering.length() > 1) {
        	rendering = rendering.substring(1, rendering.length() - 1);
        }
        return RenderingEscapeUtils.getEscapedRendering(rendering);
    }

    public OWLClass getOWLClass(String rendering) {
    	return renderingCache.getOWLClass(stripAndEscapeRendering(rendering));
    }

    @Override
    public Set<OWLEntity> getOWLEntities(String rendering) {
        return renderingCache.getOWLEntities(rendering);
    }

    public OWLObjectProperty getOWLObjectProperty(String rendering) {
    	return renderingCache.getOWLObjectProperty(stripAndEscapeRendering(rendering));
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
    	return renderingCache.getOWLDataProperty(stripAndEscapeRendering(rendering));
    }


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering) {
    	return renderingCache.getOWLAnnotationProperty(stripAndEscapeRendering(rendering));
    }


    public OWLNamedIndividual getOWLIndividual(String rendering) {
    	return renderingCache.getOWLIndividual(stripAndEscapeRendering(rendering));
    }


    public OWLDatatype getOWLDatatype(String rendering) {
    	return renderingCache.getOWLDatatype(stripAndEscapeRendering(rendering));
    }


    public OWLEntity getOWLEntity(String rendering) {
    	return renderingCache.getOWLEntity(stripAndEscapeRendering(rendering));
    }


    public Set<String> getOWLEntityRenderings() {
        return renderingCache.getOWLEntityRenderings();
    }


    public Set<OWLClass> getMatchingOWLClasses(String match) {
        return getEntities(match, OWLClass.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp) {
        return getEntities(match, OWLClass.class, fullRegExp);
    }
    
    public Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp, int flags) {
    	return getEntities(match, OWLClass.class, fullRegExp, flags);
    }


    public Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match) {
        return getEntities(match, OWLObjectProperty.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp) {
        return getEntities(match, OWLObjectProperty.class, fullRegExp);
    }
    
    public Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLObjectProperty.class, fullRegExp, flags);
    }


    public Set<OWLDataProperty> getMatchingOWLDataProperties(String match) {
        return getEntities(match, OWLDataProperty.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp) {
        return getEntities(match, OWLDataProperty.class, fullRegExp);
    }
    
    public Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLDataProperty.class, fullRegExp, flags);
    }


    public Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match) {
        return getEntities(match, OWLNamedIndividual.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp) {
        return getEntities(match, OWLNamedIndividual.class, fullRegExp);
    }
    
    public Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLNamedIndividual.class, fullRegExp, flags);
    }


    public Set<OWLDatatype> getMatchingOWLDatatypes(String match) {
        return getEntities(match, OWLDatatype.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp) {
        return getEntities(match, OWLDatatype.class, fullRegExp);
    }
    
    public Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLDatatype.class, fullRegExp, flags);
    }


    public Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match) {
        return getEntities(match, OWLAnnotationProperty.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp) {
        return getEntities(match, OWLAnnotationProperty.class, fullRegExp);
    }
    
    public Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLAnnotationProperty.class, fullRegExp, flags);
    }


    public Set<OWLEntity> getMatchingOWLEntities(String match) {
        return getEntities(match, OWLEntity.class, OWLEntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp) {
        return getEntities(match, OWLEntity.class, fullRegExp);
    }
    
    public Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp, int flags) {
        return getEntities(match, OWLEntity.class, fullRegExp, flags);
    }


    public Set<OWLEntity> getEntities(IRI iri) {

        Set<OWLEntity> entities = new HashSet<>();

        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (ont.containsClassInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLClass(iri));
            }
            if (ont.containsObjectPropertyInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLObjectProperty(iri));
            }
            if (ont.containsDataPropertyInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLDataProperty(iri));
            }
            if (ont.containsIndividualInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLNamedIndividual(iri));
            }
            if (ont.containsAnnotationPropertyInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLAnnotationProperty(iri));
            }
            if (ont.containsDatatypeInSignature(iri)){
                entities.add(mngr.getOWLDataFactory().getOWLDatatype(iri));
            }
        }
        return entities;
    }


    private <T extends OWLEntity> Set<T> getEntities(String match, Class<T> type, boolean fullRegExp) {
        return getEntities(match, type, fullRegExp, Pattern.CASE_INSENSITIVE);
    }
    
    private <T extends OWLEntity> Set<T> getEntities(String match, Class<T> type, boolean fullRegExp, int flags) {
        if (match.length() == 0) {
            return Collections.emptySet();
        }

        if (fullRegExp) {
            return doRegExpSearch(match, type, flags);
        }
        else {
            return doWildcardSearch(match, type);
        }
    }


    private <T extends OWLEntity> Set<T> doRegExpSearch(String match, Class<T> type, int flags) {
        Set<T> results = new HashSet<>();
        try {
            Pattern pattern = Pattern.compile(match, flags);
            for (String rendering : getRenderings(type)) {
                Matcher m = pattern.matcher(rendering);
                if (m.find()) {
                    T ent = getEntity(rendering, type);
                    if (ent != null) {
                        results.add(ent);
                    }
                }
            }
        }
        catch (PatternSyntaxException e) {
            logger.warn("Invalid regular expression: " + e.getMessage());
        }
        return results;
    }

    /* @@TODO fix wildcard searching - it does not handle the usecases correctly
     * eg A*B will not work, and endsWith is implemented the same as contains
     * (probably right but this should not be implemented separately)
     */
    private <T extends OWLEntity> Set<T> doWildcardSearch(String match, Class<T> type) {
        Set<T> results = new HashSet<>();

        if (match.equals(WILDCARD)){
            results = getAllEntities(type);
        }
        else{
            SimpleWildCardMatcher matcher;
            if (match.startsWith(WILDCARD)) {
                if (match.length() > 1 && match.endsWith(WILDCARD)) {
                    // Contains
                    matcher = String::contains;
                    match = match.substring(1, match.length() - 1);
                }
                else {
                    // Ends with
                    matcher = String::contains;
                    match = match.substring(1, match.length());
                }
            }
            else {
                // Starts with
                if (match.endsWith(WILDCARD) && match.length() > 1) {
                    match = match.substring(0, match.length() - 1);
                }
                // @@TODO handle matches exactly?
                matcher = (rendering, s) -> rendering.startsWith(s) || rendering.startsWith("'" + s);
            }

            if (match.trim().length() == 0) {
                logger.debug("Attempt to match the empty string (no results)");
            }
            else{
                match = match.toLowerCase();
                logger.debug("Match: {}", match);
                for (String rendering : getRenderings(type)) {
                    if (rendering.length() > 0){
                        if (matcher.matches(rendering.toLowerCase(), match)) {
                            results.add(getEntity(rendering, type));
                        }
                    }
                }
            }

        }

        return results;
    }


    @SuppressWarnings("unchecked")
    private <T extends OWLEntity> Set<T> getAllEntities(Class<T> type) {
        if (type.equals(OWLDatatype.class)){
            return (Set<T>)new OWLDataTypeUtils(mngr.getOWLOntologyManager()).getKnownDatatypes(mngr.getActiveOntologies());
        }
        else{
            Set<T> entities = new HashSet<>();
            for (OWLOntology ont: mngr.getActiveOntologies()){
                if (type.equals(OWLClass.class)){
                    entities.addAll((Set<T>)ont.getClassesInSignature());
                }
                else if (type.equals(OWLObjectProperty.class)){
                    entities.addAll((Set<T>)ont.getObjectPropertiesInSignature());
                }
                else if (type.equals((OWLDataProperty.class))){
                    entities.addAll((Set<T>)ont.getDataPropertiesInSignature());
                }
                else if (type.equals(OWLIndividual.class)){
                    entities.addAll((Set<T>)ont.getIndividualsInSignature());
                }
                else if (type.equals(OWLAnnotationProperty.class)){
                    entities.addAll((Set<T>)ont.getAnnotationPropertiesInSignature());
                }
                else if (type.equals(OWLDatatype.class)){
                    entities.addAll((Set<T>)ont.getDatatypesInSignature());
                }
            }
            return entities;
        }
    }


    private <T extends OWLEntity> T getEntity(String rendering, Class<T> type) {
        if (OWLClass.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLClass(rendering));
        }
        else if (OWLObjectProperty.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLObjectProperty(rendering));
        }
        else if (OWLDataProperty.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLDataProperty(rendering));
        }
        else if (OWLNamedIndividual.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLIndividual(rendering));
        }
        else if (OWLAnnotationProperty.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLAnnotationProperty(rendering));
        }
        else if (OWLDatatype.class.isAssignableFrom(type)){
            return type.cast(renderingCache.getOWLDatatype(rendering));
        }
        else{
            return type.cast(renderingCache.getOWLEntity(rendering));
        }
    }


    private <T extends OWLEntity> Set<String> getRenderings(Class<T> type) {
        if (OWLClass.class.isAssignableFrom(type)){
            return renderingCache.getOWLClassRenderings();
        }
        else if (OWLObjectProperty.class.isAssignableFrom(type)){
            return renderingCache.getOWLObjectPropertyRenderings();
        }
        else if (OWLDataProperty.class.isAssignableFrom(type)){
            return renderingCache.getOWLDataPropertyRenderings();
        }
        else if (OWLNamedIndividual.class.isAssignableFrom(type)){
            return renderingCache.getOWLIndividualRenderings();
        }
        else if (OWLAnnotationProperty.class.isAssignableFrom(type)){
            return renderingCache.getOWLAnnotationPropertyRenderings();
        }
        else if (OWLDatatype.class.isAssignableFrom(type)){
            return renderingCache.getOWLDatatypeRenderings();
        }
        else{
            return renderingCache.getOWLEntityRenderings();
        }
    }


    private interface SimpleWildCardMatcher {

        boolean matches(String rendering, String s);
    }
}
