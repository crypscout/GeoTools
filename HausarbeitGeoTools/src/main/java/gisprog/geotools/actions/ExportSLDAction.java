package gisprog.geotools.actions;

import gisprog.geotools.view.HGTWindow;
import org.geotools.api.style.Style;
import org.geotools.xml.styling.SLDTransformer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

public class ExportSLDAction extends AbstractAction {

    public ExportSLDAction(HGTWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (window.getMap().layers().isEmpty()) {
            window.setStatus("SLD exportieren nicht möglich.");
            return;
        }
        // Dateiauswahl
        window.setStatus("Datei auswählen...");
        JFileChooser fileChooser = new JFileChooser(
                "C:\\Users\\jessi\\studium\\semester 7\\gis programmierung\\jts\\data"); // !!!! EINMAL OBEN DEFINIEREN
        FileNameExtensionFilter filter = new FileNameExtensionFilter("SLD-Dateien", "sld");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.CANCEL_OPTION) {
            window.setStatus("SLD-Dateiauswahl abgebrochen");
            return;
        }
        // Style auswählen
        int n = window.getMap().layers().size() - 1;
        Style style = window.getMap().layers().get(n).getStyle();
        // Style speichern
        try (PrintWriter pw = new PrintWriter(fileChooser.getSelectedFile())) {
            SLDTransformer sldt = new SLDTransformer();
            sldt.setIndentation(4); // Einrückung festlegen
            sldt.transform(style, pw); // Ausgabe auf PrintWriter
            window.setStatus("SLD erfolgreich exportiert");
        } catch (Exception ex) {
            ex.printStackTrace();
            window.setStatus("FEHLER beim SLD Export");
        }
    }
}
