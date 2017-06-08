package com.quickode.eyemusic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

//import com.crashlytics.android.Crashlytics;
import com.quickode.eyemusic.fragments.ContactUs;
import com.quickode.eyemusic.fragments.GamesFragment;
import com.quickode.eyemusic.fragments.InputType;
import com.quickode.eyemusic.fragments.PlayCamera;
import com.quickode.eyemusic.fragments.PlayTraining;
import com.quickode.eyemusic.fragments.PlayVideo;
import com.quickode.eyemusic.fragments.SettingsFragment;
import com.quickode.eyemusic.fragments.TrainingFolderList;
import com.quickode.eyemusic.fragments.TrainingFolderList.RowInList;
import com.quickode.eyemusic.fragments.TrainingImagesList;
import com.quickode.eyemusic.fragments.VideoTutorial;
import com.quickode.eyemusic.fragments.WebViewFragment;
import com.quickode.eyemusic.models.AppSettings;
import com.quickode.eyemusic.tools.CommonConstans;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements globalEventListener{

    protected static final String TAG = "eyeMusic-MainActivity";

    private Fragment _currentFragment;
    private Fragment _lastFragment;

    private AppSettings trainingSettings;
    private AppSettings cameraSettings;
    private AppSettings videoSettings;
    private AppSettings currentSettings;
    private final static String TRAINING="Training_images";
    private Menu mMenu;

    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //	PreferenceManager.setDefaultValues(this, R.xml.settings, true);//TODO last param if to read again or not- store values from run to run

        trainingSettings= new AppSettings("Training",loadPref("Training"),mPrefs);
        cameraSettings= new AppSettings("Camera",loadPref("Camera"),mPrefs);
        videoSettings=new AppSettings("Video",loadPref("Video"),mPrefs);
        loadStaticPref();
        getActionBar().setDisplayHomeAsUpEnabled(false);

        currentSettings=videoSettings;

        switchContent(new InputType(),
                InputType.class.getSimpleName(), null, true);
        boolean visitedAlready=mPrefs.getBoolean("VISITED",false);
        if (!visitedAlready)	{
            switchContent(new VideoTutorial(),
                    VideoTutorial.class.getSimpleName(), null, true);
            Editor prefsEditor = mPrefs.edit();
            prefsEditor.putBoolean("VISITED", true);
            prefsEditor.commit();
        }



    }
    private void loadStaticPref(){
        try {
            Log.d("EYE_MUSIC","load static");
            JSONObject json= new JSONObject(mPrefs.getString(CommonConstans.STATIC_PREF, ""));

            AppSettings.setSpeed((json.getInt("speed")));
            Log.d("EYE_MUSIC","speed: "+json.getInt("speed"));
            AppSettings.setBlackAndWhite(json.getBoolean("blackAndWhite"));
            Log.d("EYE_MUSIC","blackAndWhite: "+json.getBoolean("blackAndWhite"));
            AppSettings.setNegative(json.getBoolean("negative"));
            Log.d("EYE_MUSIC","negative: "+json.getBoolean("negative"));
            AppSettings.setRead(json.getBoolean("read"));
            Log.d("EYE_MUSIC","read: "+json.getBoolean("read"));
            AppSettings.setShowClustered(json.getBoolean("showClustered"));
            Log.d("EYE_MUSIC","showClustered: "+json.getBoolean("showClustered"));
            AppSettings.setShowOriginal(json.getBoolean("showOriginal"));
            Log.d("EYE_MUSIC","showOriginal: "+json.getBoolean("showOriginal"));
            AppSettings.setAlgo(json.getBoolean("algo"));
            Log.d("EYE_MUSIC","setAlgo: "+json.getBoolean("algo"));
            AppSettings.setCartoonize(json.getBoolean("cartoonize"));
            Log.d("EYE_MUSIC","setCartoonize: "+json.getBoolean("cartoonize"));
            AppSettings.setFaceDetection(json.getBoolean("faceDetection"));
            Log.d("EYE_MUSIC","setFaceDetection: "+json.getBoolean("faceDetection"));
            AppSettings.setFullBodyDetection(json.getBoolean("fullBodyDetection"));
            Log.d("EYE_MUSIC","setFullBodyDetection: "+json.getBoolean("fullBodyDetection"));
            AppSettings.setUpperBodyDetection(json.getBoolean("upperBodyDetection"));
            Log.d("EYE_MUSIC","setUpperBodyDetection: "+json.getBoolean("upperBodyDetection"));
            AppSettings.setObjectDetectionCropped(json.getBoolean("objectDetectionCropped"));
            Log.d("EYE_MUSIC","objectDetectionCropped: "+json.getBoolean("objectDetectionCropped"));
            AppSettings.setShowOriginal(json.getBoolean("displayOriginal"));
            Log.d("EYE_MUSIC","displayOriginal: "+json.getBoolean("displayOriginal"));

        } catch (JSONException e) {
            Log.d("EYE_MUSIC","catch***************************************************");
            e.printStackTrace();
            AppSettings.resetStaticFields();
        }
    }

    private int loadPref(String inputType){
        Log.d("EYE_MUSIC","open pref name: " +inputType);
        int repeat=-1;
//		try {

        //JSONObject json= new JSONObject(mPrefs.getInt(inputType, -1));

        repeat=	(mPrefs.getInt(inputType, -1));


//		} catch (JSONException e) {
//			repeat=-1;
//		}
        return repeat;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mMenu=menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Bundle bundle= new Bundle();
                bundle.putSerializable("settings",currentSettings);
                switchContent(new SettingsFragment(),
                        SettingsFragment.class.getSimpleName(), bundle, true);
//			replaceFragmentContent(SettingsFragment.class,bundle);
                return true;
            case android.R.id.home:
                FragmentManager fm= getFragmentManager();
                if(fm.getBackStackEntryCount()>0){
                    fm.popBackStack();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/*	public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
	    Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(mutableBitmap);
	    drawable.setBounds(0, 0, widthPixels, heightPixels);
	    drawable.draw(canvas);

	    return mutableBitmap;
	}*/


    /**
     * Switch content by replacing fragments
     *
     * @param fragment
     *            the fragment to load
     * @param tag
     *            the fragment's tag
     * @param addToBackStack
     *            if true the transtion will be added to backStack
     */
    public void switchContent(Fragment fragment, String tag, Bundle args,
                              boolean addToBackStack) {
        // add args if any
        if (args != null)
            fragment.setArguments(args);

        // start transaction
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.content_frame, fragment, tag);

        // add to backStack if needed
        _currentFragment=fragment;
        if (addToBackStack){
            transaction.addToBackStack(_currentFragment.getClass().getSimpleName());}


        // commit
        transaction.commit();

    }

    @Override
    public void onFolderSelected(RowInList row) {

        Bundle bundle= new Bundle();
        //	bundle.putStringArray("LIST",row.subs);
        bundle.putString("PATH",row.path);
        if (!row.lastLevel){

            switchContent(new TrainingFolderList(),
                    TrainingFolderList.class.getSimpleName(), bundle, true);

        }
        else{
            switchContent(new TrainingImagesList(),
                    TrainingImagesList.class.getSimpleName(), bundle, true);

//			replaceFragmentContent(TrainingImagesList.class,bundle);
        }


    }

    @Override
    public void onImageSelect(String path, String[] list,int position) {
        Bundle bundle= new Bundle();
        bundle.putString("PATH", path);
        bundle.putStringArray("LIST",list);
        bundle.putInt("POSITION", position);
        bundle.putSerializable("SETTINGS",trainingSettings);
        switchContent(new PlayTraining(),
                PlayTraining.class.getSimpleName(), bundle, true);

//		replaceFragmentContent(PlayTraining.class,bundle);

    }

    @Override
    public void openTraining() {
        currentSettings=trainingSettings;
        Bundle bundle= new Bundle();
        //bundle.putStringArray("LIST",GlobalParameters.trainingFiles);
        bundle.putString("PATH",TRAINING);
        switchContent(new TrainingFolderList(),
                TrainingFolderList.class.getSimpleName(), bundle, true);


    }

    @Override
    public void openCamera() {
        currentSettings=cameraSettings;
        Bundle bundle= new Bundle();
        bundle.putSerializable("SETTINGS",cameraSettings);
        switchContent(new PlayCamera(),
                PlayCamera.class.getSimpleName(), bundle, true);
//		replaceFragmentContent(PlayCamera.class,bundle);
    }

    @Override
    public void openVideo() {
        currentSettings=videoSettings;
        Bundle bundle= new Bundle();

        bundle.putSerializable("SETTINGS",videoSettings);
        switchContent(new PlayVideo(),
                PlayVideo.class.getSimpleName(), bundle, true);

    }

    @Override
    public void openInputType() {
        clearBackStack();

        switchContent(new InputType(),
                InputType.class.getSimpleName(), null, true);

    }

    @Override
    public void openHelp(String link) {
        WebViewFragment helpFragment=new WebViewFragment();
        helpFragment.init(link);
        switchContent(helpFragment,
                WebViewFragment.class.getSimpleName(), null, true);
//		replaceFragmentContent(WebViewFragment.class,null);

    }

    @Override
    public void onPause()
    {
        super.onPause();

//		trainingSettings.savePref();
//		cameraSettings.savePref();
//		videoSettings.savePref();
//		AppSettings.saveStaticPref();
    }
    @Override
    public void openGames() {
        GamesFragment GamesFragment=new GamesFragment();
//		GamesFragment.init(CommonConstans.gamesLink);
        switchContent(GamesFragment,
                GamesFragment.class.getSimpleName(), null, true);
//		replaceFragmentContent(GamesFragment.class,null);

    }



    public void clearBackStack() {

        FragmentManager fm = getFragmentManager();
        // fm.popBackStackImmediate(null,
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int backStackCount = 0;

        try {
            while (fm.getBackStackEntryCount() > 0) {
                if (getFragmentManager().getBackStackEntryCount() > backStackCount) {
                    String className = getFragmentManager()
                            .getBackStackEntryAt(backStackCount).getName();

//					if (!className.equals("HomePageFragment")) {
                    fm.popBackStackImmediate(className,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
//					}

                    backStackCount++;
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            Log.d("fragment", ex.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        _currentFragment = fetchTopFragment();

        if (_currentFragment instanceof InputType){
            alertDialogLeaveTheApp();
        }else if (_currentFragment instanceof GamesFragment){
            boolean hasBack=((GamesFragment)_currentFragment).backPressed();
            if (!hasBack){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }
    public Fragment fetchTopFragment() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            String tag = getFragmentManager().getBackStackEntryAt(
                    getFragmentManager().getBackStackEntryCount() - 1)
                    .getName();
//			String tag=((Object) this).getClass().getSimpleName();
            return (Fragment) getFragmentManager()
                    .findFragmentByTag(tag);
        }

        return null;

    }
    private void alertDialogLeaveTheApp(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.leaveAppTitle))
                .setMessage(getResources().getString(R.string.leaveApp))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    @Override
    public void openContactUs() {
        switchContent(new ContactUs(),
                ContactUs.class.getSimpleName(), null, true);

    }
    @Override
    public void playTutorial() {
        switchContent(new VideoTutorial(),
                VideoTutorial.class.getSimpleName(), null, true);

    }
    @Override
    public void disableSettings() {
        MenuItem item = mMenu.findItem(R.id.action_settings);
        item.setVisible(false);

    }
    @Override
    public void enableSettings() {
        MenuItem item = mMenu.findItem(R.id.action_settings);
        item.setVisible(true);

    }


//	protected void replaceFragmentContent(
//			Class<? extends Fragment> fragmentClass, Bundle args) {
////		replaceFragmentContent(fragmentClass, true, R.anim.slide_in_right,
////				R.anim.slide_out_right, R.anim.slide_in_left,
////				R.anim.slide_out_left, args);
//		replaceFragmentContent(fragmentClass, true, 0,
//				0, 0,
//				0, args);
//		
//	}










//	protected void replaceFragmentContent(
//			Class<? extends Fragment> fragmentClass,
//			boolean fromHistory, int enterAnim, int exitAnim,
//			int backEnterAnim, int backExitAnim, Bundle args) {
//		try {
//			Fragment newFragment = null;
//			boolean addArgs = false;
//			_lastFragment = fetchTopFragment();
//
//			if (fromHistory) {
//				newFragment = (Fragment) getFragmentManager()
//						.findFragmentByTag(fragmentClass.getSimpleName());
//
//			}
//
//			if (newFragment == null) {
//				newFragment = fragmentClass.newInstance();
//			}
////			} else {
////
////				
////						if (_lastFragment.getClass().equals(
////								newFragment.getClass())) {
////							getFragmentManager().popBackStackImmediate();
////						} else {
////							getFragmentManager().beginTransaction()
////									.remove(newFragment).commitAllowingStateLoss();
////							_lastFragment = fetchTopFragment();
////						}
////					
////					newFragment = fragmentClass.newInstance();
////					addArgs = true;
////
////				
////
////			}
//
//			newFragment.setHasOptionsMenu(true);
//			FragmentTransaction trans = getFragmentManager()
//					.beginTransaction();
//
//			
//			
//			
//			
//			if (enterAnim > 0 && exitAnim > 0) {
//				if (backEnterAnim > 0 && backExitAnim > 0) {
//					trans.setCustomAnimations(enterAnim, exitAnim,
//							backEnterAnim, backExitAnim);
//				} else {
//					trans.setCustomAnimations(enterAnim, exitAnim);
//				}
//			} else {
//				trans.setTransition(FragmentTransaction.TRANSIT_NONE);
//			}
//			
//			
//		
////			trans.setCustomAnimations(R.anim.enter_enim, R.anim.exit_enim);
//
//			if (!addArgs
//					&& getFragmentManager().getBackStackEntryCount() > 0
//					&& getFragmentManager()
//							.getBackStackEntryAt(
//									getFragmentManager()
//											.getBackStackEntryCount() - 1)
//							.getName()
//							.equals(newFragment.getClass().getSimpleName())) {
//
//			} else {
//				try {
//					if (args != null) {
//						newFragment.setArguments(args);
//					}
//				} catch (Exception exp) {
//					exp.printStackTrace();
//				}
//
//				trans.replace(R.id.content_frame, newFragment, newFragment
//						.getClass().getSimpleName());
//
//				if (!(newFragment instanceof SettingsFragment)) {
//					trans.addToBackStack(newFragment.getClass().getSimpleName());
//				}
//			}
//
//			_currentFragment = newFragment;
//
//			trans.commitAllowingStateLoss();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//	
//	public Fragment fetchTopFragment() {
//		if (getFragmentManager().getBackStackEntryCount() > 0) {
//			String tag = getFragmentManager().getBackStackEntryAt(
//					getFragmentManager().getBackStackEntryCount() - 1)
//					.getName();
//			return (Fragment) getFragmentManager()
//					.findFragmentByTag(tag);
//		}
//
//		return null;
//
//	}
//
//	@Override
//	public void onBackPressed() {
//
//		
//		_currentFragment = fetchTopFragment();
//
//		
//			super.onBackPressed();
//		
//
//	}


}
