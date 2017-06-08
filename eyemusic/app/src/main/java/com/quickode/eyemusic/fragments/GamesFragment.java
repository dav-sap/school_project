package com.quickode.eyemusic.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.interfaces.SoundProccessListener;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.models.ImageProcessingManager;
import com.quickode.eyemusic.models.SoundPlayManager;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.Observer;
import com.quickode.eyemusic.tools.Publisher;


public class GamesFragment extends WebViewFragment implements Observer {

	private SoundPlayManager currentSpm;
	private boolean isPlaying;
	private boolean isAnalyze;

	int j=0;

	static ImageProcessingManager ipManager;
	boolean toStop;
	View toppest;
	Bitmap bitmap;
	WebView mView;
	boolean eyeMusicPlay;
	//	RelativeLayout playORPause;
	boolean forceClose;
	AsyncTask<Void, Void, Void> curTask;
	ImageView playPause;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		URL=CommonConstans.gamesLink;
		currentUrl = CommonConstans.gamesLink;
		isAnalyze=false;
		ipManager= new ImageProcessingManager(getActivity());

		//		toppest = ((ViewGroup) getActivity().getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);

	} 


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v=super.onCreateView(inflater, container, savedInstanceState);
		mCallback.enableSettings();
		mView =(WebView) v.findViewById(R.id.webPage);

		playPause=(ImageView)v.findViewById(R.id.play_pause_image);


		playPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!toStop){

					stop();
					playPause.setImageResource(R.drawable.play_full_width);
				}else{
					Publisher.getInstance().addObserver(GamesFragment.this);
					toStop=false;
					screenShot();
					playPause.setImageResource(R.drawable.pause_full_width);
				}
			}
		});

		return v;
	}

	@SuppressLint("NewApi") @Override 
	public void onResume(){

		super.onResume();
		((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.games));
		mView.setWebViewClient(new SwAWebClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(CommonConstans.gamesUrlPlay)||url.equals(CommonConstans.gamesUrlStop)){
					eyeMusicPlay=url.equals(CommonConstans.gamesUrlPlay);
					Log.d("DEBUG","eyemusic play: "+String.valueOf(eyeMusicPlay));
					if (eyeMusicPlay){
						playPause.setVisibility(View.VISIBLE);
						playPause.setImageResource(R.drawable.pause_full_width);
						playPause.setEnabled(true);
						Publisher.getInstance().addObserver(GamesFragment.this);
						toStop=false;
						Handler h = new Handler();
						h.postDelayed(new Runnable() {

							@Override
							public void run() {
								screenShot();

							}
						}, 500);

					}else{
						stop();
					}
					return true;
				}
				else{
					return false;
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				String msgConnectionError=getResources().getString(R.string.msg_connection_error);
				view.loadData(msgConnectionError, "text/plain", null);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				currentUrl=url;
				Log.d("GAMES DEBUG", "onPageFinished current url is: "+currentUrl);
				Log.d("DEBUG","on page finish");
				if (!url.equals(CommonConstans.gamesLink) ){

				}else{


					playPause.setVisibility(View.GONE);


					stop();
				}


			}
		});

		mView.evaluateJavascript("gamesDidAppear();", new ValueCallback<String>() {

			@Override
			public void onReceiveValue(String value) {
				Log.d("DEBUG","success to run JS");

			}
		});

	}




	public synchronized void screenShot() {


		if ( !isAnalyze ){
			isAnalyze=true;
			Log.d("DEBUG","take new pic");





			//			if (mView.getWidth()>0 && mView.getHeight()>0){
			final Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(),Config.ARGB_8888);  
			//				getActivity().runOnUiThread(new Runnable() {
			//					@Override
			//					public void run() {

			Canvas canvas = new Canvas(bitmap);  
			mView.draw(canvas);
			play(bitmap);
			//					}
			//				});


			//addImageToGallery(bitmap); 

			//			}else{
			//
			//				Handler handler= new Handler();
			//				handler.postDelayed(new Runnable() {
			//
			//					@Override
			//					public void run() {
			//						Log.d("DEBUG","post delay");
			//						isAnalyze=false;
			//						screenShot();
			//
			//					}
			//				}, 500);
			//
			//			
			//		}
		}
		//		

	}




	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){

	}

	private void play(final Bitmap screenShot){

		j++;

		final AsyncTask<Void, Void, Void>  task=new AsyncTask<Void, Void, Void>() {

			int i=j;
			@SuppressLint("NewApi")
			@Override
			protected void onPreExecute (){
				caculateClustered(screenShot);

			};


			@Override
			protected Void doInBackground(Void... params) {


				SoundPlayManager spm= new SoundPlayManager(getActivity());
				spm.convertPhotoToSound(AppSettings.getSpeed(),ipManager.getFilesMatrix(),ipManager.getVolumeMatrix());
				currentSpm=spm;
				Log.d("DEBUG",i+"\tfinish convert to sound");
				if (isAdded()){
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Publisher.getInstance().notifyAllObservers();

						}
					});
				}

				if (!isPlaying){
					isPlaying=true;

					playInBackground(i);
					while (currentSpm!=null && spm!=currentSpm && !toStop){
						Log.d("DEBUG","new spm when finish play");

						playInBackground(i);
					}
					isPlaying=false;

				}
				return null;
			}


			@Override
			protected void onPostExecute(Void result) {
				if (!toStop){
					if (bitmap!=null){
						bitmap.recycle();
					}
					//					screenShot();
				}

			}

		};
		curTask=task;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			task.execute();
	}




	private void playInBackground(final int i){
		if(!toStop && j>=i){
			Log.d("DEBUG",i+"start to play");
			int progressStatus=0;
			if (currentSpm!=null){
				SoundPlayManager spm=currentSpm;

				spm.playSound(new SoundProccessListener() {

					@Override
					public boolean onFinishPlayColumn(final int col) {

						return toStop;
					}
				}, progressStatus++);
				Log.d("DEBUG",i+"finish to play");
				if (spm!=currentSpm){
					Log.d("DEBUG","new spm when finish play");

					playInBackground(i);
				}else{
					isPlaying=false;
				}
			}
		}
	}
	private Bitmap caculateClustered(Bitmap screenShot){

		Bitmap resizeBitmap=Bitmap.createScaledBitmap(screenShot, CommonConstans.IMAGE_WIDTH,CommonConstans.IMAGE_HEIGHT, false);
		return ipManager.analizePhoto(resizeBitmap);

	}

	private void stop(){
		Log.d("DEBUG","STOP!!!!!");
		j=0;
		Publisher.getInstance().removeObserver(this);
		toStop=true;;
		currentSpm=null;
		if (curTask!=null){
			curTask.cancel(true);
			curTask=null;
		}
		isAnalyze=false;
		isPlaying=false;
	}
	@SuppressLint("NewApi") @Override
	public void onPause(){
		super.onPause();
		Publisher.getInstance().removeObserver(this);
		toStop=true;
		isAnalyze=false;
		isPlaying=false;
		mView.evaluateJavascript("gamesDidDisappear();", new ValueCallback<String>() {

			@Override
			public void onReceiveValue(String value) {
				Log.d("DEBUG","success to run JS");

			}
		});

	}

	@Override
	public void onStop() {
		super.onStop();

	}
	@Override
	public void onUpdate() {
		Log.d("DEBUG","on update");
		isAnalyze=false;
		screenShot();

	}


	@Override
	public boolean backPressed() {
		boolean toReturn=super.backPressed();
		stop();
		return toReturn;
	}
}
