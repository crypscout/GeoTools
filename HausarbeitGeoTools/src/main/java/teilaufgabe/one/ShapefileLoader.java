package teilaufgabe.one;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShapefileLoader {


    public static Optional<SimpleFeatureCollection> loadShapefile(String name) {
        try {
            URL url = ShapefileLoader.class.getResource(name);
            if (url == null) {
                log.warning("File not found");
                return Optional.empty();
            }

            ShapefileDataStore store = new ShapefileDataStore(url);
            store.setTryCPGFile(true);
            return Optional.of(store.getFeatureSource().getFeatures());
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return Optional.empty();
    }
}
