package gisprog.geotools.actions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class Util {

    /*
     * Methode, um die URL richtig aufzustellen (Formatierung)
     */
    public static URL buildURL(String path, Map<String, String> query)
            throws URISyntaxException, MalformedURLException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : query.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1); // remove last '&'
        String queryString = sb.toString();

        URI uri = new URI(path);
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), queryString, uri.getFragment()).toURL();
    }
}
