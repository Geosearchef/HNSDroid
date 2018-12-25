package de.geosearchef.hnsdroid.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerType {
	HIDER("h"), SEEKER("s");

	private final String key;

	public static PlayerType fromKey(String k) {
		for(PlayerType value : PlayerType.values()) {
			if(value.getKey().equals(k)) {
				return value;
			}
		}

		return null;
	}
}
