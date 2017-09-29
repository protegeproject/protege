package org.protege.editor.owl.model.deprecation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 30 Aug 2017
 */
public class OBIProfile_TestCase {

    private DeprecationProfile profile;

    @Before
    public void setUp() throws Exception {
        File file = new File("../protege-desktop/src/main/resources/conf/deprecation/obi.yaml");
        System.out.println(file.getAbsoluteFile());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        profile = mapper.readValue(file, DeprecationProfile.class);
    }

    @Test
    public void shouldParseYaml() {
        assertThat(profile, is(notNullValue()));
    }
}
