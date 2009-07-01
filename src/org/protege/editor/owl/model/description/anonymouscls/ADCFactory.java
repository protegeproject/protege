package org.protege.editor.owl.model.description.anonymouscls;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.parser.OWLParseException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

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
public class ADCFactory extends OWLObjectVisitorAdapter {

    private static final Logger logger = Logger.getLogger(ADCFactory.class);

    private AnonymousDefinedClassManager adcManager;

    private Set<OWLClassExpression> descrs = new HashSet<OWLClassExpression>();


    public ADCFactory(AnonymousDefinedClassManager adcManager) {
        this.adcManager = adcManager;
    }


    public List<OWLOntologyChange> getADCsForOntology(OWLOntology ont){
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        descrs.clear();
        for (OWLClassAxiom ax : ont.getGeneralClassAxioms()){
            ax.accept(this);
        }

        for (OWLAnnotation annotation : ont.getAnnotations()){ // get annotations on ontology
            annotation.accept(this);
        }

        for (OWLClassExpression descr : descrs){
            OWLEntityCreationSet<OWLClass> chSet = adcManager.createAnonymousClass(ont, descr);
            changes.addAll(chSet.getOntologyChanges());
        }

        return changes;
    }


    public void visit(OWLSubClassOfAxiom ax) {
        if (ax.getSubClass().isAnonymous()){
            descrs.add(ax.getSubClass());
        }
    }


    public void visit(OWLEquivalentClassesAxiom ax) {
        for (OWLClassExpression descr : ax.getClassExpressions()){
            if (descr.isAnonymous()){
                descrs.add(descr);
            }
        }
    }


    public void visit(OWLAnnotation annotation) {
        if (annotation.getProperty().getURI().equals(adcManager.getURI())){
            annotation.getValue().accept(this);
        }
    }


    public void visit(OWLStringLiteral literal) {
        try{
            OWLClassExpression descr = parseOWLDescription(literal.getLiteral());
            descrs.add(descr);
        }
        catch(OWLParseException e){
            logger.error(e);
        }
    }


    public void visit(OWLTypedLiteral owlTypedLiteral) {
        try{
            OWLClassExpression descr = parseOWLDescription(owlTypedLiteral.getLiteral());
            descrs.add(descr);
        }
        catch(OWLParseException e){
            logger.error(e);
        }
    }


    private OWLClassExpression parseOWLDescription(String s) throws OWLParseException {
        throw new OWLParseException("Retrieving ADCs from annotations not currently implemented");
    }
}
