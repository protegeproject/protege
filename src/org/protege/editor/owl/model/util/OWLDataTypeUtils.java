package org.protege.editor.owl.model.util;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
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
 * Date: Sep 11, 2008<br><br>
 */
public class OWLDataTypeUtils {

    private OWLOntologyManager mngr;


    public OWLDataTypeUtils(OWLOntologyManager mngr) {
        this.mngr = mngr;
    }


    public Set<OWLDatatype> getBuiltinDatatypes(){
        Set<OWLDatatype> datatypes = new HashSet<OWLDatatype>();
        final OWLDataFactory df = mngr.getOWLDataFactory();

        datatypes.add(df.getTopDatatype());
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            datatypes.add(df.getOWLDatatype(IRI.create(uri)));
        }
        datatypes.add(df.getOWLDatatype(OWLRDFVocabulary.RDF_XML_LITERAL.getIRI()));

        return datatypes;
    }


    public Set<OWLDatatype> getReferencedDatatypes(Set<OWLOntology> onts){
        Set<OWLDatatype> referencedTypes = new HashSet<OWLDatatype>();
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
