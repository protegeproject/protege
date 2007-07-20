package org.protege.editor.owl.model.description.manchester;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.*;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ManchesterSyntaxParser implements OWLDescriptionParser {

    private OWLModelManager owlModelManager;

    private EntityMapper entityMapper;

    private DataTypeMapper dataTypeMapper;

    private Map<Integer, String> tokenDescriptionMap;


    public void setOWLModelManager(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        entityMapper = new EntityMapperImpl(owlModelManager);
        dataTypeMapper = new DataTypeMapperImpl();
        tokenDescriptionMap = new HashMap<Integer, String>();
        addTokenDescription(ManchesterOWLParserConstants.ALL);
        addTokenDescription(ManchesterOWLParserConstants.AND);
        addTokenDescription(ManchesterOWLParserConstants.HAS);
        addTokenDescription(ManchesterOWLParserConstants.MAX);
        addTokenDescription(ManchesterOWLParserConstants.MIN);
        addTokenDescription(ManchesterOWLParserConstants.EXACT);
        addTokenDescription(ManchesterOWLParserConstants.NOT);
        addTokenDescription(ManchesterOWLParserConstants.OR);
        addTokenDescription(ManchesterOWLParserConstants.SOME);
        addTokenDescription(ManchesterOWLParserConstants.THAT);
        addTokenDescription(ManchesterOWLParserConstants.OPENBRACE);
        addTokenDescription(ManchesterOWLParserConstants.CLOSEBRACE);
        addTokenDescription(ManchesterOWLParserConstants.OPENPAR);
        addTokenDescription(ManchesterOWLParserConstants.CLOSEPAR);
        addTokenDescription(ManchesterOWLParserConstants.OPENSQPAR);
        addTokenDescription(ManchesterOWLParserConstants.CLOSESQPAR);
        addTokenDescription(ManchesterOWLParserConstants.CLASSID, "Class name");
        addTokenDescription(ManchesterOWLParserConstants.OBJECTPROPID, "Object property name");
        addTokenDescription(ManchesterOWLParserConstants.DATAPROPID, "Data property name");
        addTokenDescription(ManchesterOWLParserConstants.INDIVIDUALID, "Individual name");
        addTokenDescription(ManchesterOWLParserConstants.DATATYPEID, "Data type name (e.g. int, float)");
        addTokenDescription(ManchesterOWLParserConstants.DATAVALUE, "(e.g. 3.2, 5)");
        addTokenDescription(ManchesterOWLParserConstants.QUOTE);
        addTokenDescription(ManchesterOWLParserConstants.SUBCLASSOF);
        addTokenDescription(ManchesterOWLParserConstants.DISJPOINTWITH);
        addTokenDescription(ManchesterOWLParserConstants.EQUIVALENTTO);
        addTokenDescription(ManchesterOWLParserConstants.PLUS);
        addTokenDescription(ManchesterOWLParserConstants.MINUS);
        addTokenDescription(ManchesterOWLParserConstants.POSSIBLY);
        addTokenDescription(ManchesterOWLParserConstants.COMPOSITION, "Role composition operator (o)");
        addTokenDescription(ManchesterOWLParserConstants.COMMA);
        addTokenDescription(ManchesterOWLParserConstants.LEN);
        addTokenDescription(ManchesterOWLParserConstants.MAX_EXC);
        addTokenDescription(ManchesterOWLParserConstants.MAX_INC);
        addTokenDescription(ManchesterOWLParserConstants.MIN_INC);
        addTokenDescription(ManchesterOWLParserConstants.MIN_EXC);
        addTokenDescription(ManchesterOWLParserConstants.PAT);
        addTokenDescription(ManchesterOWLParserConstants.DOUBLE_CARET, "Data type marker '^^'");
        addTokenDescription(ManchesterOWLParserConstants.BOOLEAN, "Boolean value e.g. 'true' or 'false'");
        addTokenDescription(ManchesterOWLParserConstants.FLOAT, "Float value e.g. 33.3");
        addTokenDescription(ManchesterOWLParserConstants.INT, "Integer value e.g. 33");
        addTokenDescription(ManchesterOWLParserConstants.STRING, "String");
        addTokenDescription(ManchesterOWLParserConstants.RULE_IMP, "Rule implication symbol (\u2192)");
        addTokenDescription(ManchesterOWLParserConstants.RULE_IMP, "Rule atom conjunction symbol (\u2227)");
        addTokenDescription(ManchesterOWLParserConstants.QUESTION_MARK);
    }


    private void addTokenDescription(int token, String desc) {
        tokenDescriptionMap.put(token, desc);
    }


    private void addTokenDescription(int token) {
        addTokenDescription(token, getTokenImage(token));
    }


    private static String getTokenImage(int token) {
        return ManchesterOWLParserConstants.tokenImage[token];
    }


    public boolean isWellFormed(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              null,
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            manchesterOWLParser.Parse();
            return true;
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public boolean isClassAxiomWellFormed(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              null,
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            manchesterOWLParser.ParseClassAxiom();
            return true;
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public boolean isWellFormedObjectPropertyChain(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              null,
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            manchesterOWLParser.ParsePropertyChain();
            return true;
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public List<OWLObjectPropertyExpression> createObjectPropertyChain(String expression) throws
                                                                                          OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              owlModelManager.getOWLDataFactory(),
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            return manchesterOWLParser.ParsePropertyChain();
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public boolean isSWRLRuleWellFormed(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              owlModelManager.getOWLDataFactory(),
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            manchesterOWLParser.ParseRule();
            return true;
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public SWRLRule createSWRLRule(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                              owlModelManager.getOWLDataFactory(),
                                                                              entityMapper,
                                                                              dataTypeMapper,
                                                                              new StringReader(expression));
            return manchesterOWLParser.ParseRule();
        }
        catch (ParseException e) {
            throw getOWLDescriptionParserException(e);
        }
    }


    public OWLClassAxiom createOWLClassAxiom(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser parser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                 owlModelManager.getOWLDataFactory(),
                                                                 entityMapper,
                                                                 dataTypeMapper,
                                                                 new StringReader(expression)

            );
            try {
                return parser.ParseClassAxiom();
            }
            catch (ParseException e) {
                throw getOWLDescriptionParserException(e);
            }
        }
        catch (OWLException e) {
            throw new OWLExpressionParserException(e);
        }
    }


    public OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException {
        try {
            ManchesterOWLParser parser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
                                                                 owlModelManager.getOWLDataFactory(),
                                                                 entityMapper,
                                                                 dataTypeMapper,
                                                                 new StringReader(expression)

            );
            try {
                return parser.Parse();
            }
            catch (ParseException e) {
                throw getOWLDescriptionParserException(e);
            }
        }
        catch (OWLException e) {
            throw new OWLExpressionParserException(e);
        }
    }

//    public boolean isWellFormedNode(String text) throws OWLExpressionParserException, OWLException {
//        try {
//            ManchesterOWLParser manchesterOWLParser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
//                                                                              null,
//                                                                              entityMapper,
//                                                                              dataTypeMapper,
//                                                                              new StringReader(text));
//            manchesterOWLParser.ParseQuery();
//            return true;
//        }
//        catch (ParseException e) {
//            throw getOWLDescriptionParserException(e);
//        }
//    }
//
//
//    public OWLDescriptionNode createOWLDescriptionNode(String text) throws OWLExpressionParserException, OWLException {
//        try {
//            ManchesterOWLParser parser = new ManchesterOWLParser(owlModelManager.getActiveOntologies(),
//                                                                 owlModelManager.getOWLDataFactory(),
//                                                                 entityMapper,
//                                                                 dataTypeMapper,
//                                                                 new StringReader(text)
//
//            );
//            try {
//                return parser.ParseQuery();
//            }
//            catch (ParseException e) {
//                throw getOWLDescriptionParserException(e);
//            }
//        }
//        catch (OWLException e) {
//            throw new OWLExpressionParserException(e);
//        }
//    }


    private static boolean isTokenExpected(ParseException e, int tokenId) {
        for (int i = 0; i < e.expectedTokenSequences.length; i++) {
            int[] currentSequence = e.expectedTokenSequences[i];
            int expectedToken = currentSequence[currentSequence.length - 1];
            if (tokenId == expectedToken) {
                return true;
            }
        }
        return false;
    }


    private OWLExpressionParserException getOWLDescriptionParserException(ParseException e) {
        return new OWLExpressionParserException(getErrorMessage(e),

                                                e.currentToken.next.beginColumn - 1,
                                                e.currentToken.next.endColumn,
                                                isTokenExpected(e, ManchesterOWLParserConstants.CLASSID),
                                                isTokenExpected(e, ManchesterOWLParserConstants.OBJECTPROPID),
                                                isTokenExpected(e, ManchesterOWLParserConstants.DATAPROPID),
                                                isTokenExpected(e, ManchesterOWLParserConstants.INDIVIDUALID),
                                                isTokenExpected(e, ManchesterOWLParserConstants.DATATYPEID),
                                                getExpectedKeyWords(e));
    }


    private String getErrorMessage(ParseException e) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Encountered \"");
        buffer.append(e.currentToken.next.image);
        buffer.append("\" at line ");
        buffer.append(e.currentToken.next.beginLine);
        buffer.append(", column ");
        buffer.append(e.currentToken.next.beginColumn);
        buffer.append('\n');
        buffer.append("Was expecting either:\n");
        Set<Integer> added = new HashSet<Integer>();
        for (int i = 0; i < e.expectedTokenSequences.length; i++) {
            int current = e.expectedTokenSequences[i][0];
            if (!added.contains(current)) {
                buffer.append("\t\t");
                buffer.append(tokenDescriptionMap.get(current));
                buffer.append('\n');
                added.add(current);
            }
        }
        return buffer.toString();
    }


    private static Set<String> getExpectedKeyWords(ParseException e) {
        Set<String> expectedKeyWords = new HashSet<String>();
        addIfExpected(e, ManchesterOWLParserConstants.ALL, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.AND, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.EXACT, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.HAS, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.MAX, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.MIN, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.NOT, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.OR, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.SOME, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.THAT, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.SUBCLASSOF, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.DISJPOINTWITH, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.EQUIVALENTTO, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.MINUS, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.PLUS, expectedKeyWords);
        addIfExpected(e, ManchesterOWLParserConstants.POSSIBLY, expectedKeyWords);
        return expectedKeyWords;
    }


    private static void addIfExpected(ParseException e, int tokenId, Set<String> keyWords) {
        if (isTokenExpected(e, tokenId)) {
            String s = ManchesterOWLParserConstants.tokenImage[tokenId];
            keyWords.add(s.substring(1, s.length() - 1));
        }
    }
}
