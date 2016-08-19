package org.protege.editor.core.log;


import org.protege.editor.core.FileUtils;

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
        view = new JPanel(new BorderLayout(7, 7));
        view.setPreferredSize(new Dimension(800, 600));
        JList<LogRecordElement> list = new JList<>(logRecordModel = new LogRecordModel());
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(15);
        view.add(sp);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearLogButton = new JButton("Clear log");
        clearLogButton.addActionListener(e -> clearView());
        JButton showLogFile = new JButton("Show log file");
        showLogFile.addActionListener(e -> FileUtils.showLogFile());
        buttonPanel.add(showLogFile);
        buttonPanel.add(clearLogButton);
        view.add(buttonPanel, BorderLayout.SOUTH);
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
