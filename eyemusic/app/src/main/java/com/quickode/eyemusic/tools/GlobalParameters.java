package com.quickode.eyemusic.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.HashMap;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.models.AppSettings;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;

public class GlobalParameters {


	private static final String TAG = "GlobalParameters";


	private static GlobalParameters _insatnce=null;
	private final static String TRAINING="Training_images";

	public float BRIGHTNESS=(float) 0.9;
	public float DARKNESS=(float) 0.05;
	public float SITURATION=(float) 0.13;
	private Context mContext;

	private static final double SAMPLE_RATE = 44100;
	private static final double BYTE_PER_SAMPLE = 2.0;
	private static final double MILI_SECOND_IN_SECOND = 1000.0;
	private static final double BYTE_IN_MILI_SECOND = (SAMPLE_RATE * BYTE_PER_SAMPLE)/ MILI_SECOND_IN_SECOND;
	private static final int MAX_DURATION=105;
	private static final int TOTAL_BYTES=(int) (MAX_DURATION*BYTE_IN_MILI_SECOND*CommonConstans.IMAGE_HEIGHT)+1;// +1 to avoid null pointer in end case
	private int bytesInPixel;
	//public static  String[] trainingFiles;
	private HashMap<String, byte[]> mSoundsFiles;
	public HashMap<String, TrainingNode> mTrainingFiles;

	private GlobalParameters(Context context){
		mContext=context;

		mTrainingFiles=new HashMap<String, TrainingNode>();
		downloadFiles();
		loadFiles();
	}

	
	private void downloadFiles(){
		
		   File file = new File(mContext.getExternalFilesDir(null)
	                .getAbsolutePath());
		   if (file.list().length==0){
		try {
	        ZipResourceFile expansionFile = APKExpansionSupport
	                .getAPKExpansionZipFile(mContext, 1, 0);

	        ZipEntryRO[] zip = expansionFile.getAllEntries();
	        Log.e("", "zip[0].isUncompressed() : " + zip[0].isUncompressed());
	        Log.e("",
	                "mFile.getAbsolutePath() : "
	                        + zip[0].mFile.getAbsolutePath()); 
	        Log.e("", "mFileName : " + zip[0].mFileName);
	        Log.e("", "mZipFileName : " + zip[0].mZipFileName);
	        Log.e("", "mCompressedLength : " + zip[0].mCompressedLength);

	     
	        (new ZipHelper()).unzip(zip[0].mZipFileName, file);

	        if (file.exists()) {
	            Log.e("", "unzipped : " + file.getAbsolutePath());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		   }
	}
	private void loadFiles(){
	
		mSoundsFiles=new HashMap<String, byte[]>();
		File file = new File(mContext.getExternalFilesDir(null)
                .getAbsolutePath() + "");
		
		
	
		File files[] = file.listFiles();
//		Resources res= mContext.getResources();
//		Field[] fields=R.raw.class.getFields();
		String fileName;
//		for(int count=0; count < fields.length; count++){
//		for (int count=0;count<files.length;count++){
//			fileName=(fields[count].getName());
		for (File f:files){
			fileName=f.getName();
			if (!fileName.equals("gtm_analytics")){
				if (fileName.equals("scilence")){
					Log.d("DEBUG","put scilence");
				}
				mSoundsFiles.put(fileName, FileToArrayOfBytes(fileName));
			}

		}


	}
	int i=0;
	public byte[] FileToArrayOfBytes(String fileName)
	{

//		Resources res = mContext.getResources();
//		int resId=res.getIdentifier("raw/"+fileName, "raw", mContext.getPackageName());
//		AssetFileDescriptor afd =res.openRawResourceFd(resId);
//
//		byte[] bFile = new byte[(int) afd.getLength()];
//		try {
//
//			InputStream ins = res.openRawResource(
//					res.getIdentifier("raw/"+fileName,
//							"raw", mContext.getPackageName()));
//			FileInputStream fileInputStream=afd.createInputStream();
		
		
		File file = new File(mContext.getExternalFilesDir(null)
                .getAbsolutePath() );
		File files[] = file.listFiles();
		File curFile = null;
		
		for (File f:files){
			if (f.getName().equals(fileName)){
				curFile=f;
			}
		}
		byte[] bFile = new byte[(int) curFile.length()];
			
			try{
				FileInputStream fileInputStream=new FileInputStream(curFile);
			//convert file into array of bytes

			int wasRead=fileInputStream.read(bFile);
			if(wasRead!=bFile.length){
				Log.e(TAG, "failed to read the file: "+fileName);
			}
			fileInputStream.close();
			
//			ins.close();
//			afd.close();

		}catch(Exception e){
			e.printStackTrace();
		}



		return bFile;
	}


//	public ZipResourceFile getExpansionFile() {
//
//		String fileName = Helpers.getExpansionAPKFileName(this, false, 1);
//
//		        int filesize = 445461159;
//		if (Helpers.doesFileExist(this, fileName, , false)) {
//
//		    try {
//		        return APKExpansionSupport.getAPKExpansionZipFile(
//		                getBaseContext(), 1, 1);
//
//		    } catch (IOException e) {
//		        // TODO Auto-generated catch block
//
//		        e.printStackTrace();
//		    }
//		}
//		return null;       }




	public static GlobalParameters getInstance(Context context){
		if (_insatnce==null){
			_insatnce=new GlobalParameters(context);
		}
		return _insatnce;
	}



	public HashMap<String, byte[]> getFiles(){
		return mSoundsFiles;
	}


	public float getBRIGHTNESS() {
		return BRIGHTNESS;
	}
	public void setBRIGHTNESS(float bRIGHTNESS) {
		BRIGHTNESS = bRIGHTNESS;
	}
	public float getDARKNESS() {
		return DARKNESS;
	}
	public void setDARKNESS(float dARKNESS) {
		DARKNESS = dARKNESS;
	}
	public float getSITURATION() {
		return SITURATION;
	}
	public void setSITURATION(float sITURATION) {
		SITURATION = sITURATION;
	}
}
