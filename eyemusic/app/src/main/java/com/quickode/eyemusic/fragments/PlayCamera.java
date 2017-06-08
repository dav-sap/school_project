package com.quickode.eyemusic.fragments;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.SwitchPreference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.interfaces.AsyncResponse;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.tools.CommonConstans;
import com.quickode.eyemusic.tools.ImageCompression;
import com.quickode.eyemusic.tools.Utils;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PlayCamera extends playMusic implements AsyncResponse {

//	//to initialize openCV
//	static {
//		if(!OpenCVLoader.initDebug()) {
//			Log.i("OpenCv", "Initialization Failed");
//		} else {
//			Log.i("OpenCv", "Initialization Success");
//		}
//	}

	private static final int REQUEST_IMAGE_CAPTURE = 0;
	//private static final int PICK_IMAGE = 1;
	private final int GALLERY_INTENT_CALLED = 1;	
	private final int GALLERY_KITKAT_INTENT_CALLED = 2;
	private boolean hasPic;
	Bitmap photo;
	private Uri file;
	MediaScannerConnection conn;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		mSettings=(AppSettings) bundle.getSerializable("SETTINGS");
		hasPic=false;

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		play.setEnabled(hasPic);
		initDialogOptions();


		return mView;
	}
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.image_input));

	}



	private void initDialogOptions(){
		final ImageView choosePic=((ImageView)mView.findViewById(R.id.ImageView_choose_picture));
		choosePic.setVisibility(View.VISIBLE);

		final String[] option = new String[] { "Take a picture", "Choose from library", "Cancel" }; 
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, option); 
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity()); 
		builder.setTitle("Load picture"); 
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) 
			{ 

				switch(which){
				case 0:
//					takePhoto();
					takePicture();
					break;
				case 1:
					chooseFromGallery();
					break;
				case 2:
					break;
				}
			} 
		});
		final AlertDialog dialog = builder.create();
		choosePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//to ensure the click will make pause and not play
				//				if (!toStop){
				//					Log.d("EYE_MUSIC","stop because press select pic");
				//					performPlayClick(null);
				//					//play.performClick();
				//				}
				performPauseClick();
				dialog.show();

			}
		});
	}
	/* Checks if external storage is available for read and write */
	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	private File getOutputMediaFile(){
		if (this.isExternalStorageWritable()) {
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES), "CameraDemo");

			if (!mediaStorageDir.exists()){
				if (!mediaStorageDir.mkdirs()){
					Log.d("CameraDemo", "failed to create directory");
					return null;
				}
			}

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			return new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else {
			Log.e("ERROR ", "Can't write to external storage");
			return null;
		}

	}

	@SuppressLint("NewApi")
	private String getFilePath(Context context, Uri uri) throws URISyntaxException {
		String selection = null;
		String[] selectionArgs = null;
		// Uri is different in versions after KITKAT (Android 4.4), we need to
		if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				return Environment.getExternalStorageDirectory() + "/" + split[1];
			} else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				uri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("image".equals(type)) {
					uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				selection = "_id=?";
				selectionArgs = new String[]{
						split[1]
				};
			}
		}
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = {
					MediaStore.Images.Media.DATA
			};
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver()
						.query(uri, projection, selection, selectionArgs, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	private boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	public void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		file = Uri.fromFile(getOutputMediaFile());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

		startActivityForResult(intent, 100);
	}


	public void pixelImg(String imgPath) {
		Bitmap imageBitmap = BitmapFactory.decodeFile(imgPath);
		photo = imageBitmap; //photo to be displayed

		//convert bitmap to matrix
		Mat image = new Mat (imageBitmap.getWidth(), imageBitmap.getHeight(), CvType.CV_8UC1);
		org.opencv.android.Utils.bitmapToMat(imageBitmap, image);
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap result = Bitmap.createBitmap(image.width(), image.height(), conf);
		org.opencv.android.Utils.matToBitmap(image, result);

		photo = result;
		photo=imageBitmap;

//		if (!toStop){
//			play.performClick();
//		}
		//	toStop=true;
		analayzeFromBegining=true;
		play.setEnabled(true);
		hasPic=true;
//			performPlayClick(photo, originalPhoto);
		performPlayClick(photo);
		play.performClick();
	}


	@Override
	public void processFinish(String output){
		//Here you will receive the result fired from async class
		//of onPostExecute(result) method.
		pixelImg(output);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {

				try{

//					CheckBox analyzImg = (CheckBox)getView().findViewById(R.id.checkBox_img);
					if (AppSettings.getAnalyzeImg()) {
						new ImageCompression(this.getActivity(), this).execute(file.getPath());
					}else {
						pixelImg(file.getPath());
					}


				}
				catch (Exception e){
					e.printStackTrace();
				}
			}

		}
		else if(requestCode == GALLERY_INTENT_CALLED && data != null && data.getData() != null) {
			if(resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				try {
					String path = getFilePath(this.getActivity(), selectedImage);
//					CheckBox analyzImg = (CheckBox)getView().findViewById(R.id.checkBox_img);

					if (AppSettings.getAnalyzeImg()) {
						new ImageCompression(this.getActivity(), this).execute(path);
					} else {
						pixelImg(path);
					}

				} catch (Exception URISyntaxException) {
					Log.e("URI", "Gallery file can't get path");
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void afterPlay() {
		// TODO Auto-generated method stub

	}

	private void chooseFromGallery(){

		Intent intent = new Intent(); 
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT_CALLED);

	}

	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = getActivity().getContentResolver().query( contentUri,
				proj, // Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	protected void loadPic() {
		if (photo != null){
			performPlayClick(photo);
		}
	}



}
