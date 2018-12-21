package de.geosearchef.hnsdroid.toolbox;

public class Toolbox {

	//TODO: use pooling?
	public static void runAsync(Runnable runnable) {
		new Thread(runnable).start();
	}
}
