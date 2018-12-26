package de.geosearchef.hnsdroid.toolbox;

import android.app.ProgressDialog;
import android.content.Context;

public class Toolbox {

	//TODO: use pooling?
	public static void runAsync(Runnable runnable) {
		new Thread(runnable).start();
	}


	public static ProgressDialog generateProgressDialog(Context context, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		return progressDialog;
	}
}
