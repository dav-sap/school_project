package com.quickode.eyemusic.fragments;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class InputType extends Fragment{


	private View mView;
	private globalEventListener mCallback;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


	}
	 @Override
		public void onResume() {
		        super.onResume();
		        
		        ((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.input_type));
//		        ((MainActivity)getActivity()).getActionBar().setDisplayHomeAsUpEnabled(false);
		    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// inflate
		mView = inflater.inflate(R.layout.layout_input_type, null);
		initButtons();
		return mView;
	}

	private void initButtons(){
		mView.findViewById(R.id.training).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.openTraining();

			}
		});

		mView.findViewById(R.id.image).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCallback.openCamera();

			}
		});
		
		mView.findViewById(R.id.video).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.openVideo();
				
			}
		});
		
mView.findViewById(R.id.games).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.openGames();
				
			}
		});
	}

	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data){
		ActionBar actionBar = (getActivity()).getActionBar();
		actionBar.setTitle("Input type");
	}

}
