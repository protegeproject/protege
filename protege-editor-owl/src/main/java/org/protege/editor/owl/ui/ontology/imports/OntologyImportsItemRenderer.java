package org.protege.editor.owl.ui.ontology.imports;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.layout.*;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 12/06/2014
 */
public class OntologyImportsItemRenderer extends PageCellRenderer {

    private OWLEditorKit editorKit;

    public OntologyImportsItemRenderer(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    @Override
    protected void fillPage(Page page, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        render(list.getSelectionForeground(), list.getSelectionBackground(), page, value, isSelected);
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return c;
    }

    private void render(Color fg, Color bg, Page page, Object value, boolean sel) {
        if (!(value instanceof OntologyImportItem)) {
            page.addParagraph(value.toString());
        } else {
            Color foreground = sel ? fg : Color.DARK_GRAY;
            OntologyImportItem item = (OntologyImportItem) value;
            OWLOntologyManager man = editorKit.getOWLModelManager().getOWLOntologyManager();
            OWLOntology ont = man.getImportedOntology(item.getImportDeclaration());
            OntologyIRIShortFormProvider sfp = new OntologyIRIShortFormProvider();
            Color declIRIColor = sel ? fg : Color.BLACK;
            IRI iri = item.getImportDeclaration().getIRI();
            page.addParagraph(iri.toQuotedString(), declIRIColor);
            page.setMarginBottom(5);
            if (ont != null) {
                String shortForm = sfp.getShortForm(ont);
                Paragraph ontPara = page.addParagraph(shortForm, declIRIColor);
                ontPara.setMarginTop(5);
                ontPara.setMarginLeft(40);
                OWLOntologyID ontologyID = ont.getOntologyID();
                if (!ontologyID.isAnonymous()) {
                    Paragraph ontologyIriPara = page.addParagraph("Ontology IRI: ", Color.GRAY);
                    ontologyIriPara.setMarginLeft(40);
                    IRI ontologyIRI = ontologyID.getOntologyIRI();
                    ontologyIriPara.append(ontologyIRI.toQuotedString(), foreground);
                    ontologyIriPara.setMarginTop(2);
                    IRI versionIRI = ontologyID.getVersionIRI();
                    if (versionIRI != null) {
                        Paragraph versionIriPara = page.addParagraph("Version IRI: ", Color.GRAY);
                        versionIriPara.setMarginLeft(40);
                        versionIriPara.setMarginTop(2);
                        versionIriPara.append(versionIRI.toQuotedString(), foreground);
                    }
                }
                Paragraph locPara = page.addParagraph("Location: ", Color.GRAY);
                locPara.setMarginLeft(40);
                locPara.setMarginTop(2);
                IRI documentIRI = man.getOntologyDocumentIRI(ont);
                final int pos = locPara.getLength();
                if (documentIRI.getScheme().equalsIgnoreCase("file")) {
                    File file = new File(documentIRI.toURI());
                    locPara.append(file.getAbsolutePath(), foreground);
                    locPara.addLink(new LinkSpan(new FileLink(file), new Span(pos, locPara.getLength())));
                } else {
                    locPara.append(documentIRI.toString(), foreground);
                    locPara.addLink(new LinkSpan(new HTTPLink(documentIRI.toURI()), new Span(pos, locPara.getLength())));
                }
            }
        }
    }

    @Override
    protected int getMaxAvailablePageWidth(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Insets insets = list.getInsets();//OWLFrameList.ITEM_BORDER.getBorderInsets();
        int componentWidth = list.getWidth();
        JViewport vp = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, list);
        if (vp != null) {
            componentWidth = vp.getViewRect().width;
        }
        int width = componentWidth - list.getInsets().left - list.getInsets().right - insets.left + insets.right - 20;
        return width > 0 ? width : 500;
    }

    @Override
    protected void fillPage(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        render(table.getSelectionForeground(), table.getSelectionBackground(), page, value, isSelected);
    }

    @Override
    protected int getMaxAvailablePageWidth(Page page, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return table.getColumnModel().getColumn(column).getWidth() - 5;
    }
}
