package org.protege.editor.owl.ui.find;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.find.OWLEntityFinderPreferences;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EntityFinderField extends JTextField {
	private static final long serialVersionUID = -5383341925424297227L;

	private OWLEditorKit editorKit;

    private JList resultsList;

    private JWindow window;

    private JComponent parent;


    public EntityFinderField(JComponent parent, OWLEditorKit editorKit) {
        super(20);
        putClientProperty("JTextField.variant", "search");
        this.parent = parent;
        this.editorKit = editorKit;
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeResults();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectEntity();
                }
            }


            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    decrementListSelection();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    incrementListSelection();
                }
            }
        });
        getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }


            public void insertUpdate(DocumentEvent e) {
                performFind();
            }


            public void removeUpdate(DocumentEvent e) {
                performFind();
            }
        });
        // tooltip and fixed cell size code at svn revision 23344
        resultsList = new JList();
        resultsList.setCellRenderer(editorKit.getWorkspace().createOWLCellRenderer());
        resultsList.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 2 && resultsList.getSelectedValue() instanceof OWLEntity) {
                    selectEntity();
                }
            }
        });
    }


    private void selectEntity() {
        OWLEntity selEntity = (OWLEntity) resultsList.getSelectedValue();
        if (selEntity != null) {
            closeResults();
            EntityFinderField.this.editorKit.getWorkspace().getOWLSelectionModel().setSelectedEntity(selEntity);
            editorKit.getWorkspace().displayOWLEntity(selEntity);
        }
    }


    private void incrementListSelection() {
        if (resultsList.getModel().getSize() > 0) {
            int selIndex = resultsList.getSelectedIndex();
            selIndex++;
            if (selIndex > resultsList.getModel().getSize() - 1) {
                selIndex = 0;
            }
            resultsList.setSelectedIndex(selIndex);
            resultsList.scrollRectToVisible(resultsList.getCellBounds(selIndex, selIndex));
        }
    }


    private void decrementListSelection() {
        if (resultsList.getModel().getSize() > 0) {
            int selIndex = resultsList.getSelectedIndex();
            selIndex--;
            if (selIndex < 0) {
                selIndex = resultsList.getModel().getSize() - 1;
            }
            resultsList.setSelectedIndex(selIndex);
            resultsList.scrollRectToVisible(resultsList.getCellBounds(selIndex, selIndex));
        }
    }


    private void closeResults() {
        getWindow().setVisible(false);
        resultsList.setListData(new Object []{});
    }


    private Timer timer = new Timer(400, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            executeFind();
            timer.stop();
        }
    });


    private void executeFind() {
        if (getText().trim().length() > 0) {
            Set<OWLEntity> results = editorKit.getModelManager().getOWLEntityFinder().getMatchingOWLEntities(getText());
            showResults(results);
        }
        else {
            closeResults();
        }
    }


    private void performFind() {
        timer.setDelay((int) OWLEntityFinderPreferences.getInstance().getSearchDelay());
        timer.restart();
    }


    private JWindow getWindow() {
        if (window == null) {
            Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
            window = new JWindow(w);
            window.setFocusableWindowState(false);
            JScrollPane sp = ComponentFactory.createScrollPane(resultsList);
            sp.setBorder(null);
            window.setContentPane(sp);
            addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    window.setVisible(false);
                    resultsList.setListData(new Object []{});
                }
            });
            SwingUtilities.getRoot(this).addComponentListener(new ComponentAdapter() {
                public void componentMoved(ComponentEvent e) {
                    closeResults();
                }
            });
        }
        return window;
    }


    private void showResults(Set<OWLEntity> results) {
        JWindow window = getWindow();
        if (results.size() > 0) {
            Point pt = new Point(0, 0);
            SwingUtilities.convertPointToScreen(pt, this);
            window.setLocation(pt.x, pt.y + getHeight() + 2);
            window.setSize(getWidth(), 400);
            resultsList.setListData(getSortedResults(results));
            window.setVisible(true);
            window.validate();
            resultsList.setSelectedIndex(0);
        }
        else {
            resultsList.setListData(new Object [0]);
        }
    }
    
    /*
     * This is an opportunity to add some code to deal with large result sets.  The problem
     * is that if you give a big set of results to a JList then it will will take a very long time 
     * (35 seconds on my work desktop to display 29000 entities beginning with an 'a').  A sort already 
     * involves calculating the browser text for each item (and this is not the bottleneck).  So a potential
     * solution is to use the setProtypicalItem method of the jlist to set the width and height to the max.
     * But for some reason this only worked the first time when I tried it and the results would probably not be
     * optimal.
     * 
     * The More... could perhaps be instrumented to completely fill the list when clicked.
     */
    private Object[] getSortedResults(Set<OWLEntity> results) {
        TreeSet<OWLEntity> ts = new TreeSet<OWLEntity>(editorKit.getModelManager().getOWLObjectComparator());
        ts.addAll(results);
        int maxSize = 150;
        boolean tooMany = ts.size() > maxSize;
        Object[] arrayResults = new Object[tooMany ? maxSize : ts.size()];
        int i = 0;
        for (OWLEntity e : ts) {
        	if (tooMany && i >= arrayResults.length - 1) {
        		break;
        	}
        	arrayResults[i++] = e;
        }
        if (tooMany) {
        	arrayResults[maxSize - 1] = "More...";
        }
        return arrayResults;
    }
}
