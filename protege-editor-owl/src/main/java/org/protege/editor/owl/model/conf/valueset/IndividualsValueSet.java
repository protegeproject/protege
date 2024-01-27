package org.protege.editor.owl.model.conf.valueset;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.conf.IRIExpander;
import org.protege.editor.owl.ui.renderer.RenderingEscapeUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
@JsonTypeName("Individuals")
public class IndividualsValueSet implements ValueSet {

    @Nonnull
    private final String classIri;

    @JsonCreator
    public IndividualsValueSet(@Nonnull @JsonProperty("classIri") String classIri) {
        this.classIri = checkNotNull(classIri);
    }

    @Nonnull
    public Optional<IRI> getClassIri() {
        return IRIExpander.expand(classIri);
    }

    @Nonnull
    @Override
    public List<LabelledValue> getLabelledValues(@Nonnull OWLModelManager modelManager) {
        return getClassIri().map(iri -> {
            OWLClass cls = modelManager.getOWLDataFactory().getOWLClass(iri);
            return modelManager.getActiveOntologies().stream()
                               .flatMap(o -> o.getClassAssertionAxioms(cls).stream())
                               .map(OWLClassAssertionAxiom::getIndividual)
                               .filter(OWLIndividual::isNamed)
                               .filter(i -> !modelManager.isDeprecated(i))
                               .map(ind -> new LabelledValue(ind, RenderingEscapeUtils.unescape(modelManager.getRendering(ind))))
                               .sorted((o1, o2) -> o1.getLabel().compareToIgnoreCase(o2.getLabel()))
                               .collect(toList());
        }).orElse(Collections.emptyList());
    }
}
