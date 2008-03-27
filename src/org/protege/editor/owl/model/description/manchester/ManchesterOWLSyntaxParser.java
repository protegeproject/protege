package org.protege.editor.owl.model.description.manchester;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxDescriptionParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.expression.OWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.vocab.Namespaces;
import org.semanticweb.owl.vocab.XSDVocabulary;

import java.net.URI;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Nov-2007<br><br>
 */
public class ManchesterOWLSyntaxParser implements OWLDescriptionParser {

    private OWLModelManager owlModelManager;

    private ManchesterOWLSyntaxDescriptionParser p;

    private static final String ESCAPE_CHAR = "'";


    public ManchesterOWLSyntaxParser(OWLModelManager man) {
        this.owlModelManager = man;
        OWLEntityChecker entityChecker = new OWLEntityChecker() {

            public OWLClass getOWLClass(String string) {
                OWLClass ent = owlModelManager.getOWLClass(string);
                if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
                    ent = getOWLClass(ESCAPE_CHAR + string + ESCAPE_CHAR);
                }
                return ent;
            }


            public OWLObjectProperty getOWLObjectProperty(String string) {
                OWLObjectProperty ent = owlModelManager.getOWLObjectProperty(string);
                if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
                    return getOWLObjectProperty(ESCAPE_CHAR + string + ESCAPE_CHAR);
                }
                return ent;
            }


            public OWLDataProperty getOWLDataProperty(String string) {
                OWLDataProperty ent = owlModelManager.getOWLDataProperty(string);
                if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
                    ent = getOWLDataProperty(ESCAPE_CHAR + string + ESCAPE_CHAR);
                }
                return ent;
            }


            public OWLIndividual getOWLIndividual(String string) {
                OWLIndividual ent = owlModelManager.getOWLIndividual(string);
                if (ent == null && !string.startsWith(ESCAPE_CHAR) && !string.endsWith(ESCAPE_CHAR)){
                    return getOWLIndividual(ESCAPE_CHAR + string + ESCAPE_CHAR);
                }
                return ent;
            }


            public OWLDataType getOWLDataType(String string) {
                String fullName = Namespaces.XSD + string;
                for(XSDVocabulary v : XSDVocabulary.values()) {
                    if(v.toString().equals(fullName)) {
                        return owlModelManager.getOWLDataFactory().getOWLDataType(URI.create(Namespaces.XSD + string));
                    }
                }
                return null;
            }

        };
        p = new ManchesterOWLSyntaxDescriptionParser(owlModelManager.getOWLDataFactory(), entityChecker);
    }


    public OWLClassAxiom createOWLClassAxiom(String expression) throws OWLExpressionParserException {
        return null;
    }


    public OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException {
        try {
            return p.parse(expression);
        }
        catch (ParserException e) {
            throw convertException(e);
        }
    }


    public SWRLRule createSWRLRule(String expression) throws OWLExpressionParserException {
        return null;
    }


    public boolean isClassAxiomWellFormed(String expression) throws OWLExpressionParserException {
        return false;
    }


    public boolean isSWRLRuleWellFormed(String expression) throws OWLExpressionParserException {
        return false;
    }


    public boolean isWellFormed(String expression) throws OWLExpressionParserException {
        try {
                p.parse(expression);
                return true;
        }
        catch (ParserException e) {
            throw convertException(e);
        }
    }


    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    private static OWLExpressionParserException convertException(ParserException ex) {
        int endPos = ex.getCurrentToken().length();
        if (ex.getCurrentToken().equals(ManchesterOWLParserConstants.tokenImage[ManchesterOWLParserConstants.EOF])){
            endPos = ex.getStartPos(); // because start + length of <EOF> would push us past the end of the document
        }
        return new OWLExpressionParserException(ex.getMessage(),
                                                ex.getStartPos(),
                                                endPos,
                                                ex.isClassNameExpected(),
                                                ex.isObjectPropertyNameExpected(),
                                                ex.isDataPropertyNameExpected(),
                                                ex.isIndividualNameExpected(),
                                                ex.isDatatypeNameExpected(),
                                                ex.getExpectedKeywords());
    }
}
