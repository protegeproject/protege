package org.protege.editor.owl.ui.view;

import java.awt.datatransfer.Clipboard;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewClipboard {

    private static ViewClipboard instance;

    private Clipboard clipboard;


    private ViewClipboard() {
        this.clipboard = new Clipboard("View");
    }


    public Clipboard getClipboard() {
        return clipboard;
    }


    public static synchronized ViewClipboard getInstance() {
        if (instance == null) {
            instance = new ViewClipboard();
        }
        return instance;
    }
}
