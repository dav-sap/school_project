package com.quickode.eyemusic.tools;




public interface finishAnalyze {
	
	public void addObserver(Observer notificationObserver);
	public void removeObserver(Observer notificationObserver);
	public void notifyAllObservers();


	public  void clearObservers();
	

}
