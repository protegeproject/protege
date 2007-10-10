package org.protege.editor.owl.model.find;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;


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

    private OWLModelManager owlModelManager;


    public EntityFinderImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public Set<OWLEntity> getMatchingClasses(String match) {
        return null;
    }


    public Set<OWLEntity> getEntities(String s) {
        if (s.length() == 0) {
            return Collections.EMPTY_SET;
        }
        Set<OWLEntity> results = new HashSet<OWLEntity>();
        if (EntityFinderPreferences.getInstance().isUseRegularExpressions()) {
            try {
                Pattern pattern = Pattern.compile(s);
                for (String rendering : owlModelManager.getOWLEntityRenderings()) {
                    Matcher m = pattern.matcher(rendering);
                    if (m.find()) {
                        OWLEntity ent = owlModelManager.getOWLEntity(rendering);
                        if (ent != null) {
                            results.add(ent);
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.warn("Invalid regular expression: " + e.getMessage());
            }
        }
        else {
            SimpleWildCardMatcher matcher;
            if (s.startsWith("*")) {
                if (s.length() > 1 && s.endsWith("*")) {
                    // Contains
                    matcher = new SimpleWildCardMatcher() {
                        public boolean matches(String rendering, String s) {
                            return rendering.indexOf(s) != -1;
                        }
                    };
                    s = s.substring(1, s.length() - 1);
                }
                else {
                    // Ends with
                    matcher = new SimpleWildCardMatcher() {
                        public boolean matches(String rendering, String s) {
                            return rendering.indexOf(s) != -1;
                        }
                    };
                    s = s.substring(1, s.length());
                }
            }
            else {
                if (s.endsWith("*") && s.length() > 1) {
                    s = s.substring(0, s.length() - 1);
                }
                // Matches exactly?
                matcher = new SimpleWildCardMatcher() {
                    public boolean matches(String rendering, String s) {
                        return rendering.startsWith(s);
                    }
                };
            }
            if (s.trim().length() == 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempt to match the empty string (no results)");
                }
                return Collections.EMPTY_SET;
            }
            s = s.toLowerCase();
            if (logger.isDebugEnabled()) {
                logger.debug("Match: " + s);
            }
            for (String rendering : owlModelManager.getOWLEntityRenderings()) {
                int offset = 0;
                if (rendering.charAt(0) == '\'') {
                    offset = 1;
                }
                if (matcher.matches(rendering.toLowerCase(), s)) {
                    results.add(owlModelManager.getOWLEntity(rendering));
                }
            }
        }

        return results;
    }


    private interface SimpleWildCardMatcher {

        boolean matches(String rendering, String s);
    }
}
