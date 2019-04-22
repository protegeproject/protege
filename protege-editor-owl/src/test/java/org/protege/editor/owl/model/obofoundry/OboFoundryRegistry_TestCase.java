package org.protege.editor.owl.model.obofoundry;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryRegistry_TestCase {


    @Test
    public void shouldParseRegistry() throws IOException {
        InputStream inputStream = OboFoundryRegistry_TestCase.class.getResourceAsStream("/obofoundry/OboFoundry.json");
        OboFoundryRegistry registry = OboFoundryRegistryParser.parseRegistry(inputStream);
        assertNotNull(registry);
    }
}
