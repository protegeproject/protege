package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.ExplanationFrame;
import org.protege.editor.owl.ui.frame.ExplanationFrameSectionRow;
import org.semanticweb.owl.model.OWLAxiom;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
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
 * Bio-Health Informatics Group<br>
 * Date: 16-Jan-2008<br><br>
 */
public class ExplanationFrameList extends OWLFrameList2<OWLAxiom> {

    private static final String MOVE_UP = "MOVE_UP";

    private static final String MOVE_DOWN = "MOVE_DOWN";

    private static final String INCREASE_INDENT = "INCREASE_INDENT";

    private static final String DECREASE_INDENT = "DECREASE_INDENT";

    public ExplanationFrameList(OWLEditorKit editorKit) {
        super(editorKit, new ExplanationFrame(editorKit));
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), MOVE_UP);
        getActionMap().put(MOVE_UP, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedRowUp();
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), MOVE_DOWN);
        getActionMap().put(MOVE_DOWN, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveSelectedRowDown();
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), DECREASE_INDENT);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), DECREASE_INDENT);
        getActionMap().put(DECREASE_INDENT, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                decreaseSelectedRowIndent();
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), INCREASE_INDENT);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), INCREASE_INDENT);
        getActionMap().put(INCREASE_INDENT, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                increaseSelectedRowIndent();
            }
        });

    }

    public List<ExplanationFrameSectionRow> getSelectedFrameSectionRows() {
        List<ExplanationFrameSectionRow> rows = new ArrayList<ExplanationFrameSectionRow>();
        for(Object selValue : getSelectedValues()) {
            if(selValue instanceof ExplanationFrameSectionRow) {
                rows.add((ExplanationFrameSectionRow) selValue);
            }
        }
        return rows;
    }

    public void moveSelectedRowUp() {
        int [] selIndices = getSelectedIndices();
        for(int i  = 0; i < selIndices.length; i++) {
            ExplanationFrameSectionRow row = (ExplanationFrameSectionRow) getModel().getElementAt(selIndices[i]);
            if(row.getFrameSection().moveUp(row.getAxiom())) {
                selIndices[i] = selIndices[i] - 1;
            }
        }
        setSelectedIndices(selIndices);
    }

    public void moveSelectedRowDown() {
        int [] selIndices = getSelectedIndices();
        for(int i  = 0; i < selIndices.length; i++) {
            ExplanationFrameSectionRow row = (ExplanationFrameSectionRow) getModel().getElementAt(selIndices[i]);
            if(row.getFrameSection().moveDown(row.getAxiom())) {
                selIndices[i] = selIndices[i] + 1;
            }
        }
        setSelectedIndices(selIndices);
    }

    public void increaseSelectedRowIndent() {
        int [] selIndices = getSelectedIndices();
        List<ExplanationFrameSectionRow> rows = getSelectedFrameSectionRows();
        for (ExplanationFrameSectionRow row : rows) {
            row.getFrameSection().increaseIndent(row.getAxiom());
        }
        setSelectedIndices(selIndices);
    }

    public void decreaseSelectedRowIndent() {
        int [] selIndices = getSelectedIndices();  
        List<ExplanationFrameSectionRow> rows = getSelectedFrameSectionRows();
        for (ExplanationFrameSectionRow row : rows) {
            row.getFrameSection().decreaseIndent(row.getAxiom());
        }
        setSelectedIndices(selIndices);
    }


    public ExplanationFrame getFrame() {
        return (ExplanationFrame) super.getFrame();
    }
}
