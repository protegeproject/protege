package org.protege.editor.owl.ui.util;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-05-08
 */
public interface SuggestOracle<T> {

    Stream<T> getMatches(@Nonnull String query);
}
