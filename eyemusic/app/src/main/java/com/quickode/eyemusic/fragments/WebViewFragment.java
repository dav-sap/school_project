package com.quickode.eyemusic.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;
import com.quickode.eyemusic.tools.CommonConstans;

public  class WebViewFragment extends Fragment {

	protected String URL;
	protected String currentUrl;
	protected WebView wv;
	LinearLayout root;
	SwAWebClient webClient;
	View v;
	protected globalEventListener mCallback;
	boolean firstLoad;
	public void init(String url) {
		currentUrl = url;
		URL=url;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (globalEventListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement "
					+ globalEventListener.class.getSimpleName());
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {       
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		firstLoad=true;
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
mCallback.disableSettings();
		v = inflater.inflate(R.layout.layout_web, container, false);
		wv = (WebView) v.findViewById(R.id.webPage);
//		wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wv.setFocusableInTouchMode(true);
		root=(LinearLayout)v.findViewById(R.id.root);
		if (currentUrl != null) {
			Log.d("SwA", "Current URL  1["+currentUrl+"]");

			wv.getSettings().setJavaScriptEnabled(true);
			
			 webClient=new SwAWebClient(){
				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

					Log.e(String.valueOf(errorCode), description);

					String summary = "<html><body style='background: white;'><p style='color: black;'>"+CommonConstans.helpText+"</p></body></html>";       
					view.loadData(summary, "text/html", null);


				}
			};
			wv.setWebViewClient(webClient);


		}
//		wv.setOnKeyListener(new OnKeyListener()
//		{
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event)
//			{
//				
//				Boolean toReturn=false;
//				if(event.getAction() == KeyEvent.ACTION_DOWN)
//				{
//					WebView webView = (WebView) v;
//
//					switch(keyCode)
//					{
//					case KeyEvent.KEYCODE_BACK:
//						backPressed();
//						
//						break;
//					}
//				}
//
//				return toReturn;
//			}
//		});
		Log.d("GAMES DEBUG", "current url is: "+currentUrl);
	
		wv.loadUrl(currentUrl);
		
		return v;
	}
	
	public  boolean backPressed(){
		boolean hasBack=false;
//		if(wv.canGoBack())
//		{
			if (wv.getUrl()!=null && !wv.getUrl().equals(URL)){
				wv.loadUrl(URL);
			hasBack= true;
			}
//		}
		return hasBack;
	}

	
	
//	public void updateUrl(String url) {
//		Log.d("SwA", "Update URL ["+url+"] - View ["+getView()+"]");
//		URL = url;
//
//		wv = (WebView) getView().findViewById(R.id.webPage);
//		wv.getSettings().setJavaScriptEnabled(true);
//		wv.loadUrl(url);
//
//
//	}

	class SwAWebClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onLoadResource(WebView  view, String  url){
			System.out.println("on load res");
		}

	}


	@Override
	public void onPause() {
		super.onPause();
//		root.removeView(wv); 
		//        wv.removeAllViews();
		Log.d("GAMES DEBUG", "onPause current url is: "+currentUrl);
		mCallback.enableSettings();
	}

	@Override
	public void onResume() {
		super.onResume();
//		if (root.findViewById(R.id.webPage)==null){
//			root.addView(wv);
//		}
		
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wv.destroy();
	}


}
