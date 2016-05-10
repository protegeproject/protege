package org.protege.editor.core.prefs;

import java.io.Serializable;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface Preferences extends Serializable {


    void clear();

    // String


    String getString(String key, String def);


    void putString(String key, String val);


    List<String> getStringList(String key, List<String> def);


    void putStringList(String key, List<String> val);

    // Int


    int getInt(String key, int def);


    void putInt(String key, int val);

    // Float


    float getFloat(String key, float def);


    void putFloat(String key, float val);

    // long


    long getLong(String key, long def);


    void putLong(String key, long val);

    // double


    double getDouble(String key, double def);


    void putDouble(String key, double val);

    // boolean


    boolean getBoolean(String key, boolean def);


    void putBoolean(String key, boolean val);

    // byte array


    byte [] getByteArray(String key, byte [] def);


    void putByteArray(String key, byte [] val);


    List<byte []> getByteArrayList(String key, List<byte []> def);


    void putByteArrayList(String key, List<byte []> val);
}
