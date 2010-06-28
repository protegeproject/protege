package org.protege.osgi.framework;


public class FelixWrapper {
    public static final String ARG_PROPERTY = "command.line.arg.";

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        if (args != null) {
            int counter = 0;
            for (String arg : args) {
                System.setProperty(ARG_PROPERTY + (counter++), arg);
            }
        }
        org.apache.felix.main.Main.main(new String[0]);
    }

}
