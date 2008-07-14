package org.protege.editor.owl.model.find;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EntityFinderImpl implements EntityFinder {

    private static final Logger logger = Logger.getLogger(EntityFinderImpl.class);

    private OWLEntityRenderingCache renderingCache;


    public EntityFinderImpl(OWLEntityRenderingCache renderingCache) {
        this.renderingCache = renderingCache;
    }

    public Set<OWLClass> getMatchingOWLClasses(String match) {
        return getEntities(match, OWLClass.class, EntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp) {
        return getEntities(match, OWLClass.class, fullRegExp);
    }


    public Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match) {
        return getEntities(match, OWLObjectProperty.class, EntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp) {
        return getEntities(match, OWLObjectProperty.class, fullRegExp);
    }


    public Set<OWLDataProperty> getMatchingOWLDataProperties(String match) {
        return getEntities(match, OWLDataProperty.class, EntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp) {
        return getEntities(match, OWLDataProperty.class, fullRegExp);
    }


    public Set<OWLIndividual> getMatchingOWLIndividuals(String match) {
        return getEntities(match, OWLIndividual.class, EntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp) {
        return getEntities(match, OWLIndividual.class, fullRegExp);
    }


//    public Set<OWLDataType> getMatchingOWLDataTypes(String match, boolean fullRegExp) {
//        return getEntities(match, OWLDataType.class, fullRegExp);
//    }


    public Set<OWLEntity> getEntities(String match) {
        return getEntities(match, OWLEntity.class, EntityFinderPreferences.getInstance().isUseRegularExpressions());
    }


    public Set<OWLEntity> getEntities(String match, boolean fullRegExp) {
        return getEntities(match, OWLEntity.class, fullRegExp);
    }


    private <T extends OWLEntity> Set<T> getEntities(String match, Class<T> type, boolean fullRegExp) {
        if (match.length() == 0) {
            return Collections.emptySet();
        }

        if (fullRegExp) {
            return doRegExpSearch(match, type);
        }
        else {
            return doWildcardSearch(match, type);
        }
    }


    private <T extends OWLEntity> Set<T> doRegExpSearch(String match, Class<T> type) {
        Set<T> results = new HashSet<T>();
        try {
            Pattern pattern = Pattern.compile(match);
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
        catch (Exception e) {
            logger.warn("Invalid regular expression: " + e.getMessage());
        }
        return results;
    }

    /* @@TODO fix wildcard searching - it does not handle the usecases correctly
     * eg A*B will not work, and endsWith is implemented the same as contains
     * (probably right but this should not be implemented separately)
     */
    private <T extends OWLEntity> Set<T> doWildcardSearch(String match, Class<T> type) {
        Set<T> results = new HashSet<T>();
        SimpleWildCardMatcher matcher;
        if (match.startsWith("*")) {
            if (match.length() > 1 && match.endsWith("*")) {
                // Contains
                matcher = new SimpleWildCardMatcher() {
                    public boolean matches(String rendering, String s) {
                        return rendering.indexOf(s) != -1;
                    }
                };
                match = match.substring(1, match.length() - 1);
            }
            else {
                // Ends with
                matcher = new SimpleWildCardMatcher() {
                    public boolean matches(String rendering, String s) {
                        return rendering.indexOf(s) != -1;
                    }
                };
                match = match.substring(1, match.length());
            }
        }
        else {
            // Starts with
            if (match.endsWith("*") && match.length() > 1) {
                match = match.substring(0, match.length() - 1);
            }
            // @@TODO handle matches exactly?
            matcher = new SimpleWildCardMatcher() {
                public boolean matches(String rendering, String s) {
                    return rendering.startsWith(s) || rendering.startsWith("'" + s);
                }
            };
        }

        if (match.trim().length() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Attempt to match the empty string (no results)");
            }
        }
        else{
            match = match.toLowerCase();
            if (logger.isDebugEnabled()) {
                logger.debug("Match: " + match);
            }
            for (String rendering : getRenderings(type)) {
                if (rendering.length() > 0){
                    if (matcher.matches(rendering.toLowerCase(), match)) {
                        results.add(getEntity(rendering, type));
                    }
                }
            }
        }
        return results;
    }


    private <T extends OWLEntity> T getEntity(String rendering, Class<T> type) {
        if (type.equals(OWLClass.class)){
            return (T)renderingCache.getOWLClass(rendering);
        }
        else if (type.equals(OWLObjectProperty.class)){
            return (T)renderingCache.getOWLObjectProperty(rendering);
        }
        else if (type.equals((OWLDataProperty.class))){
            return (T)renderingCache.getOWLDataProperty(rendering);
        }
        else if (type.equals(OWLIndividual.class)){
            return (T)renderingCache.getOWLIndividual(rendering);
        }
//        else if (type.equals(OWLDataType.class)){
//            return (T)renderingCache.getOWLDatatype(rendering);
//        }
        else{
            return (T)renderingCache.getOWLEntity(rendering);
        }
    }


    private <T extends OWLEntity> Set<String> getRenderings(Class<T> type) {
        if (type.equals(OWLClass.class)){
            return renderingCache.getOWLClassRenderings();
        }
        else if (type.equals(OWLObjectProperty.class)){
            return renderingCache.getOWLObjectPropertyRenderings();
        }
        else if (type.equals((OWLDataProperty.class))){
            return renderingCache.getOWLDataPropertyRenderings();
        }
        else if (type.equals(OWLIndividual.class)){
            return renderingCache.getOWLIndividualRenderings();
        }
//        else if (type.equals(OWLDataType.class)){
//            return renderingCache.getOWLDatatypeRenderings();
//        }
        else{
            return renderingCache.getOWLEntityRenderings();
        }
    }


    private interface SimpleWildCardMatcher {

        boolean matches(String rendering, String s);
    }
}
