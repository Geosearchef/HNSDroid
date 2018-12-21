package de.geosearchef.hnsdroid.toolbox;

import com.google.gson.Gson;
import unirest.ObjectMapper;

public class GsonObjectMapper implements ObjectMapper {
	private final Gson gson = new Gson();

	@Override
	public <T> T readValue(String value, Class<T> valueType) {
		return gson.fromJson(value, valueType);
	}

	@Override
	public String writeValue(Object value) {
		return gson.toJson(value);
	}
}
