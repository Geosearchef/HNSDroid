package de.geosearchef.hnsdroid;

import de.geosearchef.hnsdroid.activities.GameMapFragment;
import de.geosearchef.hnsdroid.data.GameConfig;
import de.geosearchef.hnsdroid.data.Location;
import de.geosearchef.hnsdroid.data.PlayerType;
import de.geosearchef.hnsdroid.data.TimedLocation;
import de.geosearchef.hnsdroid.responses.PositionsResponse;
import de.geosearchef.hnsdroid.toolbox.Callback;
import de.geosearchef.hnsdroid.toolbox.Logger;
import de.geosearchef.hnsdroid.web.HttpTemplate;
import de.geosearchef.hnsdroid.web.WebService;

import java.util.ArrayList;
import java.util.List;

public class GameService {

	public static volatile boolean inGame = false;
	public static int playerId;
	public static String username;
	public static String uuid;
	public static PlayerType playerType;

	public static GameConfig gameConfig;
	public static String gameTitle;
	public static int gameKey;


	public static long behindServerTime = 0;
	public static long nextRevealTime;
	public static long gameEndTime;

	public static volatile List<TimedLocation> locations = new ArrayList<>();//SYNCHRONIZE
	public static volatile GameMapFragment gameMapFragment;

	public static volatile Location phantomLocation = new Location(0, 0, 5);
	public static volatile String phantomName = "I am a phantom!";

	private static volatile UpdateThread updateThread;

	public synchronized static void onJoinGame(String gameTitle, int gameKey, GameConfig gameConfig) {
		GameService.gameTitle = gameTitle;
		GameService.gameKey = gameKey;
		GameService.gameConfig = gameConfig;

		onLeaveGame();

		inGame = true;
		updateThread = new UpdateThread();
		updateThread.start();
	}

	private synchronized static void onLeaveGame() {
		if(! inGame) {
			return;
		}

		inGame = false;

		updateThread = null;
	}

	public static void onPositionsReceived(PositionsResponse positionsResponse) {
		behindServerTime = positionsResponse.getCurrentServerTime() - System.currentTimeMillis();
		nextRevealTime = positionsResponse.getNextRevealTime();
		gameEndTime = positionsResponse.getGameEndTime();

		synchronized (locations) {
			locations.clear();
			locations.addAll(positionsResponse.getLocations());
		}

		if(gameMapFragment != null) {
			gameMapFragment.loadLocations();
		}
	}

	public static boolean isPhantom() {
		return playerType == PlayerType.PHANTOM;
	}

	private static class UpdateThread extends Thread {
		@Override
		public void run() {

			while(true) {
                if(! inGame || updateThread != this) {
                    return;
                }

                Logger.info("Sending own position");
                try {
                	if(playerType != PlayerType.PHANTOM) {
						WebService.ownPosition(LocationService.getMyLocation(), phantomName);
					} else {
                		WebService.ownPosition(phantomLocation, phantomName);
					}
				} catch(HttpTemplate.HttpException e) {
					//TODO: show a toast, this is not the UI thread
					Logger.error(e);
				}

                Logger.info("Polling positions");
                WebService.pollPositions(new Callback() {
					@Override
					public void onSuccess(Object o) {

					}
					@Override
					public void onFailure(Exception e) {
						Logger.error(e);
						//TODO: show toast, this is not a UI thread
					}
				});

				try { Thread.sleep(gameConfig.getLocationUpdateInterval()); } catch(InterruptedException e) { Logger.error(e); }
			}

		}
	}

	public static long serverTime() {
		return System.currentTimeMillis() + behindServerTime;
	}
}
