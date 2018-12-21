package de.geosearchef.hnsdroid.toolbox;


import lombok.Getter;
import lombok.var;

import java.util.Collection;
import java.util.LinkedList;

public class CompletableFuture<T> {

	@Getter
	private volatile boolean completed = false;//True for completion or exceptionally

	@Getter
	private volatile T value;

	@Getter
	private volatile boolean completedExceptionally = false;

	@Getter
	private volatile RuntimeException exception = null;

	private Collection<Runnable> listeners = new LinkedList<>();
	private Collection<Consumer<Exception>> exceptionHandlers = new LinkedList<>();

	private Object lock = new Object();

	public CompletableFuture() {

	}

	public void complete(T value) {
		if(completed) {
			throw new RuntimeException("Future already completed");
		}
		this.value = value;
		completed = true;

		synchronized (lock) {
			lock.notifyAll();
		}

		for(Runnable listener : listeners) {
			Toolbox.runAsync(listener);
		}
	}

	public void completeExceptionally(Exception e) {
		if(completed) {
			throw new RuntimeException("Future already completed");
		}
		this.completedExceptionally = true;
		completed = true;

		synchronized (lock) {
			lock.notifyAll();
		}

		for(Consumer<Exception> exceptionHandler : exceptionHandlers) {
			Toolbox.runAsync(() -> exceptionHandler.accept(e));
		}
	}

	public void thenRun(Runnable runnable) {
		listeners.add(runnable);
	}

	public void thenAccept(Consumer<T> consumer) {
		listeners.add(() -> {
			consumer.accept(value);
		});
	}

	public <S> void thenApply(Function<T, S> function) {
		CompletableFuture<S> newFuture = new CompletableFuture<>();
		listeners.add(() -> {
			try {
				S newValue = function.apply(value);
				newFuture.complete(newValue);
			} catch(Exception e) {
				newFuture.completeExceptionally(e);
			}
		});
	}

	public void exceptionally(Consumer<Exception> consumer) {
		exceptionHandlers.add(consumer);
	}

	public void join() {
		synchronized (lock) {
			while(! completed) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		}
	}

	public T get() {
		join();
		return value;
	}




	public static CompletableFuture completedFuture() {
		var future = new CompletableFuture();
		future.complete(null);
		return future;
	}

	public static CompletableFuture runAsync(Runnable runnable) {
		var future = new CompletableFuture();
		Toolbox.runAsync(() -> {
			try {
				runnable.run();
				future.complete(null);
			} catch(Exception e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}

	public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
		var future = new CompletableFuture<T>();
		Toolbox.runAsync(() -> {
			try {
				T value = supplier.supply();
				future.complete(value);
			} catch(Exception e) {
				future.completeExceptionally(e);
			}
		});
		return future;
	}
}
