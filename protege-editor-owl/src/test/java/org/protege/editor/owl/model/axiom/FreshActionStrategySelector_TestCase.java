package org.protege.editor.owl.model.axiom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.OWLEditorKit;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 28/05/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class FreshActionStrategySelector_TestCase {

    @Mock
    private FreshAxiomLocationPreferences preferences;

    @Mock
    private OWLEditorKit editorKit;

    @Test
    public void shouldSelectActiveOntologyLocationStrategy() {
        when(preferences.getFreshAxiomLocation()).thenReturn(FreshAxiomLocation.ACTIVE_ONTOLOGY);
        FreshActionStrategySelector selector = new FreshActionStrategySelector(preferences, editorKit);
        FreshAxiomLocationStrategy strategy = selector.getFreshAxiomLocationStrategy();
        assertThat(strategy, is(instanceOf(ActiveOntologyLocationStrategy.class)));
    }

    @Test
    public void shouldSelectSubjectDefinitionOntologyLocationStrategy() {
        when(preferences.getFreshAxiomLocation()).thenReturn(FreshAxiomLocation.SUBJECT_DEFINING_ONTOLOGY);
        FreshActionStrategySelector selector = new FreshActionStrategySelector(preferences, editorKit);
        FreshAxiomLocationStrategy strategy = selector.getFreshAxiomLocationStrategy();
        assertThat(strategy, is(instanceOf(SubjectDefinitionLocationStrategy.class)));
    }
}
