package org.protege.editor.core.log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogRecordModel extends AbstractListModel<LogRecord> {

    private final List<LogRecord> logRecordList = new ArrayList<>();

    public void clear() {
        logRecordList.clear();
    }

    @Override
    public int getSize() {
        return logRecordList.size();
    }

    @Override
    public LogRecord getElementAt(int index) {
        return logRecordList.get(index);
    }

    public void append(LogRecord record) {
        logRecordList.add(record);
        int lastIndex = logRecordList.size() - 1;
        fireIntervalAdded(this, lastIndex, lastIndex);
    }
}
