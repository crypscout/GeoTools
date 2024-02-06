package gisprog.geotools.model.stations;

import java.util.List;

//besitzt automtisch getter und setter
public record Data(List<Station> getStammdatenResult) {
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Station s : getStammdatenResult) {
			sb.append(s.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
