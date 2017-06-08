package com.quickode.eyemusic;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

//import com.crashlytics.android.Crashlytics;
import com.quickode.eyemusic.tools.GlobalParameters;
import com.quickode.eyemusic.tools.TrainingNode;

public class SplashActivity extends Activity {

	// Splash screen timer
	  private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {	
				GlobalParameters.getInstance(SplashActivity.this);
				TrainingNode.init(SplashActivity.this);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				  Intent i = new Intent(SplashActivity.this, MainActivity.class);
				  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
	                startActivity(i);
	                SplashActivity.this.finish();

			};
		}.execute();

	
		



	}
	
	



}
