package de.geosearchef.hnsdroid.data;

import lombok.Data;

@Data
public class GameConfig {
	private final GameOptions gameOptions;
	private final int locationUpdateInterval;
}
