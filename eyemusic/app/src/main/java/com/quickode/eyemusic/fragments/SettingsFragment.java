package com.quickode.eyemusic.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickode.eyemusic.MainActivity;
import com.quickode.eyemusic.R;
import com.quickode.eyemusic.globalEventListener;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.tools.CommonConstans;

public class SettingsFragment extends PreferenceFragment {

	private AppSettings mSettings;
	private globalEventListener mCallback;
	private static final String HELP_URL="http://brain.huji.ac.il/EM_Training/Instructions.html";
	private Preference inputType;
	ListPreference repeatList;
	Preference speedList;
	EditTextPreference ip;
	EditTextPreference port;
	SwitchPreference analyzeImg;
	SwitchPreference read;
	SwitchPreference blackWhite;
	SwitchPreference negative;
	SwitchPreference original;
	SwitchPreference clustered;
	SwitchPreference cartoonize;
	SwitchPreference faceDetection;
    SwitchPreference fullBodyDetection;
    SwitchPreference upperBodyDetection;
	SwitchPreference objectDetectionCropped;
	SwitchPreference displayOriginal;
	Preference save;
	CheckBoxPreference interpulattionAlgo;
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
		addPreferencesFromResource(R.xml.settings);
		// Load the preferences from an XML resource
		mSettings=(AppSettings) getArguments().getSerializable("settings");


		initButtons();
		initLists();
		initSwitchButtons();

		Preference help = (Preference)findPreference("help");
		help.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				mCallback.openHelp(HELP_URL);
				return false;
			}
		});
		Preference tutorial = (Preference)findPreference("tutorial");
		tutorial.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				mCallback.playTutorial();
				return false;
			}
		});
		Preference contactUs = (Preference)findPreference("contact_us");
		contactUs.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				mCallback.openContactUs();
				return false;
			}
		});
		Preference reset= (Preference)findPreference("reset");
		reset.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				mSettings.resetDefault();
				setValues();
				//speedList.setSummary(CommonConstans.SPEED_NORMAL);
//				repeatList.setValue("2");
//
//
//
//				blackWhite.setChecked(false);
//				negative.setChecked(false);
//				original.setChecked(true);
//				clustered.setChecked(true);
//				read.setChecked(true);
				return true;
			}
		});

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		view.setBackgroundColor(Color.WHITE);
		setValues();

		return view;
	}
	@Override
	public void onResume() {
		super.onResume();

		((MainActivity)getActivity()).getActionBar().setTitle(getResources().getString(R.string.setting));
		//  getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void initButtons(){
		inputType = (Preference) findPreference("input_types");

		inputType.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				mCallback.openInputType();
				return true;
			}
		});

		save = (Preference)findPreference("save");
		save.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Log.i("SettingsFragment: ", "save button is pressed");
				mSettings.savePref();
				AppSettings.saveStaticPref();
				getActivity().onBackPressed();
				return true;
			}
		});


	}


	private void setValues(){
		removeListeners();
		inputType.setSummary(mSettings.getInputType());

		int repeat=mSettings.getRepeat();
		if (repeat>3){
			repeatList.setSummary(getResources().getString(R.string.unlimited));
			repeatList.setValue(getResources().getString(R.string.unlimited));
		}
		else{
			repeatList.setSummary(String.valueOf(repeat));
			repeatList.setValue(String.valueOf(mSettings.getRepeat()));
		}
		//speedList.setValue(String.valueOf(AppSettings.getSpeed()));
		ip.setText(AppSettings.getIp());
		port.setText(AppSettings.getPort());
		analyzeImg.setChecked(AppSettings.getAnalyzeImg());
		speedList.setSummary(String.valueOf(AppSettings.getSpeed()));
		blackWhite.setChecked(AppSettings.isBlackAndWhite());
		negative.setChecked(AppSettings.isNegative());
		original.setChecked(AppSettings.isShowOriginal());
		clustered.setChecked(AppSettings.isShowClustered());
		cartoonize.setChecked(AppSettings.isCartoonize());
		faceDetection.setChecked(AppSettings.isFaceDetection());
        fullBodyDetection.setChecked(AppSettings.isFullBodyDetection());
        upperBodyDetection.setChecked(AppSettings.isUpperBodyDetection());
		objectDetectionCropped.setChecked(AppSettings.isObjectDetectionCropped());
		displayOriginal.setChecked(AppSettings.isDisplayOriginal());
		read.setChecked(AppSettings.isRead());
		interpulattionAlgo.setChecked(AppSettings.getAlgo());
		initButtons();
		initLists();
		initSwitchButtons();

	}
	private void removeListeners(){
		analyzeImg.setOnPreferenceClickListener(null);
		inputType.setOnPreferenceClickListener(null);
		repeatList.setOnPreferenceChangeListener(null);
		speedList.setOnPreferenceChangeListener(null);
		blackWhite.setOnPreferenceChangeListener(null);
		negative.setOnPreferenceChangeListener(null);
		original.setOnPreferenceChangeListener(null);
		clustered.setOnPreferenceChangeListener(null);
		cartoonize.setOnPreferenceChangeListener(null);
		faceDetection.setOnPreferenceChangeListener(null);
        fullBodyDetection.setOnPreferenceChangeListener(null);
        upperBodyDetection.setOnPreferenceChangeListener(null);
		objectDetectionCropped.setOnPreferenceChangeListener(null);
		displayOriginal.setOnPreferenceChangeListener(null);
		read.setOnPreferenceChangeListener(null);
		interpulattionAlgo.setOnPreferenceChangeListener(null);
	}
	private void initSwitchButtons(){
		analyzeImg=(SwitchPreference)findPreference("analyze_img");
		ip=(EditTextPreference)findPreference("ip");
		port=(EditTextPreference)findPreference("port");

		read=(SwitchPreference)findPreference("read");
		blackWhite=(SwitchPreference)findPreference("black_white");
		negative=(SwitchPreference)findPreference("negative");
		original=(SwitchPreference)findPreference("show_original");
		clustered=(SwitchPreference)findPreference("show_clustered");
		cartoonize = (SwitchPreference)findPreference("cartoonize");
		faceDetection = (SwitchPreference)findPreference("face_detection");
        fullBodyDetection = (SwitchPreference)findPreference("full_body_detection");
        upperBodyDetection = (SwitchPreference)findPreference("upper_body_detection");
		objectDetectionCropped = (SwitchPreference)findPreference("object_detection_cropped");
        if(!AppSettings.isFaceDetection() && !AppSettings.isFullBodyDetection() &&
                !AppSettings.isUpperBodyDetection()) {
            objectDetectionCropped.setEnabled(false);
            objectDetectionCropped.setChecked(false);
            AppSettings.setObjectDetectionCropped(false);
        } else {
            objectDetectionCropped.setEnabled(true);
        }
//		faceDetectionSecond=(SwitchPreference)findPreference("face_detection_second");
		displayOriginal=(SwitchPreference)findPreference("display_original");

		interpulattionAlgo=(CheckBoxPreference)findPreference("algo");
		port.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setPort((String) newValue);
				return true;
			}
		});
		ip.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setIp((String)  newValue);
				return true;
			}
		});
		analyzeImg.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setAnalyzeImg((boolean) newValue);
				return true;
			}
		});
		//All the fields assign the opposite because isChecked return the previos state
		read.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setRead(!((SwitchPreference) preference).isChecked());
				return true;
			}
		});
		blackWhite.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setBlackAndWhite(!((SwitchPreference) preference).isChecked());
				return true;
			}
		});

		negative.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setNegative(!((SwitchPreference) preference).isChecked());
				return true;
			}
		});

		cartoonize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setCartoonize(!((SwitchPreference) preference).isChecked());
				return true;
			}
		});

		faceDetection.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setFaceDetection(!((SwitchPreference) preference).isChecked());
                if(!AppSettings.isFaceDetection()) {
                    objectDetectionCropped.setEnabled(false);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                } else {
                    objectDetectionCropped.setEnabled(true);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                    fullBodyDetection.setChecked(false);
                    AppSettings.setFullBodyDetection(false);
                    upperBodyDetection.setChecked(false);
                    AppSettings.setUpperBodyDetection(false);
                }
				return true;
			}
		});

        fullBodyDetection.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AppSettings.setFullBodyDetection(!((SwitchPreference) preference).isChecked());
                if(!AppSettings.isFullBodyDetection()) {
                    objectDetectionCropped.setEnabled(false);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                } else {
                    objectDetectionCropped.setEnabled(true);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                    faceDetection.setChecked(false);
                    AppSettings.setFaceDetection(false);
                    upperBodyDetection.setChecked(false);
                    AppSettings.setUpperBodyDetection(false);
                }
                return true;
            }
        });

        upperBodyDetection.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                AppSettings.setUpperBodyDetection(!((SwitchPreference) preference).isChecked());
                if(!AppSettings.isUpperBodyDetection()) {
                    objectDetectionCropped.setEnabled(false);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                } else {
                    objectDetectionCropped.setEnabled(true);
                    objectDetectionCropped.setChecked(false);
                    AppSettings.setObjectDetectionCropped(false);
                    faceDetection.setChecked(false);
                    AppSettings.setFaceDetection(false);
                    fullBodyDetection.setChecked(false);
                    AppSettings.setFullBodyDetection(false);
                }
                return true;
            }
        });

		objectDetectionCropped.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setObjectDetectionCropped(!((SwitchPreference) preference).isChecked());
				return true;
			}
		});

		original.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setShowOriginal(!((SwitchPreference) preference) .isChecked());
				return true;
			}
		});

		displayOriginal.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setDisplayOriginal(!((SwitchPreference) preference) .isChecked());
				return true;
			}
		});

		clustered.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {


			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setShowClustered(!((SwitchPreference) preference) .isChecked());
				return true;
			}
		});


		interpulattionAlgo.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				AppSettings.setAlgo(!((CheckBoxPreference) preference) .isChecked());

				return true;
			}
		});
	}

	private void initLists(){


		repeatList= (ListPreference) findPreference("repeat");
		speedList = (Preference) findPreference("speed");
		//list.setSummary(list.getEntry());




		repeatList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String nv = (String) newValue;
				ListPreference splashList = (ListPreference) preference;
				splashList.setSummary(splashList.getEntries()[splashList.findIndexOfValue(nv)]);
				mSettings.setRepeat(Integer.valueOf(nv));
				return true;
			}

		});

		speedList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String nv = (String) newValue;
				ListPreference splashList = (ListPreference) preference;
				splashList.setSummary(splashList.getEntries()[splashList.findIndexOfValue(nv)]);
				AppSettings.setSpeed(Integer.valueOf(nv));
				return true;
			}

		});
	}

	@Override
	public void onPause() {
		super.onPause();

	}




}