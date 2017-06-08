package com.quickode.eyemusic.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.Observer;
import com.quickode.eyemusic.tools.Publisher;
import com.quickode.eyemusic.tools.finishAnalyze;
import com.quickode.eyemusic.fragments.playMusic.Params;
import com.quickode.eyemusic.models.AppSettings;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PlayVideo extends playMusic implements SurfaceHolder.Callback, Observer{


	private 	Camera mCamera;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;

	boolean isAnalyze;
	int surfaceWidth=-1;
	int surfaceHeight=-1;
	
	//private VideoView mVideoView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle=getArguments();
		mSettings=(AppSettings) bundle.getSerializable("SETTINGS");


	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		//mVideoView= (VideoView) mView.findViewById(R.id.VideoView_source_pic_container);
		initButtons();

		//	mCamera.startPreview();
		//startVideo();
		return mView;
	}



	@Override
	public void onResume(){
		super.onResume();
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.video_input));
		setPicsToNull();
		surfaceView.setVisibility(View.VISIBLE);

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Publisher.getInstance().addObserver(this);
		isAnalyze=false;
		//  
	}



	private void initButtons(){


		//		play.setEnabled(false);
	}


	private boolean safeCameraOpen() {
		boolean qOpened = false;

		try {
			releaseCamera();
			mCamera = Camera.open();
			qOpened = (mCamera != null);
		} catch (Exception e) {
			Log.e(getString(R.string.app_name), "failed to open Camera");
			e.printStackTrace();
		}

		try {
			mCamera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//The app is always in portrait mode (landscape mode is disabled).
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
		case Surface.ROTATION_90: degrees = 90; break; //Landscape left
		case Surface.ROTATION_180: degrees = 180; break;//Upside down
		case Surface.ROTATION_270: degrees = 270; break;//Landscape right
		}
		int rotate = (info.orientation - degrees + 360) % 360;

		//STEP #2: Set the 'rotation' parameter
		Camera.Parameters params = mCamera.getParameters();
		//parameters.getSupportedPictureSizes().
		params.setRotation(rotate); 
		//params.setPreviewSize(bestSize.width, bestSize.height);
		//	params.setPictureSize(bestSize.width, bestSize.height);		
		mCamera.setParameters(params);
		mCamera.setDisplayOrientation(rotate);
		//mCamera.startPreview();


		return qOpened;    
	}


	private Size getBestPreviewSize(int width, int height)
	{
		Size result=null;
		Camera.Parameters p = mCamera.getParameters();
		for (Size size : p.getSupportedPreviewSizes()) {
			if (((double)size.width)/size.height <= ((double)width)/height) {
				//			if (size.width<=width && size.height<=height || (size.height <= width && size.width <= height)) {
				if (result==null) {
					result=size;
				} else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;

					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}

		return result;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			surfaceView.getHolder().removeCallback(this);

			mCamera.release();
			mCamera = null;

		}
	}





	@Override
	protected void afterPlay() {
		//		newPic();

	}


	private void newPic(){

		if (!isAnalyze){
			isAnalyze=true;
			//			currentSoundImage=currentTakenImage;
			//			currentSoundImageClustered=currentTakenImageClustered;
			//		surfaceView.bringToFront();
			//surfaceView.setVisibility(View.VISIBLE);


			if (surfaceHeight>0 && surfaceWidth>0){
				try{
					Camera.Parameters parameters = mCamera.getParameters();

					parameters.setPreviewSize(surfaceWidth, surfaceHeight);

					mCamera.setParameters(parameters);
				}catch(Exception e){
					//					if (mCamera==null){
					safeCameraOpen();

					//					}
				}
			}
			try{
				mCamera.setDisplayOrientation(90);
				mCamera.startPreview();

				mCamera.takePicture(null, null, mCall);
			}catch(Exception e){
				e.printStackTrace();
			}
		}



	}
	@Override
	protected void onPauseClick(){
		Publisher.getInstance().removeObserver(this);
		super.onPauseClick();
		for (int id:mTasks.keySet()){
			mTasks.get(id).cancel(true);
		}
		mTasks=new HashMap<Integer, AsyncTask<Params,Void,Void>>();
		//surfaceView.setVisibility(View.VISIBLE);
		surfaceView.bringToFront();

		if (surfaceHeight>0 && surfaceWidth>0){
			try{
				Camera.Parameters parameters = mCamera.getParameters();

				parameters.setPreviewSize(surfaceWidth, surfaceHeight);

				mCamera.setParameters(parameters);
			}catch(Exception e){
				safeCameraOpen();
			}
		}

		mCamera.setDisplayOrientation(90);

		try{
			mCamera.startPreview();
		}catch(Exception e){
			e.printStackTrace();
		}

		((ImageView)mView.findViewById(R.id.ImageView_flat_pic_container)).setBackgroundResource(R.drawable.rectangle);
		isAnalyze=false;
	}

	@Override
	protected void onPlayClick(Bitmap photo){
		Publisher.getInstance().addObserver(this);
		super.onPlayClick(photo);


	}

	Camera.PictureCallback mCall = new Camera.PictureCallback() {

		@SuppressLint("NewApi")
		public void onPictureTaken(byte[] data, Camera camera) {
			//surfaceView.setVisibility(View.INVISIBLE);
			mCamera.stopPreview();
			//	Bitmap imageBitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
			Bitmap photo = BitmapFactory.decodeByteArray(data , 0, data .length);
			//mPic=imageBitmap;
			//	mPic = Bitmap.createScaledBitmap(imageBitmap, CommonConstans.IMAGE_WIDTH,CommonConstans.IMAGE_HEIGHT, true);

//            Bitmap originalPhoto = photo; //photo to be displayed
//            Bitmap changedPhoto;
//
//			//convert bitmap to matrix
//			Mat image = new Mat (photo.getWidth(), photo.getHeight(), CvType.CV_8UC1);
//			org.opencv.android.Utils.bitmapToMat(photo, image);
//
//			if(AppSettings.isFaceDetection()) {
//				FaceDetector f = new FaceDetector(getActivity());
//				image = f.detectClosestFace(image, false);
//			} else if(AppSettings.isFaceDetectionCropped()){
//				FaceDetector f = new FaceDetector(getActivity());
//				image = f.detectClosestFace(image, true);
//			}
//			if(AppSettings.isCartoonize()) {
//				Cartoonize c = new Cartoonize();
//				image = c.cartoonize(image);
//			}
//
//			//convert matrix to bitmap
//			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//			Bitmap result = Bitmap.createBitmap(image.width(), image.height(), conf);
//			org.opencv.android.Utils.matToBitmap(image, result);
//
//            changedPhoto = result;
//			if(!AppSettings.isDisplayOriginal()){//do not display original
//				originalPhoto = result;
//			}

			analayzeFromBegining=true;
//			performPlayClick(changedPhoto, originalPhoto);
			performPlayClick(photo);

			//PlayVideo.super.onPlayClick(photo);

		}};	
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			Log.d("DEBUG","surface changed");
			//			Camera.Parameters parameters = mCamera.getParameters();
			//			Camera.Size size = getBestPreviewSize(width, height);
			//			if (size==null){
			//				parameters.setPreviewSize(width, height);
			//			}else{
			//				parameters.setPreviewSize(size.width, size.height);
			//			}
			//			mCamera.setParameters(parameters);
			//			mCamera.startPreview();


			Size size = getBestPreviewSize(width, height);
			if (size==null){
				surfaceHeight=-1;
				surfaceWidth=-1;
			}else{
				surfaceWidth=size.width;
				surfaceHeight=size.height;
			}

			newPic();



		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG,"surface created");
			if (mCamera==null){
				safeCameraOpen();

			}



		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				// Call stopPreview() to stop updating the preview surface.
				stopPreviewAndFreeCamera();
			}

		}

		private void stopPreviewAndFreeCamera() {

			if (mCamera != null) {
				// Call stopPreview() to stop updating the preview surface.
				mCamera.stopPreview();

				// Important: Call release() to release the camera for use by other
				// applications. Applications should release the camera immediately
				// during onPause() and re-open() it during onResume()).
				mCamera.release();

				mCamera = null;
			}
		}


		@Override
		public void onUpdate() {
			isAnalyze=false;
			newPic();

		}


		public void onDestroyView (){
			super.onDestroyView();


		}

		@Override
		protected void loadPic() {
			//			setPicsToNull();
			Publisher.getInstance().addObserver(this);
			newPic();

		}

		@Override
		public void onStop() {
			super.onStop();


		}



		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			Publisher.getInstance().removeObserver(this);
			getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			releaseCamera();
		}



}
