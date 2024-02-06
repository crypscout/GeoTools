package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class ExportShapefileAction extends AbstractAction {

    public ExportShapefileAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (window.getMap().layers().isEmpty()) {
            window.setStatus("Shapefile exportieren nicht möglich.");
            return;
        }
        // Dateiauswahl
        window.setStatus("Datei auswählen...");
        JFileChooser fileChooser = new JFileChooser(
                "C:\\Users\\jessi\\studium\\semester 7\\gis programmierung\\jts\\data"); // !!!! EINMAL OBEN DEFINIEREN
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Shapefiles", "shp");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            window.setStatus("Shapefile-Dateiauswahl abgebrochen");
            return;
        }
        // Layer-Auswahl
        int n = window.getMap().layers().size() - 1;
        Layer layer = window.getMap().layers().get(n);
        if (!(layer instanceof FeatureLayer)) {
            window.setStatus("Export abgebrochen");
            return;
        }
        FeatureLayer fLayer = (FeatureLayer) layer;
        try {
            // Zugriff auf die Simple Features
            SimpleFeatureCollection coll = fLayer.getSimpleFeatureSource().getFeatures();
            File file = fileChooser.getSelectedFile();
            ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
            // SimpleFeatureType bekanntgeben
            store.createSchema(coll.getSchema());
            // Cast von SimpleFeatureSource zu SimpleFeatureStore
            SimpleFeatureStore featureStore = (SimpleFeatureStore) store.getFeatureSource();
            // Transaktion beginnen
            try (Transaction t = new DefaultTransaction()) {
                featureStore.setTransaction(t);
                // Features hinzufügen
                featureStore.addFeatures(coll);
                // Transaktion beenden
                t.commit();
            }
            // Datei schliessen
            store.dispose();
            // ShapefileDumper dumper = new ShapefileDumper(file.getParentFile());
            // dumper.dump(file.getName(), coll);
            window.setStatus("Shapefile " + file.getName() + " eingelesen.");
        } catch (

                Exception ex) {
            ex.printStackTrace();
            window.setStatus("FEHLER beim Shapefile Export");
        }
    }
}
