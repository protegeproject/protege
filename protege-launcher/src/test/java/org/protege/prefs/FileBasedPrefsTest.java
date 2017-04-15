package org.protege.prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Test;

public class FileBasedPrefsTest {
	static {
		System.setProperty("java.util.prefs.PreferencesFactory", "org.protege.prefs.FileBackingStorePrefsFactory");
	}
	
	@Test
	public void testRoot() {
		Preferences root = Preferences.userRoot();
		assert(root instanceof Preferences);
	}
	
	@Test
	public void addNode() {
		Preferences root = Preferences.userRoot();
		Preferences foo = root.node("foo");
		foo.put("foo", "foo1");
		assert(foo.get("foo", "bad").equals("foo1"));		
	}
	
	@Test
	public void addInt() {
		Preferences root = Preferences.userRoot();
		Preferences foo = root.node("foo");
		foo.putInt("foo", 57);
		assert(foo.getInt("foo", 59) == 57);		
	}
	
	@Test
	public void clearNode() {
		Preferences root = Preferences.userRoot();
		Preferences foo = root.node("foo");
		foo.put("foo", "foo1");
		assert(foo.get("foo", "bad").equals("foo1"));
		try {
			foo.clear();
			assert(foo.get("foo", "bad").equals("bad"));
		} catch (BackingStoreException e) {
			e.printStackTrace();
			assert(false);			
		}		
	}
	
	@Test
	public void removeNode() {
		Preferences root = Preferences.userRoot();
		Preferences foo = root.node("foo");
		foo.put("foo", "foo1");
		assert(foo.get("foo", "bad").equals("foo1"));
		try {
			foo.removeNode();
			root.flush();
			assert(root.childrenNames().length == 0);			
		} catch (BackingStoreException e) {
			e.printStackTrace();
			assert(false);			
		}		
	}
	
	@Test
	public void addLists() {
		Preferences root = Preferences.userRoot();

		List<String> foos = new ArrayList<String>();
		foos.add("bob");
		foos.add("dodo");
		foos.add("yinghua");
		foos.add("gilberto");
		foos.add("rudi");

		putStringList(root, "foo", foos);

		List<String> foosList = this.getStringList(root, "foo", new ArrayList<String>());

		assert(foosList.size() == 5);
		assert(foosList.contains("dodo"));
		assert(foosList.contains("bob"));
		assert(foosList.contains("yinghua"));
		assert(foosList.contains("gilberto"));
		assert(foosList.contains("rudi"));	

	}
	
	
	private Preferences getList(Preferences root, String listKey, boolean create) {
        try {
            if (create || root.nodeExists(listKey)){
                return root.node(listKey);
            }
        }
        catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<String> getStringList(Preferences root, String key, List<String> def) {
        Preferences listPrefs = getList(root, key, false);
        if (listPrefs == null){
            return def;
        }
        int size = listPrefs.getInt("listSize", 0);
        List<String> list = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            list.add(listPrefs.get(Integer.toString(i), ""));
        }
        return list;
    }


    private void putStringList(Preferences root, String key, List<String> val) {
        Preferences listPrefs = getList(root, key, true);
        listPrefs.putInt("listSize", val.size());
        for(int i = 0; i < val.size(); i++) {
            listPrefs.put(Integer.toString(i), val.get(i));
        }
    }

}
