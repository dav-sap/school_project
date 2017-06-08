package com.quickode.eyemusic.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.tools.Utils;

public class GridAdapter extends BaseAdapter {
	private Context mContext;
	private String[] imgs;
	private LayoutInflater inflater;
	private String mPath;


	public GridAdapter(Activity activity,String[] mList, String path) {
		this.mContext = activity;
		this.imgs=mList;
		this.inflater = activity.getLayoutInflater();
		this.mPath=path;
	}

	public int getCount() {
		return imgs.length;
	}

	public Object getItem(int position) {
		return imgs[position];
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		String curImg= imgs[position];
		final ViewHolder holder;
		if (convertView == null) {
			holder=new ViewHolder();
			convertView  = inflater.inflate(R.layout.layout_grid_img_item, null);
			holder.textViewImgTitle = (TextView)convertView
					.findViewById(R.id.grid_item_text);
			holder.imageViewImg = (ImageView) convertView.findViewById(R.id.grid_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		InputStream ims;
		try {
			ims = mContext.getAssets().open(mPath+"/"+imgs[position]);

			// load image as Drawable
			Drawable d = Drawable.createFromStream(ims, null);
			// set image to ImageView
			holder.textViewImgTitle.setText(Utils.parseImgName(curImg));
			holder.imageViewImg.setImageDrawable(d);
		}    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}

	// view holder pattern
	public class ViewHolder {
		public TextView textViewImgTitle;
		public ImageView imageViewImg;

	}
	

}