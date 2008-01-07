package org.protege.editor.owl;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.io.File;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitFactory;
import org.protege.editor.core.editorkit.EditorKitDescriptor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEditorKitFactory implements EditorKitFactory {
    public static final String ID = "org.protege.editor.owl.OWLEditorKitFactory";

    public static final List<String> OWL_EXTENSIONS = Arrays.asList(".owl", ".rdf", ".xml");


    /**
     * Gets the identifier for this <code>EditorKitFactory</code>.
     * @return A <code>String</code> representation of the
     *         clsdescriptioneditor kit factory.
     */
    public String getId() {
        return ID;
    }


    public EditorKit createEditorKit() throws Exception {
        return new OWLEditorKit(this);
    }


    public boolean canLoad(URI uri) {
        String s = uri.toString();
        for (String ext : OWL_EXTENSIONS) {
            if (s.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


    public boolean isValidDescriptor(EditorKitDescriptor descriptor) {
        URI uri = descriptor.getURI(OWLEditorKit.URI_KEY);
        if(uri == null) {
            return false;
        }
        if(uri.getScheme().equals("file")) {
            File file = new File(uri);
            return file.exists();
        }
        return true;

    }
}
