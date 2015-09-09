package org.protege.editor.core.prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 12-Aug-2008<br><br>
 */
public class JavaBackedPreferencesImpl implements Preferences {

    /**
     * 
     */
    private static final long serialVersionUID = 1974522185816507186L;

    public static final String PROTEGE_PREFS_KEY = "PROTEGE_PREFERENCES";

    private java.util.prefs.Preferences preferences;

    public JavaBackedPreferencesImpl(String setID, String prefsID) {
        preferences = java.util.prefs.Preferences.userRoot().node(PROTEGE_PREFS_KEY).node(setID).node(prefsID);
    }


    public void clear() {
        try {
            preferences.clear();
        }
        catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }


    public String getString(String key, String def) {
        return preferences.get(key, def);
    }


    public void putString(String key, String val) {
        if (val == null){
            preferences.remove(key);
        }
        else{
            preferences.put(key, val);
        }
    }

    private java.util.prefs.Preferences getList(String listKey, boolean create) {
        try {
            if (create || preferences.nodeExists(listKey)){
                return preferences.node(listKey);
            }
        }
        catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<String> getStringList(String key, List<String> def) {
        java.util.prefs.Preferences listPrefs = getList(key, false);
        if (listPrefs == null){
            return def;
        }
        int size = listPrefs.getInt("listSize", 0);
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < size; i++) {
            list.add(listPrefs.get(Integer.toString(i), ""));
        }
        return list;
    }


    public void putStringList(String key, List<String> val) {
        java.util.prefs.Preferences listPrefs = getList(key, true);
        listPrefs.putInt("listSize", val.size());
        for(int i = 0; i < val.size(); i++) {
            listPrefs.put(Integer.toString(i), val.get(i));
        }
    }


    public int getInt(String key, int def) {
        return preferences.getInt(key, def);
    }


    public void putInt(String key, int val) {
        preferences.putInt(key, val);
    }


    public float getFloat(String key, float def) {
        return preferences.getFloat(key, def);
    }


    public void putFloat(String key, float val) {
        preferences.putFloat(key, val);
    }


    public long getLong(String key, long def) {
        return preferences.getLong(key, def);
    }


    public void putLong(String key, long val) {
        preferences.putLong(key, val);
    }


    public double getDouble(String key, double def) {
        return preferences.getDouble(key, def);
    }


    public void putDouble(String key, double val) {
        preferences.putDouble(key, val);
    }


    public boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }


    public void putBoolean(String key, boolean val) {
        preferences.putBoolean(key, val);
    }


    public byte [] getByteArray(String key, byte [] def) {
        return preferences.getByteArray(key, def);
    }


    public void putByteArray(String key, byte [] val) {
        preferences.putByteArray(key, val);
    }


    public List<byte []> getByteArrayList(String key, List<byte []> def) {
        java.util.prefs.Preferences listPrefs = getList(key, false);
        if (listPrefs == null){
            return def;
        }
        int size = listPrefs.getInt("listSize", 0);
        List<byte []> list = new ArrayList<byte []>();
        for(int i = 0; i < size; i++) {
            list.add(listPrefs.getByteArray(Integer.toString(i), new byte [] {0}));
        }
        return list;
    }


    public void putByteArrayList(String key, List<byte []> val) {
        java.util.prefs.Preferences listPrefs = getList(key, true);
        listPrefs.putInt("listSize", val.size());
        for(int i = 0; i < val.size(); i++) {
            listPrefs.putByteArray(Integer.toString(i), val.get(i));
        }
    }

}
