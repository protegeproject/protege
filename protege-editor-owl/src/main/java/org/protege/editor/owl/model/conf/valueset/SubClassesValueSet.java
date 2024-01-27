package org.protege.editor.owl.model.conf.valueset;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.protege.editor.owl.ui.renderer.RenderingEscapeUtils.unescape;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.conf.IRIExpander;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
@JsonTypeName("SubClasses")
public class SubClassesValueSet implements ValueSet {

    private final String classIri;

    public SubClassesValueSet(@Nonnull @JsonProperty("classIri") String classIri) {
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
            OWLObjectHierarchyProvider<OWLClass> hierarchyProvider = modelManager.getOWLHierarchyManager()
                                                                                 .getOWLClassHierarchyProvider();
            OWLClass cls = modelManager.getOWLDataFactory().getOWLClass(iri);
            return hierarchyProvider.getChildren(cls).stream()
                                    // Exclude deprecated entities
                                    .filter(c -> !modelManager.isDeprecated(c))
                                    .map(child -> new LabelledValue(cls, unescape(modelManager.getRendering(child))))
                                    .collect(toList());
        }).orElse(Collections.emptyList());
    }
}
