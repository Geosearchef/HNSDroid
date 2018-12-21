package de.geosearchef.hnsdroid.toolbox;

@FunctionalInterface
public interface Consumer<T> {
	void accept(T t);
}
