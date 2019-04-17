package org.protege.editor.owl.model.identifiers;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
public class IdentifiersDotOrgManager {

    private static IdentifiersDotOrg instance;

    @Nonnull
    public static synchronized IdentifiersDotOrg get() {
        if(instance == null) {
            instance = IdentifiersDotOrg.create();
        }
        return instance;
    }
}
