package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.map.WMSLayer;
import org.geotools.swing.wms.WMSLayerChooser;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.List;

public class ReadWMSAction extends AbstractAction {
    public ReadWMSAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            /*
             * URI uri = new URI(
             * "https://www.geobasisdaten.niedersachsen.de/doorman/noauth/bestand?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.1.1"
             * ); WebMapServer wms = new WebMapServer(uri.toURL()); GetMapRequest req =
             * wms.createGetMapRequest(); // List<org.geotools.wms.Layer > wmsLayer = //
             * WMSLayer.Chooser.showSelectedLayer(wms); // // for (org.geotools.wms.Layer ly
             * : wmsLayers) { // // // } req.addLayer("ueb1000_gd", "default");
             * req.setSRS("EPSG:31467"); req.setBBox("3400000,5850000,3500000,5950000");
             * req.setDimensions(1000, 1000); // req.setVersion("1.1.0")
             * req.setFormat("image/jpeg"); GetMapResponse resp = wms.issueRequest(req);
             * System.out.println("Content Type:" + resp.getContentType()); if
             * (resp.getContentType().startsWith("image")) { InputStream stream =
             * resp.getInputStream(); BufferedImage image = ImageIO.read(stream); JLabel
             * picLabel = new JLabel(new ImageIcon(image)); JFrame frame = new JFrame();
             * frame.add(picLabel); frame.pack(); frame.setVisible(true);
             *
             * GridCoverageFactory gcf = new GridCoverageFactory(); GridCoverage2D gc =
             * gcf.create("Raster NDS", image, new ReferencedEnvelope(3400000, 3500000,
             * 5850000, 5950000, CRS.decode("EPSG:31467"))); StyleBuilder sb = new
             * StyleBuilder(); Style style = sb.createStyle(sb.createRasterSymbolizer());
             * GridCoverageLayer gcLayer = new GridCoverageLayer(gc, style);
             * map.addLayer(gcLayer); } } catch (Exception e) { e.printStackTrace();
             * System.out.println(""); }
             */
            URI uri = new URI("https://maps.dwd.de/geoserver/dwd/wms");
            WebMapServer wms = new WebMapServer(uri.toURL());
            List<Layer> wmsLayer = WMSLayerChooser.showSelectLayer(wms);
            var first = wmsLayer.get(0);
            WMSLayer wmsLayer1 = new WMSLayer(wms, first);
            window.getMap().addLayer(wmsLayer1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("");

        }
    }
}
