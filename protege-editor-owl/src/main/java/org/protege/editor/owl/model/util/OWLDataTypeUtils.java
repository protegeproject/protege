package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.HashSet;
import java.util.Set;
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
 * Date: Sep 11, 2008<br><br>
 */
public class OWLDataTypeUtils {

    private OWLOntologyManager mngr;


    public OWLDataTypeUtils(OWLOntologyManager mngr) {
        this.mngr = mngr;
    }


    public Set<OWLDatatype> getBuiltinDatatypes(){
        Set<OWLDatatype> datatypes = new HashSet<>();
        final OWLDataFactory df = mngr.getOWLDataFactory();

        datatypes.add(df.getTopDatatype());
        for (OWL2Datatype dt : OWL2Datatype.values()) {
            datatypes.add(df.getOWLDatatype(dt.getIRI()));
        }        
        return datatypes;
    }


    public Set<OWLDatatype> getReferencedDatatypes(Set<OWLOntology> onts){
        Set<OWLDatatype> referencedTypes = new HashSet<>();
        for (OWLOntology ont : onts){
            referencedTypes.addAll(ont.getDatatypesInSignature());
        }
        return referencedTypes;
    }


    public Set<OWLDatatype> getKnownDatatypes(Set<OWLOntology> onts){
        Set<OWLDatatype> knownTypes = getBuiltinDatatypes();
        for (OWLOntology ont : onts){
            knownTypes.addAll(ont.getDatatypesInSignature());
        }
        return knownTypes;
    }
}
