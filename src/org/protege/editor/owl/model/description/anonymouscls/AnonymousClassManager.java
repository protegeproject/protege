package org.protege.editor.owl.model.description.anonymouscls;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.AutoIDException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.PseudoRandomAutoIDGenerator;
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
public class AnonymousClassManager implements Disposable {

    public static final String ID = AnonymousClassManager.class.getName();

    private static final String DEFAULT_ANON_CLASS_URI_PREFIX = "http://www.co-ode.org/ontologies/owlx/anon#";

    private OWLModelManager mngr;

    private PseudoRandomAutoIDGenerator idGen;


    public AnonymousClassManager(OWLModelManager mngr) {
        this.mngr = mngr;
        idGen = new PseudoRandomAutoIDGenerator();
    }


    public boolean isAnonymous(OWLClass cls){
        return cls.getURI().toString().startsWith(DEFAULT_ANON_CLASS_URI_PREFIX);
    }


    public OWLEntityCreationSet<OWLClass> createAnonymousClass(OWLOntology ont, OWLDescription descr){

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


    public OWLDescription getExpression(OWLClass cls) {
        for (OWLDescription descr : cls.getEquivalentClasses(mngr.getActiveOntologies())){
            if (!descr.equals(cls)){
                return descr;
            }
        }
        return null;
    }


    public void dispose() throws Exception {
        mngr = null;
    }
}
