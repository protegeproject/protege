package org.protege.editor.owl.model.axiom;

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public enum FreshAxiomLocation {

    ACTIVE_ONTOLOGY("ActiveOntology", editorKit -> new ActiveOntologyLocationStrategy()),

    SUBJECT_DEFINING_ONTOLOGY("SubjectDefiningOntology", editorKit -> new SubjectDefinitionLocationStrategy(
            new DefaultTopologicallySortedImportsClosureProvider(),
            new DefaultAxiomSubjectProvider(),
            new DefaultSubjectDefinitionExtractor()));

    private final String locationName;

    private final FreshAxiomLocationStrategyFactory strategy;

    FreshAxiomLocation(String locationName, FreshAxiomLocationStrategyFactory strategy) {
        this.locationName = locationName;
        this.strategy = strategy;
    }

    public String getLocationName() {
        return locationName;
    }

    public static FreshAxiomLocation getDefaultValue() {
        return ACTIVE_ONTOLOGY;
    }

    public FreshAxiomLocationStrategyFactory getStrategyFactory() {
        return strategy;
    }

    public static Optional<FreshAxiomLocation> getLocationFromName(String name) {
        checkNotNull(name);
        for(FreshAxiomLocation location : values()) {
            if(location.getLocationName().endsWith(name)) {
                return Optional.of(location);
            }
        }
        return Optional.absent();
    }
}
