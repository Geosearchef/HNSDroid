package de.geosearchef.hnsdroid.web;

import com.android.volley.Request;
import de.geosearchef.hnsdroid.GameService;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.data.GameOptions;
import de.geosearchef.hnsdroid.data.Location;
import de.geosearchef.hnsdroid.data.PlayerType;
import de.geosearchef.hnsdroid.responses.JoinResponse;
import de.geosearchef.hnsdroid.responses.PositionsResponse;
import de.geosearchef.hnsdroid.responses.RegisterResponse;
import de.geosearchef.hnsdroid.toolbox.Callback;
import lombok.Getter;
import lombok.var;

import static de.geosearchef.hnsdroid.toolbox.Toolbox.runAsync;


public class WebService {

	public static String SERVER_ADDRESS_KEY = "SERVER_ADDRESS";
	public static String SERVER_PORT_KEY = "SERVER_PORT";
	public static String PREVIOUS_USERNAME_KEY = "PREVIOUS_USERNAME";

	@Getter
	private static final int DEFAULT_PORT = 29848;
	@Getter
	private static final String DEFAULT_ADDRESS = "192.168.178.34";

	@Getter
	private static String connectedServerAddress;
	@Getter
	private static int connectedServerPort;

	private static HttpTemplate template = new HttpTemplate(HNSService.getApplicationContext());

	/**
	 * Attempts to connect to the specified server
	 *
	 */
	public static void connectToServer(final String serverAddress, final int serverPort, final String username, final Callback callback) {
		if(username.equals("")) {
			callback.onFailure(new RuntimeException("Username musn't be empty"));
			return;
		}

		runAsync(new Runnable() {
			@Override
			public void run() {
				connectedServerAddress = serverAddress;
				connectedServerPort = serverPort;

				RegisterResponse response;
				try {
					response = template.<RegisterResponse>sendSyncRequest(
							"/register",
							Request.Method.POST,
							null,
							RegisterResponse.class,
							template.params(
									"username", username,
									"uuid", HNSService.getUUID()
							));
				} catch(HttpTemplate.HttpException e) {
					callback.onFailure(e);
					return;
				}

				GameService.username = username;
				GameService.uuid = HNSService.getUUID();
				GameService.playerId = response.getPlayerId();

				var prefEditor = HNSService.getSharedPreferences().edit();
				prefEditor.putString(SERVER_ADDRESS_KEY, serverAddress);
				prefEditor.putString(PREVIOUS_USERNAME_KEY, username);
				prefEditor.apply();

				callback.onSuccess();
			}
		});
	}

	public static void joinGame(final int gameKey, final PlayerType playerType, final Callback callback) {
		runAsync(new Runnable() {
			@Override
			public void run() {
				JoinResponse response;
				try {
					response = template.<JoinResponse>sendSyncRequest(
							"/joinGame",
							Request.Method.POST,
							null,
							JoinResponse.class,
							template.paramsAuthorized(
									"key", String.valueOf(gameKey),
									"playerType", playerType.getKey()
							));
				} catch(HttpTemplate.HttpException e) {
					callback.onFailure(e);
					return;
				}

				GameService.playerType = playerType;
				GameService.onJoinGame(response.getTitle(), response.getKey(), response.getGameConfig());
				callback.onSuccess();
			}
		});
	}

	public static void createGame(final String gameTitle, final PlayerType playerType, final GameOptions gameOptions, final Callback callback) {
		runAsync(new Runnable() {
			@Override
			public void run() {
				JoinResponse response;
				try {
					response = template.<JoinResponse>sendSyncRequest(
							"/createGame",
							Request.Method.POST,
							gameOptions,
							JoinResponse.class,
							template.paramsAuthorized(
									"title", gameTitle,
									"playerType", playerType.getKey()
							));
				} catch(HttpTemplate.HttpException e) {
					callback.onFailure(e);
					return;
				}

				GameService.playerType = playerType;
				GameService.onJoinGame(response.getTitle(), response.getKey(), response.getGameConfig());
				callback.onSuccess();
			}
		});
	}

	public static void ownPosition(final Location location, final String phantomName) throws HttpTemplate.HttpException {
		template.sendSyncRequest(
				"/ownPosition",
				Request.Method.POST,
				location,
				null,
				template.paramsAuthorized(
						"phantomName", phantomName
				)
		);
	}

	public static void pollPositions(final Callback callback) {
		runAsync(new Runnable() {
			@Override
			public void run() {
				PositionsResponse response;
				try {
					response = template.<PositionsResponse>sendSyncRequest(
							"/positions",
							Request.Method.GET,
							null,
							PositionsResponse.class,
							template.paramsAuthorized()
					);
				} catch(HttpTemplate.HttpException e) {
					callback.onFailure(e);
					return;
				}

				GameService.onPositionsReceived(response);

				callback.onSuccess();
			}
		});
	}


	private static String buildRoute(String route) {
		if(!route.startsWith("/")) {
			route = "/" + route;
		}
		return String.format("http://%s:%d%s", connectedServerAddress, connectedServerPort, route);
	}




	public static void init() {

	}
}
