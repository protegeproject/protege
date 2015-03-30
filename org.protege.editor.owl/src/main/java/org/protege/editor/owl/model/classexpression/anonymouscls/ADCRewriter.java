package org.protege.editor.owl.model.classexpression.anonymouscls;

import java.util.ArrayList;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;

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
