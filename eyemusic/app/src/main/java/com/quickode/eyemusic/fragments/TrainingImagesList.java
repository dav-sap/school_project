package com.quickode.eyemusic.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;
import com.quickode.eyemusic.adapters.FoldersAdapter;
import com.quickode.eyemusic.adapters.GridAdapter;
import com.quickode.eyemusic.fragments.TrainingFolderList.RowInList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class TrainingImagesList extends Fragment{

	private globalEventListener mCallback;
	private String mPath;
	private String[] mList;
	private RelativeLayout random;
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
		Bundle bundle=getArguments();
		mPath=bundle.getString("PATH");
	}
	
	 @Override
		public void onResume() {
		        super.onResume();
		        
				((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.picture_list));

		    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_grid_view_training_images,container,false);
		GridView gridView = (GridView) view.findViewById(R.id.gridview_imgs);
		try {
			mList=getActivity().getAssets().list(mPath);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		final GridAdapter gridadapter = new GridAdapter(getActivity(),mList,mPath);
		gridView.setAdapter(gridadapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View v,
	                int position, long id) {

	           mCallback.onImageSelect(mPath,mList,position);

	        }
	    });
		((RelativeLayout)view.findViewById(R.id.random_choice)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				mCallback.onImageSelect(mPath, mList, -1);
				
			}
		});

		return view;
	}
	

}
