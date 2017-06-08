package com.quickode.eyemusic.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * turned into comment because was error
 */
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.google.gson.annotations.Expose;
import com.quickode.eyemusic.tools.CommonConstans;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppSettings implements Serializable {

	private String inputType;
	private  int repeat;
	private static int speed;
	private static boolean read;
	private static boolean blackAndWhite;
	private static boolean negative;
	private static boolean showOriginal;
	private static boolean showClustered;
	private static boolean cartoonize;
	private static boolean faceDetection;
    private static boolean fullBodyDetection;
    private static boolean upperBodyDetection;
	private static boolean objectDetectionCropped;
	private static boolean displayOriginal;
	public static boolean algo;
	public static boolean analyzeImg;
	public static String ip;
	public static String port;

	private transient static SharedPreferences mPrefs;


	//	public AppSettings(String inputType, int repeat){
	//		this.inputType=inputType;
	//		this.repeat=repeat;
	//		this.speed=75;
	//		this.blackAndWhite=false;
	//		this.negative=false;
	//		this.showOriginal=true;
	//		this.showClustered=true;
	//		this.read=true;
	//	}
	public AppSettings(){

	}

//	public AppSettings(String inputType,AppSettings cur,SharedPreferences prefs){
//		mPrefs=prefs;
//		if (cur==null){
//			this.inputType=inputType;
//			resetDefault();
//		}else{
//			this.inputType=inputType;
//			this.repeat=cur.repeat;
//		}
//	}

	public AppSettings(String inputType,int repeat,SharedPreferences prefs){
		mPrefs=prefs;
		this.inputType=inputType;
		if (repeat==-1){
			resetDefault();
		}else{

			this.repeat=repeat;
		}
	}

	public void resetDefault(){

		if (this.inputType.equals("Video") ){
			this.repeat=1;


		}else	if (this.inputType.equals("Camera") ){
			this.repeat=Integer.MAX_VALUE;
		}else{
			this.repeat=2;
		}

		resetStaticFields();

	}
	public static void resetStaticFields() {
		AppSettings.speed = 75;
		AppSettings.blackAndWhite = false;
		AppSettings.negative = false;
		AppSettings.cartoonize = false;
		AppSettings.faceDetection = false;
		AppSettings.objectDetectionCropped = false;
        AppSettings.displayOriginal = true;
		AppSettings.showOriginal = true;
		AppSettings.showClustered = true;
		AppSettings.read = true;
		AppSettings.algo = false;
		AppSettings.algo = false;
		AppSettings.analyzeImg = false;
		AppSettings.ip = "0.0.0.0";
		AppSettings.port = "3123";
	}

	public static boolean isRead() {
		return read;
	}

	public static void setRead(boolean read) {
		AppSettings.read = read;
		Log.d("EYE_MUSIC","read is: "+String.valueOf(read));
	}

	public String getInputType() {
		return inputType;
	}


	public void setInputType(String inputType) {
		this.inputType = inputType;
	}


	public  int getRepeat() {
		return repeat;
	}


	public  void setRepeat(int repeat) {
		this.repeat = repeat;
	}


	public static int getSpeed() {
		return speed;
	}


	public static void setSpeed(int speed) {
		AppSettings.speed = speed;
	}


	public static boolean isBlackAndWhite() {
		return blackAndWhite;
	}


	public static void setBlackAndWhite(boolean blackAndWhite) {
		AppSettings.blackAndWhite = blackAndWhite;
	}


	public static boolean isNegative() {
		return negative;
	}


	public static void setNegative(boolean negative) {
		AppSettings.negative = negative;
	}


	public static boolean isShowOriginal() {
		return showOriginal;
	}


	public static void setShowOriginal(boolean showOriginal) {
		AppSettings.showOriginal = showOriginal;
	}


	public static boolean isShowClustered() {
		return showClustered;
	}


	public static void setShowClustered(boolean showClustered) {
		AppSettings.showClustered = showClustered;
	}


	public static void increaseSpeed() {

		if (AppSettings.speed<100){
			AppSettings.speed+=5;
		}
	}

	public static void decreaseSpeed() {

		AppSettings.speed-=5;
		if (AppSettings.speed<5){
			AppSettings.speed=5;
		}
	}

    public static void setAlgo(Boolean isCheck){
        algo=isCheck;
    }

    public static boolean getAlgo(){
        return algo;
    }

    public static boolean isCartoonize() {
        return cartoonize;
    }

    public static void setCartoonize(boolean cartoonize) {
        AppSettings.cartoonize = cartoonize;
    }

    public static boolean isFaceDetection() {
        return faceDetection;
    }

    public static void setFaceDetection(boolean faceDetection) {
        AppSettings.faceDetection = faceDetection;
    }

    public static boolean isFullBodyDetection() {
        return fullBodyDetection;
    }

    public static void setFullBodyDetection(boolean fullBodyDetection) {
        AppSettings.fullBodyDetection = fullBodyDetection;
    }

    public static boolean isUpperBodyDetection() {
        return upperBodyDetection;
    }

    public static void setUpperBodyDetection(boolean upperBodyDetection) {
        AppSettings.upperBodyDetection = upperBodyDetection;
    }

    public static boolean isObjectDetectionCropped() {
        return objectDetectionCropped;
    }

    public static void setObjectDetectionCropped(boolean objectDetectionCropped) {
        AppSettings.objectDetectionCropped = objectDetectionCropped;
    }

    public static boolean isDisplayOriginal() {
        return displayOriginal;
    }
	public static void setPort(String val) {AppSettings.port = val;}
	public static void setIp(String val) {AppSettings.ip = val;}
	public static String getPort() {return AppSettings.port;}

	public static String getIp() {return AppSettings.ip;}
	public static void setAnalyzeImg(boolean val) {AppSettings.analyzeImg = val;}
	public static boolean getAnalyzeImg() {return AppSettings.analyzeImg;}
	public static void setDisplayOriginal(boolean displayOriginal) {
        AppSettings.displayOriginal = displayOriginal;
    }

	public void savePref(){

		Editor prefsEditor = mPrefs.edit();
		// Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


//		JsonObject obj= new JsonObject();
//		obj.addProperty("inputType",this.inputType);
//		obj.addProperty("repeat",this.repeat);

		//	//	if (this.inputType.equals("Training")){
		//			obj.addProperty("speed",AppSettings.speed);
		//			obj.addProperty("blackAndWhite",AppSettings.blackAndWhite);
		//			obj.addProperty("negative",AppSettings.negative);
		//			obj.addProperty("showOriginal",AppSettings.showOriginal);
		//			obj.addProperty("showClustered",AppSettings.showClustered);
		//			obj.addProperty("read",AppSettings.read);
		//
		//	//	}
		prefsEditor.putInt(inputType, repeat);
		prefsEditor.commit();

		Log.d("EYE_MUSIC","save pref name" +inputType+"  "+repeat);
	}

	public static void saveStaticPref(){
		Editor prefsEditor = mPrefs.edit();
		// Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


		JSONObject obj= new JSONObject();


		try {
			obj.put("speed",AppSettings.speed);

			obj.put("blackAndWhite",AppSettings.blackAndWhite);
			obj.put("negative",AppSettings.negative);
			obj.put("showOriginal",AppSettings.showOriginal);
			obj.put("showClustered",AppSettings.showClustered);
			obj.put("read",AppSettings.read);
			obj.put("algo",AppSettings.algo);
			obj.put("cartoonize", AppSettings.cartoonize);
			obj.put("faceDetection", AppSettings.faceDetection);
            obj.put("fullBodyDetection", AppSettings.fullBodyDetection);
            obj.put("upperBodyDetection", AppSettings.upperBodyDetection);
			obj.put("objectDetectionCropped", AppSettings.objectDetectionCropped);
            obj.put("displayOriginal",AppSettings.displayOriginal);
			obj.put("analyzeImg",AppSettings.analyzeImg);
			obj.put("ip",AppSettings.ip);
			obj.put("port",AppSettings.port);

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prefsEditor.putString(CommonConstans.STATIC_PREF, obj.toString());
		prefsEditor.commit();

		Log.d("EYE_MUSIC","save static" + "  " + obj.toString());
	}




}
