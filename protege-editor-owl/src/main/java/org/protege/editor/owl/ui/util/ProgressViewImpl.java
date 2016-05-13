package org.protege.editor.owl.ui.util;

import org.protege.editor.core.ui.util.Icons;
import org.semanticweb.owlapi.model.IRI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 11 May 16
 */
public class ProgressViewImpl extends JPanel implements ProgressView {

    private final JLabel messageLabel = new JLabel();

    private final JLabel subMessageLabel = new JLabel();

    public ProgressViewImpl() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel holder = new JPanel(new GridBagLayout());
        add(holder, BorderLayout.NORTH);

        Insets insets = new Insets(0, 0, 0, 0);

        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(48, 48));
        iconLabel.setIcon(Icons.getIcon("logo.48x48.png"));
        holder.add(iconLabel, new GridBagConstraints(
                0, 1,
                1, 3,
                0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 10),
                0, 0));

        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
        FontMetrics fm = getFontMetrics(messageLabel.getFont());
        Dimension labelMinSize = new Dimension(500, fm.getHeight() + 2);
        messageLabel.setPreferredSize(labelMinSize);
        holder.add(messageLabel, new GridBagConstraints(
                1, 1,
                1, 1,
                100, 0,
                GridBagConstraints.SOUTH,
                GridBagConstraints.HORIZONTAL,
                insets,
                0, 0));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        holder.add(progressBar, new GridBagConstraints(
                1, 2,
                1, 1,
                100, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 2, 0),
                0, 0));

        subMessageLabel.setFont(subMessageLabel.getFont().deriveFont(10f));
        subMessageLabel.setMinimumSize(labelMinSize);
        subMessageLabel.setPreferredSize(labelMinSize);
        holder.add(subMessageLabel, new GridBagConstraints(
                1, 3,
                1, 1,
                100, 0,
                GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL,
                insets,
                0, 0));
    }

    @Override
    public void setMessage(String message) {
        messageLabel.setText(
                String.format(message)
        );
    }

    @Override
    public void setSubMessage(String subMessage) {
        subMessageLabel.setText(
                subMessage
        );
    }

    @Override
    public JComponent asJComponent() {
        return this;
    }

    @Override
    public void clearSubMessage() {
        subMessageLabel.setText("");
    }
}
