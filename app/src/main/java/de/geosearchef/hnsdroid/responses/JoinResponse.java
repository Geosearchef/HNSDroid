package de.geosearchef.hnsdroid.responses;

import de.geosearchef.hnsdroid.data.GameConfig;
import lombok.Data;

@Data
public class JoinResponse {
	private final String title;
	private final int key;
	private final GameConfig gameConfig;
}
