package gisprog.geotools;

import gisprog.geotools.view.HGTWindow;

import java.awt.*;

public class App {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                System.setProperty("org.geotools.referencing.forceXY", "true");
                new HGTWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
