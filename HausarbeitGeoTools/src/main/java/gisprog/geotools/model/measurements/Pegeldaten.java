package gisprog.geotools.model.measurements;

import java.util.List;

// besitzt automtisch getter und setter, deshalb der record
public record Pegeldaten(List<Datenspuren> getPegelDatenspurenChartResult) {
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Datenspuren s : getPegelDatenspurenChartResult) {
			sb.append(s.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
