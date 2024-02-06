package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.style.Style;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.geotools.swing.table.FeatureCollectionTableModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class ReadShapeAction extends AbstractAction {

    public ReadShapeAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.setStatus("Datei auswählen...");
        // Dateiauswahl
        JFileChooser fileChooser = new JFileChooser(
                "C:\\Users\\jessi\\studium\\semester 7\\gis programmierung\\hausarbeit\\data");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Shapefiles", "shp");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            window.setStatus("Shapefile-Dateiauswahl abgebrochen");
            return;
        }
        try {
            File file = fileChooser.getSelectedFile();
            java.net.URL shapeURL = file.toURI().toURL();
            ShapefileDataStore store = new ShapefileDataStore(shapeURL);
            store.setTryCPGFile(true);
            // String[] typeNames = store.getTypeNames();

            SimpleFeatureSource fSource = store.getFeatureSource();
            SimpleFeatureCollection sfColl = fSource.getFeatures();
            System.out.println("Anzahl: " + sfColl.size());
            try (SimpleFeatureIterator it = sfColl.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                }
            }
//			store.dispose(); // danach kein weiterer Zugriff auf die Daten
            // Featurelayer erzeugen
            // als Layer hinzufügen
            Style style = JSimpleStyleDialog.showDialog(window.getContentPane(), fSource.getSchema());
            Layer layer = new FeatureLayer(sfColl, style);
            window.setFctm(new FeatureCollectionTableModel((SimpleFeatureCollection) layer.getFeatureSource().getFeatures()));
            window.getMap().addLayer(layer); // oder: map.layers().add(layer);

            window.setStatus("Shapefile " + file.getName() + " eingelesen.");
        } catch (Exception ex) {
        }
    }
}
