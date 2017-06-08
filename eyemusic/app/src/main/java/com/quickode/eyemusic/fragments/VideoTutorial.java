package com.quickode.eyemusic.fragments;

import com.quickode.eyemusic.R;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoTutorial extends Fragment{
	
	View mView;
	VideoView videoView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.layout_video,container,false);
		videoView=(VideoView)mView.findViewById(R.id.video_view);
		showVideo();
		return mView;
	}
	
	private void showVideo(){
//		getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
//		VideoView videoHolder = new VideoView(getActivity());
		
		//if you want the controls to appear
		videoView.setMediaController(new MediaController(getActivity()));
		Uri video = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" 
		+ R.raw.app03); //do not add any extension
		//if your file is named sherif.mp4 and placed in /raw
		//use R.raw.sherif
		videoView.setVideoURI(video);
//		setContentView(videoHolder); 
		videoView.start();
		videoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				getActivity().onBackPressed();
				
			}
		});
	}

}
