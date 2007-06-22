package org.protege.editor.core.editorkit;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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

    private static final Logger logger = Logger.getLogger(RecentEditorKitManager.class);

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


    public List<EditorKitDescriptor> getDescriptors() {
        return new ArrayList<EditorKitDescriptor>(editorKitDescriptors);
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
    }


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
        }
        catch (Exception e) {
            logger.error(e);
        }
    }


    private static File getEditorKitsFile() {
        File dataFolder = FileManager.getDataFolder();
        return new File(dataFolder, RECENT_EDITOR_KITS_FILE_NAME);
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
        catch (IOException e) {
            logger.error(e);
        }
        catch (BackingStoreException e) {
            logger.error(e);
        }
    }
}
