package org.protege.editor.owl.model.description.anonymouscls;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectDuplicator;

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
 * Date: Jan 7, 2009<br><br>
 */
public class ADCRewriter {

    private AnonymousDefinedClassManager adcManager;

    private OWLObjectDuplicator duplicator;


    public ADCRewriter(final AnonymousDefinedClassManager adcManager, final OWLDataFactory df) {
        this.adcManager = adcManager;
        this.duplicator = new OWLObjectDuplicator(df){
            public void visit(OWLClass owlClass) {
                if (adcManager.isAnonymous(owlClass)){
                    setLastObject(adcManager.getExpression(owlClass));
                }
                else{
                    setLastObject(owlClass);
                }
            }
        };
    }


    public List<OWLOntologyChange> rewriteChanges(List<? extends OWLOntologyChange> changes){
        List<OWLOntologyChange> rewrittenChanges = new ArrayList<OWLOntologyChange>();
        for (OWLOntologyChange chg : changes){
            rewrittenChanges.add(rewriteChange(chg));
        }
        return rewrittenChanges;
    }


    public OWLOntologyChange rewriteChange(OWLOntologyChange chg) {
        if (!chg.isAxiomChange()){
            return chg;
        }
        boolean rewriteRequired = false;
        for (OWLEntity entity : chg.getAxiom().getSignature()){
            if (entity.isOWLClass() && adcManager.isAnonymous(entity.asOWLClass())){
                rewriteRequired = true;
                break;
            }
        }
        if (rewriteRequired){
            if (chg instanceof AddAxiom){
                return new AddAxiom(chg.getOntology(), rewriteAxiom(chg.getAxiom()));
            }
            else{
                return new RemoveAxiom(chg.getOntology(), rewriteAxiom(chg.getAxiom()));
            }
        }
        return chg;
    }


    private OWLAxiom rewriteAxiom(OWLAxiom axiom) {
        return duplicator.duplicateObject(axiom);
    }

}
