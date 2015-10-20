package org.protege.editor.core.editorkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.protege.editor.core.ProtegeManager;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RecentEditorKitManager {

    private final Logger logger = LoggerFactory.getLogger(RecentEditorKitManager.class);

    private static RecentEditorKitManager instance;

    private List<EditorKitDescriptor> editorKitDescriptors;

    public static final String RECENT_EDITOR_KITS_FILE_NAME = "RecentEditorKits";

    public static final int MAX_EDITOR_KITS = 10;

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.recenteditorkits";


    private RecentEditorKitManager() {
        editorKitDescriptors = new ArrayList<EditorKitDescriptor>();
    }


    public static synchronized RecentEditorKitManager getInstance() {
        if (instance == null) {
            instance = new RecentEditorKitManager();
        }
        return instance;
    }
    
    public void dispose() {
        instance = null;
    }


    public List<EditorKitDescriptor> getDescriptors() {
        return new ArrayList<EditorKitDescriptor>(editorKitDescriptors);
    }

    public void pruneInvalidDescriptors() {

        for(EditorKitFactoryPlugin plugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
            EditorKitFactory factory = plugin.newInstance();
            for(Iterator<EditorKitDescriptor> it = editorKitDescriptors.iterator(); it.hasNext(); ) {
                if(!factory.isValidDescriptor(it.next())) {
                    it.remove();
                }
            }
        }
    }


    public void clear() {
        editorKitDescriptors.clear();
    }


    public void add(EditorKitDescriptor editorKitDescriptor) {
        for (Iterator<EditorKitDescriptor> it = editorKitDescriptors.iterator(); it.hasNext();) {
            EditorKitDescriptor descriptor = it.next();
            if (descriptor.equals(editorKitDescriptor)) {
                it.remove();
                break;
            }
        }
        editorKitDescriptors.add(0, editorKitDescriptor);
        // Chop any off the end
        for (int i = MAX_EDITOR_KITS - 1; editorKitDescriptors.size() > MAX_EDITOR_KITS;) {
            editorKitDescriptors.remove(i);
        }
        save();
    }


    @SuppressWarnings("unchecked")
    public void load() {
        Preferences userRoot = Preferences.userRoot();
        byte [] prefsBytes = userRoot.getByteArray(PREFERENCES_KEY, null);
        if (prefsBytes == null) {
            return;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(prefsBytes));
            editorKitDescriptors = (List<EditorKitDescriptor>) ois.readObject();
            ois.close();
            pruneInvalidDescriptors();
        }
        catch (Exception e) {
            logger.error("An error occurred whilst loading the list of recent items", e);
        }
    }

    public void save() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(editorKitDescriptors);
            oos.flush();
            oos.close();
            // Store in Java prefs
            Preferences userRoot = Preferences.userRoot();
            userRoot.putByteArray(PREFERENCES_KEY, bos.toByteArray());
            userRoot.flush();
        }
        catch (IOException | BackingStoreException e) {
            logger.error("An error occurred whilst saving the list of recent items", e);
        }
    }
}
