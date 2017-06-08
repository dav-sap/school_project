package com.quickode.eyemusic;//package com.quickode.eyemusic;
//
//import android.app.Activity;
//import android.util.Log;
//import com.google.android.gms.analytics.Tracker;
//
//public class GoogleAnalyticsEyeMusic {
//	private static GoogleAnalyticsEyeMusic	_instance;
//	private static Activity			_activity;
//	private boolean					ActivateGoogleAnalytics	= true;
//
//	public GoogleAnalyticsEyeMusic()
//	{
//
//	}
//
//	public static GoogleAnalyticsEyeMusic getInstance()
//	{
//		if (_instance == null)
//		{
//			_instance = new GoogleAnalyticsEyeMusic();
//	
//		}
//		return _instance;
//	}
//
////	public void startGoogleAnalyticsActivity(Activity activity)
////	{
////		try
////		{
////			if (ActivateGoogleAnalytics)
////			{
////				_activity = activity;
////
////				EasyTracker.getInstance(activity).activityStart(activity);
////
////			if (DreamWorksApplication.isDebug())
////				{
////					Log.d("analytics", "start " + activity.getLocalClassName());
////				}
////			}
////		}
////		catch (Exception ex)
////		{
////			if (DreamWorksApplication.isDebug())
////			{
////				Log.d("analytics", "Faild to startGoogleAnalyticsActivity");
////			}
////		}
////	}
//
//	public void startGoogleAnalyticsActivity(Activity activity)
//	{
//		try
//		{
//			if (ActivateGoogleAnalytics)
//			{
//				_activity = activity;
//				EasyTracker.getInstance(activity).activityStart(activity);
//
//				
//					Log.d("analytics", "start " + activity.getLocalClassName());
//
//			}
//		}
//		catch (Exception ex)
//		{
//			
//			
//				Log.d("analytics", "Faild to startGoogleAnalyticsActivity");
//			
//		}
//	}
//
//
//
//	public void stopGoogleAnalyticsActivity(Activity activity)
//	{
//		try
//		{
//			if (ActivateGoogleAnalytics)
//			{
//				_activity = activity;
//				EasyTracker.getInstance(activity).activityStop(activity);
//
//				
//					Log.d("analytics", "stop " + activity.getLocalClassName());
//				
//			}
//		}
//		catch (Exception ex)
//		{
//			
//				Log.d("analytics", "Faild to stopGoogleAnalyticsActivity");
//			
//		}
//	}
//
//	public void setActivateGoogleAnalytics(boolean activate)
//	{
//		ActivateGoogleAnalytics = activate;
//	}
//
//	/*
//	 * public void setContextBaseJifitiContext(IkeaMainActivity activity) { try
//	 * { EasyTracker.getInstance(activity).setContext(activity); } catch
//	 * (Exception ex) { if (IkeaApplication.isDebug()) { Log.d("analytics",
//	 * "Faild to setContext"); } } }
//	 * 
//	 * public void setContext(Context context) { try {
//	 * EasyTracker.getInstance(context).setContext(context); } catch (Exception
//	 * ex) { if (IkeaApplication.isDebug()) { Log.d("analytics",
//	 * "Faild to setContext"); } } }
//	 */
//
//	public void sendScreenName(String screenName)
//	{
//
//		if (ActivateGoogleAnalytics)
//		{
//			try
//			{
//				EasyTracker.getInstance(_activity).set(Fields.SCREEN_NAME, screenName);
//				EasyTracker.getInstance(_activity).send(MapBuilder.createAppView().build());
//				
//					Log.d("analytics screen name", "screen name: " + screenName);
//				
//			}
//			catch (Exception ex)
//			{
//				
//					Log.d("analytics", "Faild to send event");
//
//				
//			}
//		}
//	}
//
//	public void sendEvent(String category, String action, String label)
//	{
//
//		if (ActivateGoogleAnalytics)
//		{
//			try
//			{
//
//				EasyTracker.getInstance(_activity).send(MapBuilder.createEvent(category, action, label, null).build());
//
//				
//					Log.d("analytics", "Category: " + category + " Action: " + action + " label: " + label);
//				
//			}
//			catch (Exception ex)
//			{
//				
//					Log.d("analytics", "Faild to send event");
//
//				
//			}
//		}
//	}
//
//}
