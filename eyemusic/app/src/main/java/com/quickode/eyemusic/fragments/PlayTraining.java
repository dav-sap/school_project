package com.quickode.eyemusic.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.interfaces.stopPlayLisetner;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.models.SoundPlayManager;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.Utils;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class PlayTraining extends playMusic implements TextToSpeech.OnInitListener{
	
	private String mPath;
	private String mName;
	private String[] mList;
	private int mPosiotion;
	private TextToSpeech tts;
	Bitmap photo;
	Handler handler;
	Runnable runner;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Bundle bundle=getArguments();
		mPath=bundle.getString("PATH");
		//mName=bundle.getString("NAME");
		mList=bundle.getStringArray("LIST");
		mSettings=(AppSettings) bundle.getSerializable("SETTINGS");
		mPosiotion=bundle.getInt("POSITION");
		
		if (mPosiotion==-1){
			shuffleArray(mList);
			mPosiotion=0;
		}
		   setupspeak();
		
	
handler= new Handler();
runner= new Runnable(){
    public void run(){
    	analayzeFromBegining=true;
        	loadPic();
        
    }
};
		/*try {
			mList=getActivity().getAssets().list(mPath);
		} catch (IOException e) {

			e.printStackTrace();
		}*/
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater,container,savedInstanceState);
		((LinearLayout)mView.findViewById(R.id.back_forward_buttons)).setVisibility(View.VISIBLE);
		
		if (!TextUtils.isEmpty(mName)){
			((MainActivity)getActivity()).getActionBar().setTitle(Utils.parseImgName(mName));
		}
		
		
		initButtons();
		return mView;
	}
	
	 @Override
	public void onResume() {
	        super.onResume();
	        
	     

	    }
	 
	private void setupspeak() {

			tts  = new TextToSpeech(this.getActivity(), this);
	        tts.setLanguage(Locale.US);
	    }

	
	private void initButtons(){
		
	final ImageView read = (ImageView)mView.findViewById(R.id.right_btn);
	read.setContentDescription(getResources().getString(R.string.file_name));
	read.setImageResource(R.drawable.read_btn);
		read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				if (!toStop){
//					//stop current play
//					play.performClick();
//				}
				performPauseClick();
				analayzeFromBegining=true;
				//cancelTask();
				tts.speak(Utils.parseImgName(mName), TextToSpeech.QUEUE_FLUSH, null);
				


			}
		});
		
		((ImageView)mView.findViewById(R.id.back_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
//				if (!toStop){
//					//stop current play
//					play.performClick();
//				}
				performPauseClick();
				
				if (mPosiotion>0){
					mPosiotion--;
					
					loadPicInAsec();
					
					
					
					

				}
			}
		});
		((ImageView)mView.findViewById(R.id.forward_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				if (!toStop){
//					//stop current play
//					play.performClick();
//				}
				performPauseClick();
				
				if (mPosiotion<mList.length-1){
					mPosiotion++;
					loadPicInAsec();
				}
			}
		});
	}
	
	
	private void loadPicInAsec(){
		
//		final long delay =500;
		
//		handler.postDelayed(runner, delay);
		final Handler handler = new Handler();
		final long delay =500;

		
		    handler.postDelayed(new Runnable(){
		        public void run(){
		        	analayzeFromBegining=true;
		            	loadPic();
		            
		        }
		    }, delay);
	}
	@SuppressLint("NewApi")
	protected void loadPic(){
		
		mName=mList[mPosiotion];
		((MainActivity)getActivity()).getActionBar().setTitle(Utils.parseImgName(mName));
		InputStream ims=null;
		try {
			ims = getActivity().getAssets().open(mPath+"/"+mName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// load image as Drawable

	
	//fromTheBegining=true;
		Bitmap photo= ((BitmapDrawable)Drawable.createFromStream(ims, null)).getBitmap();
		//mPic= Bitmap.createScaledBitmap(mPic, CommonConstans.IMAGE_WIDTH, CommonConstans.IMAGE_HEIGHT, true);  
//	if (runNow){
		
//	}else{
		performPlayClick(photo);
//		play.performClick();
//	}
		
	}

	
	private void shuffleArray(String[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
	protected void afterPlay(){
		super.afterPlay();
		
		if( AppSettings.isRead() ){
		
			tts.speak(Utils.parseImgName(mName), TextToSpeech.QUEUE_FLUSH, null);
			}
		
	}
	
	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {
	
			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} 
			//play.performClick();
			analayzeFromBegining=true;
			loadPic();

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}


    @Override
	public void onDestroy() {
        super.onDestroy();
       
        if (tts!=null) {
            tts.shutdown();
            tts=null;
        }
    }



}
