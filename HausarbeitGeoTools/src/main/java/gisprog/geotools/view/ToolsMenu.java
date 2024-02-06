package gisprog.geotools.view;

import gisprog.geotools.actions.LocLayerAction;
import gisprog.geotools.actions.TransformCoordinatesAction;

import javax.swing.*;

public class ToolsMenu extends JMenu {

    public ToolsMenu(HGTWindow window) {
        super("Werkzeuge");
        JMenuItem pufferNewMenuItem = new JMenuItem("Koordinaten transformieren...");
        pufferNewMenuItem.addActionListener(new TransformCoordinatesAction(window));
        add(pufferNewMenuItem);

        JMenuItem locMenuItem = new JMenuItem("Ortslagen-Layer ertsllen...");
        locMenuItem.addActionListener(new LocLayerAction(window));
        add(locMenuItem);
    }
}
