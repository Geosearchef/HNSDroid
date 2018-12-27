package de.geosearchef.hnsdroid.data;

import lombok.Data;

@Data
public class TimedLocation {
	private final Location location;
	private final long timestamp;
	private final String name;
	private final boolean specialColor;
//	private transient final LocationSubject locationSubject;
	private boolean revealed = false;
}
