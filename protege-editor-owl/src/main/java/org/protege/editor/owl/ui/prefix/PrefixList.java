package org.protege.editor.owl.ui.prefix;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import org.protege.editor.core.ui.list.MList;

import com.google.common.collect.ImmutableList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixList extends MList implements PrefixMappingsView {

    private static final String TITLE = "Edit Prefix";

    private List<PrefixMapping> prefixMappings = new ArrayList<>();

    private PrefixMappingsChangedHandler changeHandler = () -> {};

    private PrefixMappingEditor editor = new PrefixMappingEditor(new PrefixMappingEditorViewImpl());

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

    @Override
    protected void handleAdd() {
        editor.clear();
        showEditorDialog().ifPresent(pm -> {
            if(!prefixMappings.contains(pm)) {
                prefixMappings.add(pm);
                refill();
                changeHandler.handlePrefixMappingsChanged();
            }
        });
    }

    private Optional<PrefixMapping> showEditorDialog() {
        int ret = JOptionPane.showConfirmDialog(this, editor.asJComponent(), TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(ret == JOptionPane.OK_OPTION) {
            return editor.getPrefixMapping();
        }
        else {
            return Optional.empty();
        }
    }

    private void handleEdit(@Nonnull PrefixMapping prefixMapping) {
        editor.setPrefixMapping(prefixMapping);
        int index = prefixMappings.indexOf(prefixMapping);
        showEditorDialog().ifPresent(pm -> {
            prefixMappings.set(index, pm);
            refill();
            changeHandler.handlePrefixMappingsChanged();
        });
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
