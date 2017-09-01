package org.protege.editor.owl.ui.renderer;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologySetProvider;
import org.semanticweb.owlapi.util.*;

/**
 * A short form provider that generates short forms based on entity annotation
 * values. A list of preferred annotation URIs and preferred annotation
 * languages is used to determine which annotation value to select if there are
 * multiple annotations for the entity whose short form is being generated. If
 * there are multiple annotations the these annotations are ranked by preferred
 * IRI and then by preferred language.
 *
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class AnnotationValueShortFormProvider implements ShortFormProvider {

    @Nonnull
    private final OWLOntologySetProvider ontologySetProvider;
    @Nonnull
    private final ShortFormProvider alternateShortFormProvider;
    @Nonnull
    private final IRIShortFormProvider alternateIRIShortFormProvider;
    @Nonnull
    private final List<OWLAnnotationProperty> annotationProperties;
    @Nonnull
    private final Map<OWLAnnotationProperty, List<String>> preferredLanguageMap;
    @Nonnull
    private StringAnnotationVisitor literalRenderer = new StringAnnotationVisitor();

    /**
     * Constructs an annotation value short form provider. Using
     * {@code SimpleShortFormProvider} as the alternate short form provider
     *
     * @param annotationProperties
     *        A {@code List} of preferred annotation properties. The list is
     *        searched from start to end, so that annotations that have a
     *        property at the start of the list have a higher priority and are
     *        selected over annotations with properties that appear towards or
     *        at the end of the list.
     * @param preferredLanguageMap
     *        A map which maps annotation properties to preferred languages. For
     *        any given annotation property there may be a list of preferred
     *        languages. Languages at the start of the list have a higher
     *        priority over languages at the end of the list. This parameter may
     *        be empty but it must not be {@code null}.
     * @param ontologySetProvider
     *        An {@code OWLOntologySetProvider} which provides a set of ontology
     *        from which candidate annotation axioms should be taken. For a
     *        given entity, all ontologies are examined.
     */
    public AnnotationValueShortFormProvider(
            @Nonnull List<OWLAnnotationProperty> annotationProperties,
            @Nonnull Map<OWLAnnotationProperty, List<String>> preferredLanguageMap,
            @Nonnull OWLOntologySetProvider ontologySetProvider) {
        this(annotationProperties, preferredLanguageMap, ontologySetProvider,
             new SimpleShortFormProvider());
    }

    /**
     * Constructs an annotation short form provider.
     *
     * @param annotationProperties
     *        A {@code List} of preferred annotation properties. The list is
     *        searched from start to end, so that annotations that have a
     *        property at the start of the list have a higher priority and are
     *        selected over annotations with properties that appear towards or
     *        at the end of the list.
     * @param preferredLanguageMap
     *        A map which maps annotation properties to preferred languages. For
     *        any given annotation property there may be a list of preferred
     *        languages. Languages at the start of the list have a higher
     *        priority over languages at the end of the list. This parameter may
     *        be empty but it must not be {@code null}.
     * @param ontologySetProvider
     *        An {@code OWLOntologySetProvider} which provides a set of ontology
     *        from which candidate annotation axioms should be taken. For a
     *        given entity, all ontologies are examined.
     * @param alternateShortFormProvider
     *        A short form provider which will be used to generate the short
     *        form for an entity that does not have any annotations. This
     *        provider will also be used in the case where the value of an
     *        annotation is an {@code OWLIndividual} for providing the short
     *        form of the individual.
     */
    public AnnotationValueShortFormProvider(
            @Nonnull List<OWLAnnotationProperty> annotationProperties,
            @Nonnull Map<OWLAnnotationProperty, List<String>> preferredLanguageMap,
            @Nonnull OWLOntologySetProvider ontologySetProvider,
            @Nonnull ShortFormProvider alternateShortFormProvider) {
        this(ontologySetProvider, alternateShortFormProvider,
             new SimpleIRIShortFormProvider(), annotationProperties,
             preferredLanguageMap);
    }

    /**
     * @param ontologySetProvider
     *        ontologies
     * @param alternateShortFormProvider
     *        short form provider
     * @param alternateIRIShortFormProvider
     *        iri short form provider
     * @param annotationProperties
     *        annotation properties
     * @param preferredLanguageMap
     *        preferred language map
     */
    public AnnotationValueShortFormProvider(
            @Nonnull OWLOntologySetProvider ontologySetProvider,
            @Nonnull ShortFormProvider alternateShortFormProvider,
            @Nonnull IRIShortFormProvider alternateIRIShortFormProvider,
            @Nonnull List<OWLAnnotationProperty> annotationProperties,
            @Nonnull Map<OWLAnnotationProperty, List<String>> preferredLanguageMap) {
        this.ontologySetProvider = checkNotNull(ontologySetProvider,
                                                "ontologySetProvider cannot be null");
        this.alternateShortFormProvider = checkNotNull(
                alternateShortFormProvider,
                "alternateShortFormProvider cannot be null");
        this.alternateIRIShortFormProvider = checkNotNull(
                alternateIRIShortFormProvider,
                "alternateIRIShortFormProvider cannot be null");
        this.annotationProperties = checkNotNull(annotationProperties,
                                                 "annotationProperties cannot be null");
        this.preferredLanguageMap = checkNotNull(preferredLanguageMap,
                                                 "preferredLanguageMap cannot be null");
    }

    @Nonnull
    @Override
    public String getShortForm(@Nonnull OWLEntity entity) {
        for (OWLAnnotationProperty prop : annotationProperties) {
            // visit the properties in order of preference
            AnnotationLanguageFilter checker = new AnnotationLanguageFilter(
                    prop, preferredLanguageMap.get(prop));
            for (OWLOntology ontology : ontologySetProvider.getOntologies()) {
                    for (OWLAxiom ax : ontology.getAnnotationAssertionAxioms(entity
                                                                              .getIRI())) {
                        ax.accept(checker);
                    }
            }
            OWLObject match = checker.getMatch();
            if (match != null) {
                return getRendering(match);
            }
        }
        return alternateShortFormProvider.getShortForm(entity);
    }

    /**
     * Obtains the rendering of the specified object. If the object is a
     * constant then the rendering is equal to the literal value, if the object
     * is an individual then the rendering is equal to the rendering of the
     * individual as provided by the alternate short form provider
     *
     * @param object
     *        The object to the rendered
     * @return The rendering of the object.
     */
    @Nonnull
    private String getRendering(@Nonnull OWLObject object) {
        // We return the literal value of constants or use the alternate
        // short form provider to render individuals.
        if (object instanceof OWLLiteral) {
            // TODO refactor this method to use the annotation value visitor
            return literalRenderer.visit((OWLLiteral) object);
        } else if (object instanceof IRI) {
            return alternateIRIShortFormProvider.getShortForm((IRI) object);
        } else {
            return alternateShortFormProvider.getShortForm((OWLEntity) object);
        }
    }

    /** @return the annotation URIs that this short form provider uses. */
    @Nonnull
    public List<OWLAnnotationProperty> getAnnotationProperties() {
        return annotationProperties;
    }

    /** @return the preferred language map */
    @Nonnull
    public Map<OWLAnnotationProperty, List<String>> getPreferredLanguageMap() {
        return preferredLanguageMap;
    }

    @Override
    public void dispose() {}

    private static class AnnotationLanguageFilter extends
            OWLObjectVisitorAdapter {

        private final OWLAnnotationProperty prop;
        private final List<String> preferredLanguages;
        @Nullable
        OWLObject candidateValue = null;
        int lastLangMatchIndex = Integer.MAX_VALUE;

        AnnotationLanguageFilter(OWLAnnotationProperty prop,
                                 List<String> preferredLanguages) {
            this.prop = prop;
            this.preferredLanguages = preferredLanguages;
        }

        @Nullable
        public OWLObject getMatch() {
            return candidateValue;
        }

        @Override
        public void visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
            if (lastLangMatchIndex > 0 && axiom.getProperty().equals(prop)) {
                // a perfect match - no need to carry on search
                axiom.getValue().accept(this);
            }
        }

        @Override
        public void visit(@Nonnull OWLLiteral node) {
            if (preferredLanguages == null || preferredLanguages.isEmpty()) {
                // if there are no languages just match the first thing
                lastLangMatchIndex = 0;
                candidateValue = node;
            } else {
                int index = preferredLanguages.indexOf(node.getLang());
                if (index >= 0 && index < lastLangMatchIndex) {
                    lastLangMatchIndex = index;
                    candidateValue = node;
                }
            }
        }

        @Override
        public void visit(IRI iri) {
            // No language
            candidateValue = iri;
        }
    }

    /**
     * @param literalRenderer
     *        the literalRenderer to set
     */
    public void setLiteralRenderer(
            @Nonnull StringAnnotationVisitor literalRenderer) {
        this.literalRenderer = checkNotNull(literalRenderer);
    }
}