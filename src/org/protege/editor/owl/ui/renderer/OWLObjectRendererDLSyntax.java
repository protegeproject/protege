package org.protege.editor.owl.ui.renderer;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLObjectRendererDLSyntax extends OWLObjectVisitorAdapter implements OWLObjectRenderer {

    private static final Logger logger = Logger.getLogger(OWLObjectRendererImpl.class);


    private StringBuffer buffer;

    private OWLEntityRenderer entityRenderer;

    private BracketWriter bracketWriter;

    private OWLModelManager mngr;


    public OWLObjectRendererDLSyntax(OWLModelManager mngr) {
        this.mngr = mngr;
        buffer = new StringBuffer();
        bracketWriter = new BracketWriter();
    }


    public void setFocusedObject(OWLObject object) {

    }


    public void initialise() {
    }


    public void dispose() {
    }


    protected String getAndKeyWord() {
        return "\u2293";
    }


    protected String getOrKeyWord() {
        return "\u2294";
    }


    protected String getNotKeyWord() {
        return "\u00ac";
    }


    protected String getSomeKeyWord() {
        return "\u2203";
    }


    protected String getAllKeyWord() {
        return "\u2200";
    }

//    protected String getValueKeyWord() {
//        return "value";
//    }


    protected String getMinKeyWord() {
        return "\u2265";
    }


    protected String getMaxKeyWord() {
        return "\u2264";
    }


    protected String getExactlyKeyWord() {
        return "=";
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
        return mngr.getRendering(entity);
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
        buffer = new StringBuffer();
    }


    public String getText() {
        return buffer.toString();
    }


    private static List<OWLDescription> sort(Set<OWLDescription> descriptions) {
        List<OWLDescription> sortedDescs = new ArrayList<OWLDescription>(descriptions);
        Collections.sort(sortedDescs, new Comparator<OWLDescription>() {
            public int compare(OWLDescription o1, OWLDescription o2) {
                if (o1 instanceof OWLClass) {
                    return -1;
                }
                return 1;
            }
        });
        return sortedDescs;
    }


    public void visit(OWLObjectIntersectionOf node) {
        List<OWLDescription> ops = sort(node.getOperands());
        for (int i = 0; i < ops.size(); i++) {
            OWLDescription curOp = ops.get(i);
//            boolean bracket = getIndent() != 1;
//            if (bracket) {
//                writeOpenBracket(curOp);
//            }
            int indent = getIndent();
            curOp.accept(this);
//            if (bracket) {
//                writeCloseBracket(curOp);
//            }
            if (i < ops.size() - 1) {
                write("\n");
                insertIndent(indent);
                writeAndKeyword();
            }
        }
    }


    public void visit(OWLTypedConstant node) {
        write("\"");
        write(node.getLiteral());
        write("\"^^");
        node.getDataType().accept(this);
    }


    public void visit(OWLUntypedConstant node) {
        write("\"");
        write(node.getLiteral());
        write("\"");
        if (node.hasLang()) {
            write("@");
            write(node.getLang());
        }
    }


    public void visit(OWLDataType node) {
        write(node.getURI().getFragment());
    }


    public void visit(OWLDataOneOf node) {
        write("{");
        for (Iterator<OWLConstant> it = node.getValues().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        write("}");
    }


    public void visit(OWLDataAllRestriction node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLDataSomeRestriction node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataValueRestriction node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" {");
        node.getValue().accept(this);
        write("}");
    }


    public void visit(OWLIndividual node) {
        write(getRendering(node));
    }


    public void visit(OWLObjectAllRestriction node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }


    public void visit(OWLObjectMinCardinalityRestriction desc) {
        writeCardinality(desc, getMinKeyWord());
    }


    public void visit(OWLObjectExactCardinalityRestriction desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }


    public void visit(OWLObjectMaxCardinalityRestriction desc) {
        writeCardinality(desc, getMaxKeyWord());
    }


    private void writeCardinality(OWLObjectCardinalityRestriction desc, String keyword) {
        write(keyword);
        write(" ");
        write(Integer.toString(desc.getCardinality()));
        write(" ");
        desc.getProperty().accept(this);
        write(" ");
        writeOpenBracket(desc.getFiller());
        desc.getFiller().accept(this);
        writeCloseBracket(desc.getFiller());
    }


    public void visit(OWLDataMinCardinalityRestriction desc) {
        writeCardinality(desc, getMinKeyWord());
    }


    public void visit(OWLDataExactCardinalityRestriction desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }


    public void visit(OWLDataMaxCardinalityRestriction desc) {
        writeCardinality(desc, getMaxKeyWord());
    }


    private void writeCardinality(OWLDataCardinalityRestriction desc, String keyword) {
        write(keyword);
        write(" ");
        write(Integer.toString(desc.getCardinality()));
        write(" ");
        desc.getProperty().accept(this);
        write(" ");
        writeOpenBracket(desc.getFiller());
        desc.getFiller().accept(this);
        writeCloseBracket(desc.getFiller());
    }


    public void visit(OWLObjectProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLObjectSomeRestriction node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }


    public void visit(OWLObjectValueRestriction node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" {");
        node.getValue().accept(this);
        write("}");
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
        for (Iterator it = sort(node.getOperands()).iterator(); it.hasNext();) {
            OWLDescription curOp = (OWLDescription) it.next();
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


    public void visit(OWLObjectOneOf node) {
        write("{");
        int size = node.getIndividuals().size();
        int count = 0;
        for (Object op : node.getIndividuals()) {
            ((OWLIndividual) op).accept(this);
            if (count < size - 1) {
                write(" ");
            }
        }
        write("}");
    }


    public void visit(OWLDisjointClassesAxiom node) {
        for (Iterator<OWLDescription> it = sort(node.getDescriptions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2291 \u00ac ");
            }
        }
    }


    public void visit(OWLEquivalentClassesAxiom node) {
        for (Iterator<OWLDescription> it = sort(node.getDescriptions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2261  ");
            }
        }
    }


    public void visit(OWLSubClassAxiom node) {
        node.getSubClass().accept(this);
        write(" \u2291 ");
        node.getSuperClass().accept(this);
    }


    private void writeOpenBracket(OWLDescription description) {
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


    private void writeCloseBracket(OWLDescription description) {
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
        write(ontology.getURI().toString());
    }


    private class BracketWriter extends OWLDescriptionVisitorAdapter implements OWLDataVisitor {

        boolean nested = false;


        public boolean writeBrackets() {
            return nested;
        }


        public void visit(OWLObjectIntersectionOf owlAnd) {
            nested = true;
        }


        public void visit(OWLDataAllRestriction owlDataAllRestriction) {
            nested = true;
        }


        public void visit(OWLDataSomeRestriction owlDataSomeRestriction) {
            nested = true;
        }


        public void visit(OWLDataValueRestriction owlDataValueRestriction) {
            nested = true;
        }


        public void visit(OWLObjectAllRestriction owlObjectAllRestriction) {
            nested = true;
        }


        public void visit(OWLObjectSomeRestriction owlObjectSomeRestriction) {
            nested = true;
        }


        public void visit(OWLObjectValueRestriction owlObjectValueRestriction) {
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


        public void visit(OWLObjectMinCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLObjectExactCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLObjectMaxCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLObjectSelfRestriction desc) {
            nested = true;
        }


        public void visit(OWLDataMinCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLDataExactCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLDataMaxCardinalityRestriction desc) {
            nested = true;
        }


        public void visit(OWLDataType node) {
            nested = false;
        }


        public void visit(OWLDataComplementOf node) {
            nested = false;
        }


        public void visit(OWLDataOneOf node) {
            nested = false;
        }


        public void visit(OWLDataRangeRestriction node) {
            nested = false;
        }


        public void visit(OWLTypedConstant node) {
            nested = false;
        }


        public void visit(OWLUntypedConstant node) {
            nested = false;
        }


        public void visit(OWLDataRangeFacetRestriction node) {
            nested = false;
        }
    }
}
