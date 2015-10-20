package org.protege.editor.owl.ui.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDataVisitor;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLObjectRendererDLSyntax extends OWLObjectVisitorAdapter implements OWLObjectRenderer {

//    private static final Logger logger = LoggerFactory.getLogger(OWLObjectRendererImpl.class);

    private StringBuffer buffer;

    private BracketWriter bracketWriter;

    private OWLModelManager mngr;


    public OWLObjectRendererDLSyntax(OWLModelManager mngr) {
        this.mngr = mngr;
        buffer = new StringBuffer();
        bracketWriter = new BracketWriter();
    }


    public String render(OWLObject object) {
        reset();
        try {
            object.accept(this);
            return buffer.toString();
        }
        catch (Exception e) {
            return "<Error! " + e.getMessage() + ">";
        }
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

    protected String getMinKeyWord() {
        return "\u2265";
    }


    protected String getMaxKeyWord() {
        return "\u2264";
    }


    protected String getExactlyKeyWord() {
        return "=";
    }

    protected String getRendering(OWLEntity entity) {
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


    private static List<OWLClassExpression> sort(Set<OWLClassExpression> descriptions) {
        List<OWLClassExpression> sortedDescs = new ArrayList<OWLClassExpression>(descriptions);
        Collections.sort(sortedDescs, new Comparator<OWLClassExpression>() {
            public int compare(OWLClassExpression o1, OWLClassExpression o2) {
                if (o1 instanceof OWLClass) {
                    return -1;
                }
                return 1;
            }
        });
        return sortedDescs;
    }


    public void visit(OWLObjectIntersectionOf node) {
        List<OWLClassExpression> ops = sort(node.getOperands());
        for (int i = 0; i < ops.size(); i++) {
            OWLClassExpression curOp = ops.get(i);
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


    public void visit(OWLLiteral node) {
        if (node.isRDFPlainLiteral()) {
            write("\"");
            write(node.getLiteral());
            write("\"");
            if (node.getLang() != null) {
                write("@");
                write(node.getLang());
            }
        }
        else {
            write("\"");
            write(node.getLiteral());
            write("\"^^");
            node.getDatatype().accept(this);
        }
    }

    public void visit(OWLDatatype node) {
        write(node.getIRI().getFragment());
    }


    public void visit(OWLDataOneOf node) {
        write("{");
        for (Iterator<OWLLiteral> it = node.getValues().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        write("}");
    }


    public void visit(OWLDataAllValuesFrom node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataProperty node) {
        write(getRendering(node));
    }


    public void visit(OWLDataSomeValuesFrom node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }


    public void visit(OWLDataHasValue node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" {");
        node.getValue().accept(this);
        write("}");
    }


    public void visit(OWLNamedIndividual node) {
        write(getRendering(node));
    }


    public void visit(OWLObjectAllValuesFrom node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
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


    public void visit(OWLObjectSomeValuesFrom node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }


    public void visit(OWLObjectHasValue node) {
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
        for (Iterator<OWLClassExpression> it = sort(node.getClassExpressions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2291 \u00ac ");
            }
        }
    }


    public void visit(OWLEquivalentClassesAxiom node) {
        for (Iterator<OWLClassExpression> it = sort(node.getClassExpressions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2261  ");
            }
        }
    }


    public void visit(OWLSubClassOfAxiom node) {
        node.getSubClass().accept(this);
        write(" \u2291 ");
        node.getSuperClass().accept(this);
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
        write(ontology.getOntologyID().toString());
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


        public void visit(OWLLiteral node) {
            nested = false;
        }


        public void visit(OWLFacetRestriction node) {
            nested = false;
        }
    }
}
