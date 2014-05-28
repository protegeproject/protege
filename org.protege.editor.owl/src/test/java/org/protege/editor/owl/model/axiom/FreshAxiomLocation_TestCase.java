package org.protege.editor.owl.model.axiom;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class FreshAxiomLocation_TestCase {

    @Test
    public void shouldReturnActiveOntologyAsDefault() {
        assertThat(FreshAxiomLocation.getDefaultValue(), is(FreshAxiomLocation.ACTIVE_ONTOLOGY));
    }

    @Test
    public void shouldReturnLocationFromNameForActiveOntology() {
        Optional<FreshAxiomLocation> locationFromName = FreshAxiomLocation.getLocationFromName(
                FreshAxiomLocation.ACTIVE_ONTOLOGY.getLocationName());
        assertThat(locationFromName.isPresent(), is(true));
        assertThat(locationFromName.get(), is(FreshAxiomLocation.ACTIVE_ONTOLOGY));
    }

    @Test
    public void shouldReturnLocationFromNameForSubjectDefiningOntology() {
        Optional<FreshAxiomLocation> locationFromName = FreshAxiomLocation.getLocationFromName(
                FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY.getLocationName());
        assertThat(locationFromName.isPresent(), is(true));
        assertThat(locationFromName.get(), is(FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY));
    }

    @Test
    public void shouldReturnFactoryForActiveOntology() {
        assertThat(FreshAxiomLocation.ACTIVE_ONTOLOGY.getStrategyFactory(), is(notNullValue()));
    }

    @Test
    public void shouldReturnFactoryForSubjectDefiningOntology() {
        assertThat(FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY.getStrategyFactory(), is(notNullValue()));
    }

}
