package org.protege.editor.owl.ui.util;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-05-08
 */
public interface SuggestionToString<T> {

    @Nonnull
    String toString(@Nonnull T suggestion);
}
