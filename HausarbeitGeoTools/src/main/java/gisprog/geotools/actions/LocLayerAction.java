package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.style.Style;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.awt.event.ActionEvent;

public class LocLayerAction extends AbstractAction {


    public LocLayerAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.setStatus("Shapefile wird erstellt...");
        if (window.getMap().layers().size() < 2) {
            window.setStatus("Zu wenig Layer ausgewählt");
            return;
        }
        // Neue GeometryFactory, wo die anderen Geometrien reinkommen
        String[] attributesForMerged = {"EWZ_GER"}; // für Polygone
        SimpleFeatureTypeBuilder sftBuilder = new SimpleFeatureTypeBuilder();
        // sftBuilder.setCRS(org.geotools.referencing.crs.DefaultGeographicCRS.WGS84);

        sftBuilder.setName("MergedFeatureType");
        // es gibt sowohl Punkte, als auch Multipolygone
        sftBuilder.add("the_geom", MultiPolygon.class);
        sftBuilder.add("the_geom", Point.class);
        for (String attribute : attributesForMerged) {
            sftBuilder.add(attribute, Object.class);
        }
        sftBuilder.setName("MergedFeatureType");
        SimpleFeatureType featureType = sftBuilder.buildFeatureType();
        DefaultFeatureCollection combinedFeatures = new DefaultFeatureCollection("mergedCollection", featureType);

        try {
            // kopiere aus der anderen Shapefile die restlichen Ortslagen in den neuen Layer
            for (int i = 0; i < window.getMap().layers().size(); i++) {
                Layer layer = window.getMap().layers().get(i);
                SimpleFeatureSource source = (SimpleFeatureSource) layer.getFeatureSource();
                SimpleFeatureCollection sfc = source.getFeatures();
                SimpleFeatureIterator iter = sfc.features();
                while (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    combinedFeatures.add(feature);
                }
                iter.close();

            }
            FeatureLayer combinedLayer = new FeatureLayer(combinedFeatures, null);
            combinedLayer.setTitle("neu_Ortslage");
            window.getMap().addLayer(combinedLayer);
            printNumberOfFeatures(combinedLayer);
            try {
                window.setFctm(new FeatureCollectionTableModel(combinedFeatures));
            } catch (Exception ex) {
                window.setStatus("Da ist wohl etwas schiefgelaufen!");
            }
        } catch (Exception ex) {
            window.setStatus("Fehler beim Zusammenführen der Features");
            ex.printStackTrace();
        }
        Style style = SLD.createSimpleStyle(featureType);
        FeatureLayer combinedLayer = new FeatureLayer(combinedFeatures, style);
        combinedLayer.setTitle("neu_ortslage");

        window.setStatus("Zusammenführung abgeschlossen");
    }

    // Methode, die ausgibt, wieviele Geoemtrien (Features) erzeugt wurden [1.2]
    private void printNumberOfFeatures(FeatureLayer layer) {
        try {
            SimpleFeatureIterator iterator = (SimpleFeatureIterator) layer.getFeatureSource().getFeatures().features();
            int counter = 0;
            while (iterator.hasNext()) {
                iterator.next();
                counter++;
            }
            iterator.close();
            System.out.println(counter + "counts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
