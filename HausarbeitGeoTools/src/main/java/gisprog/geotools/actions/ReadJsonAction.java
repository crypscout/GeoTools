package gisprog.geotools.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import gisprog.geotools.model.stations.Data;
import gisprog.geotools.view.HGTWindow;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class ReadJsonAction extends AbstractAction {

    static final String URL = "https://bis.azure-api.net/PegelonlinePublic/REST/stammdaten/stationen/All";
    static final String API_KEY = "9dc05f4e3b4a43a9988d747825b39f43";

    public ReadJsonAction(HGTWindow window) {
        super(window);
    }

    /*
     * Methode, um die Daten aus der JSON Abfrage abzurufen
     */
    private static Optional<Data> loadData() {
        try {
            Map<String, String> urlParameters = Map.of("key", API_KEY);
            URL url = Util.buildURL(URL, urlParameters); // Aufbau der URL
            ObjectMapper mapper = new ObjectMapper();
            return Optional.of(mapper.readValue(url, Data.class));
        } catch (URISyntaxException | MalformedURLException e) {
            System.err.println("Problem beim Parsen der URI: " + e.getMessage());
            return Optional.empty();
        } catch (IOException e) {
            System.err.println("Problem beim Parsen von JSON: : " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        window.setStatus("JSON-Abfrage wird gelesen...");
        // URL erstellen
        Optional<Data> data = loadData();
        // Abfrage, ob Daten vorhanden sind
        if (data.isPresent()) {
            Data d = data.get();
            System.out.println(d);
            // TODO do something with data
        }
    }


}
