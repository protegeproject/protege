package org.protege.editor.owl.ui.prefix;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixMapping_TestCase {

    private static final String PREFIX_NAME = "prefixName:";

    private static final String PREFIX = "ThePrefix";


    private PrefixMapping prefixMapping;

    @Before
    public void setUp() {
        prefixMapping = PrefixMapping.get(PREFIX_NAME,
                                          PREFIX);
    }

    @Test
    public void shouldReturnSuppliedPrefixName() {
        assertThat(prefixMapping.getPrefixName(), is(PREFIX_NAME));
    }

    @Test
    public void shouldReturnSuppliedPrefix() {
        assertThat(prefixMapping.getPrefix(), is(PREFIX));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForMalformedPrefixName() {
        PrefixMapping.get("prefixNameWithoutColon", PREFIX);
    }
}
