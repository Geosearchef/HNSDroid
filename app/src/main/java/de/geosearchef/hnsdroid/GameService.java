package de.geosearchef.hnsdroid;

import de.geosearchef.hnsdroid.data.GameConfig;

public class GameService {

	public static int playerId;
	public static String username;
	public static String uuid;

	public static GameConfig gameConfig;
	public static String gameTitle;
	public static int gameKey;

	public static void onJoinGame(String gameTitle, int gameKey, GameConfig gameConfig) {
		GameService.gameTitle = gameTitle;
		GameService.gameKey = gameKey;
		GameService.gameConfig = gameConfig;

		//TODO: START/STOP THREADS
	}
}
