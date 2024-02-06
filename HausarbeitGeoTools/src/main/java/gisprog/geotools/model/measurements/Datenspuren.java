package gisprog.geotools.model.measurements;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record Datenspuren(int AktuellerMesswert, Pegelstand AktuellerPegelstand, List<Meldestufe> Meldestufen,
		List<Pegelstand> Pegelstaende) {

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AktuellerMesswert: ");
		sb.append(AktuellerMesswert);
		sb.append("\n");
		sb.append("AktuellerPegelstand: ");
		sb.append(AktuellerPegelstand);
		sb.append("\n");

		for (Meldestufe m : Meldestufen) {
			sb.append("Meldestufe: ");
			sb.append(m);
			sb.append("\n");
		}
		for (Pegelstand p : Pegelstaende) {
			sb.append("Pegelstand: ");
			sb.append(p);
			sb.append("\n");
		}

		return sb.toString();
	}

}
