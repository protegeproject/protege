
package org.protege.editor.core.ui.menu;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PopupMenuId_TestCase {

    private PopupMenuId popupMenuId;
    private String id = "[The id]";

    @Before
    public void setUp()
    {
        popupMenuId = new PopupMenuId(id);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_id_IsNull() {
        new PopupMenuId(null);
    }

    @Test
    public void shouldReturnSupplied_id() {
        assertThat(popupMenuId.getId(), is(this.id));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(popupMenuId, is(popupMenuId));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(popupMenuId.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(popupMenuId, is(new PopupMenuId(id)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_id() {
        assertThat(popupMenuId, is(not(new PopupMenuId("[b4d7ddab-7759-43c9-a349-e9ee36423b70]"))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(popupMenuId.hashCode(), is(new PopupMenuId(id).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(popupMenuId.toString(), startsWith("PopupMenuId"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForIllegalStart() {
        new PopupMenuId("TheInvalidId]");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForIllegalEnd() {
        new PopupMenuId("[TheInvalidId");
    }

}
