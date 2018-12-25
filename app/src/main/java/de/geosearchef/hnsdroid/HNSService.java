package de.geosearchef.hnsdroid;

import android.content.Context;
import android.content.SharedPreferences;
import de.geosearchef.hnsdroid.toolbox.Logger;
import de.geosearchef.hnsdroid.web.WebService;
import lombok.Getter;

public class HNSService {
	private static final String HNS_SHARED_PREFERENCES = "HNSPreferences";

	@Getter
	private static SharedPreferences sharedPreferences;
	@Getter
	private static Context applicationContext;

	public static void init(Context context) {
		Logger.init("HNSDroid");
		applicationContext = context;

		sharedPreferences = context.getSharedPreferences(HNS_SHARED_PREFERENCES, Context.MODE_PRIVATE);

		WebService.init();
	}
}
