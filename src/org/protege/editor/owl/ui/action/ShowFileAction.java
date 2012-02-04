package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ui.util.UIUtil;


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
        if (!UIUtil.isLocalFile(physicalURI)) {
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
