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
        renderThrowableInfo(sb, throwableInfo, 0);
        sb.append("</body></html>");
        return sb.toString().replace(" ", "&nbsp;");

    }

    private static void renderThrowableInfo(StringBuilder sb, ThrowableInfo throwableInfo, int depth) {
        int padding = depth + 1;
        pad(sb, padding);
        sb.append(throwableInfo.getClassName());
        sb.append("<br>\n");
        for(StackTraceElement element : throwableInfo.getStackTrace()) {
            pad(sb, padding);
            sb.append(element);
            sb.append("<br>\n");
        }
        Optional<ThrowableInfo> cause = throwableInfo.getCause();
        if(cause.isPresent()) {
            pad(sb, padding);
            sb.append("Caused by:<br>\n");
            renderThrowableInfo(sb, cause.get(), depth + 1);
        }
    }

    private static void pad(StringBuilder sb, int padding) {
        sb.append("                ");
        for(int i = 0; i < padding; i++) {
            sb.append("        ");
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
