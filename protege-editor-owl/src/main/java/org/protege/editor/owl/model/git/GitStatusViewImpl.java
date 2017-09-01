package org.protege.editor.owl.model.git;

import org.protege.editor.core.Fonts;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 2017
 */
public class GitStatusViewImpl implements GitStatusView {

    private final JLabel label = new JLabel();

    private String branchName = "";

    private boolean modified = false;

    public GitStatusViewImpl() {
        label.setFont(Fonts.getSmallDialogFont());
    }

    @Override
    public void clear() {
        branchName = "";
        modified = false;
        label.setText("");
    }

    @Override
    public void setBranch(@Nonnull String branchName) {
        if(!this.branchName.equals(branchName)) {
            this.branchName = branchName;
            rebuildView();
        }
    }

    @Override
    public void setModified(boolean modified) {
        if(this.modified != modified) {
            this.modified = modified;
            rebuildView();
        }
    }

    @Override
    public JComponent asJComponent() {
        return label;
    }

    private void rebuildView() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>Git: <span style=\"font-weight: bold;\">");
        sb.append(branchName);
        sb.append("</span>");
        if(modified) {
            sb.append(" (ontologies modified)");
        }
        sb.append("</body></html>");
        label.setText(sb.toString());
    }
}
