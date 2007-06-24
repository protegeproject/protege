package org.protege.editor.owl.ui.prefix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


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

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.prefixmanager";

    private static PrefixMapperManager instance;

    private PrefixMapper mapper;


    private PrefixMapperManager() {
        mapper = new PrefixMapperImpl();
        reload();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                save();
            }
        });
    }


    public void reload() {
        try {
            Preferences userRoot = Preferences.userRoot();
            byte [] bytes = userRoot.getByteArray(PREFERENCES_KEY, null);
            if (bytes == null) {
                return;
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream oos = new ObjectInputStream(bis);
            Map<String, String> prefixMap = (Map<String, String>) oos.readObject();
            mapper = new PrefixMapperImpl(prefixMap);
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
