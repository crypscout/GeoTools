package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.swing.dialog.JCRSChooser;
import org.locationtech.jts.geom.Geometry;

import java.awt.event.ActionEvent;

public class TransformCoordinatesAction extends AbstractAction {

    public TransformCoordinatesAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (window.getMap().layers().isEmpty()) {
            window.setStatus("Transformation der Koordinaten nicht möglich.");
            return;
        }
        // Layer-Auswahl
        int n = window.getMap().layers().size() - 1;
        Layer layer = window.getMap().layers().get(n);
        if (!(layer instanceof FeatureLayer)) {
            window.setStatus("Transformation abgebrochen");
            return;
        }

        // Auswahl Ziel-KBS
        CoordinateReferenceSystem targetCRS = JCRSChooser.showDialog();
        if (targetCRS == null) {
            window.setStatus("Transformation abgebrochen, weil kein Zielsystem ausgewählt");
            return;
        }
        try {
            // notwendige Objekte erzeugen
            FeatureLayer fLayer = (FeatureLayer) layer;
            SimpleFeatureType fType = fLayer.getSimpleFeatureSource().getSchema();
            CoordinateReferenceSystem sourceCRS = fType.getCoordinateReferenceSystem();
            MathTransform mtr = CRS.findMathTransform(sourceCRS, targetCRS);
            SimpleFeatureType newType = SimpleFeatureTypeBuilder.retype(fType, targetCRS);
            MemoryFeatureCollection newColl = new MemoryFeatureCollection(newType);
            // Über alle Features des Layer iterieren

            SimpleFeatureCollection coll = fLayer.getSimpleFeatureSource().getFeatures();
            try (SimpleFeatureIterator it = coll.features()) {
                while (it.hasNext()) {
                    SimpleFeature f = it.next();
                    Geometry geom = (Geometry) f.getDefaultGeometry();
                    // Geometry transformieren
                    Geometry tGeom = JTS.transform(geom, mtr);
                    // passende Feature erstellen
                    SimpleFeature newF = SimpleFeatureBuilder.retype(f, newType);
                    newF.setDefaultGeometry(tGeom);
                    newColl.add(newF);
                }
            }
            // neues Layer hinzufügen
            FeatureLayer newLayer = new FeatureLayer(newColl, layer.getStyle());
            window.getMap().addLayer(newLayer);
            window.setStatus("Transformation erfolgreich durchgeführt.");
        } catch (Exception ex) {
            ex.printStackTrace();
            window.setStatus("FEHLER BEI DER TRANSFORMATION." + ex.getLocalizedMessage());
        }
    }
}
