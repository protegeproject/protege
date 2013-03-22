package org.protege.editor.core.platform;

public class OSGi {
    private OSGi() {}
    
    /**
     * This tells the application plugin whether it should call system exit on quit.
     * 
     * If Protege is started by the launcher, then the clean shutdown of Protege is handled by the launcher.
     * However developers will often start Protege without the launcher and it appears that in that case
     * Protege does not know when to exit.  This routine tells the Protege core application plugin if the 
     * System.exit should be called in the Protege core application handling.
     * 
     */
    public static boolean systemExitHandledByLauncher() {
        String forceExit = System.getProperty("org.protege.osgi.launcherHandlesExit");
        return forceExit != null && forceExit.toLowerCase().equals("true");
    }

}
