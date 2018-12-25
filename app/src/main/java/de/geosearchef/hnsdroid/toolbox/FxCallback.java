package de.geosearchef.hnsdroid.toolbox;

import android.app.Activity;

public abstract class FxCallback<T> extends Callback<T> {
	public abstract void onSuccessFX(T t);

	public void onFailureFX(Exception e) {

	}

	public void onSuccess(final T t) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onSuccessFX(t);
			}
		});
	}

	public void onFailure(final Exception e) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onFailureFX(e);
			}
		});
	}

	private final Activity activity;
	public FxCallback(Activity activity) {
		this.activity = activity;
	}
}
