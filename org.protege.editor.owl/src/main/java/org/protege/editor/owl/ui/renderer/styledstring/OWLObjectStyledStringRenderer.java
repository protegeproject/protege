package org.protege.editor.owl.ui.renderer.styledstring;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.protege.editor.owl.ui.renderer.context.NullClassSatisfiabilityChecker;
import org.protege.editor.owl.ui.renderer.context.NullDataPropertySatisfiabilityChecker;
import org.protege.editor.owl.ui.renderer.context.NullDeprecatedObjectChecker;
import org.protege.editor.owl.ui.renderer.context.NullLinkFactory;
import org.protege.editor.owl.ui.renderer.context.NullObjectPropertySatisfiabilityChecker;
import org.protege.editor.owl.ui.renderer.context.OWLObjectRenderingContext;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLHasValueRestriction;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLQuantifiedRestriction;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

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

        @Override
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

        @Override
        public void visit(OWLSubClassOfAxiom axiom) {
            axiom.getSubClass().accept(this);
            renderSpace();
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUBCLASS_OF);
            renderSpace();
            axiom.getSuperClass().accept(this);
        }

        @Override
        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {

        }

        @Override
        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.ASYMMETRIC);
            axiom.getProperty().accept(this);
        }

        @Override
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

        @Override
        public void visit(OWLDisjointClassesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getClassExpressionsAsList(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_CLASSES, COMMA_SEPARATOR);
        }


        private void renderDomainAxiom(OWLPropertyDomainAxiom<?> axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DOMAIN);
            axiom.getDomain().accept(this);
        }

        @Override
        public void visit(OWLDataPropertyDomainAxiom axiom) {
            renderDomainAxiom(axiom);
        }

        @Override
        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            renderDomainAxiom(axiom);
        }

        @Override
        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        }

        @Override
        public void visit(OWLDifferentIndividualsAxiom axiom) {
            renderBinaryOrNaryList(axiom.getIndividuals(), ManchesterOWLSyntax.SAME_AS, ManchesterOWLSyntax.SAME_INDIVIDUAL, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.DISJOINT_WITH, ManchesterOWLSyntax.DISJOINT_PROPERTIES, COMMA_SEPARATOR);
        }

        private void renderRangeAxiom(OWLPropertyRangeAxiom<?, ?> axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.RANGE);
            axiom.getRange().accept(this);
        }

        @Override
        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            renderRangeAxiom(axiom);
        }

        @Override
        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            renderPropertyAssertion(axiom);
        }

        @Override
        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        @Override
        public void visit(OWLDisjointUnionAxiom axiom) {
            axiom.getOWLClass().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DISJOINT_UNION_OF);
            renderCollection(axiom, axiom.getClassExpressions(), COMMA_SEPARATOR, ComplexClassExpressionBracketingStrategy.get());
        }

        @Override
        public void visit(OWLDeclarationAxiom axiom) {
            axiom.getEntity().accept(new OWLEntityVisitor() {
                @Override
                public void visit(OWLClass owlClass) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.CLASS);
                }

                @Override
                public void visit(OWLObjectProperty property) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.OBJECT_PROPERTY);
                }

                @Override
                public void visit(OWLDataProperty dataProperty) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.DATA_PROPERTY);
                }

                @Override
                public void visit(OWLNamedIndividual individual) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.INDIVIDUAL);
                }

                @Override
                public void visit(OWLDatatype owlDatatype) {
                    renderKeywordColonSpace(ManchesterOWLSyntax.DATATYPE);
                }

                @Override
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

        @Override
        public void visit(OWLAnnotationAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
            renderSpace();
            axiom.getProperty().accept(this);
            renderSpace();
            axiom.getValue().accept(this);
        }

        @Override
        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.SYMMETRIC);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLDataPropertyRangeAxiom axiom) {
            renderRangeAxiom(axiom);
        }

        @Override
        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getProperties(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_PROPERTIES, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLClassAssertionAxiom axiom) {
            axiom.getIndividual().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.TYPE);
            axiom.getClassExpression().accept(this);
        }

        @Override
        public void visit(OWLEquivalentClassesAxiom axiom) {
            renderBinaryOrNaryList(axiom.getClassExpressions(), ManchesterOWLSyntax.EQUIVALENT_TO, ManchesterOWLSyntax.EQUIVALENT_CLASSES, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            renderPropertyAssertion(axiom);
        }

        @Override
        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.TRANSITIVE);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.IRREFLEXIVE);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        @Override
        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            renderKeywordColonSpace(ManchesterOWLSyntax.INVERSE_FUNCTIONAL);
            axiom.getProperty().accept(this);
        }

        @Override
        public void visit(OWLSameIndividualAxiom axiom) {
            renderBinaryOrNaryList(axiom.getIndividuals(), ManchesterOWLSyntax.SAME_AS, ManchesterOWLSyntax.SAME_INDIVIDUAL, COMMA_SEPARATOR);
        }

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            renderCollection(axiom, axiom.getPropertyChain(), " o ", NullBracketingStrategy.get());
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        @Override
        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            axiom.getFirstProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.INVERSE_OF);
            axiom.getSecondProperty().accept(this);
        }

        @Override
        public void visit(OWLHasKeyAxiom axiom) {
        }


        @Override
        public void visit(OWLDatatypeDefinitionAxiom axiom) {
        }

        @Override
        public void visit(SWRLRule swrlRule) {
            renderCollection(swrlRule, swrlRule.getBody(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(" -> ");
            renderCollection(swrlRule, swrlRule.getHead(), COMMA_SEPARATOR, NullBracketingStrategy.get());
        }

        @Override
        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.SUB_PROPERTY_OF);
            axiom.getSuperProperty().accept(this);
        }

        @Override
        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.DOMAIN);
            axiom.getDomain().accept(this);
        }

        @Override
        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.RANGE);
            axiom.getRange().accept(this);
        }

        @Override
        public void visit(IRI iri) {
            builder.append(iri.toQuotedString());
        }

        @Override
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

        @Override
        public void visit(OWLClass owlClass) {
            renderEntity(owlClass);
        }

        @Override
        public void visit(OWLObjectIntersectionOf owlObjectIntersectionOf) {
            renderCollection(owlObjectIntersectionOf, owlObjectIntersectionOf.getOperands(), ManchesterOWLSyntax.AND, ComplexClassExpressionBracketingStrategy.get());
        }

        @Override
        public void visit(OWLObjectUnionOf owlObjectUnionOf) {
            renderCollection(owlObjectUnionOf, owlObjectUnionOf.getOperands(), ManchesterOWLSyntax.OR, ComplexClassExpressionBracketingStrategy.get());
        }

        @Override
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


        @Override
        public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
            renderQuantifiedRestriction(owlObjectSomeValuesFrom, ManchesterOWLSyntax.SOME);
        }

        @Override
        public void visit(OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
            renderQuantifiedRestriction(owlObjectAllValuesFrom, ManchesterOWLSyntax.ONLY);
        }


        private void renderHasValueRestriction(
                OWLHasValueRestriction<?> restriction) {
            restriction.getProperty().accept(this);
            renderKeywordWithSpaces(ManchesterOWLSyntax.VALUE);
            restriction.getValue().accept(this);
        }

        @Override
        public void visit(OWLObjectHasValue owlObjectHasValue) {
            renderHasValueRestriction(owlObjectHasValue);
        }

        private void renderCardinalityRestriction(
                OWLCardinalityRestriction<?> restriction,
                ManchesterOWLSyntax keyword) {
            restriction.getProperty().accept(this);
            renderKeywordWithSpaces(keyword);
            builder.append(restriction.getCardinality());
            renderSpace();
            renderRestrictionFiller(restriction.getFiller());
        }

        @Override
        public void visit(OWLObjectMinCardinality owlObjectMinCardinality) {
            renderCardinalityRestriction(owlObjectMinCardinality, ManchesterOWLSyntax.MIN);
        }

        @Override
        public void visit(OWLObjectExactCardinality owlObjectExactCardinality) {
            renderCardinalityRestriction(owlObjectExactCardinality, ManchesterOWLSyntax.EXACTLY);
        }

        @Override
        public void visit(OWLObjectMaxCardinality owlObjectMaxCardinality) {
            renderCardinalityRestriction(owlObjectMaxCardinality, ManchesterOWLSyntax.MAX);
        }

        @Override
        public void visit(OWLObjectHasSelf owlObjectHasSelf) {
            owlObjectHasSelf.getProperty().accept(this);
            renderSpace();
            renderKeyword(ManchesterOWLSyntax.SELF);
        }

        @Override
        public void visit(OWLObjectOneOf owlObjectOneOf) {
            builder.append("{");
            renderCollection(owlObjectOneOf, owlObjectOneOf.getIndividuals(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append("}");
        }

        @Override
        public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
            renderQuantifiedRestriction(owlDataSomeValuesFrom, ManchesterOWLSyntax.SOME);
        }

        @Override
        public void visit(OWLDataAllValuesFrom owlDataAllValuesFrom) {
            renderQuantifiedRestriction(owlDataAllValuesFrom, ManchesterOWLSyntax.ONLY);
        }

        @Override
        public void visit(OWLDataHasValue owlDataHasValue) {
            renderHasValueRestriction(owlDataHasValue);
        }

        @Override
        public void visit(OWLDataMinCardinality owlDataMinCardinality) {
            renderCardinalityRestriction(owlDataMinCardinality, ManchesterOWLSyntax.MIN);
        }

        @Override
        public void visit(OWLDataExactCardinality owlDataExactCardinality) {
            renderCardinalityRestriction(owlDataExactCardinality, ManchesterOWLSyntax.EXACTLY);
        }

        @Override
        public void visit(OWLDataMaxCardinality owlDataMaxCardinality) {
            renderCardinalityRestriction(owlDataMaxCardinality, ManchesterOWLSyntax.MAX);
        }

        @Override
        public void visit(OWLDatatype owlDatatype) {
            renderEntity(owlDatatype);
        }

        @Override
        public void visit(OWLDataComplementOf owlDataComplementOf) {
            renderKeyword(ManchesterOWLSyntax.NOT);
            renderSpace();
            owlDataComplementOf.getDataRange().accept(this);
        }

        @Override
        public void visit(OWLDataOneOf owlDataOneOf) {
            builder.append("{");
            renderCollection(owlDataOneOf, owlDataOneOf.getValues(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append("}");
        }

        @Override
        public void visit(OWLDataIntersectionOf owlDataIntersectionOf) {
            renderCollection(owlDataIntersectionOf, owlDataIntersectionOf.getOperands(), ManchesterOWLSyntax.AND, NullBracketingStrategy.get());
        }

        @Override
        public void visit(OWLDataUnionOf owlDataUnionOf) {
            renderCollection(owlDataUnionOf, owlDataUnionOf.getOperands(), ManchesterOWLSyntax.OR, NullBracketingStrategy.get());
        }

        @Override
        public void visit(OWLDatatypeRestriction owlDatatypeRestriction) {
            owlDatatypeRestriction.getDatatype().accept(this);
            renderSpace();
            builder.append("[");
            renderCollection(owlDatatypeRestriction, owlDatatypeRestriction.getFacetRestrictions(), ManchesterOWLSyntax.COMMA, NullBracketingStrategy.get());
            builder.append("]");
        }

        @Override
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

        @Override
        public void visit(OWLFacetRestriction owlFacetRestriction) {
            builder.append(owlFacetRestriction.getFacet().getShortForm());
            renderSpace();
            owlFacetRestriction.getFacetValue().accept(this);
        }

        @Override
        public void visit(OWLNamedIndividual individual) {
            renderEntity(individual);
        }

        @Override
        public void visit(OWLAnnotationProperty property) {
            renderEntity(property);
        }

        @Override
        public void visit(OWLOntology owlOntology) {
            OntologyIRIShortFormProvider ontSfp = renderingContext.getOntologyIRIShortFormProvider();
            String ontShortForm = ontSfp.getShortForm(owlOntology);
            builder.append(ontShortForm);
            builder.appendSpace();


            OWLOntologyID id = owlOntology.getOntologyID();
            if (!id.isAnonymous()) {
                builder.appendWithStyle(id.getOntologyIRI().get()
                        .toQuotedString(), Style.getForeground(Color.DARK_GRAY));
                if (id.getVersionIRI().isPresent()) {
                    builder.appendSpace();
                    builder.appendWithStyle(id.getVersionIRI().get()
                            .toQuotedString(), Style.getForeground(Color.GRAY));
                }
            }
        }

        @Override
        public void visit(OWLObjectProperty property) {
            renderEntity(property);
        }

        @Override
        public void visit(OWLObjectInverseOf owlObjectInverseOf) {
            renderKeyword(ManchesterOWLSyntax.INVERSE_OF);
            renderSpace();
            owlObjectInverseOf.getInverse().accept(this);
        }

        @Override
        public void visit(OWLDataProperty dataProperty) {
            renderEntity(dataProperty);
        }

        private void renderSWRLAtom(SWRLAtom atom, OWLObject predicate) {
            predicate.accept(this);
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        @Override
        public void visit(SWRLClassAtom swrlClassAtom) {
            renderSWRLAtom(swrlClassAtom, swrlClassAtom.getPredicate());
        }

        @Override
        public void visit(SWRLDataRangeAtom swrlDataRangeAtom) {
            renderSWRLAtom(swrlDataRangeAtom, swrlDataRangeAtom.getPredicate());
        }

        @Override
        public void visit(SWRLObjectPropertyAtom swrlObjectPropertyAtom) {
            renderSWRLAtom(swrlObjectPropertyAtom, swrlObjectPropertyAtom.getPredicate());
        }

        @Override
        public void visit(SWRLDataPropertyAtom swrlDataPropertyAtom) {
            renderSWRLAtom(swrlDataPropertyAtom, swrlDataPropertyAtom.getPredicate());
        }

        @Override
        public void visit(SWRLBuiltInAtom atom) {
            atom.getPredicate().accept(this);
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        @Override
        public void visit(SWRLVariable swrlVariable) {
            builder.append("?");
            builder.append(swrlVariable.getIRI().getFragment());
        }

        @Override
        public void visit(SWRLIndividualArgument swrlIndividualArgument) {
            swrlIndividualArgument.getIndividual().accept(this);
        }

        @Override
        public void visit(SWRLLiteralArgument swrlLiteralArgument) {
            swrlLiteralArgument.getLiteral().accept(this);
        }

        @Override
        public void visit(SWRLSameIndividualAtom atom) {
            builder.append(ManchesterOWLSyntax.SAME_INDIVIDUAL.toString());
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

        @Override
        public void visit(SWRLDifferentIndividualsAtom atom) {
            builder.append(ManchesterOWLSyntax.DIFFERENT_INDIVIDUALS.toString());
            builder.append("(");
            renderCollection(atom, atom.getAllArguments(), COMMA_SEPARATOR, NullBracketingStrategy.get());
            builder.append(")");
        }

    }

}
