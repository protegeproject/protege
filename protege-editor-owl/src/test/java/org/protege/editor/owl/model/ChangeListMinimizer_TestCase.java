package org.protege.editor.owl.model;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Jul 16
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeListMinimizer_TestCase {

    private ChangeListMinimizer minimizer;

    private List<OWLOntologyChange> changes;

    @Mock
    private OWLAxiom axiomA, axiomB;

    @Mock
    private AddAxiom addAxiom;

    @Mock
    private RemoveAxiom removeAxiom;

    @Before
    public void setUp() throws Exception {
        minimizer = new ChangeListMinimizer();
        changes = new ArrayList<>();
        when(addAxiom.isAddAxiom()).thenReturn(true);
        when(removeAxiom.isRemoveAxiom()).thenReturn(true);
    }

    @Test
    public void shouldPreserveSingleAddAxiom() {
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(addAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(addAxiom));
    }

    @Test
    public void shouldPreserveSingleRemoveAxiom() {
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(removeAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(removeAxiom));
    }

    @Test
    public void shouldPreserveNonAxiomChanges() {
        AddOntologyAnnotation change = mock(AddOntologyAnnotation.class);
        changes.add(change);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(change));
    }

    @Test
    public void shouldCancelAdditionFollowedByRemoval() {
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(addAxiom);
        changes.add(removeAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, is(empty()));
    }

    @Test
    public void shouldCancelFirstAdditionFollowedByRemoval() {
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(addAxiom);
        changes.add(removeAxiom);
        changes.add(addAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(addAxiom));
    }

    @Test
    public void shouldCancelFirstAndSecondAdditionFollowedByRemoval() {
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(addAxiom);
        changes.add(removeAxiom);
        changes.add(addAxiom);
        changes.add(removeAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, is(empty()));
    }

    @Test
    public void shouldCollapseMultipleAdds() {
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(addAxiom);
        changes.add(addAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(addAxiom));
    }

    @Test
    public void shouldCancelRemovalFollowedByAddition() {
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        when(addAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(removeAxiom);
        changes.add(addAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, is(empty()));
    }

    @Test
    public void shouldCollapseMultipleRemoves() {
        when(removeAxiom.getAxiom()).thenReturn(axiomA);
        changes.add(removeAxiom);
        changes.add(removeAxiom);
        List<OWLOntologyChange> minimizedChanges = minimizer.getMinimisedChanges(changes);
        assertThat(minimizedChanges, contains(removeAxiom));
    }

}
