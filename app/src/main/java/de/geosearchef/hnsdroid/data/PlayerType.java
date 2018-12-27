package de.geosearchef.hnsdroid.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerType {
	HIDER("h", "Hider"), SEEKER("s", "Seeker"), PHANTOM("p", "Phantom");

	private final String key;
	private final String displayName;

	public static PlayerType fromKey(String k) {
		for(PlayerType value : PlayerType.values()) {
			if(value.getKey().equals(k)) {
				return value;
			}
		}

		return null;
	}

	public static PlayerType fromDisplayName(String name) {
		for(PlayerType value : PlayerType.values()) {
			if(value.getDisplayName().equals(name)) {
				return value;
			}
		}

		return null;
	}
}
