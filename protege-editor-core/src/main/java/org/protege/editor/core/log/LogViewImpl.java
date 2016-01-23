package org.protege.editor.core.log;


import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogViewImpl implements LogView {

    private final JComponent view;

    private final LogRecordModel logRecordModel;

    public LogViewImpl() {
        view = new JPanel(new BorderLayout());
        view.setPreferredSize(new Dimension(800, 600));
        JList<LogRecordElement> list = new JList<>(logRecordModel = new LogRecordModel());
        view.add(new JScrollPane(list));

        list.setFont(new Font("monospaced", Font.PLAIN, 12));
        list.setCellRenderer(new LogRecordRenderer());
    }

    @Override
    public JComponent asJComponent() {
        return view;
    }

    @Override
    public void clearView() {
        logRecordModel.clear();
    }

    @Override
    public void append(LogRecord logRecord) {
        SwingUtilities.invokeLater(() -> logRecordModel.append(logRecord));
    }

}
