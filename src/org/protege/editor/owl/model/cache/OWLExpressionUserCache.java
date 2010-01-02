package org.protege.editor.owl.model.cache;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owlapi.model.*;

import java.net.URI;
import java.util.*;

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
 * Date: Sep 10, 2008<br><br>
 */
public class OWLExpressionUserCache implements Disposable {

    private Logger logger = Logger.getLogger(OWLExpressionUserCache.class);


    private static final String ID = OWLExpressionUserCache.class.getName();


    private Map<OWLClassExpression, String> renderingsCache = new HashMap<OWLClassExpression, String>();

    private List<String> cacheInternalForm = new ArrayList<String>();
    private List<String> cacheExternalForm = null;

    private OWLModelManager mngr;

    private OWLModelManagerListener modelManagerListener = new OWLModelManagerListener(){

        public void handleChange(OWLModelManagerChangeEvent event) {
            if (event.getType().equals(EventType.ACTIVE_ONTOLOGY_CHANGED) ||
                event.getType().equals(EventType.ENTITY_RENDERER_CHANGED) ||
                event.getType().equals(EventType.ENTITY_RENDERING_CHANGED)){
                refresh();
            }
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
        load();
    }


    public void add(OWLClassExpression owlDescription, String rendering) {
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
            cacheExternalForm = new ArrayList<String>();
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
        save();
        renderingsCache.clear();
        renderingsCache = null;
        cacheExternalForm = null;
        cacheInternalForm = null;
        mngr.removeListener(modelManagerListener);
        mngr = null;
    }


    private void save() {
//        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());
//        prefs.putStringList(getPrefsID(), cacheInternalForm);
    }


    private void load() {
//        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());
//        if (prefs != null){
//            cacheExternalForm = new ArrayList<String>();
//            for (String internal : prefs.getStringList(getPrefsID(), new ArrayList<String>())){
//                String external = fromInternalForm(internal);
//                try {
//                    OWLClassExpression descr = mngr.getOWLExpressionCheckerFactory().getOWLClassExpressionChecker().createObject(external);
//                    renderingsCache.put(descr, internal);
//                    cacheInternalForm.add(internal);
//                    cacheExternalForm.add(external);
//                }
//                catch (OWLExpressionParserException e) {
//                    logger.warn("Could not reload expression from history files: " + internal);
//                }
//            }
//        }
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
        if (s[0].equals(OWLCLASS)){
            return mngr.getOWLDataFactory().getOWLClass(IRI.create(s[1]));
        }
        else if (s[0].equals(OWLOBJECTPROPERTY)){
            return mngr.getOWLDataFactory().getOWLObjectProperty(IRI.create(s[1]));
        }
        else if (s[0].equals(OWLDATAPROPERTY)){
            return mngr.getOWLDataFactory().getOWLDataProperty(IRI.create(s[1]));
        }
        else if (s[0].equals(OWLANNOTATIONPROPERTY)){
            return mngr.getOWLDataFactory().getOWLAnnotationProperty(IRI.create(s[1]));
        }
        else if (s[0].equals(OWLINDIVIDUAL)){
            return mngr.getOWLDataFactory().getOWLNamedIndividual(IRI.create(s[1]));
        }
        else if (s[0].equals(OWLDATATYPE)){
            return mngr.getOWLDataFactory().getOWLDatatype(IRI.create(s[1]));
        }
        return null;
    }


    class InternalFormEntityRenderer implements OWLEntityRenderer, OWLEntityVisitor {

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
