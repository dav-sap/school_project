package com.quickode.eyemusic.adapters;

import java.util.List;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.fragments.TrainingFolderList.RowInList;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         {@code MainListFragment}'s {@code Folder}s adapter
 * 
 */
public class FoldersAdapter extends BaseAdapter {

	// data
	private List<RowInList> folders;
	private Resources resources;
	// inflater
	private LayoutInflater inflater;

	public FoldersAdapter(Activity activity, List<RowInList> folders) {
		this.folders = folders;
		this.inflater = activity.getLayoutInflater();
		this.resources = activity.getApplicationContext().getResources();

	}

	@Override
	public int getCount() {
		return (folders == null) ? 0 : folders.size();
	}

	@Override
	public Object getItem(int position) {
		return (folders == null) ? null : folders.get(position);
	}

	@Override
	public long getItemId(int position) {
		// not used
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// folder item at position
		RowInList folder = folders.get(position);

		final ViewHolder holder;
		if (convertView == null || convertView.getTag()==null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_row_int_training_folder_list, null);
			holder.textViewFolderName = (TextView) convertView
					.findViewById(R.id.textView_folder_name);
			holder.textViewNumOfItems= (TextView)convertView.findViewById(R.id.textView_num_of_items);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set values
		holder.textViewFolderName.setText(folder.name);
		holder.textViewNumOfItems.setText(""+folder.subs.size());

		return convertView;	
	}
	
	

	// view holder pattern
	public class ViewHolder {
		public TextView textViewFolderName, textViewNumOfItems;
		
	}

	public List<RowInList> getFolders() {
		return folders;
	}

	public void setFolders(List<RowInList> folders) {
		this.folders = folders;
	}

}
