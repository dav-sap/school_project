package com.quickode.eyemusic.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;


public class TrainingNode {

	private static Context mContext;
	String path;
	String name;
	int numOfSubs;
	ArrayList<TrainingNode> subDirectories;




	public TrainingNode(String path,String name ) {
		this.path = path;
		this.name=name;
		numOfSubs=0;
		subDirectories= new ArrayList<TrainingNode>();


	}

	/*private TrainingNode(String line,BufferedReader reader, int depth){

		this.path=line.trim();
		this.name="kk";
		this.subDirectories=new ArrayList<TrainingNode>();
		String nextLine=null;
		try {
			nextLine = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nextLine!=null){
			int nextDepth=nextLine.length() - nextLine.replace("\t", "").length();
			while(nextDepth>depth){
				subDirectories.add(new TrainingNode(nextLine, reader,nextDepth));
			}
		}
		numOfSubs=subDirectories.size();
		GlobalParameters.getInstance(mContext).mTrainingFiles.put(this.path,this);

	}*/
	
	private TrainingNode(String line){
		if (line.endsWith("/")){
			line=line.substring(0,line.lastIndexOf("/"));
		}
		this.path=line.trim();
		
		//if (line.contains("/")){
		this.name=line.substring(line.lastIndexOf("/")+1);
		//}
	//	else{
		//	this.name=line;
	//	}
		this.subDirectories=new ArrayList<TrainingNode>();

		GlobalParameters.getInstance(mContext).mTrainingFiles.put(this.path,this);

	}
	
	public static void init(Context context){
		mContext=context;
		AssetManager assetManager=context.getAssets();
		try {
			InputStream is= assetManager.open("FileStructure.dir");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
TrainingNode node;
			while ((line = reader.readLine()) != null) {
				node= new TrainingNode(line);
				String parentPath=node.path.replace("/"+node.name,"");
				if (GlobalParameters.getInstance(context).mTrainingFiles.containsKey(parentPath) &&!parentPath.equals(node.path)){
					GlobalParameters.getInstance(context).mTrainingFiles.get(parentPath).subDirectories.add(node);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

/*	public static void init(Context context){
		mContext=context;
		AssetManager assetManager=context.getAssets();
		try {
			InputStream is= assetManager.open("FileStructure.dir");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				new TrainingNode(line, reader, 0);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}*/
	/*
	public static void addOneNode(TrainingNode node,AssetManager assetManager){
		try {
			String[] list = assetManager.list(node.path);
			if (list!=null && list.length>0){
				node.subDirectories= new TrainingNode[list.length];
				node.numOfSubs=node.subDirectories.length;
				for (int i=0;i<list.length;i++){
					node.subDirectories[i]=new TrainingNode(node.path+"/"+list[i],list[i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GlobalParameters.trainingFiles.put(node.path,node);

	}*/



	public String getName() {
		return name;
	}



	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getNumOfSubs() {
		return numOfSubs;
	}

	public ArrayList<TrainingNode> getSubDirectories() {
		return subDirectories;
	}



}
