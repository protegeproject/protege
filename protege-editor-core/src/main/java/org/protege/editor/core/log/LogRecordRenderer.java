package org.protege.editor.core.log;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogRecordRenderer implements ListCellRenderer<LogRecord> {

    private final DefaultListCellRenderer defaultRenderer;

    private final static Border border = BorderFactory.createEmptyBorder(2, 0, 2, 0);

    public LogRecordRenderer() {
        defaultRenderer = new DefaultListCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends LogRecord> list, LogRecord record, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(list, record, index, isSelected, cellHasFocus);
        String formattedText = getFormattedText(record);
        label.setText(formattedText);
        if(!isSelected) {
            if(record.getFormattedMessage().startsWith("---") && record.getFormattedMessage().endsWith("---")) {
                label.setForeground(Color.GRAY);
            }
            else if(record.getLogLevel() == LogLevel.ERROR) {
                label.setForeground(new Color(220, 0, 0));
            }
            else if(record.getLogLevel() == LogLevel.WARN) {
                label.setForeground(new Color(255, 135, 0));
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
        if(!record.getThrowableInfo().isPresent()) {
           return firstLine;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><span style=\"font-weight: bold;\">");
        sb.append(firstLine);
        sb.append("</span><br>\n");
        ThrowableInfo throwableInfo = record.getThrowableInfo().get();
        sb.append("                        ");
        sb.append(throwableInfo.getClassName());
        sb.append("<br>\n");
        for(StackTraceElement element : throwableInfo.getStackTrace()) {
            sb.append("                            ");
            sb.append(element);
            sb.append("<br>\n");
        }
        sb.append("</body></html>");
        return sb.toString().replace(" ", "&nbsp;");

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
