package de.geosearchef.hnsdroid.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LocationType {
	HIDER("h"), SEEKER("s");//fake point, resource, ....

	private final String key;

	public static LocationType fromKey(String k) {
		for(LocationType value : LocationType.values()) {
			if(value.getKey().equals(k)) {
				return value;
			}
		}

		return null;
	}
}
