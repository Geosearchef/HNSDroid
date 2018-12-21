package de.geosearchef.hnsdroid;

import android.annotation.SuppressLint;
import de.geosearchef.hnsdroid.toolbox.CompletableFuture;
import de.geosearchef.hnsdroid.toolbox.GsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import unirest.HttpResponse;
import unirest.JsonNode;
import unirest.Unirest;


@Slf4j
public class WebService {

	public static String SERVER_ADDRESS = "SERVER_ADDRESS";
	public static String SERVER_PORT = "SERVER_PORT";

	private static String connectedServerAddress;
	private static int connectedServerPort;



	/**
	 * Attempts to connect to the specified server
	 *
	 */
	public static CompletableFuture<Integer> connectToServer(String serverAddress, int serverPort) {
		//TODO: configurable port
		connectedServerAddress = serverAddress;
		connectedServerPort = serverPort;

		return CompletableFuture.supplyAsync(() -> {
			HttpResponse<JsonNode> response = Unirest //TODO: replace object
					.post(buildRoute("/register"))
					.queryString("name", "")
					.queryString("uuid", "")
					.asJson();




			//TODO: on success
			var prefEditor = HNSService.getSharedPreferences().edit();
			prefEditor.putString(SERVER_ADDRESS, serverAddress);
			prefEditor.apply();

			//TODO:
			return null;
		});
	}


	private static String buildRoute(String route) {
		if(!route.startsWith("/")) {
			route = "/" + route;
		}
		return String.format("http://%s:%d%s", connectedServerAddress, connectedServerPort, route);
	}




	public static void init() {
		initPrefences();

		Unirest.config()
				.setObjectMapper(new GsonObjectMapper())
				.setDefaultHeader("accept", "application/json");
	}

	@SuppressLint("ApplySharedPref")
	private static void initPrefences() {
		var prefs = HNSService.getSharedPreferences();
		var editor = prefs.edit();

		editor.putString(SERVER_ADDRESS, "geosearchef.de");
		editor.putInt(SERVER_PORT, 29848);

		editor.commit();
	}
}
