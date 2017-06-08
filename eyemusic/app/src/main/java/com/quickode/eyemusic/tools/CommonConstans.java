package com.quickode.eyemusic.tools;

public class CommonConstans {
	
	public final static int IMAGE_WIDTH = 50;
	public final static int IMAGE_HEIGHT = 30;
	
	public final static int SPEED_SLOW = 105;
	public final static int SPEED_FAST = 5;
	public final static int SPEED_NORMAL = 50;
	public static final int SPEED_BASE = 5;
	
	
	public static final String STATIC_PREF="static_pref";
	public enum InputType{
		Training, Camera, Video
	}
	
	public static final String gamesLink ="http://brain.huji.ac.il/EM_Training_dev/Mobile.html?appVersion=1.2.2&osVersion=android";
//	public static final String gamesLink ="http://brain.huji.ac.il/EM_Training/Mobile.html?appVersion=1.2&osVersion=android";



	
	
	
	public static final String gamesUrlPlay ="eyemusic://imagePlaying?action=play";
	public static final String gamesUrlStop ="eyemusic://imagePlaying?action=stop";
	public static final String helpText="<br>Welcome to the EyeMusic Mobile training page.</br><br>The lessons here will help you improve your skill with the EyeMusic while enjoying challenging games and helping us collect performance data for the next iterations of development.<br>The basic rules of the EyeMusic transformation are: <br>(1) Height is transformed into musical notes on a Blues scale.<br>(2) The x information is encoded in time, with a drum beat at the end of each frame.<br>(3) Color is encoded by different musical instruments.<br>With active internet access in your device, additional information is available on this page.";

	
	public static final String CONTACT_MAIL="amir.amedis.lab+android@gmail.com";
	public static final String ANALYITICS_PROPERTY_ID="UA-54665727-1";
}
