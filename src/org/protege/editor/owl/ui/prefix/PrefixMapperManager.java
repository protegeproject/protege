package org.protege.editor.owl.ui.prefix;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.PreferencesManager;

import java.io.*;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMapperManager {

    private static final Logger logger = Logger.getLogger(PrefixMapperManager.class);

    private static final String PREFERENCES_ID = "org.protege.editor.owl.prefixmanager";
    private static final String PREF_KEY = "data";

    private static PrefixMapperManager instance;

    private PrefixMapper mapper;


    private PrefixMapperManager() {
        mapper = new PrefixMapperImpl();
        reload();
    }


    public void reload() {
        try {
            byte [] bytes = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_ID).getByteArray(PREF_KEY, null);
            if (bytes == null) {
                bytes = java.util.prefs.Preferences.userRoot().getByteArray(PREFERENCES_ID, null);
            }
            if (bytes != null){
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream oos = new ObjectInputStream(bis);
            Map<String, String> prefixMap = (Map<String, String>) oos.readObject();
            mapper = new PrefixMapperImpl(prefixMap);
            }
        }
        catch (IOException e) {
            logger.error(e);
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
        }
    }


    public void save() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(mapper.getPrefixMap());

            PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_ID).putByteArray(PREF_KEY, bos.toByteArray());
        }
        catch (IOException e) {
            logger.error(e);
        }
    }


    public static synchronized PrefixMapperManager getInstance() {
        if (instance == null) {
            instance = new PrefixMapperManager();
        }
        return instance;
    }


    public PrefixMapper getMapper() {
        return mapper;
    }


    public void setPrefixes(Map<String, String> prefixValueMap) {
        mapper = new PrefixMapperImpl(prefixValueMap);
    }


    public Map<String, String> getPrefixes() {
        return mapper.getPrefixMap();
    }
}
