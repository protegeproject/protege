package org.protege.osgi.framework;

import org.apache.felix.framework.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/11/15
 */
public class FrameworkLogger extends Logger {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(FrameworkLogger.class);

    @Override
    protected void doLog(Bundle bundle, ServiceReference sr, int level, String msg, Throwable throwable) {
        if(level == LOG_ERROR) {
            logger.error(msg, throwable);
        }
        else if (level == LOG_WARNING) {
            logger.warn(msg, throwable);
        }
        else if(level == LOG_INFO) {
            logger.info(msg, throwable);
        }
        else if(level == LOG_DEBUG) {
            logger.debug(msg, throwable);
        }
    }
}
