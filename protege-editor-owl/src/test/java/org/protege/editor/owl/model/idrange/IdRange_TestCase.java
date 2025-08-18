package org.protege.editor.owl.model.idrange;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
public class IdRange_TestCase {

    private static final int LOWER_BOUND = 33;

    private static final int UPPER_BOUND = 44;

    private IdRange range;

    @Before
    public void setUp() {
        range = IdRange.getIdRange(LOWER_BOUND, UPPER_BOUND);
    }

    @Test
    public void shouldGetLowerBound() {
        assertThat(range.getLowerBound(), is(LOWER_BOUND));
    }

    @Test
    public void shouldGetUpperBound() {
        assertThat(range.getUpperBound(), is(UPPER_BOUND));
    }

    @Test
    public void shouldBeWellFormed() {
        assertThat(range.isWellFormed(), is(true));
    }

    @Test
    public void shouldNotBeEmpty() {
        assertThat(range.isEmpty(), is(false));
    }

    @Test
    public void shouldBeEmpty() {
        assertThat(IdRange.getIdRange(2, 2).isEmpty(), is(true));
    }

    @Test
    public void shouldNotBeWellformed() {
        assertThat(IdRange.getIdRange(3, 2).isWellFormed(), is(false));
    }
}
