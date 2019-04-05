package org.protege.editor.owl.model.merge;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Mar 2018
 */
@RunWith(MockitoJUnitRunner.class)
public class MergeEntitiesChangeListGenerator_TestCase {


    private OWLOntology rootOntology;

    private OWLDataFactory dataFactory;

    private ImmutableSet<OWLEntity> sourceEntities;

    private OWLClass targetEntity;

    private OWLAnnotationProperty rdfsLabel;

    private OWLAnnotationProperty skosPrefLabel;

    private OWLAnnotationProperty skosAltLabel;

    private OWLAnnotationProperty oboSynonym;

    private OWLAnnotationProperty rdfsComment;

    private OWLAnnotationValue hello;

    private OWLAnnotationValue bonjour;

    private OWLAnnotationValue hi;

    private OWLClass clsC;

    private OWLOntologyManager manager;

    private OWLClass sourceEntity;

    @Before
    public void setUp() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        IRI iriA = IRI.create("http://ontology.org/A");
        IRI iriB = IRI.create("http://ontology.org/B");
        IRI iriC = IRI.create("http://ontology.org/C");
        sourceEntity = Class(iriA);
        sourceEntities = ImmutableSet.of(sourceEntity);
        targetEntity = Class(iriB);
        clsC = Class(iriC);
        rdfsLabel = dataFactory.getRDFSLabel();
        skosPrefLabel = dataFactory.getOWLAnnotationProperty(SKOSVocabulary.PREFLABEL.getIRI());
        skosAltLabel = dataFactory.getOWLAnnotationProperty(SKOSVocabulary.ALTLABEL.getIRI());
        oboSynonym = dataFactory.getOWLAnnotationProperty(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI());
        rdfsComment = dataFactory.getRDFSComment();
        hello = Literal("Hello", "en");
        bonjour = Literal("Bonjour", "fr");
        hi = Literal("hi", "en");
        rootOntology = Ontology(manager,
                                SubClassOf(sourceEntity, clsC),
                                SubClassOf(targetEntity, clsC),
                                AnnotationAssertion(rdfsLabel, sourceEntity.getIRI(), hello),
                                AnnotationAssertion(skosPrefLabel, sourceEntity.getIRI(), bonjour),
                                AnnotationAssertion(rdfsComment, sourceEntity.getIRI(), hi));

    }

    private void createGeneratorAndApplyChanges() {
        MergeEntitiesChangeListGenerator gen = new MergeEntitiesChangeListGenerator(rootOntology, dataFactory, sourceEntities, targetEntity);
        List<OWLOntologyChange> changeList = gen.generateChanges();
        manager.applyChanges(changeList);
    }

    @Test
    public void shouldReplaceSourceWithTarget() {
        createGeneratorAndApplyChanges();
        assertThat(rootOntology.containsEntityInSignature(sourceEntity), is(false));
        assertThat(rootOntology.containsAxiom(SubClassOf(sourceEntity, clsC)), is(false));
        assertThat(rootOntology.containsAxiom(SubClassOf(targetEntity, clsC)), is(true));
        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsComment, sourceEntity.getIRI(), hi)), is(false));
        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsComment, targetEntity.getIRI(), hi)), is(true));
    }

    @Test
    public void shouldReplaceSourceRdfsLabelWithTargetAltLabel() {
        createGeneratorAndApplyChanges();
        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsLabel, sourceEntity.getIRI(), hello)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsLabel, targetEntity.getIRI(), hello)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(skosAltLabel, targetEntity.getIRI(), hello)), is(true));
    }

    @Test
    public void shouldReplaceSourceSkosPrefLabelWithTargetAltLabel() {
        createGeneratorAndApplyChanges();
        assertThat(rootOntology.containsAxiom(AnnotationAssertion(skosPrefLabel, sourceEntity.getIRI(), bonjour)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(skosPrefLabel, targetEntity.getIRI(), bonjour)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(skosAltLabel, targetEntity.getIRI(), bonjour)), is(true));
    }

    @Test
    public void shouldReplaceSourceRdfsLabelWithOboSynonymIfOboId() {
        targetEntity = Class(IRI.create("http://purl.obolibrary.org/TO/TO_00000123"));
        createGeneratorAndApplyChanges();
        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsLabel, sourceEntity.getIRI(), hello)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(rdfsLabel, targetEntity.getIRI(), hello)), is(false));

        assertThat(rootOntology.containsAxiom(AnnotationAssertion(oboSynonym, targetEntity.getIRI(), hello)), is(true));
    }
}

