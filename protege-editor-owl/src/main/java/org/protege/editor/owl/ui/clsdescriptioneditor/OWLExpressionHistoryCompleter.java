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
*
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
                        SwingUtilities.invokeLater(() -> {
                            tc.setText(lastSuggestion);
                            tc.setSelectionEnd(lastSuggestion.length());
                            tc.setSelectionStart(caretLocation);
                            suggestingContent = false;
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
