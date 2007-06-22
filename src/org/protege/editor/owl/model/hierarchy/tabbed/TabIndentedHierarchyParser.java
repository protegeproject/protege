package org.protege.editor.owl.model.hierarchy.tabbed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-Jul-2006<br><br>
 * <p/>
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
        lineList = new ArrayList<Line>();
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
            lineList.add(new Line(getIndent(normalisedLine), trimmedLine));
        }
    }


    private List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<Edge>();
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
