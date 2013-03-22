package org.protege.editor.owl.ui.search;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultComparator;
import org.protege.editor.owl.model.search.SearchResultSet;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 28/09/2012
 */
public class SearchResultsTableModel extends AbstractTableModel {

    public static final int DEFAULT_CATEGORY_SIZE_LIMIT = 10;

    private OWLEditorKit editorKit;

    private int categorySizeLimit = DEFAULT_CATEGORY_SIZE_LIMIT;

    private java.util.List<ResultsTableModelRow> rows = new ArrayList<ResultsTableModelRow>();

    public SearchResultsTableModel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
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


    public SearchResult getSearchResultAt(int row) {
        ResultsTableModelRow modelRow = rows.get(row);
        return modelRow.getSearchResult();
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
        fireTableDataChanged();
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
            return "Found in";
        }
        else if (column == 1) {
            return "Entity";
        }
        else if (column == 2) {
            return "Match";
        }
        else {
            return "";
        }
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return 3;
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
            else if (columnIndex == 1) {
                return searchResult.getSubject();
            }
            else if (columnIndex == 2) {
                return searchResult.getSearchString();
            }
            else {
                return "";
            }
        }
    }


    private class PlaceHolderRow extends ResultsTableModelRow {

        private PlaceHolderRow(int categoryResultCount) {
            super(null, 0, categoryResultCount);
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

        @Override
        public boolean isFirstRowInCategory() {
            return false;
        }
    }

}
