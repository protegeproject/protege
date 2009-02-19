package org.protege.editor.owl.model.parser;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.Namespaces;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import java.net.URI;/*
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
 * Date: May 8, 2008<br><br>
 */
public class ProtegeOWLEntityChecker implements OWLEntityChecker {

    private static final String ESCAPE_CHAR = "'";

    private OWLModelManager mngr;


    public ProtegeOWLEntityChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public OWLClass getOWLClass(String string) {
        OWLClass ent = mngr.getOWLClass(string);
        if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
            ent = getOWLClass(ESCAPE_CHAR + string + ESCAPE_CHAR);
        }
        return ent;
    }


    public OWLObjectProperty getOWLObjectProperty(String string) {
        OWLObjectProperty ent = mngr.getOWLObjectProperty(string);
        if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
            return getOWLObjectProperty(ESCAPE_CHAR + string + ESCAPE_CHAR);
        }
        return ent;
    }


    public OWLDataProperty getOWLDataProperty(String string) {
        OWLDataProperty ent = mngr.getOWLDataProperty(string);
        if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
            ent = getOWLDataProperty(ESCAPE_CHAR + string + ESCAPE_CHAR);
        }
        return ent;
    }


    public OWLIndividual getOWLIndividual(String string) {
        OWLIndividual ent = mngr.getOWLIndividual(string);
        if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
            return getOWLIndividual(ESCAPE_CHAR + string + ESCAPE_CHAR);
        }
        return ent;
    }


    public OWLDataType getOWLDataType(String string) {
        if (OWLRDFVocabulary.RDFS_LITERAL.getShortName().equals(string)){
            return mngr.getOWLDataFactory().getOWLDataType(OWLRDFVocabulary.RDFS_LITERAL.getURI());
        }
        String fullName = Namespaces.XSD + string;
        for(XSDVocabulary v : XSDVocabulary.values()) {
            if(v.toString().equals(fullName)) {
                return mngr.getOWLDataFactory().getOWLDataType(URI.create(Namespaces.XSD + string));
            }
        }
        return null;
    }
}
