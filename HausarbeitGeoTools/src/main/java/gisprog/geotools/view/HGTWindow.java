package gisprog.geotools.view;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapLayerTable;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.table.FeatureCollectionTableModel;

import javax.swing.*;
import java.awt.*;

public class HGTWindow extends JFrame {

    private final MapContent map = new MapContent();
    private JLabel statusLabel;
    private FeatureCollectionTableModel fctm;


    public HGTWindow() {
        initialize();
        setVisible(true);
    }

    private void initialize() {
        setBounds(100, 100, 800, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Leiste oben für Verarbeitungen
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new FileMenu(this);
        menuBar.add(fileMenu);


        JMenu werkzeugNewMenu = new ToolsMenu(this);
        menuBar.add(werkzeugNewMenu);


        statusLabel = new JLabel("Anwendung gestartet");

        // Kartenbereich
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        JMapPane mapPane = new JMapPane(map);
        JPanel mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(mapPane, BorderLayout.CENTER);
        JMapStatusBar statusBar = JMapStatusBar.createDefaultStatusBar(mapPane);
        mapPanel.add(statusBar, BorderLayout.SOUTH);
        getContentPane().add(mapPanel, BorderLayout.CENTER);
        MapLayerTable layerTab = new MapLayerTable(mapPane);
        getContentPane().add(layerTab, BorderLayout.WEST);

        // Druckknöpfe
        ButtonPanel buttonPanel = new ButtonPanel(this, mapPane);
        mapPanel.add(buttonPanel, BorderLayout.NORTH);
    }

    public MapContent getMap() {
        return map;
    }

    public FeatureCollectionTableModel getFctm() {
        return fctm;
    }

    public void setFctm(FeatureCollectionTableModel fctm) {
        this.fctm = fctm;
    }

    /**
     * Status setzen
     */
    public void setStatus(String txt) {
        System.out.println(txt);
        statusLabel.setText(" " + txt);
    }

}
