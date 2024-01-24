package org.protege.editor.owl.model.deprecation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.model.entity.HomeOntologySupplier;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 28 Aug 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityDeprecator_TestCase {

    private static final String LABEL_PREFIX = "!label prefix!";

    private static final String VALUE_PREFIX = "!value prefix!";

    private static final String THE_DEPRECATION_REASON = "The deprecation reason";

    private static final String THE_LABEL = "The Label";

    private static final String SEE_ALSO = "See Also";

    @Mock
    private DeprecationProfile deprecationProfile;

    private OWLDataFactory dataFactory;

    private EntityDeprecator<OWLClass> entityDeprecator;

    private OWLOntology ont;

    private OWLClass toDeprecate;

    private IRI replacedByAP;

    private IRI alternateAP;

    private IRI reasonAP;

    private OWLClass superCls;

    private OWLClass subCls;

    private OWLClass replacementCls;

    private OWLClass alternateCls;

    private OWLAnnotationAssertionAxiom otherAnnoAssertion;


    @Before
    public void setUp() throws Exception {

        dataFactory = new OWLDataFactoryImpl();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        PrefixManager pm = new DefaultPrefixManager();
        pm.setDefaultPrefix("http://the.ontology/ont/");
        toDeprecate = Class("DeprecatedCls", pm);
        superCls = Class("SuperCls", pm);
        subCls = Class("SubCls", pm);
        replacementCls = Class("ReplacementCls", pm);
        alternateCls = Class("AlternateCls", pm);
        ont = Ontology(
                manager,
                SubClassOf(toDeprecate, superCls),
                SubClassOf(subCls, toDeprecate),
                AnnotationAssertion(dataFactory.getRDFSLabel(),
                                                toDeprecate.getIRI(),
                                                Literal(THE_LABEL)),
                AnnotationAssertion(dataFactory.getRDFSSeeAlso(),
                                    toDeprecate.getIRI(),
                                    Literal(SEE_ALSO)),
                otherAnnoAssertion = AnnotationAssertion(dataFactory.getRDFSIsDefinedBy(),
                                    toDeprecate.getIRI(),
                                    Literal("Some Other Annotation Assertion"))
        );
        Set<OWLOntology> ontologies = Collections.singleton(ont);
        when(deprecationProfile.getDeprecatedEntityLabelPrefix()).thenReturn("!deprecated!");
        replacedByAP = AnnotationProperty("replacedBy", pm).getIRI();
        alternateAP = AnnotationProperty("alternateEntity", pm).getIRI();
        reasonAP = AnnotationProperty("reason", pm).getIRI();
        when(deprecationProfile.shouldRemoveLogicalDefinition()).thenReturn(true);
        when(deprecationProfile.getReplacedByAnnotationPropertyIri()).thenReturn(Optional.of(replacedByAP));
        when(deprecationProfile.getDeprecationTextualReasonAnnotationPropertyIri()).thenReturn(Optional.of(reasonAP));
        when(deprecationProfile.getAlternateEntityAnnotationPropertyIri()).thenReturn(Optional.of(alternateAP));
        when(deprecationProfile.getDeprecatedEntityLabelPrefix()).thenReturn(LABEL_PREFIX);
        when(deprecationProfile.getPreservedAnnotationValuePrefix()).thenReturn(VALUE_PREFIX);
        when(deprecationProfile.getPreservedAnnotationValuePropertiesIris()).thenReturn(Collections.singleton(dataFactory.getRDFSSeeAlso().getIRI()));
        when(deprecationProfile.getDeprecatedClassParentIri()).thenReturn(Optional.empty());
        //when(deprecationProfile.getDeprecatedObjectPropertyParentIri()).thenReturn(Optional.empty());
        //when(deprecationProfile.getDeprecatedDataPropertyParentIri()).thenReturn(Optional.empty());
        //when(deprecationProfile.getDeprecatedAnnotationPropertyParentIri()).thenReturn(Optional.empty());
        //when(deprecationProfile.getDeprecatedIndividualParentClassIri()).thenReturn(Optional.empty());
        when(deprecationProfile.getDeprecationCode()).thenReturn(Optional.empty());

        DeprecateEntityInfo<OWLClass> info = new DeprecateEntityInfo<>(
                toDeprecate,
                replacementCls,
                THE_DEPRECATION_REASON,
                Collections.singleton(alternateCls),
                null);
        entityDeprecator = new EntityDeprecator<>(info,
                                                  deprecationProfile,
                                                  ontologies,
                                                  new HomeOntologySupplier(),
                                                  dataFactory);
    }

    private void performDeprecation() {
        ont.getOWLOntologyManager().applyChanges(entityDeprecator.getChanges());
    }

    @Test
    public void shouldAddOWLDeprecatedAnnotation() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLDeprecated(),
                toDeprecate.getIRI(),
                dataFactory.getOWLLiteral(true)
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }


    @Test
    public void shouldAddReplacedByAnnotation() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLAnnotationProperty(replacedByAP),
                toDeprecate.getIRI(),
                replacementCls.getIRI()
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }

    @Test
    public void shouldAddReasonAnnotation() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLAnnotationProperty(reasonAP),
                toDeprecate.getIRI(),
                Literal(THE_DEPRECATION_REASON)
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }


    @Test
    public void shouldAddAlternateAnnotations() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLAnnotationProperty(alternateAP),
                toDeprecate.getIRI(),
                alternateCls.getIRI()
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }

    @Test
    public void shouldContainPrefixedLabel() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getRDFSLabel(),
                toDeprecate.getIRI(),
                dataFactory.getOWLLiteral(LABEL_PREFIX + " " + THE_LABEL)
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }

    @Test
    public void shouldContainPrefixedAnnotation() {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getRDFSSeeAlso(),
                toDeprecate.getIRI(),
                dataFactory.getOWLLiteral(VALUE_PREFIX + " " + SEE_ALSO)
        );
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }

    @Test
    public void shouldNotContainLogicalDefinition() {
        OWLSubClassOfAxiom ax = SubClassOf(toDeprecate, superCls);
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(false));
    }

    @Test
    public void shouldContainLogicalDefinition() {
        when(deprecationProfile.shouldRemoveLogicalDefinition()).thenReturn(false);
        OWLSubClassOfAxiom ax = SubClassOf(toDeprecate, superCls);
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(true));
    }

    @Test
    public void shouldNotReplaceEntityInLogicalDefinition() {
        OWLSubClassOfAxiom ax = SubClassOf(replacementCls, superCls);
        performDeprecation();
        assertThat(ont.containsAxiom(ax), is(false));
    }

    @Test
    public void shouldReplaceUsagesOfEntity() {
        performDeprecation();
        OWLSubClassOfAxiom ax = SubClassOf(subCls, replacementCls);
        assertThat(ont.containsAxiom(ax), is(true));
        OWLSubClassOfAxiom ax3 = SubClassOf(subCls, toDeprecate);
        assertThat(ont.containsAxiom(ax3), is(false));
    }

    @Test
    public void shouldNotReplaceLogicalDefinition() {
        performDeprecation();
        OWLSubClassOfAxiom ax2 = SubClassOf(replacementCls, superCls);
        assertThat(ont.containsAxiom(ax2), is(false));
    }

    @Test
    public void shouldLeaveOtherAnnotationAssertionsIntact() {
        performDeprecation();
        assertThat(ont.containsAxiom(otherAnnoAssertion), is(true));
    }

    @Test
    public void shouldNotLeaveOtherAnnotationAssertionsIntact() {
        when(deprecationProfile.shouldRemoveAnnotationAssertions()).thenReturn(true);
        performDeprecation();
        assertThat(ont.containsAxiom(otherAnnoAssertion), is(false));
    }

}
