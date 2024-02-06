package gisprog.geotools.model.measurements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gisprog.geotools.actions.Util;

public class Wasserpegel {
	static String URL_TEMPLATE = "https://bis.azure-api.net/PegelonlinePublic/REST/chart/station/{0}/datenspuren/parameter/{1}/tage/{2}";
	static String API_KEY = "9dc05f4e3b4a43a9988d747825b39f43";
	// https://bis.azureapi.net/PegelonlinePublic/REST/chart/station/344/datenspuren/parameter/1/tage/-7?key=9dc05f4e3b4a43a9988d747825b39f43

	public static void main(String args[]) {
		loadData(344, 1, -7).ifPresent(System.out::println);
	}

	private static Optional<Pegeldaten> loadData(int STA_ID, int PAT_ID, int tage) {
		try {

			String URL = MessageFormat.format(URL_TEMPLATE, STA_ID, PAT_ID, tage);

			Map<String, String> urlParameters = Map.of("key", API_KEY);
			URL url = Util.buildURL(URL, urlParameters);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			System.out.println("checking for url: " + url.toString());
			return Optional.of(mapper.readValue(url, Pegeldaten.class));
		} catch (URISyntaxException | MalformedURLException e) {
			System.err.println("Failed to parse URI: " + e.getMessage());
			return Optional.empty();
		} catch (IOException e) {
			System.err.println("Failed to parse JSON: " + e.getMessage());
			return Optional.empty();
		}
	}



}
