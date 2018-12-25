package de.geosearchef.hnsdroid.web;

import android.annotation.SuppressLint;
import de.geosearchef.hnsdroid.HNSService;
import de.geosearchef.hnsdroid.toolbox.Callback;
import lombok.Getter;
import lombok.var;

import static de.geosearchef.hnsdroid.toolbox.Toolbox.runAsync;


public class WebService {

	public static String SERVER_ADDRESS_KEY = "SERVER_ADDRESS";
	public static String SERVER_PORT_KEY = "SERVER_PORT";
	public static String PREVIOUS_USERNAME_KEY = "PREVIOUS_USERNAME";

	@Getter
	private static final int DEFAULT_PORT = 28140;

	@Getter
	private static String connectedServerAddress;
	@Getter
	private static int connectedServerPort;

	private static HttpTemplate template = new HttpTemplate(HNSService.getApplicationContext());

	/**
	 * Attempts to connect to the specified server
	 *
	 */
	public static void connectToServer(final String serverAddress, final int serverPort, final String username, final Callback<Object> callback) {
		//TODO: configurable port

		runAsync(new Runnable() {
			@Override
			public void run() {
				connectedServerAddress = serverAddress;
				connectedServerPort = serverPort;




				callback.onSuccess(null);

				//TODO: on success
				var prefEditor = HNSService.getSharedPreferences().edit();
				prefEditor.putString(SERVER_ADDRESS_KEY, serverAddress);
				prefEditor.putString(PREVIOUS_USERNAME_KEY, username);
				prefEditor.apply();

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
		initPrefences();
	}

	@SuppressLint("ApplySharedPref")
	private static void initPrefences() {
		var prefs = HNSService.getSharedPreferences();
		var editor = prefs.edit();

		editor.putString(SERVER_ADDRESS_KEY, "geosearchef.de");
		editor.putInt(SERVER_PORT_KEY, 29848);

		editor.commit();
	}
}
