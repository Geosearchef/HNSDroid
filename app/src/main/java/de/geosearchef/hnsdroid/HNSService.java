package de.geosearchef.hnsdroid;

import android.content.Context;
import android.content.SharedPreferences;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HNSService {
	private static final String HNS_SHARED_PREFERENCES = "HNSPreferences";

	@Getter
	private static SharedPreferences sharedPreferences;

	public static void init(Context context) {
		sharedPreferences = context.getSharedPreferences(HNS_SHARED_PREFERENCES, Context.MODE_PRIVATE);

		WebService.init();
	}
}
