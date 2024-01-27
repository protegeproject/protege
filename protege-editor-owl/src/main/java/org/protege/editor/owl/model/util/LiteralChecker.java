package org.protege.editor.owl.model.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.SAXParsers;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 1 Jun 16
 */
public class LiteralChecker {

    public static boolean isLiteralIsInLexicalSpace(OWLLiteral literal) {
        OWLDatatype d = literal.getDatatype();
        if(d.isRDFPlainLiteral()) {
            return true;
        }
        if(d.isString()) {
            return true;
        }
        if(d.isBuiltIn()) {
            OWL2Datatype builtIn = d.getBuiltInDatatype();
            if (builtIn.equals(OWL2Datatype.RDF_XML_LITERAL)) {
                return checkXMLLiteral(literal);
            }
            else {
                Pattern pattern = builtIn.getPattern();
                return pattern.matcher(literal.getLiteral()).matches();
            }
        }
        else {
            return true;
        }
    }

    private static boolean checkXMLLiteral(OWLLiteral literal) {
        try {
            SAXParsers.initParserWithOWLAPIStandards(null, "1000000").parse(new InputSource(new StringReader(literal.getLiteral())),
                    new DefaultHandler());
            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

}
