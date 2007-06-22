package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowFileAction extends AbstractAction {

    private static final Logger logger = Logger.getLogger(ShowFileAction.class);

    private URI physicalURI;


    public ShowFileAction(URI physicalURI) {
        super("Show file...");
        this.physicalURI = physicalURI;
        if (!physicalURI.getScheme().equals("file")) {
            throw new IllegalArgumentException("URI must be a file URI!");
        }
    }


    public void actionPerformed(ActionEvent e) {
        try {
            FileUtils.showFile(new File(physicalURI));
        }
        catch (IOException ex) {
            logger.error(ex);
        }
    }
}
