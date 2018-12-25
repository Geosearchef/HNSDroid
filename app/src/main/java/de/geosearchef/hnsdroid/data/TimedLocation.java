package de.geosearchef.hnsdroid.data;

import lombok.Data;

@Data
public class TimedLocation {
	private final Location location;
	private final long timestamp;
	//TODO: ignore field in gson
	private transient final LocationSubject locationSubject;
	private boolean revealed = false;
}
