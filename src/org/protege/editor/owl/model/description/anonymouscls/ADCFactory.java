package org.protege.editor.owl.model.description.anonymouscls;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.parser.OWLParseException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Date: Jan 8, 2009<br><br>
 */
public class ADCFactory extends OWLAxiomVisitorAdapter {

    private static final Logger logger = Logger.getLogger(ADCFactory.class);

    private AnonymousDefinedClassManager adcManager;

    private Set<OWLDescription> descrs = new HashSet<OWLDescription>();


    public ADCFactory(AnonymousDefinedClassManager adcManager) {
        this.adcManager = adcManager;
    }


    public List<OWLOntologyChange> getADCsForOntology(OWLOntology ont){
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        descrs.clear();
        for (OWLClassAxiom ax : ont.getGeneralClassAxioms()){
            ax.accept(this);
        }

        for (OWLOntologyAnnotationAxiom ax : ont.getAnnotations(ont)){
            ax.accept(this);
        }

        for (OWLDescription descr : descrs){
            OWLEntityCreationSet<OWLClass> chSet = adcManager.createAnonymousClass(ont, descr);
            changes.addAll(chSet.getOntologyChanges());
        }

        return changes;
    }


    public void visit(OWLSubClassAxiom ax) {
        if (ax.getSubClass().isAnonymous()){
            descrs.add(ax.getSubClass());
        }
    }


    public void visit(OWLEquivalentClassesAxiom ax) {
        for (OWLDescription descr : ax.getDescriptions()){
            if (descr.isAnonymous()){
                descrs.add(descr);
            }
        }
    }


    public void visit(OWLOntologyAnnotationAxiom ax) {
        if (ax.getAnnotation().getAnnotationURI().equals(adcManager.getAnnotationURI())){
            String value = ax.getAnnotation().getAnnotationValueAsConstant().getLiteral();
            try{
                OWLDescription descr = parseOWLDescription(value);
                descrs.add(descr);
            }
            catch(OWLParseException e){
                logger.error(e);
            }
        }
    }


    private OWLDescription parseOWLDescription(String s) throws OWLParseException {
        throw new OWLParseException("Retrieving ADCs from annotations not currently implemented");
    }
}
