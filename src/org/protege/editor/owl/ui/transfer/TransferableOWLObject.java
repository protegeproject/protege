package org.protege.editor.owl.ui.transfer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                    builder.append(owlModelManager.getRendering(obj));
                    builder.append("\n");
                }
                return builder.toString();
            }
        };
        dataFlavorMap.put(DataFlavor.stringFlavor, stringTransferHandler);
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
