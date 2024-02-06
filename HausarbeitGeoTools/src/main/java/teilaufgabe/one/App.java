package teilaufgabe.one;

import lombok.extern.java.Log;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

@Log
public class App {

    private static final String EINWOHNERZAHL = "EWZ_GER"; // "EWZ"
    private static final int targetEinwohnerdichte = 1700;


    public static void main(String[] args) {

        // load shapefiles
        Optional<SimpleFeatureCollection> siedlungenCollection = ShapefileLoader.loadShapefile("/Siedlung/sie01_f.shp");
        Optional<SimpleFeatureCollection> ortslagenCollection = ShapefileLoader.loadShapefile("/Ortslage/GN250_p_Ortslage.shp");
        if (siedlungenCollection.isEmpty() || ortslagenCollection.isEmpty()) {
            log.warning("Could not load shapefiles");
            return;
        }
        log.info("Shapefiles loaded");

        SimpleFeatureCollection siedlungenNiedersachsen = siedlungenCollection.get();
        SimpleFeatureCollection ortslagenDeutschland = ortslagenCollection.get();

        // filter for Niedersachsen
        MemoryFeatureCollection ortslagenNiedersachsen = filterByBundesland(ortslagenDeutschland, "Niedersachsen");
        log.info("Ortslagen nach Niedersachsen gefiltert");

        MemoryFeatureCollection mergedCollection = mergeFeatureCollections(siedlungenNiedersachsen, ortslagenNiedersachsen);


        mergedCollection = addBKGBufferData(mergedCollection, ortslagenNiedersachsen);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Shapefiles", "shp"));
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveShapefile(mergedCollection, file);
        }
    }


    private static MemoryFeatureCollection mergeFeatureCollections(SimpleFeatureCollection siedlungen, SimpleFeatureCollection ortslagen) {
        // merge featureTypes of both collections
        SimpleFeatureTypeBuilder sftBuilder = new SimpleFeatureTypeBuilder();
        sftBuilder.setName("MergedFeatureType");
        // übernehme alle Attribute von siedlungen
        sftBuilder.init(siedlungen.getSchema());
        // Erstelle Attribut für Einwohnerzahl
        sftBuilder.add(EINWOHNERZAHL, String.class);
        final SimpleFeatureType TYPE = sftBuilder.buildFeatureType();


        MemoryFeatureCollection mergedCollection = new MemoryFeatureCollection(TYPE);


        // Lade die Anzahl der Einwohner der Ortslagen in die Siedlungen
        try (SimpleFeatureIterator iterator = siedlungen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                SimpleFeature mergedFeature = SimpleFeatureBuilder.retype(feature, TYPE);

                int einwohnerzahl = getEinwohnerZahl(feature, ortslagen);
                mergedFeature.setAttribute(EINWOHNERZAHL, Integer.toString(einwohnerzahl));

                mergedCollection.add(mergedFeature);
            }
        }
        log.info("Einwohnerzahl durch BKG geladen");

        // Lade die Anzahl der Einwohner der angrenzenden Siedlungen in die Siedlungen
        MemoryFeatureCollection resultCollection = new MemoryFeatureCollection(TYPE);
        try (SimpleFeatureIterator iterator = mergedCollection.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                int einwohnerzahl = getEinwohnerZahlDurchAngrenzendeSiedlungen(feature, mergedCollection);
                feature.setAttribute(EINWOHNERZAHL, Integer.toString(einwohnerzahl));
                resultCollection.add(feature);
            }
        }
        log.info("Einwohnerzahl durch anliegende Siedlungen geladen");


        return mergedCollection;
    }

    private static int getEinwohnerZahl(SimpleFeature siedlung, SimpleFeatureCollection ortslagen) {
        int sum = 0;
        MultiPolygon siedlungGeom = (MultiPolygon) siedlung.getAttribute("the_geom");
        try (SimpleFeatureIterator iterator = ortslagen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature ortslage = iterator.next();
                Point ortslageGeom = (Point) ortslage.getAttribute("the_geom");
                if (siedlungGeom.contains(ortslageGeom)) {
                    sum += Integer.parseInt((String) ortslage.getAttribute(EINWOHNERZAHL));
                }
            }
        }
        return sum;
    }

    private static int getEinwohnerZahlDurchAngrenzendeSiedlungen(SimpleFeature siedlung, SimpleFeatureCollection siedlungen) {
        int sum = 0;
        double area = 0;
        MultiPolygon siedlungGeom = (MultiPolygon) siedlung.getAttribute("the_geom");
        try (SimpleFeatureIterator iterator = siedlungen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature otherSiedlung = iterator.next();
                MultiPolygon otherSiedlungGeom = (MultiPolygon) otherSiedlung.getAttribute("the_geom");
                int count = Integer.parseInt((String) otherSiedlung.getAttribute(EINWOHNERZAHL));
                // nur wenn die anliegende Siedlung auch Einwohner hat
                if (count > 0 && siedlungGeom.touches(otherSiedlungGeom)) {
                    sum += count;
                    area += otherSiedlungGeom.getArea();
                }
            }
        }
        if (area == 0 || sum == 0) return (int) (targetEinwohnerdichte * siedlungGeom.getArea());
        double einwohnerdichte = sum / area;
        return (int) (einwohnerdichte * siedlungGeom.getArea());
    }


    private static MemoryFeatureCollection filterByBundesland(SimpleFeatureCollection collection, String bundesland) {
        MemoryFeatureCollection filteredCollection = new MemoryFeatureCollection(collection.getSchema());
        try (SimpleFeatureIterator iterator = collection.features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                if (feature.getAttribute("BUNDESLAND").equals(bundesland)) {
                    filteredCollection.add(feature);
                }
            }
        }
        return filteredCollection;
    }


    private static MemoryFeatureCollection addBKGBufferData(SimpleFeatureCollection merged, SimpleFeatureCollection ortslagen) {
        // find all bkg not contained in mergedCollection
        MemoryFeatureCollection resultCollection = new MemoryFeatureCollection(merged.getSchema());
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(merged.getSchema());

        try (SimpleFeatureIterator iterator = ortslagen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature ortslage = iterator.next();
                Point ortslageGeom = (Point) ortslage.getAttribute("the_geom");
                if (!contains(merged, ortslageGeom)) {
                    // baue Puffer um ortslage
                    int einwohnerzahl = Integer.parseInt((String) ortslage.getAttribute(EINWOHNERZAHL));
                    double bufferRadius = Math.sqrt(einwohnerzahl / (Math.PI * targetEinwohnerdichte));
                    Point point = (Point) ortslage.getDefaultGeometry();
                    Polygon bufferZone = (Polygon) point.buffer(bufferRadius);
                    Polygon resultPolygon = trim(bufferZone, merged);
                    if (resultPolygon != null) {

                        SimpleFeature feature = builder.buildFeature(null);
                        feature.setAttribute("the_geom", resultPolygon);
                        feature.setAttribute(EINWOHNERZAHL, Integer.toString(einwohnerzahl));
                        resultCollection.add(feature);
                    }
                }
            }
        }
        log.info("BKG Daten mit Puffer geladen");

        // vereinige resultCollection mit mergedCollection
        try (SimpleFeatureIterator iterator = merged.features()) {
            while (iterator.hasNext()) {
                resultCollection.add(iterator.next());
            }
        }
        log.info("Datensätze vereinigt");

        return resultCollection;
    }


    private static Polygon trim(Polygon bufferZone, SimpleFeatureCollection siedlungen) {
        // trimme bufferZone
        try (SimpleFeatureIterator iterator = siedlungen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature siedlung = iterator.next();
                MultiPolygon siedlungGeom = (MultiPolygon) siedlung.getAttribute("the_geom");
                if (bufferZone.contains(siedlungGeom)) {
                    bufferZone = (Polygon) bufferZone.difference(siedlungGeom);
                }
            }
        }
        return bufferZone;
    }

    private static boolean contains(SimpleFeatureCollection siedlungen, Point ortslageGeom) {
        try (SimpleFeatureIterator iterator = siedlungen.features()) {
            while (iterator.hasNext()) {
                SimpleFeature siedlung = iterator.next();
                MultiPolygon siedlungGeom = (MultiPolygon) siedlung.getAttribute("the_geom");
                if (siedlungGeom.contains(ortslageGeom)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void saveShapefile(SimpleFeatureCollection collection, File file) {
        try {
            ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
            // SimpleFeatureType bekanntgeben
            store.createSchema(collection.getSchema());
            // Cast von SimpleFeatureSource zu SimpleFeatureStore
            SimpleFeatureStore featureStore = (SimpleFeatureStore) store.getFeatureSource();
            // Transaktion beginnen
            try (Transaction t = new DefaultTransaction()) {
                featureStore.setTransaction(t);
                // Features hinzufügen
                featureStore.addFeatures(collection);
                // Transaktion beenden
                t.commit();
            }
            // Datei schliessen
            store.dispose();
        } catch (Exception e) {
            log.warning("FEHLER beim Shapefile Export");
        }
    }


}
