package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.style.Style;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.swing.styling.JSimpleStyleDialog;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadWFSAction extends AbstractAction {
    public ReadWFSAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.setStatus("WFS-Layer einlesen...");
        Map<String, Object> params = new HashMap<>();
        params.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", "https://maps.dwd.de/geoserver/dwd/ows"); // Datenbanktyp

        try {
            // DataStore-Objekt anlegen
            DataStore store = DataStoreFinder.getDataStore(params);
            System.out.println("DataStore-Objekt angelegt");
            // Zugriff auf Tabelle

            String typeName = "dwd:RBSN_RR";
            SimpleFeatureSource fSource = store.getFeatureSource(typeName);
            // in eine Hauptspeicher-Collection überführen
            SimpleFeatureCollection coll = fSource.getFeatures();
            // falls man WFS überschreiben möchte, braucht man eine Kopie
            MemoryFeatureCollection mColl = new MemoryFeatureCollection(fSource.getSchema());
            try (SimpleFeatureIterator it = coll.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                    mColl.add(f);
                }
            }
            store.dispose();

            Style style = JSimpleStyleDialog.showDialog(null, mColl.getSchema());
            Layer layer = new FeatureLayer(mColl, style);
            window.getMap().addLayer(layer); // oder: map.layers().add(layer);
            window.setStatus("Layer " + typeName.toString() + "aus WFS eingelesen");
        } catch (IOException ex) {
            ex.printStackTrace();
            window.setStatus("Fehler beim PostGIS-Tabelle einlesen." + ex.getMessage());
        }
    }
}
