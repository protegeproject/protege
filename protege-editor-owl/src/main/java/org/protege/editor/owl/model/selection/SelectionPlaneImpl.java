package org.protege.editor.owl.model.selection;

import org.protege.editor.core.util.HandlerRegistration;
import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;
import java.awt.event.HierarchyListener;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
public class SelectionPlaneImpl implements SelectionPlane {


    private final List<SelectionDriver> selectionDrivers = new ArrayList<>();

    private final OWLSelectionModel selectionModel;

    private Optional<SelectionDriver> lastSelectionDriver = Optional.empty();

    private final List<SelectionDriver> lastShowingDrivers = new ArrayList<>();

    private HierarchyListener hierarchyListener = e -> handleHierarchyChange();

    public SelectionPlaneImpl(@Nonnull OWLSelectionModel selectionModel) {
        this.selectionModel = checkNotNull(selectionModel);
    }

    @Override
    public HandlerRegistration registerSelectionDriver(@Nonnull SelectionDriver driver) {
        checkNotNull(driver);
        if(selectionDrivers.contains(driver)) {
            return () -> {};
        }
        selectionDrivers.add(driver);
        driver.asComponent().addHierarchyListener(hierarchyListener);
        handleHierarchyChange();
        return () -> {
            selectionDrivers.remove(driver);
            if(lastShowingDrivers.remove(driver)) {
                lastShowingDrivers.clear();
            }
            driver.asComponent().removeHierarchyListener(hierarchyListener);
        };
    }

    @Override
    public void transmitSelection(@Nonnull SelectionDriver driver, OWLObject selection) {
        checkNotNull(driver);
        lastSelectionDriver = Optional.of(driver);
        selectionModel.setSelectedObject(selection);
    }

    private void handleHierarchyChange() {
        List<SelectionDriver> showingDrivers = selectionDrivers.stream()
                .filter(driver -> driver.asComponent().isShowing())
//                .filter(driver -> driver.getSelection().isPresent())
                .collect(toList());
        if(lastShowingDrivers.equals(showingDrivers)) {
            return;
        }
        lastShowingDrivers.clear();
        lastShowingDrivers.addAll(showingDrivers);
        if(showingDrivers.isEmpty()) {
            return;
        }
        if(showingDrivers.size() == 1) {
            // Transmit selection
            OWLObject sel = showingDrivers.get(0).getSelection().orElse(null);
            selectionModel.setSelectedObject(sel);
        }
        else {
            // Multiple selection drivers are showing.
            // Pick one and transmit its selection.
            // If one driver has transmitted the selection in the past then we use that.  If no drivers have
            // transmitted the selection in the past then we select the first one that was registered.
            if(lastSelectionDriver.isPresent() && showingDrivers.contains(lastSelectionDriver.get())) {
                OWLObject sel = lastSelectionDriver.get().getSelection().orElse(null);
                selectionModel.setSelectedObject(sel);
            }
            else {
                OWLObject sel = showingDrivers.get(0).getSelection().orElse(null);
                selectionModel.setSelectedObject(sel);
            }
        }

    }
}
