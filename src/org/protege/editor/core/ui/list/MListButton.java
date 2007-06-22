package org.protege.editor.core.ui.list;

import java.awt.*;
import java.awt.event.ActionListener;
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
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br><br>
 */
public abstract class MListButton {

    private String name;

    private Color rollOverColor;

    private ActionListener actionListener;

    private Rectangle bounds;

    private Object rowObject;


    protected MListButton(String name, Color rollOverColor, ActionListener actionListener) {
        this.name = name;
        this.rollOverColor = rollOverColor;
        this.actionListener = actionListener;
    }


    public String getName() {
        return name;
    }


    public Object getRowObject() {
        return rowObject;
    }


    public void setRowObject(Object rowObject) {
        this.rowObject = rowObject;
    }


    public Color getRollOverColor() {
        return rollOverColor;
    }


    public ActionListener getActionListener() {
        return actionListener;
    }


    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }


    public Rectangle getBounds() {
        return bounds;
    }


    /**
     * Paints the button content. For convenience, the graphics origin will be
     * the top left corner of the button
     * @param g The graphics which should be used for rendering
     *          the content
     */
    public abstract void paintButtonContent(Graphics2D g);
}
