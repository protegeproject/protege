package org.protege.editor.core.ui.preferences;

import org.protege.editor.core.ModelManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePluginInstance;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class PreferencesPanel extends JPanel implements ProtegePluginInstance {

    /**
     * 
     */
    private static final long serialVersionUID = -8206871260849152828L;

    private String label;

    private EditorKit editorKit;


    public void setup(String label, EditorKit editorKit) {
        this.label = label;
        this.editorKit = editorKit;
        setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        setLayout(new PreferencesPanelLayoutManager(this));
    }


    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
        if (comp instanceof PreferencesPanel) {
            if (constraints instanceof String) {
                ((PreferencesPanel) comp).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
                        (String) constraints), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        }
    }


    protected void addVerticalStrut(final int size) {
        add(new JPanel() {
            /**
             * 
             */
            private static final long serialVersionUID = -1660234580003844634L;

            public Dimension getPreferredSize() {
                return new Dimension(size, size);
            }
        });
    }


    public String getLabel() {
        return label;
    }


    public EditorKit getEditorKit() {
        return editorKit;
    }


    public ModelManager getModelManager() {
        return getEditorKit().getModelManager();
    }


    public abstract void applyChanges();
}
