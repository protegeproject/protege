package org.protege.editor.owl.ui.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultComparator;
import org.protege.editor.owl.model.search.SearchResultSet;
import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nullable;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 28/09/2012
 */
public class SearchResultsTableModel extends AbstractTableModel {

    public static final int DEFAULT_CATEGORY_SIZE_LIMIT = 10;

    private static final String FOUND_IN = "Found in";

    private static final String ENTITY = "Entity";

    private static final String OBO_ID = "Id";

    private static final String MATCH = "Match";

    private OWLEditorKit editorKit;

    private int categorySizeLimit = DEFAULT_CATEGORY_SIZE_LIMIT;

    private java.util.List<ResultsTableModelRow> rows = new ArrayList<>();

    private boolean hasOboIdsInResults = false;

    public SearchResultsTableModel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public void clear() {
        rows.clear();
        if (hasOboIdsInResults) {
            hasOboIdsInResults = false;
            fireTableStructureChanged();
        }
        else {
            fireTableDataChanged();
        }
    }

    /**
     * Gets the column used to show OBO Ids.
     *
     * @return The column index used to show OBO Ids or -1 if there is no such column
     */
    public int getOboIdColumn() {
        if (hasOboIds()) {
            return 2;
        }
        else {
            return -1;
        }
    }

    private boolean hasOboIds() {
        return rows.stream()
                .map(ResultsTableModelRow::getSearchResult)
                .filter(Objects::nonNull)
                .map(SearchResult::getSubject)
                .filter(subject -> subject instanceof OWLEntity)
                .map(subject -> (OWLEntity) subject)
                .map(OWLEntity::getIRI)
                .anyMatch(OboUtilities::isOboIri);
    }

    public int getResultsColumn() {
        if (hasOboIds()) {
            return 3;
        }
        else {
            return 2;
        }
    }

    public int getCategorySizeLimit() {
        return categorySizeLimit;
    }

    public void setCategorySizeLimit(int categorySizeLimit) {
        if (categorySizeLimit != this.categorySizeLimit) {
            this.categorySizeLimit = categorySizeLimit;
            fireTableDataChanged();
        }
    }

    public void clearCategorySizeLimit() {
        if (categorySizeLimit != Integer.MAX_VALUE) {
            categorySizeLimit = Integer.MAX_VALUE;
            fireTableDataChanged();
        }
    }

    public void setResultList(Collection<SearchResult> resultList) {
        rows.clear();
        SearchResultSet resultSet = new SearchResultSet(resultList);
        for (String category : resultSet.getCategories()) {
            java.util.List<SearchResult> categoryResult = resultSet.getCategoryResults(category);
            Collections.sort(categoryResult, new SearchResultComparator(editorKit));
            int count = 0;
            int categoryResultsCount = resultSet.getCategoryResultsCount(category);
            for (SearchResult searchResult : categoryResult) {
                rows.add(new ResultsTableModelRow(searchResult, count, categoryResultsCount));
                count++;
                if (count == categorySizeLimit) {
                    break;
                }
            }
            if (categoryResultsCount > categorySizeLimit) {
                rows.add(new PlaceHolderRow(categoryResultsCount));
            }
        }
        boolean containsOboIds = hasOboIds();
        if (containsOboIds != hasOboIdsInResults) {
            hasOboIdsInResults = containsOboIds;
            fireTableStructureChanged();
        }
        else {
            fireTableDataChanged();
        }
    }

    public ResultsTableModelRow getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    public SearchResult getSearchResult(int rowIndex) {
        ResultsTableModelRow row = rows.get(rowIndex);
        return row.searchResult;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return FOUND_IN;
        }
        if (column == 1) {
            return ENTITY;
        }

        if (hasOboIds()) {
            if (column == 2) {
                return OBO_ID;
            }
            if (column == 3) {
                return MATCH;
            }
        }
        else {
            if (column == 2) {
                return MATCH;
            }
        }

        return "";
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return hasOboIds() ? 4 : 3;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        ResultsTableModelRow row = rows.get(rowIndex);
        return row.getRenderableObject(columnIndex);
    }


    public boolean isFirstRowInCategory(int rowIndex) {
        return rows.get(rowIndex).isFirstRowInCategory();
    }

    private class ResultsTableModelRow {

        private SearchResult searchResult;

        private int categoryRowIndex;

        private int categoryResultCount;

        private ResultsTableModelRow(SearchResult searchResult, int categoryRowIndex, int categoryResultCount) {
            this.searchResult = searchResult;
            this.categoryRowIndex = categoryRowIndex;
            this.categoryResultCount = categoryResultCount;
        }

        @Nullable
        public SearchResult getSearchResult() {
            return searchResult;
        }

        public boolean isFirstRowInCategory() {
            return categoryRowIndex == 0;
        }

        public int getCategoryResultCount() {
            return categoryResultCount;
        }

        public Object getRenderableObject(int columnIndex) {
            if (columnIndex == 0) {
                return searchResult.getGroupDescription();
            }
            if (columnIndex == 1) {
                return searchResult.getSubject();
            }
            if (hasOboIds()) {
                if (columnIndex == 2) {
                    OWLObject subject = searchResult.getSubject();
                    if (subject instanceof OWLEntity) {
                        return OboUtilities.getOboIdFromIri(((OWLEntity) subject).getIRI()).orElse("");
                    }
                }
                if (columnIndex == 3) {
                    return searchResult.getSearchString();
                }
            }
            else {
                if (columnIndex == 2) {
                    return searchResult.getSearchString();
                }
            }
            return "";
        }
    }


    private class PlaceHolderRow extends ResultsTableModelRow {

        private PlaceHolderRow(int categoryResultCount) {
            super(null, 0, categoryResultCount);
        }

        @Override
        public boolean isFirstRowInCategory() {
            return false;
        }

        @Override
        public Object getRenderableObject(int columnIndex) {
            if (columnIndex == 1) {
                return "    + " + (getCategoryResultCount() - categorySizeLimit) + " more results...";
            }
            else {
                return "";
            }
        }
    }

}
