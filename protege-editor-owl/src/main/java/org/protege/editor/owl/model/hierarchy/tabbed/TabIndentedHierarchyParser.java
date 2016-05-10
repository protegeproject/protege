package org.protege.editor.owl.model.hierarchy.tabbed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TabIndentedHierarchyParser {

    private List<Line> lineList;

    private String prefix;

    private String suffix;

    private int tabSize;


    public TabIndentedHierarchyParser(int indent) {
        this(indent, null, null);
    }


    public TabIndentedHierarchyParser(int indent, String prefix, String suffix) {
        tabSize = indent;
        this.prefix = prefix;
        this.suffix = suffix;
    }


    public List<Edge> parse(Reader reader) throws IOException {
        lineList = new ArrayList<>();
        BufferedReader r = new BufferedReader(reader);
        createLines(r);
        return getEdges();
    }


    private void createLines(BufferedReader reader) throws IOException {
        String tabString = "";
        for (int i = 0; i < tabSize; i++) {
            tabString += " ";
        }
        String line;
        while ((line = reader.readLine()) != null) {
            String normalisedLine = line.replaceAll("\t", tabString);
            String trimmedLine = normalisedLine.trim();
            if (prefix != null) {
                trimmedLine = prefix + trimmedLine;
            }
            if (suffix != null) {
                trimmedLine = trimmedLine + suffix;
            }
            if (trimmedLine.length() > 0){
                lineList.add(new Line(getIndent(normalisedLine), trimmedLine));
            }
        }
    }


    private List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        // Loop through all lines and build up a tree
        for (int i = 0; i < lineList.size(); i++) {
            Line curLine = lineList.get(i);
            // Gets the previous non sibling line
            Edge e = null;
            for (int j = i - 1; j > -1; j--) {
                Line candidateLine = lineList.get(j);
                if (candidateLine.getIndent() < curLine.getIndent()) {
                    // Got our parent
                    e = new Edge(curLine.getLine(), candidateLine.getLine());
                    break;
                }
            }
            if (e == null) {
                e = new Edge(curLine.getLine(), null);
            }
            edges.add(e);
        }
        return edges;
    }


    private int getIndent(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                count++;
            }
            else {
                break;
            }
        }
        return count / tabSize;
    }


    private class Line {

        private int indent;

        private String line;


        public Line(int indent, String line) {
            this.indent = indent;
            this.line = line;
        }


        public int getIndent() {
            return indent;
        }


        public String getLine() {
            return line;
        }
    }
}
