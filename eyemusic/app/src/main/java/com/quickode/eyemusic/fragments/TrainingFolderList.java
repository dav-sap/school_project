package com.quickode.eyemusic.fragments;

import java.io.IOException;
import java.util.ArrayList;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;
import com.quickode.eyemusic.adapters.FoldersAdapter;
import com.quickode.eyemusic.tools.GlobalParameters;
import com.quickode.eyemusic.tools.TrainingNode;



import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TrainingFolderList extends Fragment{
	private globalEventListener mCallback;
	private ArrayList<RowInList> mList;
	private String mPath;
	private AssetManager assetManager;
	private ArrayList<TrainingNode> mFilesNameList;
	private ListView list;
	private View view;
	private final static String TRAINING="Training_images";

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
		//mFilesNameList=bundle.getStringArray("LIST");
		mPath=bundle!=null? bundle.getString("PATH"):TRAINING;

		mFilesNameList=GlobalParameters.getInstance(getActivity()).mTrainingFiles.get(mPath).getSubDirectories();
		assetManager=getActivity().getAssets();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPath.equals(TRAINING)){
			((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.lessons));
		}
		else{
			((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.category));
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view  = inflater.inflate(R.layout.layout_training_folder_list,container,false);
		list=(ListView) view.findViewById(R.id.list);
		initList();
		FoldersAdapter adapter= new FoldersAdapter(getActivity(), mList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				RowInList row= mList.get(position);
				mCallback.onFolderSelected(row);
			}
		});

		/** Setting the list adapter for the ListFragment */


		return view;
	}



	private void initList() {

		//	TrainingNode node;
		//String[] list;
		mList= new ArrayList<RowInList>();


		for (int i=0;i<mFilesNameList.size();i++){

			//String[] subs=assetManager.list(path+"/"+mFilesNameList[i]);

			//boolean lastLevelOfDir=node.getSubDirectories()[0].getNumOfSubs()==0;
			RowInList row = new RowInList(mFilesNameList.get(i));//.get(i).getPath(),mFilesNameList.get(i).getName(),mFilesNameList.get,lastLevelOfDir);
			//RowInList row = new RowInList(node.getPath(),node.getName(),num,lastLevelOfDir);
			mList.add(row);
		}

	}

	//	@Override
	//	public void onListItemClick(ListView l, View v, int position, long id) {
	//
	//		RowInList row= mList.get(position);
	//		mCallback.onFolderSelected(row);
	//
	//	}

	public class RowInList{
		public String name;
		public String path;
		public ArrayList<TrainingNode> subs;
		public boolean lastLevel;







		public RowInList(TrainingNode trainingNode) {
			this.path=trainingNode.getPath();
			this.name=parsefolderName(trainingNode.getName());
			this.subs=trainingNode.getSubDirectories();
			lastLevel=this.subs.get(0).getName().contains(".");

		}




		public  String parsefolderName(String name){
			int _index=name.indexOf("_");
			int lessonNumer= Integer.parseInt(name.substring(0,_index));
			name=name.substring(_index+1,name.length());
			name=name.replace("_"," ");

			if (mPath==TRAINING){

				name= "Lesson "+lessonNumer+" - "+name;
			}
			return name;
		}

	}
}
