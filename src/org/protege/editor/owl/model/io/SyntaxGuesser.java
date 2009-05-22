package org.protege.editor.owl.model.io;

import de.uulm.ecs.ai.owl.krssparser.KRSS2OntologyFormat;
import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.obo.parser.OBOOntologyFormat;
import org.coode.owl.rdf.turtle.TurtleOntologyFormat;
import org.semanticweb.owl.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.OWLOntologyFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * Date: May 11, 2009<br><br>
 *
 * A really nasty fail fast quick-check of the file contents to work out which format it is.
 * This does not use SAX for xml and is unlikely to work if namespaces are varied
 */
public class SyntaxGuesser {

    public OWLOntologyFormat getSyntax(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine().trim();
        if (line.startsWith("<?xml")){
            while (true){
                line = reader.readLine().trim();
                if (line.length() > 0){
                    if (line.contains("<!ENTITY") || line.contains("<!DOCTYPE") || line.contains("]>")){

                    }
                    else if (line.contains("<rdf:RDF")){
                        return new RDFXMLOntologyFormat();
                    }
                    else if (line.contains("<Ontology") || line.contains("<owl2xml:Ontology")){
                        return new OWLXMLOntologyFormat();
                    }
                    else{
                        return null;
                    }
                }
            }
        }
        else if (line.startsWith("Namespace:")){
            return new ManchesterOWLSyntaxOntologyFormat();
        }
        else if (line.startsWith("Namespace(")){
            return new OWLFunctionalSyntaxOntologyFormat();
        }
        else if (line.startsWith("@prefix")){
            return new TurtleOntologyFormat();
        }
        else if (line.startsWith("(define-")){
            return new KRSS2OntologyFormat();
        }
        else if (line.startsWith("format-version: 1.2")){
            return new OBOOntologyFormat();
        }
        return null;
    }
}
