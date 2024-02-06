package gisprog.geotools.model.measurements;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record Pegelstand(

		@JsonDeserialize(using = JavaScriptDateDeserializer.class) Date Datum, String Grundwasserstandsklasse,
		int Wert) {

	static class JavaScriptDateDeserializer extends JsonDeserializer<OffsetDateTime> {
		public JavaScriptDateDeserializer() {
		}

		private final Pattern JAVASCRIPT_DATE = Pattern.compile("/Date\\((-?\\d+)([+-]\\d+)\\)/");

		@Override
		public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String value = p.getValueAsString();
			Matcher matcher = JAVASCRIPT_DATE.matcher(value);
			if (matcher.matches()) {
				String epoch = matcher.group(1);
				String offset = matcher.group(2);

				Instant instant = Instant.ofEpochMilli(Long.parseLong(epoch));

				return OffsetDateTime.ofInstant(instant, ZoneOffset.of(offset));
			}

			return null;
		}
	}
}
