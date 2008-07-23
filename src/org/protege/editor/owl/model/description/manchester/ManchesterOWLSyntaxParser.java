package org.protege.editor.owl.model.description.manchester;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.SWRLRule;

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
 *
 * @deprecated use <code>OWLExpressionCheckerFactory</code>
 */
public class ManchesterOWLSyntaxParser implements OWLDescriptionParser {

    private OWLModelManager mngr;

    public ManchesterOWLSyntaxParser(OWLModelManager man) {
        this.mngr = man;
    }


    public OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLSyntaxEditorParser p = setupParser(expression);
            return p.parseDescription();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }

    public boolean isWellFormed(String expression) throws OWLExpressionParserException {
        createOWLDescription(expression);
        return true;
    }


    public OWLClassAxiom createOWLClassAxiom(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLSyntaxEditorParser p = setupParser(expression);
            return p.parseClassAxiom();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }


    public boolean isClassAxiomWellFormed(String expression) throws OWLExpressionParserException {
        createOWLClassAxiom(expression);
        return true;
    }


    public SWRLRule createSWRLRule(String expression) throws OWLExpressionParserException {
        return null; //@@TODO can use ManchesterSyntaxPArser temporarilly
    }


    public boolean isSWRLRuleWellFormed(String expression) throws OWLExpressionParserException {
        return false;
    }


    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.mngr = owlModelManager;
    }


    private ManchesterOWLSyntaxEditorParser setupParser(String expression) {
        ManchesterOWLSyntaxEditorParser p = new ManchesterOWLSyntaxEditorParser(mngr.getOWLDataFactory(), expression);
        p.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr));
        p.setBase(mngr.getActiveOntology().getURI().toString());
        return p;
    }
}
