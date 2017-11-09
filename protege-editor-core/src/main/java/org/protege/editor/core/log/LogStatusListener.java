package org.protege.editor.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public interface LogStatusListener {

    void eventLogged(ILoggingEvent event);

    void statusCleared();
}
