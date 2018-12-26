package de.geosearchef.hnsdroid.toolbox;

public abstract class Callback<T>  {

	public abstract void onSuccess(T t);
	public void onSuccess() {
		onSuccess(null);
	}

	public void onFailure(Exception e) {

	}
}
