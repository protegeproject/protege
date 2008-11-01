package org.protege.editor.core.prefs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @deprecated JavaBackedPreferences should be used
 */
public class PreferencesImpl implements Preferences {

    private Map<String, Object> map;


    public PreferencesImpl() {
        map = new HashMap<String, Object>();
    }


    private Object getObject(String key, Object def) {
        Object obj = map.get(key);
        if (obj == null) {
            obj = def;
            map.put(key, obj);
        }
        return obj;
    }


    public void clear() {
        throw new UnsupportedOperationException();
    }


    public String getString(String key, String def) {
        return (String) getObject(key, def);
    }


    public void putString(String key, String val) {
        map.put(key, val);
    }


    public List<String> getStringList(String key, List<String> def) {
        return (List<String>) getObject(key, def);
    }


    public void putStringList(String key, List<String> val) {
        map.put(key, val);
    }


    public int getInt(String key, int def) {
        return (Integer) getObject(key, def);
    }


    public void putInt(String key, int val) {
        map.put(key, val);
    }


    public float getFloat(String key, float def) {
        return (Float) getObject(key, def);
    }


    public void putFloat(String key, float val) {
        map.put(key, val);
    }


    public long getLong(String key, long def) {
        return (Long) getObject(key, def);
    }


    public void putLong(String key, long val) {
        map.put(key, val);
    }


    public double getDouble(String key, double def) {
        return (Double) getObject(key, def);
    }


    public void putDouble(String key, double val) {
        map.put(key, val);
    }


    public boolean getBoolean(String key, boolean def) {
        return (Boolean) getObject(key, def);
    }


    public void putBoolean(String key, boolean val) {
        map.put(key, val);
    }


    public byte [] getByteArray(String key, byte [] def) {
        return (byte []) getObject(key, def);
    }


    public void putByteArray(String key, byte [] val) {
        map.put(key, val);
    }


    public List<byte []> getByteArrayList(String key, List<byte []> def) {
        return (List<byte []>) getObject(key, def);
    }


    public void putByteArrayList(String key, List<byte []> val) {
        map.put(key, val);
    }
}
