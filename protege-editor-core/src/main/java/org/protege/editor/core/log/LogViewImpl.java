package org.protege.editor.core.log;


import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ui.action.TimestampOutputAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogViewImpl implements LogView {

    private final JComponent view;

    private final LogRecordModel logRecordModel;
    
    private final JList<LogRecordElement> list;

    public LogViewImpl() {
        view = new JPanel(new BorderLayout(7, 7));
        view.setPreferredSize(new Dimension(800, 600));
        list = new JList<>(logRecordModel = new LogRecordModel());
        JScrollPane sp = new JScrollPane(list);
        sp.getVerticalScrollBar().setUnitIncrement(15);
        view.add(sp);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearLogButton = new JButton("Clear log");
        clearLogButton.setToolTipText("Remove all log messages");
        clearLogButton.addActionListener(e -> clearView());
        JButton showLogFile = new JButton("Show log file");
        showLogFile.setToolTipText("Show the log file in the system file browser");
        showLogFile.addActionListener(e -> FileUtils.showLogFile());
        JButton timeStampButton = new JButton("Time stamp");
		timeStampButton.addActionListener(e -> TimestampOutputAction.createTimeStamp(view));
		timeStampButton.setToolTipText("Print a timestamp and optional message into the logs or console");
        buttonPanel.add(showLogFile);
        buttonPanel.add(timeStampButton);
        buttonPanel.add(clearLogButton);
        view.add(buttonPanel, BorderLayout.SOUTH);
        list.setFont(new Font("monospaced", Font.PLAIN, 12));
        list.setCellRenderer(new LogRecordRenderer());
        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if( index > -1 ) {
                    list.setToolTipText(logRecordModel.getElementAt(index).getTooltip());
                }
            }
        });
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
		SwingUtilities.invokeLater(() -> {
			int lastVisible = list.getLastVisibleIndex();
			boolean autoScroll = (lastVisible == logRecordModel.getSize() - 1);
			logRecordModel.append(logRecord);
			if (autoScroll) {
				list.ensureIndexIsVisible(logRecordModel.getSize() - 1);
			}
		});
    }

}
