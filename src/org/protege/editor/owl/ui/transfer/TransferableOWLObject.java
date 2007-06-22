package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.semanticweb.owl.model.OWLObject;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Date: 04-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class TransferableOWLObject implements Transferable {

    private OWLModelManager owlModelManager;

    private List<OWLObject> owlObjects;

    private Map<DataFlavor, TransferHandler> dataFlavorMap;

    private TransferHandler stringTransferHandler;


    public TransferableOWLObject(final OWLModelManager owlModelManager, List<OWLObject> objects) {
        this.owlModelManager = owlModelManager;
        owlObjects = new ArrayList<OWLObject>(objects);
        dataFlavorMap = new HashMap<DataFlavor, TransferHandler>();
        dataFlavorMap.put(OWLObjectDataFlavor.OWL_OBJECT_DATA_FLAVOR, new TransferHandler() {
            public Object getTransferData() {
                return new ArrayList<OWLObject>(owlObjects);
            }
        });
        stringTransferHandler = new TransferHandler() {
            public Object getTransferData() {
                StringBuilder builder = new StringBuilder();
                for (OWLObject obj : owlObjects) {
                    OWLEntityRenderer ren = owlModelManager.getOWLEntityRenderer();
                    builder.append(owlModelManager.getOWLObjectRenderer().render(obj, ren));
                    builder.append("\n");
                }
                return builder.toString();
            }
        };
        dataFlavorMap.put(DataFlavor.getTextPlainUnicodeFlavor(), stringTransferHandler);
    }


    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor [] dataFlavors = new DataFlavor[dataFlavorMap.size()];
        System.arraycopy(dataFlavorMap.keySet().toArray(), 0, dataFlavors, 0, dataFlavors.length);
        return dataFlavors;
    }


    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return dataFlavorMap.keySet().contains(flavor);
    }


    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        TransferHandler handler = dataFlavorMap.get(flavor);
        if (handler == null) {
            throw new UnsupportedFlavorException(flavor);
        }
        return handler.getTransferData();
    }


    private interface TransferHandler {

        Object getTransferData();
    }
}
