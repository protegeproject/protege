package org.protege.editor.owl.ui.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionAutoCompleter;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationVisitor;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLConstantAnnotation;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectAnnotation;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLTypedConstant;
import org.semanticweb.owl.model.OWLUntypedConstant;
import org.semanticweb.owl.vocab.DublinCoreVocabulary;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import uk.ac.manchester.cs.owl.OWLDataFactoryImpl;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 10-Feb-2007<br><br>
 */
public class OWLAnnotationEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLAnnotation> implements OWLAnnotationVisitor {

    private static final Logger logger = Logger.getLogger(OWLAnnotationEditor.class);

    private MList uriList;


    private JTextArea annotationContent;

    private JComboBox langComboBox;

    private JComboBox datatypeComboBox;

    private OWLEditorKit owlEditorKit;

    private JPanel editingPanel;


    private URI uriBeingAdded;

    private OWLDescriptionAutoCompleter autoCompleter;

    private OWLDataFactory dataFactory = new OWLDataFactoryImpl();

    private static final String OTHER = "Other...";


    public OWLAnnotationEditor(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;

        annotationContent = new JTextArea(8, 40);
        annotationContent.setWrapStyleWord(true);
        annotationContent.setLineWrap(true);
        autoCompleter = new OWLDescriptionAutoCompleter(owlEditorKit, annotationContent, new OWLExpressionChecker() {
            public void check(String text) throws OWLExpressionParserException, OWLException {
                throw new OWLExpressionParserException(text,
                                                       0,
                                                       text.length(),
                                                       true,
                                                       true,
                                                       true,
                                                       true,
                                                       true,
                                                       new HashSet<String>());
            }


            public Object createObject(String text) throws OWLExpressionParserException, OWLException {
                return null;
            }
        });

        langComboBox = new JComboBox();
        datatypeComboBox = new JComboBox();
        editingPanel = new JPanel(new GridBagLayout()) {
            public boolean requestFocusInWindow() {
                return annotationContent.requestFocusInWindow();
            }
        };
        editingPanel.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
        uriList = new MList() {
            protected void handleAdd() {
                handleAddURI();
            }
        };
        uriList.setCellRenderer(new OWLCellRenderer(owlEditorKit) {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                setTransparent();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        rebuildAnnotationURIList();
        fillLangComboBox();
        fillDatatypeComboBox();
        createLayout();
        uriList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    public void clear() {
        setAnnotation(null);
    }


    private void handleAddURI() {
        try {
            String uriString = JOptionPane.showInputDialog(editingPanel,
                                                           "Please specify an annotation URI",
                                                           "Annotation URI",
                                                           JOptionPane.PLAIN_MESSAGE);
            if (uriString != null) {
                URI uri = new URI(uriString);
                if (!uri.isAbsolute()) {
                    uri = URI.create(owlEditorKit.getModelManager().getActiveOntology().getURI() + "#" + uri.toString());
                }
                uriBeingAdded = uri;
            }
            rebuildAnnotationURIList();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private void fillLangComboBox() {
        langComboBox.setModel(new DefaultComboBoxModel(new String []{null, "en", "de", "es", "fr", "pt"}));
    }


    private void fillDatatypeComboBox() {
        List<OWLDataType> datatypeList = new ArrayList<OWLDataType>();
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            datatypeList.add(dataFactory.getOWLDataType(uri));
        }

        Collections.sort(datatypeList, new Comparator<OWLDataType>() {
            public int compare(OWLDataType o1, OWLDataType o2) {
                return o1.getURI().compareTo(o2.getURI());
            }
        });
        datatypeList.add(0, null);
        datatypeComboBox.setModel(new DefaultComboBoxModel(datatypeList.toArray()));
    }


    private void rebuildAnnotationURIList() {
        // Custom
        // Built in
        // Dublin core

        List list = new ArrayList();

        List<URIItem> custom = new ArrayList<URIItem>();
        Set<URI> customURIs = new HashSet<URI>();
        for (OWLOntology ont : owlEditorKit.getOWLModelManager().getOntologies()) {
            customURIs.addAll(ont.getAnnotationURIs());
        }
        if (uriBeingAdded != null) {
            customURIs.add(uriBeingAdded);
        }
        customURIs.removeAll(OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES);
        customURIs.removeAll(DublinCoreVocabulary.ALL_URIS);
        for (URI uri : customURIs) {
            custom.add(new URIItem(uri));
        }
        list.add(new MListSectionHeader() {
            public String getName() {
                return "Custom annotation URIs";
            }


            public boolean canAdd() {
                return true;
            }
        });


        Collections.sort(custom);
        list.addAll(custom);

        list.add(new MListSectionHeader() {
            public String getName() {
                return "Built in annotation URIs";
            }


            public boolean canAdd() {
                return false;
            }
        });

        List<URIItem> builtIn = new ArrayList<URIItem>();
        for (URI uri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTIES) {
            builtIn.add(new URIItem(uri));
        }
        Collections.sort(builtIn);
        list.addAll(builtIn);


        list.add(new MListSectionHeader() {
            public String getName() {
                return "Dublin Core annotation URIs";
            }


            public boolean canAdd() {
                return false;
            }
        });

        List<URIItem> dublinCore = new ArrayList<URIItem>();
        for (URI uri : DublinCoreVocabulary.ALL_URIS) {
            dublinCore.add(new URIItem(uri));
        }
        Collections.sort(dublinCore);
        list.addAll(dublinCore);


        uriList.setListData(list.toArray());
        if (uriBeingAdded != null) {
            setSelectedURI(uriBeingAdded);
        }
        else {
            setSelectedURI(OWLRDFVocabulary.RDFS_COMMENT.getURI());
        }
    }


    private void createLayout() {

        editingPanel.add(new JScrollPane(annotationContent),
                         new GridBagConstraints(1,
                                                1,
                                                5,
                                                1,
                                                100.0,
                                                100.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.BOTH,
                                                new Insets(7, 7, 7, 7),
                                                0,
                                                0));

        editingPanel.add(new JLabel("Annotation URI"),
                         new GridBagConstraints(0,
                                                0,
                                                1,
                                                1,
                                                0.0,
                                                0.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE,
                                                new Insets(7, 7, 0, 7),
                                                0,
                                                0));


        editingPanel.add(new JLabel("Annotation value"),
                         new GridBagConstraints(1,
                                                0,
                                                5,
                                                1,
                                                0.0,
                                                0.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.NONE,
                                                new Insets(7, 7, 0, 7),
                                                0,
                                                0));


        JPanel listHolder = new JPanel(new BorderLayout());
        listHolder.add(new JScrollPane(uriList));
        listHolder.setPreferredSize(new Dimension(200, 300));
        editingPanel.add(listHolder,
                         new GridBagConstraints(0,
                                                1,
                                                1,
                                                2,
                                                100.0,
                                                100.0,
                                                GridBagConstraints.NORTHWEST,
                                                GridBagConstraints.BOTH,
                                                new Insets(7, 7, 7, 7),
                                                0,
                                                0));


        editingPanel.add(new JLabel("Type"),
                         new GridBagConstraints(1,
                                                3,
                                                1,
                                                1,
                                                0.0,
                                                0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 7, 0, 7),
                                                0,
                                                0));


        editingPanel.add(datatypeComboBox,
                         new GridBagConstraints(2,
                                                3,
                                                1,
                                                1,
                                                0.0,
                                                0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5),
                                                40,
                                                0));

        editingPanel.add(new JLabel("Lang"),
                         new GridBagConstraints(3,
                                                3,
                                                1,
                                                1,
                                                0.0,
                                                0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 20, 0, 0),
                                                0,
                                                0));


        editingPanel.add(langComboBox,
                         new GridBagConstraints(4,
                                                3,
                                                1,
                                                1,
                                                100.0,
                                                0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 5),
                                                40,
                                                0));
    }


    private void setSelectedURI(URI uri) {
        for (int i = 0; i < uriList.getModel().getSize(); i++) {
            Object o = uriList.getModel().getElementAt(i);
            if (o instanceof URIItem) {
                URIItem item = (URIItem) o;
                if (item.uri.equals(uri)) {
                    uriList.setSelectedIndex(i);
                    uriList.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }


    private URI getSelectedURI() {
        Object selVal = uriList.getSelectedValue();
        if (selVal instanceof URIItem) {
            return ((URIItem) selVal).uri;
        }
        return null;
    }


    private boolean isLangSelected() {
        return langComboBox.getSelectedItem() != null;
    }


    private boolean isDataTypeSelected() {
        return datatypeComboBox.getSelectedItem() != null;
    }


    private String getSelectedLang() {
        return (String) langComboBox.getSelectedItem();
    }


    /**
     * Gets the selected datatype
     * @return The selected datatype, or <code>null</code>
     *         if no datatype is selected.
     */
    private OWLDataType getSelectedDataType() {
        return (OWLDataType) datatypeComboBox.getSelectedItem();
    }


    public void setAnnotation(OWLAnnotation annotation) {
        rebuildAnnotationURIList();
        fillDatatypeComboBox();
        if (annotation != null) {
            setSelectedURI(annotation.getAnnotationURI());
            annotation.accept(this);
        }
        else {
            setSelectedURI(OWLRDFVocabulary.RDFS_COMMENT.getURI());
            annotationContent.setText("");
        }
    }


    public void visit(OWLObjectAnnotation annotation) {
    }


    public void visit(OWLConstantAnnotation annotation) {
        annotationContent.setText(annotation.getAnnotationValue().getLiteral());
        OWLConstant val = annotation.getAnnotationValue();
        if (val.isTyped()) {
            datatypeComboBox.setSelectedItem(((OWLTypedConstant) val).getDataType());
        }
        else {
            langComboBox.setSelectedItem(((OWLUntypedConstant) val).getLang());
        }
    }


    public OWLAnnotation getAnnotation() {
        URI uri = getSelectedURI();
        String value = annotationContent.getText();
        OWLConstant constant;
        if (isDataTypeSelected()) {
            constant = dataFactory.getOWLTypedConstant(value, getSelectedDataType());
        }
        else {
            if (isLangSelected()) {
                constant = dataFactory.getOWLUntypedConstant(value, getSelectedLang());
            }
            else {
                constant = dataFactory.getOWLUntypedConstant(value);
            }
        }
        return dataFactory.getOWLConstantAnnotation(uri, constant);
    }


    public JComponent getEditorComponent() {
        return editingPanel;
    }


    public JComponent getInlineEditorComponent() {
        return getEditorComponent();
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLAnnotation getEditedObject() {
        return getAnnotation();
    }


    public void dispose() {

    }


    private class URIItem implements MListItem, Comparable<URIItem> {

        private URI uri;


        public URIItem(URI uri) {
            this.uri = uri;
        }


        public String toString() {
            String ren = uri.getFragment();
            if (ren == null) {
                int sep = uri.toString().lastIndexOf("/");
                if (sep != -1) {
                    ren = uri.toString().substring(sep + 1, uri.toString().length());
                }
                else {
                    return uri.toString();
                }
            }
            return ren;
        }


        public boolean isEditable() {
            return false;
        }


        public void handleEdit() {
        }


        public boolean isDeleteable() {
            return false;
        }


        public boolean handleDelete() {
            return false;
        }


        public String getTooltip() {
            return uri.toString();
        }


        public int compareTo(URIItem o) {
            return this.toString().compareTo(o.toString());
        }
    }
}
