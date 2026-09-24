package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLObjectRendererDLSyntax implements OWLObjectVisitor, OWLObjectRenderer {

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
        List<OWLClassExpression> sortedDescs = new ArrayList<>(descriptions);
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

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf node) {
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

    @Override
    public void visit(@Nonnull OWLLiteral node) {
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

    @Override
    public void visit(@Nonnull OWLDatatype node) {
        visit(node.getIRI());
    }

    @Override
    public void visit(@Nonnull IRI iri) {
        Optional<String> remainder = iri.getRemainder();
        if(remainder.isPresent()) {
            write(remainder.get());
        }
        else {
            write(iri.toString());
        }
    }
    @Override
    public void visit(@Nonnull OWLDataOneOf node) {
        write("{");
        for (Iterator<OWLLiteral> it = node.getValues().iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write(" ");
            }
        }
        write("}");
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataProperty node) {
        write(getRendering(node));
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        node.getFiller().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" {");
        node.getFiller().accept(this);
        write("}");
    }

    @Override
    public void visit(@Nonnull OWLNamedIndividual node) {
        write(getRendering(node));
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom node) {
        write(getAllKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality desc) {
        writeCardinality(desc, getMinKeyWord());
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality desc) {
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

    @Override
    public void visit(@Nonnull OWLDataMinCardinality desc) {
        writeCardinality(desc, getMinKeyWord());
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality desc) {
        writeCardinality(desc, getExactlyKeyWord());
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality desc) {
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

    @Override
    public void visit(@Nonnull OWLObjectProperty node) {
        write(getRendering(node));
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" ");
        writeOpenBracket(node.getFiller());
        node.getFiller().accept(this);
        writeCloseBracket(node.getFiller());
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue node) {
        write(getSomeKeyWord());
        write(" ");
        node.getProperty().accept(this);
        write(" {");
        node.getFiller().accept(this);
        write("}");
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf node) {
        writeNotKeyword();
        write(" ");
        writeOpenBracket(node.getOperand());
        node.getOperand().accept(this);
        writeCloseBracket(node.getOperand());
    }


    protected void writeNotKeyword() {
        write(getNotKeyWord());
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf node) {
        int indent = getIndent();
        for (Iterator<OWLClassExpression> it = sort(node.getOperands()).iterator(); it.hasNext();) {
            OWLClassExpression curOp = it.next();
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

    @Override
    public void visit(@Nonnull OWLClass node) {
        write(getRendering(node));
    }

    @Override
    public void visit(@Nonnull OWLObjectOneOf node) {
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

    @Override
    public void visit(@Nonnull OWLDisjointClassesAxiom node) {
        for (Iterator<OWLClassExpression> it = sort(node.getClassExpressions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2291 \u00ac ");
            }
        }
    }

    @Override
    public void visit(@Nonnull OWLEquivalentClassesAxiom node) {
        for (Iterator<OWLClassExpression> it = sort(node.getClassExpressions()).iterator(); it.hasNext();) {
            it.next().accept(this);
            if (it.hasNext()) {
                write("  \u2261  ");
            }
        }
    }

    @Override
    public void visit(@Nonnull OWLSubClassOfAxiom node) {
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

    @Override
    public void visit(@Nonnull OWLOntology ontology) {
        write(ontology.getOntologyID().toString());
    }


    private class BracketWriter implements OWLClassExpressionVisitor, OWLDataVisitor {

        boolean nested = false;


        public boolean writeBrackets() {
            return nested;
        }

        @Override
        public void visit(@Nonnull OWLObjectIntersectionOf owlAnd) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataAllValuesFrom owlDataAllRestriction) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataHasValue owlDataValueRestriction) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectAllValuesFrom owlObjectAllRestriction) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectHasValue owlObjectValueRestriction) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectComplementOf owlNot) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectUnionOf owlOr) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLClass owlClass) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLObjectOneOf owlObjectOneOf) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLObjectMinCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectExactCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectMaxCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLObjectHasSelf desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataMinCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataExactCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataMaxCardinality desc) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDatatype node) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLDataComplementOf node) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLDataIntersectionOf owlDataIntersectionOf) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDataUnionOf owlDataUnionOf) {
            nested = true;
        }

        @Override
        public void visit(@Nonnull OWLDatatypeRestriction owlDatatypeRestriction) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLDataOneOf node) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLLiteral node) {
            nested = false;
        }

        @Override
        public void visit(@Nonnull OWLFacetRestriction node) {
            nested = false;
        }


    }
}
