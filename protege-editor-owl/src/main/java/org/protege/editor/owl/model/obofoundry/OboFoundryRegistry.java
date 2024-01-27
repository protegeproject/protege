package org.protege.editor.owl.model.obofoundry;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
@AutoValue
public abstract class OboFoundryRegistry {

    @JsonCreator
    @Nonnull
    public static OboFoundryRegistry get(@Nonnull @JsonProperty(
            "ontologies") ImmutableList<OboFoundryEntry> ontologies) {
        ImmutableMap.Builder<String, OboFoundryEntry> builder = ImmutableMap.builder();
        ontologies.forEach(o -> builder.put(o.getId(), o));
        ImmutableMap<String, OboFoundryEntry> byId = builder.build();
        return new AutoValue_OboFoundryRegistry(ontologies, byId);
    }

    public static OboFoundryRegistry empty() {
        return get(ImmutableList.of());
    }

    @JsonProperty("ontologies")
    public abstract ImmutableList<OboFoundryEntry> getOntologies();

    @Nonnull
    public abstract ImmutableMap<String, OboFoundryEntry> getEntriesById();

    @JsonIgnore
    public Optional<OboFoundryEntry> getOntology(@Nonnull String id) {
        return Optional.ofNullable(getEntriesById().get(id));
    }
}
