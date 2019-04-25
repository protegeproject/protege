package org.protege.editor.owl.model.idrange;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public interface NoRangeForUserNameHandler {

    void handleNoRangeForUserName(@Nonnull String userName,
                                  @Nonnull IdRangesPolicy policy);
}
