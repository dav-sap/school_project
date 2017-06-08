package com.quickode.eyemusic.tools;

import java.util.ArrayList;

public class Publisher implements finishAnalyze {

	private static Publisher theInstance = null;
	private ArrayList<Observer> notificationObservers;

	private Publisher() {
		notificationObservers = new ArrayList<Observer>();

	}

	public static Publisher getInstance() {

		if (theInstance == null) {
			theInstance = new Publisher();

		}
		return theInstance;
	}

	@Override
	public void addObserver(Observer notificationObserver) {
		if (!notificationObservers.contains(notificationObserver)) {
			notificationObservers.add(notificationObserver);
		}

	}

	@Override
	public void removeObserver(Observer notificationObserver) {
		notificationObservers.remove(notificationObserver);
	}

	@Override
	public void notifyAllObservers() {
		for (Observer observer : notificationObservers) {
			observer.onUpdate();
			//observer.onUpdateForHomePage();
		}

	}

	
	@Override
	public void clearObservers() {
		notificationObservers.clear();

	}

	
}
