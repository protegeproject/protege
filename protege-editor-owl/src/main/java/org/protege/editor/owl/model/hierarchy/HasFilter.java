package org.protege.editor.owl.model.hierarchy;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/11/15
 */
public interface HasFilter<E> {

    void setFilter(Predicate<E> filter);

    void clearFilter();

    /**
     * Gets the filter.
     * @return The filter.  Not null.
     */
    Predicate<E> getFilter();
}
