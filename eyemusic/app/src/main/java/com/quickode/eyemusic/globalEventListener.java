package com.quickode.eyemusic;

import com.quickode.eyemusic.fragments.TrainingFolderList.RowInList;
import com.quickode.eyemusic.models.AppSettings;



public interface globalEventListener {
	
	public void onFolderSelected(RowInList row);
	public void onImageSelect(String path, String[] mList, int position);
	public void openCamera();
	public void openVideo();
	public void openTraining();
	public void openInputType();
	public void openHelp(String link);
	public void openGames();
	public void openContactUs();
	public void playTutorial();
	public void disableSettings();
	public void enableSettings();

}
