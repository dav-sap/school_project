package com.quickode.eyemusic.fragments;

import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;
import com.quickode.eyemusic.tools.CommonConstans;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ContactUs extends Fragment {

	View view;
	EditText email;
	CheckBox impairiments;
	EditText impairimentsSpecifty;
	CheckBox mailingList;
	EditText questions;
	EditText suggestion;
	EditText freeText;
	Button submit;
	protected globalEventListener mCallback;
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.contact_us, container, false);
		mCallback.disableSettings();
		email=(EditText)view.findViewById(R.id.email_edit_text);
		impairiments=(CheckBox)view.findViewById(R.id.impairiments_checkbox);
		impairimentsSpecifty=(EditText)view.findViewById(R.id.impairiments_specify_edit_text);
		mailingList=(CheckBox)view.findViewById(R.id.mailing_list_check_box);
		questions=(EditText)view.findViewById(R.id.question_edit_text);
		suggestion=(EditText)view.findViewById(R.id.suggestion_edit_text);
		freeText=(EditText)view.findViewById(R.id.free_text_edit_text);
		submit=(Button)view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submitForm();
				
			}
		});
		return view;
	}
	
	private void submitForm(){
		String form="";
		form+="Email: "+email.getText().toString()+"\n";
		form+="Blind: "+String.valueOf(impairiments.isChecked())+"\n";
		form+="Blind description: "+impairimentsSpecifty.getText().toString()+"\n";
		form+="Add to mailing list: "+String.valueOf(mailingList.isChecked())+"\n";
		form+="Question: "+questions.getText().toString()+"\n";
		form+="Suggestion: "+suggestion.getText().toString()+"\n";
		form+="Free text: "+freeText.getText().toString()+"\n";
		sendEmail(form);
	}
	
	private void sendEmail(String form){
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{CommonConstans.CONTACT_MAIL});		  
			
		email.putExtra(Intent.EXTRA_SUBJECT, "eye music");
		email.putExtra(Intent.EXTRA_TEXT, form);
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
		
	
	}
	
	@Override
	public void onPause() {
		mCallback.enableSettings();
		super.onPause();
	}
	
}
