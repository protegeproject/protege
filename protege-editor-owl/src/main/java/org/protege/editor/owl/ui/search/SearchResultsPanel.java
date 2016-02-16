package org.protege.editor.owl.ui.search;

import static com.google.common.base.Preconditions.checkNotNull;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEditorKitOntologyShortFormProvider;
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.protege.editor.owl.model.search.SearchResultSet;
import org.protege.editor.owl.model.util.OWLUtilities;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.context.OWLObjectRenderingContext;
import org.protege.editor.owl.ui.renderer.styledstring.FontWeightAttribute;
import org.protege.editor.owl.ui.renderer.styledstring.ForegroundAttribute;
import org.protege.editor.owl.ui.renderer.styledstring.OWLObjectStyledStringRenderer;
import org.protege.editor.owl.ui.renderer.styledstring.ProtegeStyles;
import org.protege.editor.owl.ui.renderer.styledstring.StrikeThroughAttribute;
import org.protege.editor.owl.ui.renderer.styledstring.Style;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.protege.editor.owl.ui.renderer.styledstring.StyledStringPanel;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.google.common.base.Optional;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/09/2012
 */
public class SearchResultsPanel extends JPanel {

    public static final int HEADER_SPACING = 10;

    public static final int CATEGORY_COLUMN_PREFERRED_WIDTH = 120;
    public static final int ENTITY_COLUMN_PREFERRED_WIDTH = 220;
    public static final int MATCH_COLUMN_PREFERRED_WIDTH = 640;

    private OWLEditorKit editorKit;

    private JTable resultsTable;

    private SearchResultSet searchResultSet;

    private final JScrollPane scrollPane;


    private SearchResultsTableModel model;

    private SearchResultClickedListener searchResultClickedListener = (searchResult, e) -> {
    };


    public SearchResultsPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        model = new SearchResultsTableModel(editorKit);
        setLayout(new BorderLayout());

        resultsTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        resultsTable.setIntercellSpacing(new Dimension(0, 0));
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.setRowMargin(0);

        scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(null);
        add(scrollPane);

        setupColumnRenderers();

        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        scrollPane.getViewport().addChangeListener(e -> {
            handleScrollpaneViewportChanged();
        });
    }

    public void setSearchResultClickedListener(SearchResultClickedListener l) {
        this.searchResultClickedListener = checkNotNull(l);
    }

    private void setupColumnRenderers() {
        TableColumnModel columnModel = resultsTable.getColumnModel();
        TableColumn categoryColumn = columnModel.getColumn(0);
        categoryColumn.setCellRenderer(new ResultsTableCellRendererWrapper(new CategoryRenderer()));
        categoryColumn.setPreferredWidth(CATEGORY_COLUMN_PREFERRED_WIDTH);
        TableColumn entityColumn = columnModel.getColumn(1);
        entityColumn.setCellRenderer(new ResultsTableCellRendererWrapper(new EntityRenderer()));
        entityColumn.setPreferredWidth(ENTITY_COLUMN_PREFERRED_WIDTH);
        TableColumn matchColumn = columnModel.getColumn(2);
        matchColumn.setCellRenderer(new ResultsTableCellRendererWrapper(new EntityFinderResultsRenderer(editorKit)));
        matchColumn.setPreferredWidth(MATCH_COLUMN_PREFERRED_WIDTH);
    }

    private void handleScrollpaneViewportChanged() {
        Rectangle tableVisibleRect = resultsTable.getVisibleRect();
        TableColumn categoryColumn = resultsTable.getColumnModel().getColumn(0);
        int columnWidth = categoryColumn.getWidth();
        int rowHeight = resultsTable.getRowHeight() * 2;
        scrollPane.repaint(new Rectangle(tableVisibleRect.x, tableVisibleRect.y, columnWidth, rowHeight));
    }


    private void handleMouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Optional<SearchResult> selectedSearchResult = getSelectedSearchResult();
            if (selectedSearchResult.isPresent()) {
                searchResultClickedListener.handleSearchResultClicked(selectedSearchResult.get(), e);
            }
        }
    }

    public void setSearchResults(SearchResultSet searchResultSet, int categorySizeLimit) {
        this.searchResultSet = searchResultSet;
        setData(searchResultSet.getSearchResults());
    }

    public void clearSearchResults() {
        clearData();
    }


    public void setCategorySizeLimit(int sizeLimit) {
        this.model.setCategorySizeLimit(sizeLimit);
        refill();
    }

    public void clearCategorySizeLimit() {
        this.model.clearCategorySizeLimit();
        refill();
    }


    public Optional<SearchResult> getSelectedSearchResult() {
        int selRow = resultsTable.getSelectedRow();
        if (selRow == -1) {
            return Optional.absent();
        }
        return Optional.of(model.getSearchResult(selRow));
    }


    public void refill() {
        if (searchResultSet != null) {
            setData(searchResultSet.getSearchResults());
        }
    }

    /**
     * Gets the OWLEntity at the specified row
     * @param selRow the row
     * @return The entity at the specified row.
     */
    public Optional<OWLEntity> getEntityAtRow(int selRow) {
        Object object = model.getValueAt(selRow, 1);
        if (object instanceof OWLEntity) {
            return Optional.of((OWLEntity) object);
        }
        else {
            return Optional.absent();
        }
    }

    /**
     * Gets the first selected entity.
     * @return The first selected entity.
     */
    public Optional<OWLEntity> getSelectedEntity() {
        int selRow = resultsTable.getSelectedRow();
        if (selRow == -1) {
            return Optional.absent();
        }
        return getEntityAtRow(selRow);
    }

    public java.util.List<OWLEntity> getSelectedEntities() {
        java.util.List<OWLEntity> results = new ArrayList<>();
        for (int selIndex : resultsTable.getSelectedRows()) {
            Optional<OWLEntity> entity = getEntityAtRow(selIndex);
            if (entity.isPresent()) {
                results.add(entity.get());
            }
        }
        return results;
    }


    public void moveSelectionDown() {
        int size = model.getRowCount();
        if (size == 0) {
            return;
        }
        int selectedIndex = resultsTable.getSelectionModel().getLeadSelectionIndex();
        int nextSelIndex = selectedIndex + 1;
        if (nextSelIndex == size) {
            nextSelIndex = 0;
        }
        resultsTable.getSelectionModel().setSelectionInterval(nextSelIndex, nextSelIndex);
        resultsTable.scrollRectToVisible(resultsTable.getCellRect(nextSelIndex, 0, true));
    }

    public void moveSelectionUp() {
        int size = model.getRowCount();
        if (size == 0) {
            return;
        }
        int selectedIndex = resultsTable.getSelectionModel().getLeadSelectionIndex();
        int nextSelIndex = selectedIndex - 1;
        if (nextSelIndex < 0) {
            nextSelIndex = size - 1;
        }
        resultsTable.getSelectionModel().setSelectionInterval(nextSelIndex, nextSelIndex);
        resultsTable.scrollRectToVisible(resultsTable.getCellRect(nextSelIndex, 0, true));
    }


    private void setData(Collection<SearchResult> results) {
        model.setResultList(results);
        Font font = OWLRendererPreferences.getInstance().getFont();
        resultsTable.setFont(font);
//        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        int rowHeight = font.getSize() + 4;
        resultsTable.setRowHeight(rowHeight);
        for (int i = 1; i < model.getRowCount(); i++) {
            if (isHeaderRow(i)) {
                resultsTable.setRowHeight(i, rowHeight + HEADER_SPACING);
            }
        }
        if (resultsTable.getModel().getRowCount() > 0) {
            resultsTable.getSelectionModel().setSelectionInterval(0, 0);
        }

    }

    private void clearData() {
        model.clear();
    }


    private boolean isHeaderRow(int rowIndex) {
        return resultsTable.getModel().getRowCount() != 0 && model.isFirstRowInCategory(rowIndex);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////
    //////
    //////  Renderers
    //////
    //////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class ResultsTableCellRendererWrapper implements TableCellRenderer {

        private TableCellRenderer delegate;

        private ResultsTableCellRendererWrapper(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JComponent c = (JComponent) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isHeaderRow(row) && row != 0) {
                Border border = c.getBorder();
                Border headerBorder = BorderFactory.createMatteBorder(HEADER_SPACING, 0, 0, 0, table.getBackground());
                if (border == null) {
                    c.setBorder(headerBorder);
                }
                else {
                    c.setBorder(BorderFactory.createCompoundBorder(headerBorder, border));
                }
            }
            return c;
        }
    }

    private class EntityFinderResultsRenderer implements TableCellRenderer {

        private DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

        private StyledStringPanel styledStringPanel = new StyledStringPanel();

        private EntityFinderResultsRenderer(OWLEditorKit editorKit) {
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof String && model.getSearchResult(row) != null) {

                SearchResult searchResult = model.getSearchResult(row);

                StyledString styledStringRep = searchResult.getStyledSearchSearchString();

                StyledString.Builder builder = styledStringRep.builder();

                styledStringPanel.setBorder(BorderFactory.createEmptyBorder());
                styledStringPanel.setFont(OWLRendererPreferences.getInstance().getFont());
                if (isSelected) {
                    styledStringPanel.setBackground(table.getSelectionBackground());
                    styledStringPanel.setForeground(table.getSelectionForeground());
                    builder.applyStyleAttributes(ForegroundAttribute.get(table.getSelectionForeground()));
                }
                else {
                    styledStringPanel.setBackground(table.getBackground());
                    styledStringPanel.setForeground(table.getForeground());
                }
                Style highlightStyle = ProtegeStyles.getHighlightStyle();
                for (SearchResultMatch match : searchResult.getMatches()) {
                    int from = match.getStart();
                    int to = match.getOffset();
                    builder.applyStyle(from, to, highlightStyle);
                }
                styledStringPanel.setStyledString(builder.build());

                return styledStringPanel;
            }
            else {
                return defaultTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }

    }


    private class CategoryRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String toRender = "";
            if (model.isFirstRowInCategory(row)) {
                toRender = value.toString();
            }
            else {
                Rectangle previousCellRect = table.getCellRect(row - 1, column, true);
                if (!table.getVisibleRect().intersects(previousCellRect)) {
                    toRender = value.toString();
                }
            }
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, toRender, isSelected, hasFocus, row, column);

            label.setHorizontalTextPosition(SwingConstants.RIGHT);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            if (!isSelected) {
                label.setForeground(Color.GRAY);
            }
            return label;
        }
    }


    private class EntityRenderer extends DefaultTableCellRenderer {

        private StyledStringPanel styledStringPanel = new StyledStringPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof OWLObject) {


                OWLObject owlObject = (OWLObject) value;


                OWLObjectRenderingContext renderingContext = new OWLObjectRenderingContext(new OWLEditorKitShortFormProvider(editorKit), new OWLEditorKitOntologyShortFormProvider(editorKit));
                OWLObjectStyledStringRenderer renderer = new OWLObjectStyledStringRenderer(renderingContext);
                StyledString styledString = renderer.getRendering(owlObject);


                StyledString.Builder builder = styledString.builder();

                SearchResult searchResult = model.getSearchResult(row);
                if (searchResult.getCategory() == SearchCategory.DISPLAY_NAME) {
                    for (SearchResultMatch match : searchResult.getMatches()) {
                        int start = match.getStart();
                        int end = match.getOffset();
                        builder.applyStyleAttributes(start, end, FontWeightAttribute.getBoldFontWeight());
                    }
                }

                if (OWLUtilities.isDeprecated(editorKit.getOWLModelManager(), owlObject)) {
                    builder.applyStyleAttributes(StrikeThroughAttribute.getSingle());
                }
                if (isSelected) {
                    styledStringPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 10, table.getSelectionBackground()));
                    styledStringPanel.setForeground(table.getSelectionForeground());
                    styledStringPanel.setBackground(table.getSelectionBackground());
                    builder.applyStyleAttributes(ForegroundAttribute.get(table.getSelectionForeground()));
                }
                else {
                    styledStringPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 10, table.getBackground()));
                    styledStringPanel.setForeground(null);
                    styledStringPanel.setBackground(null);
                }
                styledStringPanel.setStyledString(builder.build());
                styledStringPanel.setFont(OWLRendererPreferences.getInstance().getFont());
                styledStringPanel.setIcon(editorKit.getOWLWorkspace().getOWLIconProvider().getIcon(owlObject));
                return styledStringPanel;

            }
            else {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setIcon(null);
                if (!isSelected) {
                    label.setForeground(Color.LIGHT_GRAY);
                }
                return label;
            }
        }

    }

}
