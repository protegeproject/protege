package org.protege.editor.owl;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.EditorKitFactory;
import org.protege.editor.core.ui.util.UIUtil;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEditorKitFactory implements EditorKitFactory {

    public static final String ID = "org.protege.editor.owl.OWLEditorKitFactory";

    public static final List<String> OWL_EXTENSIONS = Collections.unmodifiableList(Arrays.asList(".owl", ".rdf", ".xml"));


    /**
     * Gets the identifier for this <code>EditorKitFactory</code>.
     * @return A <code>String</code> representation of the
     *         clsdescriptioneditor kit factory.
     */
    @Nonnull
    public String getId() {
        return ID;
    }


    @Nonnull
    public EditorKit createEditorKit() {
        return new OWLEditorKit(this);
    }


    public boolean canLoad(@Nonnull URI uri) {
        String s = uri.toString();
        for (String ext : OWL_EXTENSIONS) {
            if (s.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


    public boolean isValidDescriptor(@Nonnull EditorKitDescriptor descriptor) {
        URI uri = descriptor.getURI(OWLEditorKit.URI_KEY);
        if(uri == null || uri.getScheme() == null) {
            return false;
        }
        if (UIUtil.isLocalFile(uri)) {
            File file = new File(uri);
            return file.exists();
        }
        return true;

    }
}
