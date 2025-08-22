package org.protege.editor.owl.model.cache;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.util.*;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 10, 2008<br><br>
 */
public class OWLExpressionUserCache implements Disposable {

    private static final String ID = OWLExpressionUserCache.class.getName();


    private Map<OWLClassExpression, String> renderingsCache = new HashMap<>();

    private List<String> cacheInternalForm = new ArrayList<>();
    private List<String> cacheExternalForm = null;

    private OWLModelManager mngr;

    private boolean enabled = false;

    private OWLModelManagerListener modelManagerListener = event -> {
        if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED) ||
            event.getType().equals(EventType.ENTITY_RENDERER_CHANGED) ||
            event.getType().equals(EventType.ENTITY_RENDERING_CHANGED) ||
            event.getType().equals(EventType.ONTOLOGY_RELOADED)){
            refresh();
        }
    };


    // Implemented as its own factory for now
    public static OWLExpressionUserCache getInstance(OWLModelManager mngr){
        OWLExpressionUserCache instance = mngr.get(ID);
        if (instance == null){
            instance = new OWLExpressionUserCache(mngr);
            mngr.put(ID, instance);
        }
        return instance;
    }


    private OWLExpressionUserCache(OWLModelManager mngr){
        this.mngr = mngr;
        mngr.addListener(modelManagerListener);
    }


    public void add(OWLClassExpression owlDescription, String rendering) {
        if(!enabled) {
            return;
        }
        if (!getRenderings().contains(rendering)){
            getRenderings().add(0, rendering); // add them backwards
        }

        String internalRendering = toInternalForm(rendering);
        if (!cacheInternalForm.contains(internalRendering)){
            cacheInternalForm.add(0, internalRendering); // add them backwards
        }


        renderingsCache.put(owlDescription, internalRendering);
    }


    public List<String> getRenderings() {
        if (cacheExternalForm == null){
            cacheExternalForm = new ArrayList<>();
            for (String s : cacheInternalForm){
                final String externalForm = fromInternalForm(s);
                if (externalForm != null){
                    cacheExternalForm.add(externalForm);
                }
            }
        }
        return cacheExternalForm;
    }


    public String getRendering(OWLClassExpression descr) {
        return fromInternalForm(renderingsCache.get(descr));
    }


    public void refresh(){
        cacheExternalForm = null;
    }


    public void dispose() {
        renderingsCache.clear();
        renderingsCache = null;
        cacheExternalForm = null;
        cacheInternalForm = null;
        mngr.removeListener(modelManagerListener);
        mngr = null;
    }

    private String getPrefsID() {
        final String fragment = mngr.getRendering(mngr.getActiveOntology());

        return fragment + "-" + mngr.getActiveOntology().hashCode();
    }


////////////////////////////////

    // handle rendering to and from a canonical form that captures all
    // of the required information - the type and the URI of entities
    // replaces the entity names with typed URIs to be more robust against renderer changes
    // typed URIs are of the form OWLClass::http://etc etc

    private static final String OWLCLASS = "OWLClass";
    private static final String OWLOBJECTPROPERTY = "OWLObjectProperty";
    private static final String OWLDATAPROPERTY = "OWLDataProperty";
    private static final String OWLANNOTATIONPROPERTY = "OWLAnnotationProperty";
    private static final String OWLINDIVIDUAL = "OWLIndividual";
    private static final String OWLDATATYPE = "OWLDatatype";
    private static final String DELIMITER = "::";

    private static final String WHITESPACE = " \n\t{}()[]'";



    private String toInternalForm(String input) {
        if (input == null){
            return null;
        }

        InternalFormEntityRenderer ren = new InternalFormEntityRenderer();

        StringBuilder sb = new StringBuilder();

        StringTokenizer tokenizer = new StringTokenizer(input, WHITESPACE, true);
        int endIndex = 0;

        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            if (token.equals("'")){
                while(tokenizer.hasMoreTokens() && !token.endsWith("'")){
                    token += tokenizer.nextToken();
                }
            }

            int startIndex = input.indexOf(token, endIndex);

            OWLEntity entity = mngr.getOWLEntityFinder().getOWLEntity(token); // what if the wrong type is returned????
            if (entity != null){
                sb.append(ren.render(entity));
            }
            else{
                sb.append(token);
            }

            endIndex = startIndex + token.length();
        }
        return sb.toString();
    }



    private String fromInternalForm(String input) {
        if(!enabled) {
            return null;
        }
        if (input == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();

        StringTokenizer tokenizer = new StringTokenizer(input, WHITESPACE, true);
        int endIndex = 0;

        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            int startIndex = input.indexOf(token, endIndex);

            if (token.equals("'")){
                while(tokenizer.hasMoreTokens() && !token.endsWith("'")){
                    token += tokenizer.nextToken();
                }
            }

            OWLEntity entity = parseOWLEntity(token);
            if (entity != null){
                if (containsEntity(entity)){
                    sb.append(mngr.getRendering(entity));
                }
                else{
                    return null; // cannot find the entity so this expression will not be helpful
                }
            }
            else{
                sb.append(token);
            }

            endIndex = startIndex + token.length();
        }
        return sb.toString();
    }


    private boolean containsEntity(OWLEntity entity) {
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (ont.containsEntityInSignature(entity)){
                return true;
            }
        }
        return false;
    }


    private OWLEntity parseOWLEntity(String name) {
        String[] s = name.split(DELIMITER);
        switch (s[0]) {
            case OWLCLASS:
                return mngr.getOWLDataFactory().getOWLClass(IRI.create(s[1]));
            case OWLOBJECTPROPERTY:
                return mngr.getOWLDataFactory().getOWLObjectProperty(IRI.create(s[1]));
            case OWLDATAPROPERTY:
                return mngr.getOWLDataFactory().getOWLDataProperty(IRI.create(s[1]));
            case OWLANNOTATIONPROPERTY:
                return mngr.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(s[1]));
            case OWLINDIVIDUAL:
                return mngr.getOWLDataFactory().getOWLNamedIndividual(IRI.create(s[1]));
            case OWLDATATYPE:
                return mngr.getOWLDataFactory().getOWLDatatype(IRI.create(s[1]));
        }
        return null;
    }


    class InternalFormEntityRenderer implements ShortFormProvider, OWLEntityVisitor {

        private String cf;

        public String render(OWLEntity entity){
            cf = null;
            entity.accept(this);
            return cf;
        }


        public void visit(OWLClass entity) {
            cf = OWLCLASS + DELIMITER + entity.getIRI();
        }


        public void visit(OWLObjectProperty entity) {
            cf = OWLOBJECTPROPERTY + DELIMITER + entity.getIRI();
        }


        public void visit(OWLDataProperty entity) {
            cf = OWLDATAPROPERTY + DELIMITER + entity.getIRI();
        }


        public void visit(OWLNamedIndividual entity) {
            cf = OWLINDIVIDUAL + DELIMITER + entity.getIRI();
        }


        public void visit(OWLDatatype entity) {
            cf = OWLDATATYPE + DELIMITER + entity.getIRI();
        }


        public void visit(OWLAnnotationProperty entity) {
            cf = OWLANNOTATIONPROPERTY + DELIMITER + entity.getIRI();
        }


        public String getShortForm(OWLEntity owlEntity) {
            return render(owlEntity);
        }


        public void dispose() {
            // do nothing
        }
    }
}
