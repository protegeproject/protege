package org.protege.editor.owl.model.idrange;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class NoRangeFoundForUserNamePanel extends JPanel {

    private static final String MSG_TEMPLATE = "<html><body>Protégé has detected and loaded an Id range policy.  However, no Id range has been found for the current user (<b>%s</b>).  Please select the Id range that you wish to use.</body></html>";

    private final JLabel messageLabel = new JLabel();

    private final JList<UserIdRange> userNameList = new JList<>();

    public NoRangeFoundForUserNamePanel() {
        setLayout(new BorderLayout(7, 7));
        add(messageLabel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(userNameList);
        userNameList.setCellRenderer(new UserIdRangeListCellRenderer());
        add(sp, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 300));
    }

    public void setUserName(@Nonnull String userName) {
        String msg = String.format(MSG_TEMPLATE, userName, userName);
        messageLabel.setText(msg);
    }

    public void setIdRangesPolicy(@Nonnull IdRangesPolicy policy) {
        UserIdRange [] userNames = policy.getUserIdRanges().stream()
                .sorted(Comparator.comparing(UserIdRange::getUserId).thenComparing(rng -> rng.getIdRange().getLowerBound()))
                .toArray(UserIdRange[]::new);
        userNameList.setListData(userNames);
    }

    public Optional<UserIdRange> getSelectedRange() {
        return Optional.ofNullable(userNameList.getSelectedValue());
    }
}
