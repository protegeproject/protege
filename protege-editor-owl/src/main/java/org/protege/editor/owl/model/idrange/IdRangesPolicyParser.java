package org.protege.editor.owl.model.idrange;

import com.google.common.collect.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLDataVisitorExAdapter;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
public class IdRangesPolicyParser {

    private final OWLOntology ontology;

    private final ImmutableMultimap<IRI, OWLAnnotation> ontologyAnnotationsByPropertyIri;

    private IdRangesPolicyParser(OWLOntology ontology,
                                 ImmutableMultimap<IRI, OWLAnnotation> ontologyAnnotationsByPropertyIri) {
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
        return Multimaps.index(annotations,
                               anno -> anno == null ? null : anno.getProperty().getIRI());
    }

    /**
     * Parse the ID policy from the ontology
     */
    @Nonnull
    public IdRangesPolicy parse() {
        Optional<String> idPolicyFor = parseIdPolicyFor();
        if(!idPolicyFor.isPresent()) {
            throw new IdPolicyParseException(String.format("'Id policy for' (%s) ontology annotation not found", IdPolicyVocabulary.ID_POLICY_FOR.getIri()));
        }
        Optional<String> idPrefix = parseIdPrefix();
        if(!idPrefix.isPresent()) {
            throw new IdPolicyParseException(String.format("'Id prefix' (%s) ontology annotation not found", IdPolicyVocabulary.ID_PREFIX.getIri()));
        }
        Optional<Integer> digitCount = parseIdDigitCount();
        if(!digitCount.isPresent()) {
            throw new IdPolicyParseException(String.format("'Id digit count' (%s) ontology annotation not found", IdPolicyVocabulary.ID_DIGIT_COUNT.getIri()));
        }
        ImmutableList<UserIdRange> userIdRanges = parseUserIdRanges();
        IdRangesPolicy policy = IdRangesPolicy.get(idPrefix.get(), digitCount.get(), idPolicyFor.get(), userIdRanges);
        return policy;
    }

    /**
     * Retrieves the annotation value lexical value if it is a literal.
     * @param value The value.
     */
    private static Optional<String> toLexicalValueIfLiteral(@Nonnull OWLAnnotationValue value) {
        if(value.isLiteral()) {
            return Optional.of(((OWLLiteral) value).getLiteral());
        }
        return Optional.empty();
    }

    private static Optional<IdRange> parseIdRange(OWLDatatypeDefinitionAxiom axiom) {
        Optional<OWLDatatypeRestriction> datatypeRestriction = getDefinedDatatypeRestriction(axiom);
        return datatypeRestriction.map(IdRangesPolicyParser::getIdRange);
    }

    private static Optional<OWLDatatypeRestriction> getDefinedDatatypeRestriction(OWLDatatypeDefinitionAxiom axiom) {
        return axiom
                .getDataRange()
                .accept(new OWLDataVisitorExAdapter<Optional<OWLDatatypeRestriction>>(Optional.empty()) {
                    @Nonnull
                    @Override
                    public Optional<OWLDatatypeRestriction> visit(OWLDatatypeRestriction node) {
                        return Optional.of(node);
                    }
                });
    }

    private static IdRange getIdRange(OWLDatatypeRestriction dtr) {
        int lowerBound = 0;
        int upperBound = 0;
        for(OWLFacetRestriction restriction : dtr.getFacetRestrictions()) {
            if(restriction.getFacet() == OWLFacet.MIN_INCLUSIVE) {
                lowerBound = parseIntValue(restriction);
            }
            else if(restriction.getFacet() == OWLFacet.MAX_INCLUSIVE) {
                upperBound = parseIntValue(restriction);
            }
            else if(restriction.getFacet() == OWLFacet.MIN_EXCLUSIVE) {
                lowerBound = parseIntValue(restriction) + 1;
            }
            else if(restriction.getFacet() == OWLFacet.MAX_EXCLUSIVE) {
                upperBound = parseIntValue(restriction) - 1;
            }
        }
        return IdRange.getIdRange(lowerBound, upperBound);
    }

    private static int parseIntValue(OWLFacetRestriction restriction) {
        try {
            String lexicalValue = restriction.getFacetValue().getLiteral().trim();
            return Integer.parseInt(lexicalValue);
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    private Optional<Integer> parseIdDigitCount() {
        Optional<String> digitCountLexicalValue = parseOntologyAnnotationFirstLexicalValue(IdPolicyVocabulary.ID_DIGIT_COUNT.getIri());
        try {
            return digitCountLexicalValue.map(Integer::parseInt);
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Optional<String> parseIdPrefix() {
        return parseOntologyAnnotationFirstLexicalValue(IdPolicyVocabulary.ID_PREFIX.getIri());
    }

    private Optional<String> parseIdPolicyFor() {
        return parseOntologyAnnotationFirstLexicalValue(IdPolicyVocabulary.ID_POLICY_FOR.getIri());
    }

    private Optional<String> parseOntologyAnnotationFirstLexicalValue(IRI propertyIRI) {
        return ontologyAnnotationsByPropertyIri.
                get(propertyIRI)
                .stream()
                .map(OWLAnnotation::getValue)
                .findFirst()
                .flatMap(IdRangesPolicyParser::toLexicalValueIfLiteral);
    }

    private ImmutableList<UserIdRange> parseUserIdRanges() {
        List<UserIdRange> list = ontology
                .getDatatypesInSignature()
                .stream()
                .filter(dt -> !dt.isBuiltIn())
                .map(this::parseUserIdRange)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return ImmutableList.copyOf(list);
    }

    private Optional<UserIdRange> parseUserIdRange(OWLDatatype datatype) {
        Optional<String> allocatedTo = parseAllocatedTo(datatype);
        Optional<IdRange> idRange = parseIdRange(datatype);
        return allocatedTo.flatMap(userId -> idRange.map(rng -> UserIdRange.get(userId, rng)));
    }

    private Optional<String> parseAllocatedTo(OWLDatatype datatype) {
        return findFirstLexicalValue(datatype, getAllocatedToProperty());
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

    private OWLAnnotationProperty getAllocatedToProperty() {
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        return dataFactory.getOWLAnnotationProperty(IdPolicyVocabulary.ID_RANGE_ALLOCATED_TO.getIri());
    }

    private Optional<IdRange> parseIdRange(OWLDatatype datatype) {
        return ontology.getDatatypeDefinitions(datatype)
                .stream()
                .sorted()
                .findFirst()
                .flatMap(IdRangesPolicyParser::parseIdRange);

    }
}
