package gisprog.geotools.view;

import gisprog.geotools.actions.*;

import javax.swing.*;


public class FileMenu extends JMenu {

    public FileMenu(HGTWindow window) {
        super("Datei");
        JMenuItem readShapeMenuItem = new JMenuItem("Shape einlesen..");
        readShapeMenuItem.addActionListener(new ReadShapeAction(window));
        add(readShapeMenuItem);

        JMenuItem readJsonMenuItem = new JMenuItem("Json einlesen..");
        readJsonMenuItem.addActionListener(new ReadJsonAction(window));
        add(readJsonMenuItem);

        JMenuItem readWMSMenuItem = new JMenuItem("WMS einlesen..");
        readWMSMenuItem.addActionListener(new ReadWMSAction(window));
        add(readWMSMenuItem);

        JMenuItem writeShapefileMenuItem = new JMenuItem("Shapefile speichern...");
        writeShapefileMenuItem.addActionListener(new ExportShapefileAction(window));
        add(writeShapefileMenuItem);

        JMenuItem readWFSMenuItem = new JMenuItem("WFS-Layer einlesen..");
        readWFSMenuItem.addActionListener(new ReadWFSAction(window));
        add(readWFSMenuItem);

        JSeparator separator = new JSeparator();
        add(separator);

        JMenuItem exportSLDMenuItem = new JMenuItem("Export SLD...");
        exportSLDMenuItem.addActionListener(new ExportSLDAction(window));
        add(exportSLDMenuItem);
    }

}
