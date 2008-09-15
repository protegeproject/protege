package org.protege.editor.owl.model.cache;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.*;

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

    private static final String ID = OWLExpressionUserCache.class.getName();


    private Map<OWLDescription, List<String>> renderingsCache =
            new HashMap<OWLDescription, List<String>>();

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
    }


    public void add(OWLDescription owlDescription, String rendering) {
        if (!getRenderings().contains(rendering)){
            getRenderings().add(0, rendering); // add them backwards
        }

        String internalRendering = toInternalForm(rendering);
        if (!cacheInternalForm.contains(internalRendering)){
            cacheInternalForm.add(0, internalRendering); // add them backwards
        }

        List<String> renderings = renderingsCache.get(owlDescription);
        if (renderings == null){
            renderings = new ArrayList<String>();
            renderingsCache.put(owlDescription, renderings);
        }
        if (!renderings.contains(internalRendering)){
            renderings.add(0, internalRendering);
        }
    }


    public List<String> getRenderings() {
        if (cacheExternalForm == null){
            cacheExternalForm = new ArrayList<String>();
            for (String s : cacheInternalForm){
                cacheExternalForm.add(fromInternalForm(s));
            }
        }
        return cacheExternalForm;
    }


    public List<String> getRenderings(OWLDescription descr) {
        List<String> renderings = new ArrayList<String>();
        final List<String> cache = renderingsCache.get(descr);
        if (cache != null){
            for (String rendering : cache){
                renderings.add(fromInternalForm(rendering));
            }
        }
        return renderings;
    }


    public void refresh(){
        System.out.println("refreshing expression history");
        cacheExternalForm = null;
    }


    public void dispose() {
        renderingsCache.clear();
        renderingsCache = null;
        cacheExternalForm = null;
        cacheInternalForm = null;
        mngr = null;
    }


////////////////////////////////

    // handle rendering to and from a canonical form that captures all
    // of the required information - the type and the URI of entities
    // replaces the entity names with typed URIs to be more robust against renderer changes
    // typed URIs are of the form OWLClass::http://etc etc

    private static final String OWLCLASS = "OWLClass";
    private static final String OWLOBJECTPROPERTY = "OWLObjectProperty";
    private static final String OWLDATAPROPERTY = "OWLDataProperty";
    private static final String OWLINDIVIDUAL = "OWLIndividual";
    private static final String OWLDATATYPE = "OWLDataType";
    private static final String DELIMITER = "::";

    private static final String WHITESPACE = " \n\t{}()[]'";



    private String toInternalForm(String input) {
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

            OWLEntity entity = mngr.getOWLEntity(token); // what if the wrong type is returned????
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

            OWLEntity entity = parseOWLEntity(token);
            if (entity != null){
                sb.append(mngr.getRendering(entity));
            }
            else{
                sb.append(token);
            }

            endIndex = startIndex + token.length();
        }
        return sb.toString();
    }


    private OWLEntity parseOWLEntity(String name) {
        String[] s = name.split(DELIMITER);
        if (s[0].equals(OWLCLASS)){
            return mngr.getOWLDataFactory().getOWLClass(URI.create(s[1]));
        }
        else if (s[0].equals(OWLOBJECTPROPERTY)){
            return mngr.getOWLDataFactory().getOWLObjectProperty(URI.create(s[1]));
        }
        else if (s[0].equals(OWLDATAPROPERTY)){
            return mngr.getOWLDataFactory().getOWLDataProperty(URI.create(s[1]));
        }
        else if (s[0].equals(OWLINDIVIDUAL)){
            return mngr.getOWLDataFactory().getOWLIndividual(URI.create(s[1]));
        }
        else if (s[0].equals(OWLDATATYPE)){
            return mngr.getOWLDataFactory().getOWLDataType(URI.create(s[1]));
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
            cf = OWLCLASS + DELIMITER + entity.getURI();
        }


        public void visit(OWLObjectProperty entity) {
            cf = OWLOBJECTPROPERTY + DELIMITER + entity.getURI();
        }


        public void visit(OWLDataProperty entity) {
            cf = OWLDATAPROPERTY + DELIMITER + entity.getURI();
        }


        public void visit(OWLIndividual entity) {
            cf = OWLINDIVIDUAL + DELIMITER + entity.getURI();
        }


        public void visit(OWLDataType entity) {
            cf = OWLDATATYPE + DELIMITER + entity.getURI();
        }
    }
}
