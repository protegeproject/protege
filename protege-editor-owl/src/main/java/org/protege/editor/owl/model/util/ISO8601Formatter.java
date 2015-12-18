package org.protege.editor.owl.model.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class ISO8601Formatter implements DateFormatter {

    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @Override
    public String formatDate(Date date) {
        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat(ISO_8601_FORMAT);
        df.setTimeZone(tz);
        return df.format(new Date());
    }
}
