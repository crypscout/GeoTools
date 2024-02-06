package gisprog.geotools;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.style.Style;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class HGTWindow {
    FeatureCollectionTableModel fctm;
    // Kennwort Datastore: proggis
    private JFrame frmGeoToolsApp;
    private JLabel statusLabel;
    // Karte
    private MapContent map = new MapContent();


    public JFrame getFrmGeoToolsApp() {
        return frmGeoToolsApp;
    }

    public FeatureCollectionTableModel getFctm() {
        return fctm;
    }

    public void setFctm(FeatureCollectionTableModel fctm) {
        this.fctm = fctm;
    }

    public MapContent getMap() {
        return map;
    }

    void setStatus(String s) {

    }

    /*
     * GeoJson Datei einlesen
     */
    public void readGeoJson() {
        setStatus("GeoJSON-Datei auswählen...");
        // Dateiauswahl
        JFileChooser fileChooser = new JFileChooser(
                "C:\\Users\\jessi\\studium\\semester 7\\gis programmierung\\jts\\data");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("GeoJSON-Dateien", "json", "geojson");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            setStatus("GeoJSON-Dateiauswahl abgebrochen");
            return;
        }
        try {
            File file = fileChooser.getSelectedFile();
            FileInputStream in = new FileInputStream(file);
            FeatureJSON fJSON = new FeatureJSON();
            SimpleFeatureCollection sfColl = (SimpleFeatureCollection) fJSON.readFeatureCollection(in);
            System.out.println("Anzahl: " + sfColl.size());
            try (SimpleFeatureIterator it = sfColl.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                    System.out.println(f.getAttribute("name"));
                }
            }
//				store.dispose(); // danach kein weiterer Zugriff auf die Daten
            // Featurelayer erzeugen
            // als Layer hinzufügen
            Style style = JSimpleStyleDialog.showDialog(frmGeoToolsApp.getContentPane(), sfColl.getSchema());
            Layer layer = new FeatureLayer(sfColl, style);
            map.addLayer(layer); // oder: map.layers().add(layer);

            setStatus("GeoJSON " + file.getName() + " eingelesen.");
        } catch (Exception ex) {
        }
    }


    /*
     * Shapefile exportieren Layer als Shapefile exportierren
     */
    public void exportGeo() {
        if (map.layers().size() == 0) {
            setStatus("Geopackage exportieren nicht möglich.");
            return;
        }
        // Dateiauswahl
        setStatus("Geopackage auswählen...");
        JFileChooser fileChooser = new JFileChooser(
                "C:\\Users\\jessi\\studium\\semester 7\\gis programmierung\\jts\\data"); // !!!! EINMAL OBEN DEFINIEREN
        FileNameExtensionFilter filter = new FileNameExtensionFilter("GeoPackage", "gpk");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            setStatus("Geopackage-Auswahl abgebrochen");
            return;
        }
        // Layer-Auswahl
        int n = map.layers().size() - 1;
        Layer layer = map.layers().get(n);
        if (!(layer instanceof FeatureLayer)) {
            setStatus("Export abgebrochen");
            return;
        }
        FeatureLayer fLayer = (FeatureLayer) layer;
        try {
            // Zugriff auf die Simple Features
            SimpleFeatureCollection coll = fLayer.getSimpleFeatureSource().getFeatures();
            File file = fileChooser.getSelectedFile();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("dbtype", "geopkg"); // Datenbanktyp
            params.put("database", fileChooser.getSelectedFile());
            DataStore store = DataStoreFinder.getDataStore(params);
            // SimpleFeatureType bekannt geben
            store.createSchema(coll.getSchema());
            // Cast von SimpleFeatureSource zu SimpleFeatureStore
            SimpleFeatureStore featureStore = (SimpleFeatureStore) store
                    .getFeatureSource(coll.getSchema().getTypeName());
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
            setStatus("Layer in: " + file.getName() + " gespeichert");
        } catch (Exception e) {
            setStatus("Fehle beim Geopackage-Export: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    /*
     * Pufferzone berechnen
     *
     */
    public void buffer() {
        if (map.layers().size() == 0) {
            setStatus("Bercehnung der Pufferzone nicht möglich.");
            return;
        }
        // Layer-Auswahl
        int n = map.layers().size() - 1;
        Layer layer = map.layers().get(n);
        if (!(layer instanceof FeatureLayer)) {
            setStatus("Pufferzone-Berechnung abgebrochen");
            return;
        }

        // Abstand eingeben
        double dist = 2000;
        String numText = JOptionPane.showInputDialog(null, "Distanz : ", "" + dist);
        dist = Double.parseDouble(numText);

        try {
            // notwendige Objekte erzeugen
            FeatureLayer fLayer = (FeatureLayer) layer;
            SimpleFeatureType fType = fLayer.getSimpleFeatureSource().getSchema();
            CoordinateReferenceSystem sourceCRS = fType.getCoordinateReferenceSystem();
            CoordinateReferenceSystem targetCRS = CRS.decode("EPSG: 25832");
            MathTransform mtr = CRS.findMathTransform(sourceCRS, targetCRS);
            MathTransform mtrRev = CRS.findMathTransform(targetCRS, sourceCRS);
            SimpleFeatureTypeBuilder sftBuilder = new SimpleFeatureTypeBuilder();
            sftBuilder.setName(fType.getTypeName() + " Buffer");
            for (AttributeDescriptor attrDescr : fType.getAttributeDescriptors()) {
                if (attrDescr instanceof GeometryDescriptor) {
                    sftBuilder.setCRS(targetCRS);
                    sftBuilder.add("the_geom", MultiPolygon.class);
                } else
                    sftBuilder.add(attrDescr);
            }
            SimpleFeatureType newType = sftBuilder.buildFeatureType();
            MemoryFeatureCollection newColl = new MemoryFeatureCollection(newType);
            // Über alle Features des Layer iterieren

            SimpleFeatureCollection coll = fLayer.getSimpleFeatureSource().getFeatures();
            try (SimpleFeatureIterator it = coll.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                    Geometry geom = (Geometry) f.getDefaultGeometry();
                    // Puffer brechnen
                    Geometry tGeom = JTS.transform(geom, mtr);
                    Geometry bufGeom = tGeom.buffer(dist);
                    Geometry newGeom = JTS.transform(bufGeom, mtrRev);
                    // passende Feature erstellen
                    SimpleFeature newF = SimpleFeatureBuilder.retype(f, newType);
                    newF.setDefaultGeometry(newGeom);
                    newColl.add(newF);
                }
            }
            // neues Layer hinzufügen
            FeatureLayer newLayer = new FeatureLayer(newColl, layer.getStyle());
            map.addLayer(newLayer);
            setStatus("Pufferzone Berechnung erfolgreich durchgeführt.");
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("FEHLER BEI DER BERCHENUNG DER PUFFERZONE." + e.getLocalizedMessage());
        }
    }


    // Methode, um ein Feature in eine Collection hinzuzufügen
    private void addFeature(DefaultFeatureCollection featureCollection, SimpleFeature feature) {
        // das Schema von der Collection wird geholt
        SimpleFeatureType featureType = featureCollection.getSchema();
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(featureType);
        // es wird der Name geholt und geprüftt, ob das Featrue zur Samtgemeinde oder
        // Gemeinde gehört
        for (AttributeDescriptor attributeDescriptor : featureType.getAttributeDescriptors()) {
            String attributeName = attributeDescriptor.getLocalName();
            if (attributeName.equals("EWZ_GER")) {

            }
        }
        // Neues Feature wird erstellt
        SimpleFeature newFeature = builder.buildFeature(null);
        featureCollection.add(newFeature);
    }

    // Methode, für Abfrage, ob eine Collection eine Geometrie beinhaltet [1.2]
    private boolean collectionContainsGeometry(DefaultFeatureCollection featureCollection, Geometry geom) {
        SimpleFeatureIterator iter = featureCollection.features();
        while (iter.hasNext()) {
            SimpleFeature feature = iter.next();
            Geometry collectionGeom = (Geometry) feature.getDefaultGeometry();
            if (collectionGeom.contains(geom)) {
                return true;
            }
        }
        return false;
    }


}
