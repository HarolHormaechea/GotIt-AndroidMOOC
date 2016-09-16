package com.hhg.gotit;

import java.util.Collection;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hhg.gotit.OperationsManager.OPERATION_RESULT;
import com.hhg.gotit.OperationsManager.OPERATION_TYPE;
import com.hhg.gotit.fragments.ConfigScreen;
import com.hhg.gotit.fragments.FillQuizFragment;
import com.hhg.gotit.fragments.LogIn;
import com.hhg.gotit.fragments.NotificationsListFragment;
import com.hhg.gotit.fragments.OtherProfile;
import com.hhg.gotit.fragments.QuizViewFragment;
import com.hhg.gotit.fragments.SelfProfile;
import com.hhg.gotit.fragments.SignUp;
import com.hhg.gotit.fragments.UserDataListFragment;
import com.hhg.gotit.models.GotItApi.USER_TYPE;
import com.hhg.gotit.models.Notification;
import com.hhg.gotit.models.Quiz;
import com.hhg.gotit.models.UserData;
import com.hhg.gotit.services.OpsService;
import com.hhg.gotit.services.OpsService.ALARM_FREQUENCY;
import com.hhg.gotit.services.OpsService.BoundNetworkServiceBinder;

/**
 * 
 * 
 * @author Harold
 *
 */
public class MainActivity extends Activity{
	public enum VIEWS{SIGNUP, LOGIN, PROFILE, USER_LIST, OTHER_USER_PROFILE, NOTIFICATIONS, POST_QUIZ, SETTINGS, }
	public static String QUIZ_UPLOAD_TIME_NOTIFICATION = "com.hhg.gotit.NEW_QUIZ_TIME";
	public static final int IS_ALIVE = Activity.RESULT_FIRST_USER;
	private String TAG = getClass().getSimpleName();
	private String uIfragmentTag = "FRAGMENT";
	private Fragment currentFragment;
	private Menu menu;
	private OpsService mBoundService;
	private OperationsManager opsManager;
	private ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			BoundNetworkServiceBinder binder = (BoundNetworkServiceBinder) service; 
			mBoundService = binder.getService();
			if(opsManager == null){
				opsManager = new OperationsManager(MainActivity.this);
			}
			
			
			if(mBoundService != null){
				String text = "Service connected to activity.";
				mBoundService.registerListener(opsManager);
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				Log.d(TAG, text);
				
				Log.d(TAG, "Creating default alarm.");
				opsManager.setAlarm(OperationsManager.NOTIFICATION_FREQ.TEST);
			}
			else{
				String text = "Service connected to activity... but is null.";
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				Log.d(TAG, text);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
			String text = "Service disconnected from activity.";
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
			Log.d(TAG, text);
		}
		
	};
	
	/**
	 * We are using this method to also create our manager and pass it
	 * a reference to the service it'll use. Plus we will create
	 * the broadcast receiver which will manage received notifications.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(MainActivity.this, OpsService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		setContentView(R.layout.main);
		//On app start, we will always show the login screen.
		switchView(VIEWS.LOGIN);
	}

	
	/**
	 * Processes an operation result. It has a slot for one or more
	 * extra objects which will depend on the operation performed.
	 * 
	 * @param opType
	 * @param result
	 * @param message
	 * @param extra
	 */
	public void processResult(final OPERATION_TYPE opType,
			final OPERATION_RESULT result, final String message, final Object... extra){
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				switch(opType){
				case REGISTER_USER:
						if(OPERATION_RESULT.OK == result){
							postUiMessage(message);
							switchView(VIEWS.LOGIN);
						}else if(OPERATION_RESULT.ERROR == result){
							postUiMessage("Error when registering new user\n"+message);
						}else{
							postUiMessage("Something odd happened. "+message);
						}
					break;
				case LOGIN:
						if(OPERATION_RESULT.OK == result){
							postUiMessage(message);
							switchView(VIEWS.PROFILE);
						}
						else if(OPERATION_RESULT.ERROR == result){
							postUiMessage(message);
						}else{
							postUiMessage("Something odd happened. "+message);
						}
					break;
				case GET_PROFILE:
						if(OPERATION_RESULT.OK == result){
							postUiMessage(message);
							if(currentFragment instanceof SelfProfile){
								SelfProfile p = (SelfProfile) currentFragment;
								p.initFields((UserData) extra[0]);
							}
						}
						else if(OPERATION_RESULT.ERROR == result){
							postUiMessage(message);
						}else{
							postUiMessage("Something odd happened. "+message);
						}
					break;
				case REQ_USER_LIST:
					if(OPERATION_RESULT.OK == result){
						postUiMessage(message);
						Log.d(TAG, "User list requested and retrieved.");
						UserDataListFragment list = 
								new UserDataListFragment(MainActivity.this);
						list.setData((Collection<UserData>) extra[0]);
						switchFragment(list);
					}
					break;
				case FOLLOW:
					postUiMessage(message);
					break;
				case STOP_FOLLOW:
					postUiMessage(message);
					break;
				case OTHERS_PROFILE:
					switchFragment(new OtherProfile(MainActivity.this, (UserData) extra[0]));
					break;
				case FETCH_NOTIFICATIONS:
					NotificationsListFragment f = new NotificationsListFragment(MainActivity.this);
					f.setData(((Collection<Notification>) extra[0]));
					switchFragment(f);
					break;
				case SHOW_QUIZ:
					QuizViewFragment qvf = new QuizViewFragment(MainActivity.this);
					qvf.setData((Quiz) extra[0]);
					switchFragment(qvf);
					break;
				case POST_QUIZ:
					postUiMessage(message);
					if(OPERATION_RESULT.OK == result){
						//If the op was done properly, we return to our profile.
						switchView(VIEWS.PROFILE);
					}//Else we will remain here for the user to attempt again without
					 //losing the input he has already done.
					break;
				case GET_NEW_QUIZ:
					postUiMessage(message);
					if(OPERATION_RESULT.OK == result){
						FillQuizFragment fqf = new FillQuizFragment(MainActivity.this);
						fqf.setData((Quiz) extra[0]);
						switchFragment(fqf);
					}
					break;
				case PHOTO_DOWNLOAD:
					postUiMessage(message);
					if(OPERATION_RESULT.OK == result){
						if(currentFragment instanceof SelfProfile){
							((SelfProfile)currentFragment).setImage((Bitmap)extra[0]);
						}else if(currentFragment instanceof OtherProfile){
							((OtherProfile)currentFragment).setImage((Bitmap)extra[0]);
						}
					}
					break;
				case PHOTO_UPLOAD:
					postUiMessage(message);
					break;
				default:
					break;
				}
			}
			
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent intent;
		switch(item.getItemId()){
		case(R.id.test_service):
			intent = new Intent(getApplicationContext(), 
					ServiceTest.class);
			startActivity(intent);
			break;
		case(R.id.sign_up):
			switchView(VIEWS.SIGNUP);
			break;
		case(R.id.profile):
			switchView(VIEWS.PROFILE);
			break;
		case(R.id.user_list):
			switchView(VIEWS.USER_LIST);
			break;
		case(R.id.notifications):
			switchView(VIEWS.NOTIFICATIONS);
			break;
		case(R.id.post_quiz):
			switchView(VIEWS.POST_QUIZ);
			break;
		case(R.id.settings):
			switchView(VIEWS.SETTINGS);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void switchView(final VIEWS newView){
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				switch(newView){
				case SIGNUP:
					switchFragment(new SignUp(MainActivity.this));
					break;
				case LOGIN:
					switchFragment(new LogIn(MainActivity.this));
					break;
				case PROFILE:
					switchFragment(new SelfProfile(MainActivity.this));
					break;
				case USER_LIST:
					opsManager.getFollowableUsers();
					break;
				case NOTIFICATIONS:
					opsManager.getPendingNotifications();
					break;
				case POST_QUIZ:
					opsManager.getNewQuiz();
				case SETTINGS:
					switchFragment(new ConfigScreen(MainActivity.this));
				default:
					break;		
				}
			}
			
		});
		
	}

	private void switchFragment(Fragment fragment){
		currentFragment = fragment;
		FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.FragmentContainer, currentFragment, uIfragmentTag).commit();
	}
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}

	public OperationsManager getOpsManager() {
		return opsManager;
	}
	
	
	
	public OpsService getmBoundService() {
		return mBoundService;
	}


	public void setmBoundService(OpsService mBoundService) {
		this.mBoundService = mBoundService;
	} 


	public void postUiMessage(final String message){
		runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
			
		});
	}


	/**
	 * Changes element visibility so the UI elements which
	 * the users should not have access to are blocked.
	 * @param userType
	 */
	public void setUIMode(USER_TYPE userType) {
		switch(userType){
		case ADMIN:
			menu.findItem(R.id.test_service).setVisible(true);
			menu.findItem(R.id.sign_up).setVisible(true);
			menu.findItem(R.id.profile).setVisible(false);
			menu.findItem(R.id.user_list).setVisible(false);
			menu.findItem(R.id.notifications).setVisible(false);
			menu.findItem(R.id.post_quiz).setVisible(false);
			menu.findItem(R.id.settings).setVisible(false);
			menu.findItem(R.id.test_service).setVisible(false);	
			break;
		case FOLLOWER:
			menu.findItem(R.id.test_service).setVisible(true);
			menu.findItem(R.id.sign_up).setVisible(true);
			menu.findItem(R.id.profile).setVisible(true);
			menu.findItem(R.id.user_list).setVisible(true);
			menu.findItem(R.id.notifications).setVisible(true);
			menu.findItem(R.id.post_quiz).setVisible(false);
			menu.findItem(R.id.settings).setVisible(true);
			menu.findItem(R.id.test_service).setVisible(true);	
			break;
		case TEEN:
			menu.findItem(R.id.test_service).setVisible(true);
			menu.findItem(R.id.sign_up).setVisible(true);
			menu.findItem(R.id.profile).setVisible(true);
			menu.findItem(R.id.user_list).setVisible(true);
			menu.findItem(R.id.notifications).setVisible(true);
			menu.findItem(R.id.post_quiz).setVisible(true);
			menu.findItem(R.id.settings).setVisible(true);
			menu.findItem(R.id.test_service).setVisible(true);	
			break;
		default:
			menu.findItem(R.id.test_service).setVisible(true);
			menu.findItem(R.id.sign_up).setVisible(true);
			menu.findItem(R.id.profile).setVisible(false);
			menu.findItem(R.id.user_list).setVisible(false);
			menu.findItem(R.id.notifications).setVisible(false);
			menu.findItem(R.id.post_quiz).setVisible(false);
			menu.findItem(R.id.settings).setVisible(false);
			menu.findItem(R.id.test_service).setVisible(false);			
			break;
		
		}
	}

}
