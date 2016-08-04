package org.protege.editor.core.log;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogRecordRenderer implements ListCellRenderer<LogRecordElement> {

    private static final Color ERROR_COLOR = new Color(220, 0, 0);

    private static final Color WARNING_COLOR = new Color(255, 135, 0);

    private final DefaultListCellRenderer defaultRenderer;

    private final static Border border = BorderFactory.createEmptyBorder(2, 0, 2, 0);

    public LogRecordRenderer() {
        defaultRenderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends LogRecordElement> list, LogRecordElement element, int index, boolean isSelected, boolean cellHasFocus) {
        LogRecord record = element.getLogRecord();
        JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, record, index, isSelected, cellHasFocus);
        String formattedText = getFormattedText(record);
        label.setText(formattedText);
        if(!isSelected) {
            if(record.getFormattedMessage().startsWith("---") && record.getFormattedMessage().endsWith("---")) {
                label.setForeground(Color.GRAY);
            }
            else if(record.getLogLevel() == LogLevel.ERROR) {
                label.setForeground(ERROR_COLOR);
            }
            else if(record.getLogLevel() == LogLevel.WARN) {
                label.setForeground(WARNING_COLOR);
            }
            else if(record.getLogLevel() == LogLevel.DEBUG) {
                label.setForeground(Color.LIGHT_GRAY);
            }
        }
        label.setBorder(border);
        return label;
    }

    private String getFormattedText(LogRecord record) {
        String firstLine = String.format(
                "%7s  %s  %s",
                record.getLogLevel(),
                getTimestampRendering(record.getTimestamp()),
                record.getFormattedMessage()
        );
        if(record.getThrowableInfo().isPresent()) {
           return firstLine + " (See log file for more details)";
        }
        else {
           return firstLine;
        }
    }

    private String getTimestampRendering(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.format("%02d:%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }
}
