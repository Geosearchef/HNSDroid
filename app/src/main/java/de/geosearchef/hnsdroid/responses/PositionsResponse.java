package de.geosearchef.hnsdroid.responses;

import de.geosearchef.hnsdroid.data.TimedLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PositionsResponse {

	private final List<TimedLocation> locations;
	private final long nextRevealTime;
	private final long gameEndTime;

	private final long currentServerTime;
	//TODO: timers, game status
}
