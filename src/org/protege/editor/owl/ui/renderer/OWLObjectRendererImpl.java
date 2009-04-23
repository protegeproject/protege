package org.protege.editor.owl.ui.renderer;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLDescriptionComparator;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owl.vocab.OWLFacet;
import org.semanticweb.owl.vocab.XSDVocabulary;

import java.net.URI;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A renderer that renders objects using the Manchester OWL Syntax.
 * Axiom level OWLObjects are rendered in Manchester "style"
 */
public class OWLObjectRendererImpl implements OWLObjectVisitor, OWLObjectRenderer {

    private static final Logger logger = Logger.getLogger(OWLObjectRendererImpl.class);

    private OWLModelManager owlModelManager;

    private StringBuilder buffer;

    private BracketWriter bracketWriter;

    private Map<OWLFacet, String> facetMap;

    private Map<URI, Boolean> simpleRenderDatatypes;

    private OWLObject focusedObject;

    private OWLEntityRenderer entityRenderer;


    public OWLObjectRendererImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        buffer = new StringBuilder();
        bracketWriter = new BracketWriter();
        facetMap = new HashMap<OWLFacet, String>();
        facetMap.put(OWLFacet.MIN_EXCLUSIVE, ">");
        facetMap.put(OWLFacet.MAX_EXCLUSIVE, "<");
        facetMap.put(OWLFacet.MIN_INCLUSIVE, ">=");
        facetMap.put(OWLFacet.MAX_INCLUSIVE, "<=");
        simpleRenderDatatypes = new HashMap<URI, Boolean>();
        simpleRenderDatatypes.put(XSDVocabulary.INT.getURI(), false);
        simpleRenderDatatypes.put(XSDVocabulary.FLOAT.getURI(), false);
        simpleRenderDatatypes.put(XSDVocabulary.DOUBLE.getURI(), false);
        simpleRenderDatatypes.put(XSDVocabulary.STRING.getURI(), true);
        simpleRenderDatatypes.put(XSDVocabulary.BOOLEAN.getURI(), false);
    }


    public void setup(OWLModelManager owlModelManager) {
    }


    public void initialise() {
    }


    public void dispose() {
    }


    public OWLObject getFocusedObject() {
        return focusedObject;
    }


    public void setFocusedObject(OWLObject focusedObject) {
        this.focusedObject = focusedObject;
        if(focusedObject instanceof OWLClassExpression) {
            comparator.setFocusedDescription((OWLClassExpression) focusedObject);
        }
    }


    protected String getAndKeyWord() {
        return "and";
    }


    protected String getOrKeyWord() {
        return "or";
    }


    protected String getNotKeyWord() {
        return "not";
    }


    protected String getSomeKeyWord() {
        return "some";
    }


    protected String getAllKeyWord() {
        return "only";
    }


    protected String getValueKeyWord() {
        return "value";
    }


    protected String getMinKeyWord() {
        return "min";
    }


    protected String getMaxKeyWord() {
        return "max";
    }


    protected String getExactlyKeyWord() {
        return "exactly";
    }


    public String render(OWLObject object, OWLEntityRenderer entityRenderer) {
        reset();
        this.entityRenderer = entityRenderer;
        try {
            object.accept(this);
            return buffer.toString();
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
    }


    protected String getRendering(OWLEntity entity) {
        if (entityRenderer != null){
            return entityRenderer.render(entity);
        }
        return owlModelManager.getRendering(entity);
    }


    int lastNewLineIndex = 0;

    int currentIndex = 0;


    protected void write(String s) {
        int index = s.indexOf('\n');
        if (index != -1) {
            lastNewLineIndex = currentIndex + index;
        }
        currentIndex = currentIndex + s.length();
        buffer.append(s);
    }


    protected int getIndent() {
        return currentIndex - lastNewLineIndex;
    }


    protected void insertIndent(int indent) {
        for (int i = 0; i < indent; i++) {
            write(" ");
        }
    }


    protected void writeAndKeyword() {
        write(getAndKeyWord());
        write(" ");
    }


    public void reset() {
        lastNewLineIndex = 0;
        currentIndex = 0;
        buffer = new StringBuilder();
    }


    public String getText() {
        return buffer.toString();
    }


    private OWLDescriptionComparator comparator = new OWLDescriptionComparator(owlModelManager);


    private List<OWLClassExpression> sortClassExpressions(Set<OWLClassExpression> expressions) {
        List<OWLClassExpression> sortedDescs = new ArrayList<OWLClassExpression>(expressions);
        Collections.sort(sortedDescs, comparator);
        return sortedDescs;
    }


    public void visit(OWLObjectIntersectionOf node) {

        int indent = getIndent();
        List<OWLClassExpression> ops = sortClassExpressions(node.getOperands());
        for (int i = 0; i < ops.size(); i++) {
            OWLClassExpression curOp = ops.get(i);
            curOp.accept(this);
            if (i < ops.size() - 1) {
                write("\n");
                insertIndent(indent);
                if (curOp instanceof OWLClass && ops.get(i + 1) instanceof OWLRestriction && OWLRendererPreferences.getInstance().isUseThatKeyword()) {
                    write("that ");
                }
                else {
                    writeAndKeyword();
                }
            }
        }
    }


    public void visit(OWLTypedLiteral node) {
        if (simpleRenderDatatypes.containsKey(node.getDatatype().getURI())) {
            boolean renderQuotes = simpleRenderDatatypes.get(node.getDatatype().getURI());
            if (renderQuotes) {
                write("\"");
            }
            write(node.getLiteral());
            if (renderQuotes) {
                write("\"");
            }
        }
        else {
            write("\"");
            write(node.getLiteral());
            write("\"^^");
            node.getDatatype().accept(this);
        }
    }


    public void visit(OWLRDFTextLiteral node) {
        write("\"");
        write(node.getLiteral());
        write("\"");
        if (node.getLang() != null) {
            write("@");
            write(node.getLang());
        }
    }


    public void visit(OWLDatatype node) {
        write(node.getURI().getFragment());
    }


    public void visit(OWLDataOneOf node) {
        write("{");
        for (Iterator<OWLLiteral> it = node.getValues().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write("}");
    }


    public void visit(OWLDataComplementOf owlDataComplementOf) {
        write("not(");
        owlDataComplementOf.getDataRange().accept(this);
        write(")");
    }


    public void visit(OWLDataIntersectionOf owlDataIntersectionOf) {
        throw new NotImplementedException("Cannot render " + owlDataIntersectionOf);
    }


    public void visit(OWLDatatypeRestriction node) {
//        writeOpenBracket(node);
        node.getDatatype().accept(this);
        write("[");
        for (Iterator<OWLFacetRestriction> it = node.getFacetRestrictions().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write("]");
//        writeCloseBracket(node);
    }


    public void visit(OWLFacetRestriction node) {
        String rendering = facetMap.get(node.getFacet());
        if (rendering == null) {
            rendering = node.getFacet().getShortName();
        }
        write(rendering);
        write(" ");
        node.getFacetValue().accept(this);
    }


    public void visit(OWLObjectHasSelf desc) {
        desc.getProperty().accept(this);
        write(" ");
        write(getSomeKeyWord());
        write(" Self");
    }


    public void visit(OWLDataAllValuesFrom node) {
        node.getProperty().accept(this);
        write(" ");
        write(getAllKeyWord());
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLDataSomeValuesFrom node) {
        node.getProperty().accept(this);
        write(" ");
        write(getSomeKeyWord());
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataHasValue node) {
        node.getProperty().accept(this);
        write(" ");
        write(getValueKeyWord());
        write(" ");
        node.getValue().accept(this);
    }


    public void visit(OWLObjectAllValuesFrom node) {
        node.getProperty().accept(this);
        write(" ");
        write(getAllKeyWord());
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }


    public void visit(OWLObjectMinCardinality desc) {
        writeCardinality(desc, getMinKeyWord());
    }


    public void visit(OWLObjectExactCardinality desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }


    public void visit(OWLObjectMaxCardinality desc) {
        writeCardinality(desc, getMaxKeyWord());
    }


    private void writeCardinality(OWLObjectCardinalityRestriction desc, String keyword) {
        desc.getProperty().accept(this);
        write(" ");
        write(keyword);
        write(" ");
        write(Integer.toString(desc.getCardinality()));
        write(" ");
        writeOpenBracket(desc.getFiller());
        desc.getFiller().accept(this);
        writeCloseBracket(desc.getFiller());
    }


    public void visit(OWLDataMinCardinality desc) {
        writeCardinality(desc, getMinKeyWord());
    }


    public void visit(OWLDataExactCardinality desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }


    public void visit(OWLDataMaxCardinality desc) {
        writeCardinality(desc, getMaxKeyWord());
    }


    private void writeCardinality(OWLDataCardinalityRestriction desc, String keyword) {
        desc.getProperty().accept(this);
        write(" ");
        write(keyword);
        write(" ");
        write(Integer.toString(desc.getCardinality()));
        write(" ");
        writeOpenBracket(desc.getFiller());
        desc.getFiller().accept(this);
        writeCloseBracket(desc.getFiller());
    }


    public void visit(OWLObjectProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLObjectSomeValuesFrom node) {
        node.getProperty().accept(this);
        write(" ");
        write(getSomeKeyWord());
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }


    public void visit(OWLObjectHasValue node) {
        node.getProperty().accept(this);
        write(" ");
        write(getValueKeyWord());
        write(" ");
        node.getValue().accept(this);
    }


    public void visit(OWLObjectComplementOf node) {
        writeNotKeyword();
        write(" ");
        writeOpenBracket(node.getOperand());
        node.getOperand().accept(this);
        writeCloseBracket(node.getOperand());
    }


    protected void writeNotKeyword() {
        write(getNotKeyWord());
    }


    public void visit(OWLObjectUnionOf node) {
        int indent = getIndent();
        for (Iterator it = sortClassExpressions(node.getOperands()).iterator(); it.hasNext();) {
            OWLClassExpression curOp = (OWLClassExpression) it.next();
            writeOpenBracket(curOp);
            curOp.accept(this);
            writeCloseBracket(curOp);
            if (it.hasNext()) {
                write("\n");
                insertIndent(indent);
                writeOrKeyword();
            }
        }
    }


    private void writeOrKeyword() {
        write(getOrKeyWord());
        write(" ");
    }


    public void visit(OWLClass node) {
        write(getRendering(node));
    }


    public void visit(OWLObjectPropertyInverse property) {
        write("inv(");
        property.getInverse().accept(this);
        write(")");
    }


    public void visit(OWLObjectOneOf node) {
        write("{");
        for (Iterator<OWLIndividual> it = node.getIndividuals().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write("}");
    }


    public void visit(OWLDisjointClassesAxiom node) {
        for (Iterator<OWLClassExpression> it = sortClassExpressions(node.getClassExpressions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" disjointWith ");
            }
        }
    }


    public void visit(OWLEquivalentClassesAxiom node) {
        List<OWLClassExpression> orderedDescs = sortClassExpressions(node.getClassExpressions());
        for(Iterator<OWLClassExpression> it = orderedDescs.iterator(); it.hasNext(); ) {
            OWLClassExpression desc = it.next();
            if(orderedDescs.get(0).isOWLNothing()) {
                it.remove();
                orderedDescs.add(desc);
                break;
            }
        }

        for (Iterator<OWLClassExpression> it = orderedDescs.iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" equivalentTo ");
            }
        }
    }


    public void visit(OWLSubClassOfAxiom node) {
        node.getSubClass().accept(this);
        write(" subClassOf ");
        node.getSuperClass().accept(this);
    }


    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        write("Functional: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        write("InverseFunctional: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        write("Irreflexive: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLSubDataPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        write(" subPropertyOf ");
        axiom.getSuperProperty().accept(this);
    }


    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
        write("Reflexive: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
        write("Symmetric: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        write("Transitive: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        if (!OWLRendererPreferences.getInstance().isRenderDomainAxiomsAsGCIs()) {
            axiom.getProperty().accept(this);
            write(" domain ");
            axiom.getDomain().accept(this);
        }
        else {
            axiom.getProperty().accept(this);
            write(" some ");
            owlModelManager.getOWLDataFactory().getOWLThing().accept(this);
            write(" subClassOf ");
            axiom.getDomain().accept(this);
        }
    }


    public void visit(OWLEquivalentObjectPropertiesAxiom node) {
        for (Iterator<OWLObjectPropertyExpression> it = node.getProperties().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" equivalentTo ");
            }
        }
    }


    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        write(" range ");
        axiom.getRange().accept(this);
    }


    public void visit(OWLClassAssertionAxiom axiom) {
        axiom.getIndividual().accept(this);
        write(" types ");
        axiom.getClassExpression().accept(this);
    }


    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
        write("Functional: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLEquivalentDataPropertiesAxiom node) {
        for (Iterator<OWLDataPropertyExpression> it = node.getProperties().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" equivalentTo ");
            }
        }
    }


    public void visit(OWLSameIndividualAxiom axiom) {
        write("SameIndividuals: [");
        for (Iterator<OWLIndividual> it = axiom.getIndividuals().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write("]");
    }


    public void visit(OWLDifferentIndividualsAxiom axiom) {
        write("DifferentIndividuals: [");
        for (Iterator<OWLIndividual> it = axiom.getIndividuals().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write("]");
    }


    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
        for (Iterator<OWLDataPropertyExpression> it = axiom.getProperties().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" disjointWith ");
            }
        }
    }


    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        for (Iterator<OWLObjectPropertyExpression> it = axiom.getProperties().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" disjointWith ");
            }
        }
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        write(" ");
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getObject().accept(this);
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        axiom.getSubject().accept(this);
        write(" ");
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getObject().accept(this);
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        write("not(");
        axiom.getSubject().accept(this);
        write(" ");
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getObject().accept(this);
        write(")");
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        write("not(");
        axiom.getSubject().accept(this);
        write(" ");
        axiom.getProperty().accept(this);
        write(" ");
        axiom.getObject().accept(this);
        write(")");
    }


    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        axiom.getFirstProperty().accept(this);
        write(" inverseOf ");
        axiom.getSecondProperty().accept(this);
    }


    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        write("AntiSymmetric: ");
        axiom.getProperty().accept(this);
    }


    public void visit(OWLDataPropertyDomainAxiom axiom) {
        axiom.getProperty().accept(this);
        write(" domain ");
        axiom.getDomain().accept(this);
    }


    public void visit(OWLDataPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        write(" range ");
        axiom.getRange().accept(this);
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        write(" subPropertyOf ");
        axiom.getSuperProperty().accept(this);
    }


    public void visit(OWLDisjointUnionAxiom axiom) {
        axiom.getOWLClass().accept(this);
        write(" disjointUnionOf ");
        write("[");
        int indent = getIndent();
        for (Iterator<OWLClassExpression> it = axiom.getClassExpressions().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("\n");
                insertIndent(indent);
            }
        }
        write("]");
    }


    public void visit(OWLDeclarationAxiom axiom) {
        OWLEntity entity = axiom.getEntity();
        if (entity.isOWLClass()){
            write("Class(");
        }
        else if (entity.isOWLObjectProperty()){
            write("Object property(");
        }
        else if (entity.isOWLDataProperty()){
            write("Data property(");
        }
        else if (entity.isOWLIndividual()){
            write("Individual(");
        }
        else{
            write("(");
        }
        entity.accept(this);
        write(")");
    }


    private void writeOpenBracket(OWLClassExpression description) {
        description.accept(bracketWriter);
        if (bracketWriter.writeBrackets()) {
            write("(");
        }
    }


    private void writeOpenBracket(OWLDataRange dataRange) {
        dataRange.accept(bracketWriter);
        if (bracketWriter.writeBrackets()) {
            write("(");
        }
    }


    private void writeCloseBracket(OWLClassExpression description) {
        description.accept(bracketWriter);
        if (bracketWriter.writeBrackets()) {
            write(")");
        }
    }


    private void writeCloseBracket(OWLDataRange dataRange) {
        dataRange.accept(bracketWriter);
        if (bracketWriter.writeBrackets()) {
            write(")");
        }
    }


    public void visit(OWLOntology ontology) {
        writeOntologyURI(ontology.getURI());
    }


    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        for (Iterator<OWLObjectPropertyExpression> it = axiom.getPropertyChain().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" o ");
            }
        }
        write(" \u279E ");
        axiom.getSuperProperty().accept(this);
    }


    public void visit(SWRLRule swrlRule) {
        for (Iterator<SWRLAtom> it = swrlRule.getBody().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" , ");
            }
        }
        write(" -> ");
        for (Iterator<SWRLAtom> it = swrlRule.getHead().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" \u2227 ");
            }
        }
    }


    public void visit(SWRLClassAtom swrlClassAtom) {
        OWLClassExpression desc = swrlClassAtom.getPredicate();
        if (desc.isAnonymous()) {
            write("(");
        }
        desc.accept(this);
        if (desc.isAnonymous()) {
            write(")");
        }
        write("(");
        swrlClassAtom.getArgument().accept(this);
        write(")");
    }


    public void visit(SWRLDataRangeAtom swrlDataRangeAtom) {
        swrlDataRangeAtom.getPredicate().accept(this);
        write("(");
        swrlDataRangeAtom.getArgument().accept(this);
        write(")");
    }


    public void visit(SWRLObjectPropertyAtom swrlObjectPropertyAtom) {
        swrlObjectPropertyAtom.getPredicate().accept(this);
        write("(");
        swrlObjectPropertyAtom.getFirstArgument().accept(this);
        write(", ");
        swrlObjectPropertyAtom.getSecondArgument().accept(this);
        write(")");
    }


    public void visit(SWRLDataValuedPropertyAtom swrlDataValuedPropertyAtom) {
        swrlDataValuedPropertyAtom.getPredicate().accept(this);
        write("(");
        swrlDataValuedPropertyAtom.getFirstArgument().accept(this);
        write(", ");
        swrlDataValuedPropertyAtom.getSecondArgument().accept(this);
        write(")");
    }


    public void visit(SWRLBuiltInAtom swrlBuiltInAtom) {
        write(swrlBuiltInAtom.getPredicate().getShortName());
        write("(");
        Iterator<SWRLAtomDObject> it = swrlBuiltInAtom.getArguments().iterator();
        while (it.hasNext()) {
            SWRLAtomDObject argument = it.next();
            argument.accept(this);
            if (it.hasNext()) {
                write(", ");
            }
        }
        write(")");
    }


    public void visit(SWRLAtomDVariable swrlAtomDVariable) {
        write("?");
        write(swrlAtomDVariable.getURI().getFragment());
    }


    public void visit(SWRLAtomIVariable swrlAtomIVariable) {
        write("?");
        write(swrlAtomIVariable.getURI().getFragment());
    }


    public void visit(SWRLAtomIndividualObject swrlAtomIndividualObject) {
        swrlAtomIndividualObject.getIndividual().accept(this);
    }


    public void visit(SWRLAtomConstantObject swrlAtomConstantObject) {
        swrlAtomConstantObject.getConstant().accept(this);
    }


    public void visit(SWRLDifferentFromAtom swrlDifferentFromAtom) {
        swrlDifferentFromAtom.getPredicate().accept(this);
        write("(");
        swrlDifferentFromAtom.getFirstArgument().accept(this);
        write(", ");
        swrlDifferentFromAtom.getSecondArgument().accept(this);
        write(")");
    }


    public void visit(SWRLSameAsAtom swrlSameAsAtom) {
        swrlSameAsAtom.getPredicate().accept(this);
        write("(");
        swrlSameAsAtom.getFirstArgument().accept(this);
        write(", ");
        swrlSameAsAtom.getSecondArgument().accept(this);
        write(")");
    }


    private void writeOntologyURI(URI uri) {
        String shortName = owlModelManager.getURIRendering(uri);
        if (shortName != null) {
            write(shortName);
            write(" (");
            write(uri.toString());
            write(")");
        }
        else {
            write(uri.toString());
        }
    }


    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        axiom.getSubProperty().accept(this);
        write(" subPropertyOf ");
        axiom.getSuperProperty().accept(this);
    }



    public void visit(OWLImportsDeclaration axiom) {
        writeOntologyURI(axiom.getURI());
        if (owlModelManager.getOWLOntologyManager().getImportedOntology(axiom) == null) {
            write("      (Not Loaded)");
        }
    }


    //////////////////////////////////////// new for OWL API v3


    public void visit(OWLAnonymousIndividual node) {
        write("Anonymous : [");
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            for (OWLClassExpression desc : node.getTypes(ont)) {
                write(" ");
                desc.accept(this);
            }
        }
        write(" ]");
    }


    public void visit(OWLAnnotationProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom) {
        throw new NotImplementedException("Cannot render: " + owlAnnotationAssertionAxiom);
    }


    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
        if (!OWLRendererPreferences.getInstance().isRenderDomainAxiomsAsGCIs()) {
            axiom.getProperty().accept(this);
            write(" domain ");
            axiom.getDomain().accept(this);
        }
        else {
            axiom.getProperty().accept(this);
            write(" some ");
            owlModelManager.getOWLDataFactory().getOWLThing().accept(this);
            write(" subClassOf ");
            axiom.getDomain().accept(this);
        }
    }


    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
        axiom.getProperty().accept(this);
        write(" range ");
        axiom.getRange().accept(this);
    }


    public void visit(OWLAnnotation owlAnnotation) {
        owlAnnotation.getProperty().accept(this);
        write(" ");
        owlAnnotation.getValue().accept(this);
    }


    public void visit(OWLHasKeyAxiom owlHasKeyAxiom) {
        throw new NotImplementedException("Cannot render: " + owlHasKeyAxiom);
    }


    public void visit(OWLDatatypeDefinition owlDatatypeDefinition) {
        throw new NotImplementedException("Cannot render: " + owlDatatypeDefinition);
    }


    public void visit(OWLNamedIndividual node) {
        write(getRendering(node));
    }


    public void visit(IRI iri) {
        throw new NotImplementedException("Cannot render: " + iri);
    }



    public void visit(OWLDataUnionOf node) {
        int indent = getIndent();
        Set<OWLDataRange> ops = new TreeSet<OWLDataRange>(node.getOperands());
        for (Iterator it = ops.iterator(); it.hasNext();) {
            OWLClassExpression curOp = (OWLClassExpression) it.next();
            writeOpenBracket(curOp);
            curOp.accept(this);
            writeCloseBracket(curOp);
            if (it.hasNext()) {
                write("\n");
                insertIndent(indent);
                writeOrKeyword();
            }
        }
    }


    private class BracketWriter extends OWLClassExpressionVisitorAdapter implements OWLDataVisitor {

        boolean nested = false;


        public boolean writeBrackets() {
            return nested;
        }


        public void visit(OWLObjectIntersectionOf owlAnd) {
            nested = true;
        }


        public void visit(OWLDataAllValuesFrom owlDataAllRestriction) {
            nested = true;
        }


        public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
            nested = true;
        }


        public void visit(OWLDataHasValue owlDataValueRestriction) {
            nested = true;
        }


        public void visit(OWLObjectAllValuesFrom owlObjectAllRestriction) {
            nested = true;
        }


        public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            nested = true;
        }


        public void visit(OWLObjectHasValue owlObjectValueRestriction) {
            nested = true;
        }


        public void visit(OWLObjectComplementOf owlNot) {
            nested = true;
        }


        public void visit(OWLObjectUnionOf owlOr) {
            nested = true;
        }


        public void visit(OWLClass owlClass) {
            nested = false;
        }


        public void visit(OWLObjectOneOf owlObjectOneOf) {
            nested = false;
        }


        public void visit(OWLObjectMinCardinality desc) {
            nested = true;
        }


        public void visit(OWLObjectExactCardinality desc) {
            nested = true;
        }


        public void visit(OWLObjectMaxCardinality desc) {
            nested = true;
        }


        public void visit(OWLObjectHasSelf desc) {
            nested = true;
        }


        public void visit(OWLDataMinCardinality desc) {
            nested = true;
        }


        public void visit(OWLDataExactCardinality desc) {
            nested = true;
        }


        public void visit(OWLDataMaxCardinality desc) {
            nested = true;
        }


        public void visit(OWLDatatype node) {
            nested = false;
        }


        public void visit(OWLDataComplementOf node) {
            nested = false;
        }


        public void visit(OWLDataIntersectionOf owlDataIntersectionOf) {
            nested = true;
        }


        public void visit(OWLDataUnionOf owlDataUnionOf) {
            nested = true;
        }


        public void visit(OWLDatatypeRestriction owlDatatypeRestriction) {
            nested = false;
        }


        public void visit(OWLDataOneOf node) {
            nested = false;
        }


        public void visit(OWLDataRange node) {
            nested = true;
        }


        public void visit(OWLTypedLiteral node) {
            nested = false;
        }


        public void visit(OWLRDFTextLiteral node) {
            nested = false;
        }


        public void visit(OWLFacetRestriction node) {
            nested = false;
        }
    }
}
