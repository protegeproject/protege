package org.protege.editor.owl.ui.renderer.styledstring;

import org.protege.editor.owl.ui.renderer.context.*;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 25/09/2012
 */
public class OWLObjectStyledStringRenderer {

    public static final Style BLANK_STYLE = new Style();

    private OWLObjectRenderingContext renderingContext;


    public OWLObjectStyledStringRenderer() {
        this(new OWLObjectRenderingContext(new NullDeprecatedObjectChecker(), new NullClassSatisfiabilityChecker(), new NullObjectPropertySatisfiabilityChecker(), new NullDataPropertySatisfiabilityChecker(), new NullLinkFactory(), new SimpleShortFormProvider(), new OntologyIRIShortFormProvider()));
    }

    public OWLObjectStyledStringRenderer(OWLObjectRenderingContext renderingContext) {
        this.renderingContext = renderingContext;
    }

    public StyledString getRendering(OWLObject owlObject) {
        ObjectRenderer renderer = new ObjectRenderer();
        owlObject.accept(renderer);
        return renderer.builder.build();
    }


    private class ObjectRenderer implements OWLObjectVisitor {

        public static final String COMMA_SEPARATOR = ", ";

        private StyledString.Builder builder = new StyledString.Builder();

        private void renderKeywordWithSpaces(ManchesterOWLSyntax keyword) {
            builder.appendSpace();
            renderKeyword(keyword);
            builder.appendSpace();
        }

        private void renderKeyword(ManchesterOWLSyntax keyword) {
            Style style = ProtegeStyles.getStyles().getKeywordStyle(keyword);
            builder.appendWithStyle(keyword.toString(), style);
        }

        private void renderSpace() {
            builder.appendSpace();
        }

        private void renderColonSpace() {
            builder.append(": ");
        }

        private void renderKeywordColonSpace(ManchesterOWLSyntax keyword) {
            renderKeyword(keyword);
            renderColonSpace();
        }

        private void renderCollection(OWLObject parentObject, Collection<? extends OWLObject> collection, ManchesterOWLSyntax separator, BracketingStrategy bracketingStrategy) {
            for (Iterator<? extends OWLObject> it = collection.iterator(); it.hasNext(); ) {
                OWLObject o = it.next();
                boolean bracket = bracketingStrategy.shouldBracket(parentObject, o);
                if (bracket) {
                    builder.append("(");
                }
                o.accept(this);
                if (bracket) {
                    builder.append(")");
                }
                if (it.hasNext()) {
                    renderKeywordWithSpaces(separator);
                }
            }
        }

        private void renderCollection(OWLObject parentObject, Collection<? extends OWLObject> collection, String separator, BracketingStrategy bracketingStrategy) {
            for (Iterator<? extends OWLObject> it = collection.iterator(); it.hasNext(); ) {
                OWLObject o = it.next();
                boolean bracket = bracketingStrategy.shouldBracket(parentObject, o);
                if (bracket) {
                    builder.append("(");
                }
                o.accept(this);
                if (bracket) {
                    builder.append(")");
                }
                if (it.hasNext()) {
                    builder.append(separator);
                }
            }
        }

        public void visit(OWLAnnotation annotation) {
            int propStart = builder.mark();
            annotation.getProperty().accept(this);
            int propEnd = builder.mark();
            builder.applyStyle(propStart, propEnd, ProtegeStyles.getStyles().getAnnotationPropertyStyle());
            renderSpace();
            if (annotation.getValue() instanceof OWLLiteral) {
                OWLLiteral literal = (OWLLiteral) annotation.getValue();
                Style langStyle = ProtegeStyles.getStyles().getAnnotationLangStyle();
                if (literal.isRDFPlainLiteral()) {
                    if (!literal.getLang().isEmpty()) {
                        builder.appendWithStyle("[language: ", langStyle);
                        builder.appendWithStyle(literal.getLang(), langStyle);
                        builder.appendWithStyle("] ", langStyle);
                    }
                }
                else {
                    builder.appendWithStyle("[type: ", langStyle);
                    int dtStart = builder.mark();
                    literal.getDatatype().accept(this);
                    int dtEnd = builder.mark();
                    builder.applyStyle(dtStart, dtEnd, langStyle);
                    builder.appendWithStyle("] ", langStyle);
                }
                builder.append(literal.getLiteral());
            }
            else {
                annotation.getValue().accept(this);
            }

        }

        public void visit(OWLSubClassOfAxiom axiom) {
            axiom.getSubClass().accept(this);
            renderSpace();
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUBCLASS_OF);
            renderSpace();
            axiom.getSuperClass().accept(this);
        }

        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {

        }

        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.ASYMMETRIC);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.REFLEXIVE);
            axiom.getProperty().accept(this);
        }

        private <O> List<O> toList(Collection<O> collection) {
            if (collection instanceof List) {
                return (List<O>) collection;
            }
            else {
                return new ArrayList<O>(collection);
            }
        }

        private void renderBinaryOrNaryList(Collection<? extends OWLObject> collection, ManchesterOWLSyntax binaryKeyword, ManchesterOWLSyntax naryKeyword, String separator) {
            if (collection.size() == 2) {
                List<? extends OWLObject> list = toList(collection);
                list.get(0).accept(this);
                renderKeywordWithSpaces(binaryKeyword);
                list.get(1).accept(this);
            }
            else {
                renderKeywordColonSpace(naryKeyword);
                for (Iterator<? extends OWLObject> it = collection.iterator(); it.hasNext(); ) {
                    OWLObject o = it.next();
                    o.accept(this);
                    if (it.hasNext()) {
                        builder.append(separator);
                    }
                }
            }
        }

        public void visit(OWLDisjointClassesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getClassExpressionsAsList(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_CLASSES, COMMA_SEPARATOR);
        }


        private void renderDomainAxiom(OWLPropertyDomainAxiom<?> axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DOMAIN);
            axiom.getDomain().accept(this);
        }

        public void visit(OWLDataPropertyDomainAxiom axiom) {
            renderDomainAxiom(axiom);
        }

        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            renderDomainAxiom(axiom);
        }

        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES, COMMA_SEPARATOR);
        }

        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        }

        public void visit(OWLDifferentIndividualsAxiom axiom) {
            renderBinaryOrNaryList(axiom.getIndividuals(), ManchesterOWLSyntax.SAME_AS, ManchesterOWLSyntax.SAME_INDIVIDUAL, COMMA_SEPARATOR);
        }

        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES, COMMA_SEPARATOR);
        }

        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES, COMMA_SEPARATOR);
        }

        private void renderRangeAxiom(OWLPropertyRangeAxiom<?, ?> axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.RANGE);
            axiom.getRange().accept(this);
        }

        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            renderRangeAxiom(axiom);
        }

        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            renderPropertyAssertion(axiom);
        }

        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        public void visit(OWLDisjointUnionAxiom axiom) {
            axiom.getOWLClass().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DISJOINT_UNION_OF);
            renderCollection(axiom, axiom.getClassExpressions(), COMMA_SEPARATOR, ComplexClassExpressionBracketingStrategy.get());
        }

        public void visit(OWLDeclarationAxiom axiom) {
            axiom.getEntity().accept(new OWLEntityVisitor() {
                public void visit(OWLClass owlClass) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.CLASS);
                }

                public void visit(OWLObjectProperty property) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.OBJECT_PROPERTY);
                }

                public void visit(OWLDataProperty dataProperty) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.DATA_PROPERTY);
                }

                public void visit(OWLNamedIndividual individual) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.INDIVIDUAL);
                }

                public void visit(OWLDatatype owlDatatype) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.DATATYPE);
                }

                public void visit(OWLAnnotationProperty property) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.ANNOTATION_PROPERTY);
                }
            });
        }

        private void renderPropertyAssertion(OWLPropertyAssertionAxiom<?, ?> axiom) {
            axiom.getSubject().accept(this);
            renderSpace();
            axiom.getProperty().accept(this);
            renderSpace();
            axiom.getObject().accept(this);
        }

        public void visit(OWLAnnotationAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
            renderSpace();
            axiom.getProperty().accept(this);
            renderSpace();
            axiom.getValue().accept(this);
        }

        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.SYMMETRIC);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLDataPropertyRangeAxiom axiom) {
            renderRangeAxiom(axiom);
        }

        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES, COMMA_SEPARATOR);
        }

        public void visit(OWLClassAssertionAxiom axiom) {
            axiom.getIndividual().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.TYPE);
            axiom.getClassExpression().accept(this);
        }

        public void visit(OWLEquivalentClassesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getClassExpressions(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_CLASSES, COMMA_SEPARATOR);
        }

        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            renderPropertyAssertion(axiom);
        }

        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.TRANSITIVE);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.IRREFLEXIVE);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.INVERSE_FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        public void visit(OWLSameIndividualAxiom axiom) {
            renderBinaryOrNaryList(axiom.getIndividuals(), ManchesterOWLSyntax.SAME_AS, ManchesterOWLSyntax.SAME_INDIVIDUAL, COMMA_SEPARATOR);
        }

        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            renderCollection(axiom, axiom.getPropertyChain(), " o ", NullBracketingStrategy.get());
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            axiom.getFirstProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.INVERSE_OF);
            axiom.getSecondProperty().accept(this);
        }

        public void visit(OWLHasKeyAxiom axiom) {
        }


        public void visit(OWLDatatypeDefinitionAxiom axiom) {
        }

        public void visit(SWRLRule swrlRule) {
            renderCollection(swrlRule, swrlRule.getBody(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(" -> ");
            renderCollection(swrlRule, swrlRule.getHead(), COMMA_SEPARATOR, NullBracketingStrategy.get());
        }

        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DOMAIN);
            axiom.getDomain().accept(this);
        }

        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.RANGE);
            axiom.getRange().accept(this);
        }

        public void visit(IRI iri) {
            builder.append(iri.toQuotedString());
        }

        public void visit(OWLAnonymousIndividual individual) {
            builder.append(individual.getID().getID());
        }


        private void renderEntity(OWLEntity entity) {
            ShortFormProvider sfp = renderingContext.getShortFormProvider();
            String rendering = sfp.getShortForm(entity);
            int renderingStart = builder.mark();
            builder.append(rendering);
            int renderingEnd = builder.mark();

            Style style = new Style(ForegroundAttribute.get(Color.BLACK));
            if (renderingContext.getDeprecatedObjectChecker().isDeprecated(entity)) {
                style = style.append(ProtegeStyles.getStyles().getDeprecatedEntityStyle());
            }
            if (entity.isOWLClass()) {
                if (!renderingContext.getClassSatisfiabilityChecker().isSatisfiable(entity.asOWLClass())) {
                    style = style.append(ProtegeStyles.getStyles().getUnsatisfiableClassStyle());
                }
            }
            builder.applyStyle(renderingStart, renderingEnd, style);
        }

        public void visit(OWLClass owlClass) {
            renderEntity(owlClass);
        }

        public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
            renderCollection(owlObjectIntersectionOf, owlObjectIntersectionOf.getOperands(), ManchesterOWLSyntax.AND, ComplexClassExpressionBracketingStrategy.get());
        }

        public void visit(OWLObjectUnionOf owlObjectUnionOf) {
            renderCollection(owlObjectUnionOf, owlObjectUnionOf.getOperands(), ManchesterOWLSyntax.OR, ComplexClassExpressionBracketingStrategy.get());
        }

        public void visit(OWLObjectComplementOf owlObjectComplementOf) {
            renderKeyword(ManchesterOWLSyntax.NOT);
            renderSpace();
            owlObjectComplementOf.getOperand().accept(this);
        }

        private boolean isAtomic(OWLObject object) {
            return object instanceof OWLEntity || object instanceof OWLLiteral || object instanceof OWLObjectOneOf || object instanceof OWLDataOneOf;
        }


        private void renderRestrictionFiller(OWLObject filler) {
            if (!isAtomic(filler)) {
                builder.append("(");
                filler.accept(this);
                builder.append(")");
            }
            else {
                filler.accept(this);
            }

        }

        private void renderQuantifiedRestriction(OWLQuantifiedRestriction restriction, ManchesterOWLSyntax keyword) {
            restriction.getProperty().accept(this);
            renderKeywordWithSpaces(keyword);
            renderRestrictionFiller(restriction.getFiller());
        }


        public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            renderQuantifiedRestriction(owlObjectSomeValuesFrom, ManchesterOWLSyntax.SOME);
        }

        public void visit(OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
            renderQuantifiedRestriction(owlObjectAllValuesFrom, ManchesterOWLSyntax.ONLY);
        }


        private void renderHasValueRestriction(OWLHasValueRestriction<?> restriction) {
            restriction.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.VALUE);
            restriction.getValue().accept(this);
        }

        public void visit(OWLObjectHasValue owlObjectHasValue) {
            renderHasValueRestriction(owlObjectHasValue);
        }

        private void renderCardinalityRestriction(OWLCardinalityRestriction<?> restriction, ManchesterOWLSyntax keyword) {
            restriction.getProperty().accept(this);
            renderKeywordWithSpaces(keyword);
            builder.append(restriction.getCardinality());
            renderSpace();
            renderRestrictionFiller(restriction.getFiller());
        }

        public void visit(OWLObjectMinCardinality owlObjectMinCardinality) {
            renderCardinalityRestriction(owlObjectMinCardinality, ManchesterOWLSyntax.MIN);
        }

        public void visit(OWLObjectExactCardinality owlObjectExactCardinality) {
            renderCardinalityRestriction(owlObjectExactCardinality, ManchesterOWLSyntax.EXACTLY);
        }

        public void visit(OWLObjectMaxCardinality owlObjectMaxCardinality) {
            renderCardinalityRestriction(owlObjectMaxCardinality, ManchesterOWLSyntax.MAX);
        }

        public void visit(OWLObjectHasSelf owlObjectHasSelf) {
            owlObjectHasSelf.getProperty().accept(this);
            renderSpace();
            renderKeyword(ManchesterOWLSyntax.SELF);
        }

        public void visit(OWLObjectOneOf owlObjectOneOf) {
            builder.append("{");
            renderCollection(owlObjectOneOf, owlObjectOneOf.getIndividuals(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append("}");
        }

        public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
            renderQuantifiedRestriction(owlDataSomeValuesFrom, ManchesterOWLSyntax.SOME);
        }

        public void visit(OWLDataAllValuesFrom owlDataAllValuesFrom) {
            renderQuantifiedRestriction(owlDataAllValuesFrom, ManchesterOWLSyntax.ONLY);
        }

        public void visit(OWLDataHasValue owlDataHasValue) {
            renderHasValueRestriction(owlDataHasValue);
        }

        public void visit(OWLDataMinCardinality owlDataMinCardinality) {
            renderCardinalityRestriction(owlDataMinCardinality, ManchesterOWLSyntax.MIN);
        }

        public void visit(OWLDataExactCardinality owlDataExactCardinality) {
            renderCardinalityRestriction(owlDataExactCardinality, ManchesterOWLSyntax.EXACTLY);
        }

        public void visit(OWLDataMaxCardinality owlDataMaxCardinality) {
            renderCardinalityRestriction(owlDataMaxCardinality, ManchesterOWLSyntax.MAX);
        }

        public void visit(OWLDatatype owlDatatype) {
            renderEntity(owlDatatype);
        }

        public void visit(OWLDataComplementOf owlDataComplementOf) {
            renderKeyword(ManchesterOWLSyntax.NOT);
            renderSpace();
            owlDataComplementOf.getDataRange().accept(this);
        }

        public void visit(OWLDataOneOf owlDataOneOf) {
            builder.append("{");
            renderCollection(owlDataOneOf, owlDataOneOf.getValues(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append("}");
        }

        public void visit(OWLDataIntersectionOf owlDataIntersectionOf) {
            renderCollection(owlDataIntersectionOf, owlDataIntersectionOf.getOperands(), ManchesterOWLSyntax.AND, NullBracketingStrategy.get());
        }

        public void visit(OWLDataUnionOf owlDataUnionOf) {
            renderCollection(owlDataUnionOf, owlDataUnionOf.getOperands(), ManchesterOWLSyntax.OR, NullBracketingStrategy.get());
        }

        public void visit(OWLDatatypeRestriction owlDatatypeRestriction) {
            owlDatatypeRestriction.getDatatype().accept(this);
            renderSpace();
            builder.append("[");
            renderCollection(owlDatatypeRestriction, owlDatatypeRestriction.getFacetRestrictions(), ManchesterOWLSyntax.COMMA, NullBracketingStrategy.get());
            builder.append("]");
        }

        public void visit(OWLLiteral owlLiteral) {
            if (owlLiteral.isBoolean()) {
                builder.append(Boolean.toString(owlLiteral.parseBoolean()));
            }
            else if (owlLiteral.isDouble()) {
                builder.append(owlLiteral.parseDouble());
            }
            else if (owlLiteral.isFloat()) {
                builder.append(owlLiteral.parseFloat());
            }
            else if (owlLiteral.isInteger()) {
                builder.append(owlLiteral.parseInteger());
            }
            else {
                builder.append("\"");
                builder.append(owlLiteral.getLiteral());
                builder.append("\"^^");
                owlLiteral.getDatatype().accept(this);
            }
        }

        public void visit(OWLFacetRestriction owlFacetRestriction) {
            builder.append(owlFacetRestriction.getFacet().getPrefixedName());
            renderSpace();
            owlFacetRestriction.getFacetValue().accept(this);
        }

        public void visit(OWLNamedIndividual individual) {
            renderEntity(individual);
        }

        public void visit(OWLAnnotationProperty property) {
            renderEntity(property);
        }

        public void visit(OWLOntology owlOntology) {
            OntologyIRIShortFormProvider ontSfp = renderingContext.getOntologyIRIShortFormProvider();
            String ontShortForm = ontSfp.getShortForm(owlOntology);
            builder.append(ontShortForm);
            builder.appendSpace();


            OWLOntologyID id = owlOntology.getOntologyID();
            if (!id.isAnonymous()) {
                builder.appendWithStyle(id.getOntologyIRI().get().toQuotedString(), Style.getForeground(Color.DARK_GRAY));
                if (id.getVersionIRI().isPresent()) {
                    builder.appendSpace();
                    builder.appendWithStyle(id.getVersionIRI().get().toQuotedString(), Style.getForeground(Color.GRAY));
                }
            }
        }

        public void visit(OWLObjectProperty property) {
            renderEntity(property);
        }

        public void visit(OWLObjectInverseOf owlObjectInverseOf) {
            renderKeyword(ManchesterOWLSyntax.INVERSE_OF);
            renderSpace();
            owlObjectInverseOf.getInverse().accept(this);
        }

        public void visit(OWLDataProperty dataProperty) {
            renderEntity(dataProperty);
        }

        private void renderSWRLAtom(SWRLAtom atom, OWLObject predicate) {
            predicate.accept(this);
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        public void visit(SWRLClassAtom swrlClassAtom) {
            renderSWRLAtom(swrlClassAtom, swrlClassAtom.getPredicate());
        }

        public void visit(SWRLDataRangeAtom swrlDataRangeAtom) {
            renderSWRLAtom(swrlDataRangeAtom, swrlDataRangeAtom.getPredicate());
        }

        public void visit(SWRLObjectPropertyAtom swrlObjectPropertyAtom) {
            renderSWRLAtom(swrlObjectPropertyAtom, swrlObjectPropertyAtom.getPredicate());
        }

        public void visit(SWRLDataPropertyAtom swrlDataPropertyAtom) {
            renderSWRLAtom(swrlDataPropertyAtom, swrlDataPropertyAtom.getPredicate());
        }

        public void visit(SWRLBuiltInAtom atom) {
            atom.getPredicate().accept(this);
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        public void visit(SWRLVariable swrlVariable) {
            builder.append("?");
            builder.append(swrlVariable.getIRI().getRemainder().or(swrlVariable.getIRI().toString()));
        }

        public void visit(SWRLIndividualArgument swrlIndividualArgument) {
            swrlIndividualArgument.getIndividual().accept(this);
        }

        public void visit(SWRLLiteralArgument swrlLiteralArgument) {
            swrlLiteralArgument.getLiteral().accept(this);
        }

        public void visit(SWRLSameIndividualAtom atom) {
            builder.append(ManchesterOWLSyntax.SAME_INDIVIDUAL.toString());
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        public void visit(SWRLDifferentIndividualsAtom atom) {
            builder.append(ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS.toString());
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

    }

}
