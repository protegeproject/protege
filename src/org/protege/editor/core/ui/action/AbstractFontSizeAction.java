package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractFontSizeAction extends ProtegeAction {

    public void actionPerformed(ActionEvent e) {
//        Object[] objs = UIManager.getLookAndFeel().getDefaults().keySet().toArray();
//        for (Object obj : objs) {
//            if (obj.toString().toLowerCase().indexOf(".font") != -1) {
//                Font font = UIManager.getFont(obj);
//                UIManager.put(obj, new FontUIResource(font.deriveFont((float) font.getSize() + getDelta())));
//            }
//        }
        getWorkspace().changeFontSize(getDelta());

//        SwingUtilities.updateComponentTreeUI(ProtegeManager.getInstance().getFrame(getWorkspace()));
    }


    public void initialise() throws Exception {

    }


    public void dispose() {
    }


    protected abstract int getDelta();
}
