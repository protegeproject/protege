package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.cache.OWLExpressionUserCache;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.util.List;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 12, 2008<br><br>
 *
 * Implements a lookahead search for expressions based on the current position of the caret.
 * Can be added to any editor
 * Implementation currently gets history from OWLExpressionUserCache, but could be overridden to get from anywhere
 *
 */
public class OWLExpressionHistoryCompleter {

    private boolean suggestingContent = false;

    private OWLEditorKit eKit;

    private JTextComponent tc;

    private String lastSuggestion = null;

    private int caretLocation = 0;


    public OWLExpressionHistoryCompleter(OWLEditorKit eKit, JTextComponent tc) {
        this.eKit = eKit;
        this.tc = tc;
        tc.getDocument().addDocumentListener(new DocumentListener(){

            public void insertUpdate(DocumentEvent event) {
                handleDocumentUpdated();
            }

            public void removeUpdate(DocumentEvent event) {
                // do nothing
            }

            public void changedUpdate(DocumentEvent event) {
                // do nothing
            }
        });
    }


    private void handleDocumentUpdated() {
        if (!suggestingContent){
            final Document doc = tc.getDocument();
            caretLocation = tc.getCaretPosition()+1;
            final int docLength = doc.getLength();
            if (docLength == caretLocation){
                try {
                    String currentText = doc.getText(0, doc.getLength());
                    lastSuggestion = suggestContent(currentText.substring(0, caretLocation));
                    if (lastSuggestion != null){
                        suggestingContent = true;
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run() {
                                tc.setText(lastSuggestion);
                                tc.setSelectionEnd(lastSuggestion.length());
                                tc.setSelectionStart(caretLocation);
                                suggestingContent = false;
                            }
                        });
                    }
                }
                catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }


    private String suggestContent(String currentContent) {
        currentContent = normalise(currentContent);
        String candidate = null;
        for (String ren : getDescriptionHistory()){
            if (candidate == null && normalise(ren).startsWith(currentContent)){
                candidate = ren;
            }
        }
        return candidate;
    }


    // effectively allows us to ignore whitespace and case
    private String normalise(String s){
        s = s.replaceAll("\n", " ").replaceAll("\t", " ");
        StringBuilder buf = new StringBuilder();
        // make all spaces single
        for (int i=0; i<s.length(); i++){
            if (s.charAt(i) != ' ' || (i > 0 && s.charAt(i-1) != ' ')){
                buf.append(s.charAt(i));
            }
        }
        return buf.toString().toLowerCase();
    }


    protected List<String> getDescriptionHistory() {
        return OWLExpressionUserCache.getInstance(eKit.getModelManager()).getRenderings();
    }
}
