package com.quickode.eyemusic.models;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.tools.CommonConstans;

import android.content.Context;
import android.preference.DialogPreference;
import android.provider.SyncStateContract.Constants;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SpeedPrefernce extends DialogPreference {

	private Context mContext;
	private SeekBar mSeekBar;
	private TextView slow;
	private TextView fast;
	private TextView cancel;
	private CheckBox slowCheck;
	private CheckBox fastCheck;
	private CheckBox normalCheck;
	private TextView customValue;
	public SpeedPrefernce(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
	}
	
	 @Override
	  protected View onCreateDialogView() {
	    View view= LayoutInflater.from(mContext).inflate(R.layout.dialog_speed,null);
	    //slow=(TextView) view.findViewById(R.id.dialog_slower);
	   // fast=(TextView) view.findViewById(R.id.dialog_faster);
	   slowCheck=(CheckBox)view.findViewById(R.id.dialog_checkbox_slow);
	   fastCheck=(CheckBox)view.findViewById(R.id.dialog_checkbox_faster);
	   normalCheck=(CheckBox)view.findViewById(R.id.dialog_checkbox_normal);
	    mSeekBar=(SeekBar)view.findViewById(R.id.dialog_speed_bar);
	   customValue= ((TextView)view.findViewById(R.id.dialog_custom_seekbar_value));
	   mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar arg0, int position, boolean arg2) {
			customValue.setText(String.valueOf(position+CommonConstans.SPEED_BASE));
			
		}
	});
	   
	   mSeekBar.setProgress(AppSettings.getSpeed()-CommonConstans.SPEED_BASE);
	    initButtons();
	    return(view);
	  }
	 
	 private void initButtons(){
	
		 slowCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (slowCheck.isChecked()){
					fastCheck.setChecked(false);
					normalCheck.setChecked(false);
					mSeekBar.setProgress(CommonConstans.SPEED_SLOW-CommonConstans.SPEED_BASE);
					
				}
				
			}
		});
		 
		 fastCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (fastCheck.isChecked()){
					slowCheck.setChecked(false);
					normalCheck.setChecked(false);
					mSeekBar.setProgress(CommonConstans.SPEED_FAST-CommonConstans.SPEED_BASE);
				}
				
			}
		});
		 
		 normalCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (normalCheck.isChecked()){
						slowCheck.setChecked(false);
						fastCheck.setChecked(false);
						mSeekBar.setProgress(CommonConstans.SPEED_NORMAL-CommonConstans.SPEED_BASE);
					}
					
				}
			});
	 }
	  
	  @Override
	  protected void onBindDialogView(View v) {
	    super.onBindDialogView(v);
	    
	   // mixer.setColor(lastColor);
	  }
	  
	  @Override
	  protected void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);

	    if (positiveResult) {
	      if (slowCheck.isChecked()){
	    	  AppSettings.setSpeed(CommonConstans.SPEED_SLOW);
	      }
	      else if(fastCheck.isChecked()){
	    	  AppSettings.setSpeed(CommonConstans.SPEED_FAST);
	      }
	      else if(normalCheck.isChecked()){
	    	  AppSettings.setSpeed(CommonConstans.SPEED_NORMAL);
	      }else{
	    	  AppSettings.setSpeed(mSeekBar.getProgress()+CommonConstans.SPEED_BASE);
	      }
	    }
	    setSummary(String.valueOf(AppSettings.getSpeed()));
	  }

	 

	  @Override
	  protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
	   // lastColor=(restoreValue ? getPersistedInt(lastColor) : (Integer)defaultValue);
	  }

}
