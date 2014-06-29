package org.protege.editor.owl.ui.view.ontology;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Mar-2007<br><br>
 */
public abstract class AbstractOntologyRenderingViewComponent extends AbstractActiveOntologyViewComponent {
    private static final long serialVersionUID = 496671619048384054L;
    private static final Logger logger = Logger.getLogger(AbstractOWLViewComponent.class);
    private JTextArea textArea;
    private Thread renderThread;


    protected void initialiseOntologyView() throws Exception {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        add(new JScrollPane(textArea));
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        /*
         *  Since the rendered text for an  ontology can use so much memory, , monitor the text area to for changes
         *  in visibility; if the rendered view is hidden, hide the text;  otherwise call setText to render the text
         *  in a separate thread and display it when necessary.
         *  If rendering was started, but has not completed, interrupt the rendering  thread.
         */
        HierarchyListener hell = new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    logger.trace("textArea.isShowing() = " + textArea.isShowing());
                    if (!textArea.isShowing()) {
                        synchronized (textArea) {
                            if (renderThread != null) {
                                renderThread.interrupt();
                                renderThread = null;
                            }
                            clearText();
                        }
                    } else {

                        try {
                            setText(getOWLModelManager().getActiveOntology());
                        } catch (Exception e1) {
                            logger.error("setting text ", e1);
                        }


                    }
                }
            }

        };

        textArea.addHierarchyListener(hell);
        setText(getOWLModelManager().getActiveOntology());

    }


    protected void disposeOntologyView() {

    }


    protected void updateView(OWLOntology activeOntology) throws Exception {
        logger.debug("update view called");
        //setText(activeOntology);
    }

    /**
     * Render the ontology into the text area. If an existing rendering process is active, interrupt it.
     * Text is rendered into a customized writer to avoid memory spikes from building a string in a StringWriter,
     * then creating  a second copy when getting the string, with a third copy being created when the content is added
     * to the text area's model.
     *
     * @param activeOntology
     * @throws Exception
     */
    private void setText(final OWLOntology activeOntology) throws Exception {
        synchronized (textArea) {
            if (renderThread != null) {
                renderThread.interrupt();
                renderThread = null;
            }
        }
        final Cursor oldCursor = getCursor();
        textArea.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        clearText();
        renderThread = new Thread() {
            public void run() {
                int estimatedTextSize = estimateRenderedTextLength(activeOntology);
                TextAreaWriter textAreaWriter = new TextAreaWriter(this, textArea, estimatedTextSize);
                Writer w = new BufferedWriter(textAreaWriter);
                try {
                    renderOntology(activeOntology, w);
                    w.close();
                } catch (InterruptedIOException ioe) {
                    // ignore
                } catch (Exception e) {
                    logger.error("error rendering ontology", e);
                } finally {
                    synchronized (textArea) {
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    textArea.setCursor(oldCursor);
                                    logger.debug("set text done");
                                }
                            });
                        } catch (InterruptedException | InvocationTargetException e) {
                            // ignore
                        }
                        renderThread = null;
                    }
                }
            }
        };
        //renderThread.setDaemon(true);
        renderThread.setPriority(Thread.MIN_PRIORITY);
        renderThread.setName(getClass().getName());
        renderThread.start();
        logger.debug("returning from set text");
    }

    /**
     * Estimate the size, in characters,  of the rendered ontology
     * @param activeOntology
     * @return
     */
    protected int estimateRenderedTextLength(OWLOntology activeOntology) {
        return activeOntology.getAxiomCount() * 80;
    }

    /**
     * Replace the existing document with a place-holder.
     */
    private void clearText() {
        Document newDoc = new PlainDocument();
        textArea.setDocument(newDoc);
        textArea.setText("Ontology Not Rendered");
    }

    protected abstract void renderOntology(OWLOntology ontology, Writer writer) throws Exception;

    /**
     * A subclass of GapContent that allows characters to be inserted without requiring a new String to be
     * constructed.
     */
    static class CharInsertableGapContent extends GapContent {
        public CharInsertableGapContent(int size) {
            super(size);
        }

        public CharInsertableGapContent() {
            super();
        }

        public void insertChars(int position, final char cbuf[], final int off, final int len) throws BadLocationException {
            if (position > length() || position < 0) {
                throw new BadLocationException("Invalid insert", length());
            }

            if (off == 0) {
                replace(position, 0, cbuf, len);
            } else {
                char tmp[] = new char[len];
                System.arraycopy(cbuf, off, tmp, 0, len);
                replace(position, 0, tmp, len);
            }
        }
    }

    /**
     * A subclass of PlainDocument that allows an already populated content object
     * to be passed in to the constructor.
     */
    static class CharInsertableDocument extends PlainDocument {
        /**
         * Constructs a plain text document.  A default root element is created,
         * and the tab size set to 8. Text already in c is processed as if it had been inserted - i.e.  elements are
         * created for each line.
         *
         * @param c the container for the content
         */
        CharInsertableDocument(CharInsertableGapContent c) {
            super(c);
            DefaultDocumentEvent event = new DefaultDocumentEvent(0, c.length(), DocumentEvent.EventType.INSERT);
            insertUpdate(event, null);
            event.end();
            fireInsertUpdate(event);
        }
    }

    /**
     * A class that allows the content of a text area to be initialized using the java.io.Writer API.
     * The class should be called from a dedicated thread;  if that thread is interrupted, the first call
     * to write will throw an exception; subsequent calls will return silently (this avoids an issue in the version 3
     * manx renderer).
     * The written text will not be displayed in the text area until the writer is closed;  instead, the text area
     * will be updated with counter indicating renderer progress.
     */
    static class TextAreaWriter extends Writer {
        JTextArea jtextArea;
        Document originalDocument;
        Thread thread;
        CharInsertableGapContent content;
        int nextMessageIncrement = 1000000;
        int nextMessageThreshold = nextMessageIncrement;

        TextAreaWriter(Thread thread, JTextArea jtextArea, int size) {
            this.thread = thread;
            this.jtextArea = jtextArea;
            originalDocument = jtextArea.getDocument();
            content = new CharInsertableGapContent(size);
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            if (thread == null) {
                return;
            }
            if (thread.isInterrupted()) {
                content = null;
                thread = null;
                throw new InterruptedIOException();
            }
            try {
                content.insertChars(content.length() - 1, cbuf, off, len);
                if (content.length() > nextMessageThreshold) {
                    nextMessageThreshold += nextMessageIncrement;
                    final String message = String.format("Rendering: %,9d chars", content.length());
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (jtextArea.getDocument() == originalDocument) {
                                jtextArea.setText(message);
                            }
                        }
                    });
                }
            } catch (BadLocationException e1) {
                thread.interrupt();
                throw new IOException("Bad location in TextAreaWriter::write", e1);
            }
        }


        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {
            logger.trace("close called");
            if (thread != null && !thread.isInterrupted()) {
                thread = null;
                logger.trace("building doc ");
                final PlainDocument doc = new CharInsertableDocument(content);
                logger.trace("built doc");
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            jtextArea.setDocument(doc);
                            logger.trace("set doc");
                        }
                    });
                } catch (InterruptedException | InvocationTargetException e) {
                    throw new IOException(e);
                }
            }
        }
    }

}
