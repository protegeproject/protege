package org.protege.editor.owl.model.idrange;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLDataVisitorExAdapter;
import org.semanticweb.owlapi.vocab.OWLFacet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
public class IdRangesPolicyParser {

    @Nonnull
    private final OWLOntology ontology;

    @Nonnull
    private final ImmutableMultimap<IRI, OWLAnnotation> ontologyAnnotationsByPropertyIri;

    private IdRangesPolicyParser(@Nonnull OWLOntology ontology,
                                 @Nonnull ImmutableMultimap<IRI, OWLAnnotation> ontologyAnnotationsByPropertyIri) {
        this.ontology = checkNotNull(ontology);
        this.ontologyAnnotationsByPropertyIri = checkNotNull(ontologyAnnotationsByPropertyIri);
    }

    public static IdRangesPolicyParser get(@Nonnull OWLOntology ontology) {
        ImmutableMultimap<IRI, OWLAnnotation> annosByIri = getOntologyAnnotationsByPropertyIri(ontology);
        return new IdRangesPolicyParser(ontology, annosByIri);
    }

    private static ImmutableListMultimap<IRI, OWLAnnotation> getOntologyAnnotationsByPropertyIri(@Nonnull OWLOntology ontology) {
        List<OWLAnnotation> annotations = new ArrayList<>(ontology.getAnnotations());
        Collections.sort(annotations);
        return Multimaps.index(annotations, anno -> anno == null ? null : anno.getProperty().getIRI());
    }

    /**
     * Retrieves the annotation value lexical value if it is a literal.
     *
     * @param value The value.
     */
    private static Optional<String> toLexicalValueIfLiteral(@Nonnull OWLAnnotationValue value) {
        if(value.isLiteral()) {
            return Optional.of(((OWLLiteral) value).getLiteral());
        }
        return Optional.empty();
    }

    private static IdRange parseIdRange(OWLDatatypeDefinitionAxiom axiom) {
        OWLDatatypeRestriction datatypeRestriction = parseIdRangeDatatypeRestriction(axiom);
        return parseIdRange(datatypeRestriction);
    }

    @Nonnull
    private static OWLDatatypeRestriction parseIdRangeDatatypeRestriction(OWLDatatypeDefinitionAxiom axiom) {
        return axiom
                .getDataRange()
                .accept(new OWLDataVisitorExAdapter<Optional<OWLDatatypeRestriction>>(Optional.empty()) {
                    @Nonnull
                    @Override
                    public Optional<OWLDatatypeRestriction> visit(OWLDatatypeRestriction node) {
                        return Optional.of(node);
                    }
                })
                .orElseThrow(() -> new IdRangesPolicyParseException(String.format("Expected datatype restriction definition, but not found (%s)", axiom)));
    }

    private static IdRange parseIdRange(OWLDatatypeRestriction dtr) {
        int lowerBound = -1;
        int upperBound = -1;
        for(OWLFacetRestriction restriction : dtr.getFacetRestrictions()) {
            if(restriction.getFacet() == OWLFacet.MIN_INCLUSIVE) {
                lowerBound = parseFacetValueAsInt(restriction);
            }
            else if(restriction.getFacet() == OWLFacet.MAX_INCLUSIVE) {
                upperBound = parseFacetValueAsInt(restriction);
            }
            else if(restriction.getFacet() == OWLFacet.MIN_EXCLUSIVE) {
                lowerBound = parseFacetValueAsInt(restriction) + 1;
            }
            else if(restriction.getFacet() == OWLFacet.MAX_EXCLUSIVE) {
                upperBound = parseFacetValueAsInt(restriction) - 1;
            }
        }
        if(lowerBound == -1) {
            throw new IdRangesPolicyParseException(String.format("Expected min inclusive facet to specify lower bound of data range, but not found (%s)", dtr));
        }
        if(upperBound == -1) {
            throw new IdRangesPolicyParseException(String.format("Expected max inclusive facet to specify upper bound of data range, but not found (%s)", dtr));
        }
        return IdRange.getIdRange(lowerBound, upperBound);
    }

    private static int parseFacetValueAsInt(OWLFacetRestriction restriction) {
        try {
            String lexicalValue = restriction.getFacetValue().getLiteral().trim();
            return Integer.parseInt(lexicalValue);
        } catch(NumberFormatException e) {
            throw new IdRangesPolicyParseException(String.format("Invalid value for id range: %s %s", restriction
                    .getFacet()
                    .getShortForm(), restriction.getFacetValue().toString()));
        }
    }

    /**
     * Parse the ID policy from the ontology
     */
    @Nonnull
    public IdRangesPolicy parse() {
        String idPolicyFor = parseIdPolicyFor();
        String idPrefix = parseIdPrefix();
        int digitCount = parseIdDigitCount();
        ImmutableList<UserIdRange> userIdRanges = parseUserIdRanges();
        return IdRangesPolicy.get(idPrefix, digitCount, idPolicyFor, userIdRanges);
    }

    private Integer parseIdDigitCount() {
        Optional<String> digitCountLexicalValue = parseOntologyAnnotationFirstLexicalValue(IdRangesPolicyVocabulary.ID_DIGIT_COUNT
                                                                                                   .getIri());
        if(!digitCountLexicalValue.isPresent()) {
            throw new IdRangesPolicyParseException(String.format("'Id digit count' (%s) ontology annotation not found", IdRangesPolicyVocabulary.ID_DIGIT_COUNT
                    .getIri()));
        }
        try {
            return digitCountLexicalValue.map(Integer::parseInt).orElse(0);
        } catch(NumberFormatException e) {
            throw new IdRangesPolicyParseException(String.format("Invalid value for digit count (%s).  Expected integer.", digitCountLexicalValue.get()));
        }
    }

    private String parseIdPrefix() {
        return parseOntologyAnnotationFirstLexicalValue(IdRangesPolicyVocabulary.ID_PREFIX.getIri())
                .orElseThrow(() -> new IdRangesPolicyParseException(String.format("'Id prefix' (%s) ontology annotation not found", IdRangesPolicyVocabulary.ID_PREFIX
                        .getIri())));
    }

    private String parseIdPolicyFor() {
        return parseOntologyAnnotationFirstLexicalValue(IdRangesPolicyVocabulary.ID_POLICY_FOR.getIri())
                .orElseThrow(() -> new IdRangesPolicyParseException(String.format("'Id policy for' (%s) ontology annotation not found", IdRangesPolicyVocabulary.ID_POLICY_FOR
                .getIri())));
    }

    private Optional<String> parseOntologyAnnotationFirstLexicalValue(IRI propertyIRI) {
        return ontologyAnnotationsByPropertyIri
                .get(propertyIRI)
                .stream()
                .map(OWLAnnotation::getValue)
                .findFirst()
                .flatMap(IdRangesPolicyParser::toLexicalValueIfLiteral);
    }

    @Nonnull
    private ImmutableList<UserIdRange> parseUserIdRanges() {
        List<UserIdRange> list = ontology
                .getDatatypesInSignature()
                .stream()
                .filter(dt -> !dt.isBuiltIn())
                .filter(this::hasAllocatedToAnnotation)
                .map(this::parseUserIdRange)
                .collect(Collectors.toList());
        return ImmutableList.copyOf(list);
    }

    private boolean hasAllocatedToAnnotation(@Nonnull OWLDatatype datatype) {
        return !EntitySearcher.getAnnotationObjects(datatype, ontology, getAllocatedToProperty()).isEmpty();
    }

    @Nonnull
    private UserIdRange parseUserIdRange(OWLDatatype datatype) {
        String allocatedTo = parseAllocatedTo(datatype);
        IdRange idRange = parseIdRange(datatype);
        return UserIdRange.get(allocatedTo, idRange);
    }

    @Nonnull
    private String parseAllocatedTo(OWLDatatype datatype) {
        return findFirstLexicalValue(datatype, getAllocatedToProperty()).orElseThrow(() -> new IdRangesPolicyParseException(String.format("Expected 'allocated to' (%s) but not found on %s", IdRangesPolicyVocabulary.ID_RANGE_ALLOCATED_TO
                .getIri(), datatype.getIRI().toQuotedString())));
    }

    private Optional<String> findFirstLexicalValue(OWLDatatype datatype,
                                                   OWLAnnotationProperty property) {
        Collection<OWLAnnotation> allocatedToValues = EntitySearcher.getAnnotationObjects(datatype, ontology, property);
        return allocatedToValues
                .stream()
                .map(OWLAnnotation::getValue)
                .map(IdRangesPolicyParser::toLexicalValueIfLiteral)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .findFirst();
    }

    @Nonnull
    private OWLAnnotationProperty getAllocatedToProperty() {
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        return dataFactory.getOWLAnnotationProperty(IdRangesPolicyVocabulary.ID_RANGE_ALLOCATED_TO.getIri());
    }

    @Nonnull
    private IdRange parseIdRange(@Nonnull OWLDatatype datatype) {
        return ontology
                .getDatatypeDefinitions(datatype)
                .stream()
                .sorted()
                .findFirst()
                .map(IdRangesPolicyParser::parseIdRange)
                .orElseThrow(() -> new IdRangesPolicyParseException("Id range datatype definition not found"));

    }
}
