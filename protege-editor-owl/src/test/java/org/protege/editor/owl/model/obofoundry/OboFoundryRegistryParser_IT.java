package org.protege.editor.owl.model.obofoundry;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryRegistryParser_IT {

    @Test
    public void shouldParseRegistryFromStandardLocation() throws IOException {
        OboFoundryRegistry registry = OboFoundryRegistryParser.parseRegistryFromStandardLocation();
        assertNotNull(registry);
    }

    @Test
    public void shouldParseRegistryFromLocalCopy() throws IOException {
        OboFoundryRegistry registry = OboFoundryRegistryParser.parseRegistryFromLocalCopy();
        assertNotNull(registry);
    }

}
