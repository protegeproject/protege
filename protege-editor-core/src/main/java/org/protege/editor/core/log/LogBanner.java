package org.protege.editor.core.log;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogBanner {

    private static final int WIDTH = 80;

    private static final String FOOTER;

    static {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < WIDTH; i++) {
            sb.append("-");
        }
        FOOTER = sb.toString();
    }


    public static String start(String title) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for( ; i < WIDTH / 2 - title.length() / 2 - 1; i++) {
            sb.append("-");
        }
        sb.append(" ");
        i++;
        sb.append(title);
        i += title.length();
        sb.append(" ");
        i++;
        for(; i < WIDTH; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    public static String end() {
        return "";
    }


    @Override
    public String toString() {
        return "";
    }

    public static void main(String[] args) {
        System.out.println(LogBanner.start("Even"));
        System.out.println(LogBanner.end());
        System.out.println(LogBanner.start("Odd"));
        System.out.println(LogBanner.end());
    }
}
