package org.protege.editor.owl.model.user;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 */
public interface OrcidProvider {

    /**
     * Get the ORCID for the user.
     * @return The orcid.  Not null.
     */
    Optional<Orcid> getOrcid();
}
