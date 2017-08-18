package org.protege.editor.owl.model.git;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 2017
 */
public interface GitStatusView {

    void clear();

    void setBranch(@Nonnull String branchName);

    void setModified(boolean modified);

    JComponent asJComponent();

}
