package org.protege.editor.owl.ui.prefix;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.of;
import static org.semanticweb.owlapi.vocab.Namespaces.OWL;
import static org.semanticweb.owlapi.vocab.Namespaces.RDF;
import static org.semanticweb.owlapi.vocab.Namespaces.RDFS;
import static org.semanticweb.owlapi.vocab.Namespaces.XML;
import static org.semanticweb.owlapi.vocab.Namespaces.XSD;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.protege.editor.core.ui.list.MListItem;
import org.semanticweb.owlapi.vocab.Namespaces;

import com.google.common.collect.ImmutableSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixesListSectionRow implements MListItem, PrefixListElement {

    @Nonnull
    private PrefixMapping value;

    @Nonnull
    private Consumer<PrefixMapping> deleteHandler = pm -> {};

    @Nonnull
    private Consumer<PrefixMapping> editHandler = pm -> {};

    private static ImmutableSet<PrefixMapping> immutablePrefixMappings;

    static {
        immutablePrefixMappings = of(prefixMapping(OWL),
                                     prefixMapping(RDFS),
                                     prefixMapping(RDF),
                                     prefixMapping(XML),
                                     prefixMapping(XSD)
        );
    }

    public void setDeleteHandler(@Nonnull Consumer<PrefixMapping> deleteHandler) {
        this.deleteHandler = checkNotNull(deleteHandler);
    }

    public void setEditHandler(@Nonnull Consumer<PrefixMapping> editHandler) {
        this.editHandler = checkNotNull(editHandler);
    }

    private static PrefixMapping prefixMapping(Namespaces owl) {
        return PrefixMapping.get(owl.getPrefixName() + ":", owl.getPrefixIRI());
    }

    @Nonnull
    public PrefixMapping getValue() {
        return value;
    }

    public PrefixesListSectionRow(@Nonnull PrefixMapping value) {
        this.value = checkNotNull(value);
    }

    @Override
    public boolean isEditable() {
        return !immutablePrefixMappings.contains(value);
    }

    @Override
    public void handleEdit() {
        editHandler.accept(value);
    }

    @Override
    public boolean isDeleteable() {
        return !immutablePrefixMappings.contains(value);
    }

    @Override
    public boolean handleDelete() {
        deleteHandler.accept(value);
        return true;
    }

    @Override
    public String getTooltip() {
        return String.format("Prefix name %s maps to prefix %s", value.getPrefixName(), value.getPrefix());
    }
}
