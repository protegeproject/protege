package org.protege.editor.owl.ui.prefix;

import com.google.common.collect.ImmutableList;
import org.protege.editor.core.ui.list.MList;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixList extends MList implements PrefixMappingsView {

    private List<PrefixMapping> prefixMappings = new ArrayList<>();

    private PrefixMappingsChangedHandler changeHandler = () -> {};

    public PrefixList() {
        setCellRenderer(new PrefixMappingRenderer());
    }

    public void setPrefixMappings(@Nonnull List<PrefixMapping> prefixMappings) {
        this.prefixMappings.clear();
        this.prefixMappings.addAll(prefixMappings);
        refill();
    }

    @Override
    public void setPrefixMappingsChangedHandler(@Nonnull PrefixMappingsChangedHandler handler) {
        this.changeHandler = checkNotNull(handler);
    }

    private void refill() {
        PrefixesListSection section = new PrefixesListSection();
        Stream<Object> header = Stream.of(section);
        Stream<PrefixesListSectionRow> rows = prefixMappings
                .stream()
                .map(PrefixesListSectionRow::new)
                .peek(row -> {
                    row.setDeleteHandler(this::handleDelete);
                    row.setEditHandler(this::handleEdit);
                });
        Stream<Object> elements = Stream.concat(header, rows);
        setListData(elements.toArray());
    }

    private void handleDelete(@Nonnull PrefixMapping prefixMapping) {
        if(prefixMappings.remove(prefixMapping)) {
            refill();
            changeHandler.handlePrefixMappingsChanged();
        }
    }

    private void handleEdit(@Nonnull PrefixMapping prefixMapping) {

    }

    @Nonnull
    public ImmutableList<PrefixMapping> getPrefixMappings() {
        ImmutableList.Builder<PrefixMapping> builder = ImmutableList.builder();
        ListModel model = getModel();
        for(int i = 0; i < model.getSize(); i++) {
            Object element = model.getElementAt(i);
            if(element instanceof PrefixesListSectionRow) {
                builder.add(((PrefixesListSectionRow) element).getValue());
            }
        }
        return builder.build();
    }

}
