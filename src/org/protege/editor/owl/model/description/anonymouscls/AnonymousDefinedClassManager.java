package org.protege.editor.owl.model.description.anonymouscls;

import org.apache.log4j.Logger;
import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.AutoIDException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.PseudoRandomAutoIDGenerator;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.ArrayList;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 24, 2008<br><br>
 */
public class AnonymousDefinedClassManager implements Disposable {

    public static final String ID = AnonymousDefinedClassManager.class.getName();

    private static final String DEFAULT_ANON_CLASS_URI_PREFIX = "http://www.co-ode.org/ontologies/owlx/anon#";

    private static final URI DEFAULT_ANON_CLASS_ANNOTATION_URI = URI.create(DEFAULT_ANON_CLASS_URI_PREFIX + "anonClass");

    private OWLModelManager mngr;

    private PseudoRandomAutoIDGenerator idGen;

    private ADCRewriter adcRewriter;

    private ADCFactory adcFactory;

    private IOListener ioListener = new IOListener(){

        public void beforeSave(IOListenerEvent event) {
            //To change body of implemented methods use File | Settings | File Templates.
        }


        public void afterSave(IOListenerEvent event) {
            // do nothing
        }


        public void beforeLoad(IOListenerEvent event) {
            // do nothing
        }


        public void afterLoad(IOListenerEvent event) {
            URI uri = event.getOntologyURI();
            OWLOntology ont = mngr.getOWLOntologyManager().getOntology(uri);
            adcFactory.getADCsForOntology(ont);
        }
    };



    public AnonymousDefinedClassManager(OWLModelManager mngr) {
        this.mngr = mngr;

        idGen = new PseudoRandomAutoIDGenerator();

        adcRewriter = new ADCRewriter(this, mngr.getOWLDataFactory());

        adcFactory = new ADCFactory(this);

        mngr.addIOListener(ioListener);
    }


    public boolean isAnonymous(OWLClass cls){
        if(cls.getURI().toString().startsWith(DEFAULT_ANON_CLASS_URI_PREFIX)){
            for (OWLOntology ont : mngr.getActiveOntologies()){
                if (ont.containsClassReference(cls.getURI())){
                    return true;
                }
            }
        }
        return false;
    }


    public OWLEntityCreationSet<OWLClass> createAnonymousClass(OWLOntology ont, OWLClassExpression descr){

        OWLClass anonCls = mngr.getOWLDataFactory().getOWLClass(getNextID());

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.add(new AddAxiom(ont, mngr.getOWLDataFactory().getOWLEquivalentClassesAxiom(anonCls, descr)));

        return new OWLEntityCreationSet<OWLClass>(anonCls, changes);
    }


    private URI getNextID() {
        URI uri;
        do{
            try {
                uri = URI.create(DEFAULT_ANON_CLASS_URI_PREFIX + idGen.getNextID(OWLClass.class));
            }
            catch (AutoIDException e) {
                throw new RuntimeException(e);
            }
        }
        while (entityExists(uri));
        return uri;
    }


    private boolean entityExists(URI uri) {
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (ont.containsClassReference(uri)){
                return true;
            }
        }
        return false;
    }


    public OWLClassExpression getExpression(OWLClass cls) {
        for (OWLClassExpression descr : cls.getEquivalentClasses(mngr.getActiveOntologies())){
            if (!descr.equals(cls)){
                return descr;
            }
        }
        Logger.getLogger(this.getClass()).error("Malformed Anonymous Defined Class: " + cls);
        return null;
    }


    public ADCRewriter getChangeRewriter(){
        return adcRewriter;
    }

    public void dispose() throws Exception {
        mngr = null;
    }


    public URI getURI() {
        return DEFAULT_ANON_CLASS_ANNOTATION_URI;
    }
}
