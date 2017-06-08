package com.quickode.eyemusic.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle; 
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.interfaces.SoundProccessListener;
import com.quickode.eyemusic.interfaces.stopPlayLisetner;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.models.ImageProcessingManager;
import com.quickode.eyemusic.models.SoundPlayManager;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.Publisher;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public abstract class playMusic extends Fragment  {

    //to initialize openCV
    static {
        if(!OpenCVLoader.initDebug()) {
            Log.i("OpenCv", "Initialization Failed");
        } else {
            Log.i("OpenCv", "Initialization Success");
        }
    }

	protected final static String TAG="playMusic";
	protected View mView;
	private ImageView picContainer;
//	//	protected Bitmap sourcePic;
	protected Bitmap currentDisplayImage;
    protected Bitmap currentTakenImage;
	protected Bitmap currentTakenImageClustered;
	protected Bitmap currentSoundImage;
	protected Bitmap currentSoundImageClustered;
	private SoundPlayManager currentSpm;
	private boolean isPlaying;

	AsyncTask<Params, Void, Void> curTask;
	HashMap<Integer,AsyncTask<Params, Void, Void>> mTasks;




	private ProgressBar progressBar;
	private int progressStatus;
	protected AppSettings mSettings;
	protected  SurfaceView surfaceView;
	protected stopPlayLisetner mStopPlayListener;

	protected ImageView play;

	protected boolean cancel;
	protected boolean toStop;
	protected boolean analayzeFromBegining;
	//protected boolean analayzeFromSound;
	private int curSpeed;

	private int repeat;
	static Bitmap  clustered;
	//	static SoundPlayManager spm;
	static ImageProcessingManager ipManager;
	static boolean picWasPlayed;
	protected int numOfwaiting=0;

	private static int containerWidth;;
	private static int containerHeight;;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progressStatus=0;
		analayzeFromBegining=true;
		curSpeed=AppSettings.getSpeed();
		//no play at first
		toStop=true;

		ipManager= new ImageProcessingManager(playMusic.this.getActivity());
		//analayzeFromSound=true;
		repeat=0;
		mTasks=new HashMap<Integer, AsyncTask<Params,Void,Void>>();



	}


	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.layout_play_image,container,false);
		picContainer=((ImageView)mView.findViewById(R.id.ImageView_source_pic_container));

		play=(ImageView)mView.findViewById(R.id.play_btn);
		initPlayButton();
		progressBar= (ProgressBar)(mView.findViewById(R.id.ProgressBarMusic));
		surfaceView = (SurfaceView)mView.findViewById(R.id.surfaceview);
		surfaceView.setVisibility(View.GONE);
		initDialogSpeedOptions();
		initDialogColorOptions();
		return mView;
	}

	protected abstract void loadPic();





	@Override
	public void onResume (){
		super.onResume();
		analayzeFromBegining=true;
		if (!(playMusic.this instanceof PlayVideo) && currentDisplayImage != null){

			toStop=false;
            Log.i("playMusic", "in onResume - before entering onPlayClick");
			onPlayClick(currentDisplayImage);

		}



	}

	private void initDialogColorOptions() {
		final String[] option = new String[3];//
		option[0]=AppSettings.isBlackAndWhite()?"Color": "Black & White";
		option[1]=AppSettings.isNegative()?"Positive":"Negative";
		option[2]= "Cancel" ; 
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, option); 
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity()); 

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) 
			{ 

				switch(which){
				case 0:
					if (!AppSettings.isBlackAndWhite()){
						AppSettings.setBlackAndWhite(true);
						option[0]="Color";

					}
					else{
						AppSettings.setBlackAndWhite(false);
						option[0]="Black & White";

					}

					break;
				case 1:
					if (!AppSettings.isNegative()){
						AppSettings.setNegative(true);
						option[1]="Positive";
					}
					else{
						AppSettings.setNegative(false);
						option[1]="Negative";
					}

					break;
				case 2:
					break;
				}
				analayzeFromBegining=true;
				loadPic();



			} 
		});
		final AlertDialog dialog = builder.create(); 

		/*

		// custom dialog
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_speed);

		 */
		((ImageView)mView.findViewById(R.id.right_btn)).setContentDescription(getResources().getString(R.string.color));
		((ImageView)mView.findViewById(R.id.right_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				performPauseClick();
				dialog.setTitle("Color options:");
				dialog.show();

			}
		});

	}


	protected void performPlayClick(Bitmap photo){


		toStop=false;

		currentDisplayImage = photo;
//        currentTakenImage = photo;

		Log.d("DEBUG", "set new current taken Image");
		if(playMusic.this instanceof PlayVideo) {
            onPlayClick(photo);
        }
	}


	protected void performPauseClick(){


		toStop=true;

		//		Log.d("EYE_MUSIC","want to pause");
		if (isAdded()){
			onPauseClick();
		}


	}



	class Params{
		String[][] fileMatrix;
		float[][] volumeMatrix;
		Bitmap photo;
		public Params(String[][] fileMatrix, float[][] volumeMatrix,Bitmap photo) {
			super();
			this.fileMatrix = fileMatrix;
			this.volumeMatrix = volumeMatrix;
			this.photo=photo;
		}

	}
	int j=0;
	private void createSound(final Bitmap photo , String[][] fileMatrix, float[][] volumeMatrix){

		j++;

		//		final AsyncTask<Params, Void, Void>  task=new AsyncTask<Params, Void, Void>() {
		//			int i=j;
		//			boolean finishOneRun=false;
		//			final Handler handler = new Handler();
		//			//			SoundPlayManager spm;
		//			@SuppressLint("NewApi")
		//			@Override
		//			protected void onPreExecute (){
		//
		//			};
		//
		//
		//			@Override
		//			protected Void doInBackground(Params... params) {
		//
		//				//				if ( analayzeFromSound){
		//				Log.d("DEBUG",i+"\t"+"start convert to sound");
		//				Params parameter=params[0];
		//				SoundPlayManager spm=new SoundPlayManager(playMusic.this.getActivity());
		//				spm.convertPhotoToSound(AppSettings.getSpeed(),parameter.fileMatrix,parameter.volumeMatrix);
		//
		//				currentSpm=spm;
		//				currentSoundImage=currentTakenImage;
		//				currentSoundImageClustered=currentTakenImageClustered;
		//				Log.d("DEBUG",i+"\t"+"finish convert to sound");
		//				Publisher.getInstance().notifyAllObservers();
		//
		//
		//
		//				if (analayzeFromBegining ||	analayzeFromSound){
		//					repeat=0;
		//					progressBar.setProgress(0);
		//					progressStatus=0;
		//				}
		//				analayzeFromBegining=false; 
		//				analayzeFromSound=false; 
		//
		//
		//				if ( (playMusic.this instanceof PlayTraining) && currentTakenImage!=photo){
		//					this.cancel(true);
		//					Log.d("EYE_MUSIC",i+"\t"+"currentPlayedImage NOT equal to photo");								
		//				}else{
		//
		//
		//					Log.d("EYE_MUSIC",i+"\t"+"currentPlayedImage equal to photo");	
		//
		//					if (!isPlaying && !toStop){
		//						isPlaying=true;
		//
		//						publishProgress((Void[])null);
		//
		//						finishOneRun=	playInBackground(handler,i);
		//
		//
		//					}}
		//
		//				return null;
		//			}
		//
		//
		//			protected void onProgressUpdate(Void... progress) {
		//				/**
		//				 * Sets current progress on ProgressDialog
		//				 */
		//
		//				if (playMusic.this instanceof PlayVideo ){
		//					showImages();
		//				}
		//			}
		//
		//			@Override
		//			protected void onPostExecute(Void result) {
		//				Log.d("DEBUG",i+"\t"+"on post execute"); 
		//
		//				if (finishOneRun){
		//					Log.d("DEBUG",i+"\t"+"on post execute - finish one run"); 
		//					progressBar.setProgress(0);
		//					progressStatus=0;
		//					if (repeat==mSettings.getRepeat()){
		//						repeat=0;
		//						if (! (playMusic.this instanceof PlayVideo)){
		//							performPauseClick();
		//						}
		//						afterPlay();
		//					}
		//				}
		//				
		//		
		//
		//			}
		//
		//		};

		makeSoundThread task= new makeSoundThread();
		task.setRequestId(j);
		//		curTask=task;
		mTasks.put(j,task);
		Log.d("DEBUG","num of threads: "+mTasks.size());
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Params(fileMatrix, volumeMatrix,photo));
		else
			task.execute(new Params(fileMatrix, volumeMatrix,photo));



		//	task.execute(new Params(fileMatrix, volumeMatrix));


		//	Log.d("EYE_MUSIC","waiting to begin task");



	}

	protected   void afterPlay(){

	}



	private void showImages(){
		if (isAdded() && AppSettings.isShowOriginal()){
			picContainer.setBackgroundDrawable(new BitmapDrawable(getResources(), currentSoundImage));
			picContainer.bringToFront();
			((ImageView)mView.findViewById(R.id.play_btn)).setEnabled(true);	
		}

		if (isAdded() &&AppSettings.isShowClustered()){
			((ImageView)mView.findViewById(R.id.ImageView_flat_pic_container)).setBackgroundDrawable(new BitmapDrawable(getResources(), currentSoundImageClustered));
		}
	}

	private void initPlayButton(){

		play.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {

				Log.d(TAG,"press play");
				toStop=!toStop;
				if (!toStop){
					Log.d("EYE_MUSIC","want to play");

					loadPic();

				}else{
					Log.d("EYE_MUSIC","want to pause");
					onPauseClick();
				}


			}
		});


	}


	@SuppressLint("NewApi")
	protected void onPlayClick(Bitmap photo){
        Log.i("playMusic", "in onPlayClick");

        Bitmap result = calculateEdited(photo);

		//Bitmap picToShow=calcMaxRatio(photo);

		if (curSpeed!=AppSettings.getSpeed()){
			curSpeed=AppSettings.getSpeed();
			analayzeFromBegining=true;
		}

		if (analayzeFromBegining){
			repeat=0;
			Bitmap upperPic	= calcMaxRatio(result);
			if (AppSettings.isShowOriginal()){
				//				picContainer.setBackgroundDrawable(new BitmapDrawable(getResources(), photo));
				//				picContainer.bringToFront();
				((ImageView)mView.findViewById(R.id.play_btn)).setEnabled(true);	
			}
			else{
				//				picContainer.setBackground(null);
			}
			calculateClustered(result,upperPic);
		}

		if (!(playMusic.this instanceof PlayVideo) ){
			currentSoundImage=currentTakenImage;
			currentSoundImageClustered=currentTakenImageClustered;
			showImages();
		}

		createSound(photo,ipManager.getFilesMatrix(),ipManager.getVolumeMatrix());

		//		if (AppSettings.isShowOriginal()){
		//			picContainer.setBackground(new BitmapDrawable(getResources(), photo));
		//			picContainer.bringToFront();
		//			((ImageView)mView.findViewById(R.id.play_btn)).setEnabled(true);	
		//		}
		//		else{
		//			picContainer.setBackground(null);
		//		}

		play.setImageResource(R.drawable.pause_btn);
		play.setContentDescription(getResources().getString(R.string.pause));
	}

    /*
    * This function receives the original image, and returns the edited image depending on the
    * selected features. In case no features were selected, then the original image is returned.
    * The top image to be displayed is also saved, depending on the image display settings.
    */
    private Bitmap calculateEdited(Bitmap photo) {
		Log.i("playMusic", "in calculateEdited: photo width - " + photo.getWidth() + ", photo height - " + photo.getHeight());

        //convert bitmap to matrix
        Mat image = new Mat(photo.getWidth(), photo.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(photo, image);

        ObjectDetector detector = null;
        if (AppSettings.isFaceDetection()) {
            detector = new FaceDetector(this.getActivity());
        }
        else if(AppSettings.isFullBodyDetection()) {
            detector = new FullBodyDetector(this.getActivity());
        }
        else if(AppSettings.isUpperBodyDetection()) {
            detector = new UpperBodyDetector(this.getActivity());
        }
        if(detector != null) {
            if (AppSettings.isObjectDetectionCropped()) {
                image = detector.detectClosestObject(image, true);
            } else {
                image = detector.detectClosestObject(image, false);
            }
        }

        if (AppSettings.isCartoonize()) { // shows the image after cartoonization
            Cartoonize c = new Cartoonize();
            image = c.cartoonize(image);
        }

        //convert matrix to bitmap
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap result = Bitmap.createBitmap(image.width(), image.height(), conf);
        Utils.matToBitmap(image, result);

        currentTakenImage = AppSettings.isDisplayOriginal() ? photo : result;
        return result;
    }

	@SuppressLint("NewApi")
	private void calculateClustered(Bitmap photo, Bitmap upperPic){
		Bitmap clustered = null;

		Bitmap resizeBitmap=Bitmap.createScaledBitmap(upperPic, CommonConstans.IMAGE_WIDTH,CommonConstans.IMAGE_HEIGHT, AppSettings.algo);
		clustered=ipManager.analizePhoto(resizeBitmap);

		//		if 
		//		(AppSettings.isShowClustered()){
		//			((ImageView)mView.findViewById(R.id.ImageView_flat_pic_container)).setBackgroundDrawable(new BitmapDrawable(getResources(), clustered));
		//		}
		currentTakenImageClustered=clustered;

		//		if (! (this instanceof PlayTraining) || currentPlayedImage==photo){


		//		}
	}
	protected void onPauseClick(){
		play.setContentDescription(getResources().getString(R.string.play));
		play.setImageResource(R.drawable.play_btn);

	}
	private void initDialogSpeedOptions(){


		final String[] option = new String[] { "Slower", "Faster", "Cancel" }; 
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, option); 
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity()); 

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) 
			{ 

				switch(which){
				case 0:
					AppSettings.increaseSpeed();
					break;
				case 1:
					AppSettings.decreaseSpeed();
					break;
				case 2:
					break;
				}
				analayzeFromBegining=true;
				loadPic();
			} 
		});
		final AlertDialog dialog = builder.create();

		/*

		// custom dialog
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_speed);

		 */

		((ImageView)mView.findViewById(R.id.speed_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				performPauseClick();
				dialog.setTitle("Sound speed:\n"+AppSettings.getSpeed()+" milisecond per column");
				dialog.show();

			}
		});
	}


	protected void setPicsToNull(){
		currentSoundImage=null;
		currentSoundImageClustered=null;
		currentSpm=null;
		currentTakenImage=null;
		currentTakenImageClustered=null;
	}
	private Bitmap calcMaxRatio(Bitmap photo){
		if (containerHeight==0 || containerWidth==0){
			Drawable rect=getResources().getDrawable(R.drawable.rectangle);

			containerWidth=rect.getIntrinsicWidth();
			containerHeight=rect.getIntrinsicHeight();

		}
		picContainer.getLayoutParams().width=containerWidth;
		picContainer.getLayoutParams().height=containerHeight;

		int width=photo.getWidth();
		int height= photo.getHeight();

		int widthMult=width/CommonConstans.IMAGE_WIDTH;
		int heightMult=height/CommonConstans.IMAGE_HEIGHT;
		int mult=Math.min(widthMult, heightMult);
		Bitmap bitmap=Bitmap.createScaledBitmap(photo, CommonConstans.IMAGE_WIDTH*mult,CommonConstans.IMAGE_HEIGHT*mult,AppSettings.algo);

		return bitmap;

	}


	@Override
	public void onPause() {
		super.onPause();
		performPauseClick();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (curTask!=null){
			curTask.cancel(true);
		}
	}






	private class makeSoundThread extends AsyncTask<Params, Void, Void>{
		protected int mRequestId;
		int i=j;
		boolean finishOneRun=false;
		final Handler handler = new Handler();
		//			SoundPlayManager spm;
		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute (){

		};


		@Override
		protected Void doInBackground(Params... params) {

			//				if ( analayzeFromSound){
			Log.d("DEBUG",i+"\t"+"start convert to sound at:   "+System.currentTimeMillis());
			Params parameter=params[0];
			SoundPlayManager spm=new SoundPlayManager(playMusic.this.getActivity());
			spm.convertPhotoToSound(AppSettings.getSpeed(),parameter.fileMatrix,parameter.volumeMatrix);
			currentSoundImage=currentTakenImage;
			currentSoundImageClustered=currentTakenImageClustered;
			currentSpm=spm; 

			Log.d("DEBUG",i+"\t"+"finish convert to sound at:   "+System.currentTimeMillis());
			if(!isCancelled()){

				Publisher.getInstance().notifyAllObservers();

			}

			if (analayzeFromBegining ){
				repeat=0;
				progressBar.setProgress(0);
				progressStatus=0;
			}
			analayzeFromBegining=false;  


			if ( (playMusic.this instanceof PlayTraining) && currentTakenImage!=parameter.photo){
				//						if (  currentTakenImage!=parameter.photo){
				this.cancel(true);
				Log.d("EYE_MUSIC",i+"\t"+"currentPlayedImage NOT equal to photo");								
			}else{

				curTask=this;
				Log.d("EYE_MUSIC",i+"\t"+"currentPlayedImage equal to photo");	

				if (!isCancelled() && !toStop && !isPlaying  ){
					isPlaying=true;

					publishProgress((Void[])null);

					finishOneRun=	playInBackground(handler,i);

					while (!isCancelled() && !toStop && playMusic.this instanceof PlayVideo && spm!=currentSpm){
						Log.d("DEBUG",i+"\t"+"new spm when finish play");
						progressBar.setProgress(0);
						progressStatus=0;
						repeat=0;
						publishProgress((Void[])null);
						playInBackground(handler,i);
					}
					isPlaying=false;

				}
			}
			//				}

			return null;
		}


		protected void onProgressUpdate(Void... progress) {
			/**
			 * Sets current progress on ProgressDialog
			 */

			if (playMusic.this instanceof PlayVideo ){
				showImages();
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("DEBUG",i+"\t"+"on post execute");

			if (finishOneRun){
				Log.d("DEBUG",i+"\t"+"on post execute - finish one run"); 
				progressBar.setProgress(0);
				progressStatus=0;
				if (repeat==mSettings.getRepeat()){
					repeat=0;
					if (! (playMusic.this instanceof PlayVideo)){
						performPauseClick();
					}
					afterPlay();
				}
			}

			mTasks.remove(i);

		}

		@Override
		protected void onCancelled() {
			Log.d("DEBUG", i+" task is cancel onCanceleed");
			super.onCancelled();
			mTasks.remove(i);
		}




		public void setRequestId (int requestId)
		{
			this.mRequestId = requestId;
		}

		private boolean playInBackground(final Handler handler, int i ){
			boolean finishOneRun=false;
			SoundPlayManager spm=currentSpm;
			try{
				Log.d("DEBUG",i+"\t"+"start to play");
				while(repeat<mSettings.getRepeat() && !toStop ){
					spm.playSound(new SoundProccessListener() {

						@Override
						public boolean onFinishPlayColumn(final int col) {

							handler.post(new Runnable() {
								public void run() {
									progressStatus=col+1;
									progressBar.setProgress(progressStatus);
								}
							});
							return toStop;
						}
					}, progressStatus);

					if (progressStatus>=CommonConstans.IMAGE_WIDTH-1){
						progressBar.setProgress(0);
						progressStatus=0;
						repeat++;
						finishOneRun=true;
						//Log.d(TAG,"finish run");
					}
					else{
						finishOneRun=false;
						//Log.d(TAG," not finish run");
					}
				}
				Log.d("DEBUG",i+"\t"+"finish to play");


				//			if (playMusic.this instanceof PlayVideo && spm!=currentSpm){
				//				Log.d("DEBUG",i+"\t"+"new spm when finish play");
				//				progressBar.setProgress(0);
				//				progressStatus=0;
				//				repeat=0;
				//				publishProgress((Void[])null);
				//				playInBackground(handler,i);
				//			}else{
				//				isPlaying=false;
				//			}
			}catch(Exception e){
				e.printStackTrace();
			}
			return finishOneRun;
		}



	}
}
