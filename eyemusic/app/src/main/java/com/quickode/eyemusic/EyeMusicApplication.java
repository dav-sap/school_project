package com.quickode.eyemusic;//package com.quickode.eyemusic;
//
//import java.util.HashMap;
//
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;
//import com.quickode.eyemusic.tools.CommonConstans;
//
//import android.app.Application;
//
//public class EyeMusicApplication extends Application {
//
//private Tracker mTracker;
//
//		  
//		  
//		  synchronized Tracker getTracker() {
//			    if (mTracker==null) {
//
//			      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			      mTracker =  analytics.newTracker(CommonConstans.ANALYITICS_PROPERTY_ID);
//			     
//
//			    }
//			    return mTracker;
//			  }
//}
