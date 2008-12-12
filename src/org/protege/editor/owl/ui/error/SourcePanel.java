package org.protege.editor.owl.ui.error;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.UIUtil;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.net.URL;
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
 * Date: Dec 11, 2008<br><br>
 */
public class SourcePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(SourcePanel.class);

    private JEditorPane sourceConsole;

    public SourcePanel(final URL loc) {
        setLayout(new BorderLayout(7, 7));

        try {
            sourceConsole = new JEditorPane(loc);
            sourceConsole.setSelectionColor(Color.RED);
            final JButton saveButton = new JButton(new AbstractAction("Save as...") {
                public void actionPerformed(ActionEvent event) {
                    saveContent(loc);
                }
            });
            Box sourceHeader = new Box(BoxLayout.LINE_AXIS);
            sourceHeader.add(new JLabel("Source: "));
            final JLabel fileLabel = new JLabel(loc.toString());
            fileLabel.setFont(fileLabel.getFont().deriveFont(Font.BOLD));
            sourceHeader.add(fileLabel);
            sourceHeader.add(Box.createHorizontalGlue());
            sourceHeader.add(saveButton);
            add(sourceHeader, BorderLayout.NORTH);
            add(new JScrollPane(sourceConsole), BorderLayout.CENTER);
        }
        catch (Exception e) {
            logger.warn(e);
        }
    }

    public void highlight(final int line, final int col){
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                int caretPos = 0;
                final Document document = sourceConsole.getDocument();
                if (document instanceof PlainDocument){
                    PlainDocument doc = (PlainDocument) document;
                    caretPos = doc.getDefaultRootElement().getElement(line).getStartOffset() + col;
                }
                sourceConsole.setCaretPosition(caretPos);
                sourceConsole.setSelectionStart(caretPos);
                sourceConsole.setSelectionEnd(caretPos+1);
                sourceConsole.requestFocus();
            }
        });
    }

    private void saveContent(URL url) {
        File source = confirmFile(url);
        if (source != null){
            try {
                FileWriter fw = new FileWriter(source);
                BufferedWriter writer = new BufferedWriter(fw, 1024);
                PlainDocument doc = (PlainDocument)sourceConsole.getDocument();
                String content = doc.getText(0, doc.getLength()-1);
                writer.write(content);
                writer.flush();
                fw.flush();
                fw.close();
                logger.info("Saved source to: " + source);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    private File confirmFile(URL url) {

        JDialog f = (JDialog) SwingUtilities.getAncestorOfClass(Window.class, this);
        FileDialog fileDialog = new FileDialog(f, "Save source", FileDialog.SAVE);

        try {
            final File file = new File(url.toURI());
            fileDialog.setDirectory(file.getPath());
            fileDialog.setFile(file.getName());
        }
        catch (URISyntaxException e) {
            fileDialog.setDirectory(UIUtil.getCurrentFileDirectory());
            fileDialog.setFile(url.getFile());
        }

        fileDialog.setVisible(true);

        String fileName = fileDialog.getFile();
        if (fileName != null) {
            return new File(fileDialog.getDirectory() + fileName);
        }
        else {
            return null;
        }
    }
}
