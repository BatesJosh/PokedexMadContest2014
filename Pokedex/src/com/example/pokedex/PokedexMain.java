package com.example.pokedex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class PokedexMain extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener
//LocationClient.OnAddGeofencesResultListener,
//LocationClient.OnRemoveGeofencesResultListener
{
	private static final String TAG = "Pedometer";
	protected static final String POKEMON_EXTRA = "com.example.pokedex.pokedexmain.pokemon";
	private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    private PokedexAdapter pokeAdp;
    private ListView lstPokedex;
    private boolean mQuitting = false; //Set when user selected quit from menu, can be us used by onPause, onStop, onDestroy
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    private TextView mStepValueView;
    private int mStepValue;
    PokemonHandler db;
    
    private Location userLoc;
    private LocationClient locationClient;
    private LocationRequest locationReqInfo;
    private static final long FIVE_MINS = 60000; //60 seconds for testing
    private static final long ONE_MIN = 20000; //20 seconds for testing
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//    private boolean requestFree;    
//    private static enum requestType {ADD_FENCE, CONNECT_CLIENT};
//    private requestType rType;
    private List<Pokemon> pList;
    
//    private PendingIntent monitorIntent;
    //these are the regions within 15 miles of the user
    //and are being actively monitored
    private HashMap<String, PokemonRegion> currentRegions;
    //these are the regions that the user has entered
    //and will be used to select pokemon
//    private HashMap<String, PokemonRegion> activeRegions;
//    private boolean shuttingDown;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 Log.i(TAG, "[ACTIVITY] onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pokedex_main);
		db=new PokemonHandler(this);
		 userLoc = null;
	     locationClient = new LocationClient(this, this, this);
	     locationReqInfo = LocationRequest.create();
	     locationReqInfo.setInterval(FIVE_MINS);
	     locationReqInfo.setFastestInterval(ONE_MIN);
	     locationReqInfo.setPriority(
	                LocationRequest.PRIORITY_HIGH_ACCURACY);
//	     shuttingDown = false;
//	     requestFree = true;
//	     rType = PokedexMain.requestType.CONNECT_CLIENT;
//	     monitorIntent = null;
	     
	     currentRegions = new HashMap<String, PokemonRegion>();
//	     activeRegions = new HashMap<String, PokemonRegion>();
	     
PokemonRegionDataSource s = new PokemonRegionDataSource(this);
	     
	     if(!s.tableExists())
	     {
	    	 //Toast.makeText(this, "Creating fence table", Toast.LENGTH_SHORT);
	    	 ReadFile f = new ReadFile();
	    	 try 
	    	 {
				f.readGeofenceFile("geofence.txt", this);
			 }
	    	 catch (IOException e) 
			 {
				Log.e(null, "Error reading geofence file");
			}
	     }
	     
	     if(servicesConnected())
	     {
	    	 Toast.makeText(this, 
	    			 "Google Services found, regional Pokemon enabled", 
	    			 Toast.LENGTH_LONG).show();
	     }
	     else
	     {	 Toast.makeText(this, 
	    			 "Google Services not found, regional Pokemon disabled", 
	    			 Toast.LENGTH_LONG).show();
	     }
	     mStepValue = 0;
	     mUtils = Utils.getInstance();
	     lstPokedex=(ListView) findViewById(R.id.pokedex_view);
	    pList=db.getPokedex();
	     //Normal Code we normally use.  We will need to make sure to call an update after we unlock pokemon
	     pokeAdp=new PokedexAdapter(PokedexMain.this, R.layout.pokedex_list, pList);
	     //pokeAdp=new PokedexAdapter(PokedexMain.this, R.layout.pokedex_list, db.getPokemonType("GRASS"));
	     lstPokedex.setAdapter(pokeAdp);
	     OnItemClickListener show = showPokemon();
	     lstPokedex.setOnItemClickListener(show);
	}

	


	private OnItemClickListener showPokemon() {
		OnItemClickListener show=new OnItemClickListener(){
			 public void onItemClick(AdapterView parent, View v, int position, long id) {
				 int ball=pokeAdp.getItem(position).getID();
				 Intent intent=new Intent(parent.getContext(), PokemonScreenActivity.class);
				 intent.putExtra(POKEMON_EXTRA, ball);
				 startActivity(intent);
			    }
			 };
		return show;
	}

	

	@Override
	    protected void onStart() {
	        Log.i(TAG, "[ACTIVITY] onStart");
	        locationClient.connect();
	        super.onStart();
	    }
	 
	 protected void onResume() {
	        Log.i(TAG, "[ACTIVITY] onResume");
	        super.onResume();
	        
	        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	        mPedometerSettings = new PedometerSettings(mSettings);
	        
	        mUtils.setSpeak(mSettings.getBoolean("speak", false));
	        
	        // Read from preferences if the service was running on the last onPause
	        mIsRunning = mPedometerSettings.isServiceRunning();
	        
	        // Start the service if this is considered to be an application start (last onPause was long ago)
	        if (!mIsRunning && mPedometerSettings.isNewStart()) {
	            startStepService();
	            bindStepService();
	        }
	        else if (mIsRunning) {
	            bindStepService();
	        }
	        
	        mPedometerSettings.clearServiceRunning();

	        mStepValueView     = (TextView) findViewById(R.id.step_value);
	   /*     mPaceValueView     = (TextView) findViewById(R.id.pace_value);
	        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
	        mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
	        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
	        mDesiredPaceView   = (TextView) findViewById(R.id.desired_pace_value);

	        mIsMetric = mPedometerSettings.isMetric();
	        ((TextView) findViewById(R.id.distance_units)).setText(getString(
	                mIsMetric
	                ? R.string.kilometers
	                : R.string.miles
	        ));
	        ((TextView) findViewById(R.id.speed_units)).setText(getString(
	                mIsMetric
	                ? R.string.kilometers_per_hour
	                : R.string.miles_per_hour
	        ));
	*/        
	         //If maintaining a desired Pace or speed
	        /**
	        mMaintain = mPedometerSettings.getMaintainOption();
	        ((LinearLayout) this.findViewById(R.id.desired_pace_control)).setVisibility(
	                mMaintain != PedometerSettings.M_NONE
	                ? View.VISIBLE
	                : View.GONE
	            );
	        if (mMaintain == PedometerSettings.M_PACE) {
	            mMaintainInc = 5f;
	            mDesiredPaceOrSpeed = (float)mPedometerSettings.getDesiredPace();
	        }
	        else 
	        if (mMaintain == PedometerSettings.M_SPEED) {
	            mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
	            mMaintainInc = 0.1f;
	        }
	        Button button1 = (Button) findViewById(R.id.button_desired_pace_lower);
	        button1.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                mDesiredPaceOrSpeed -= mMaintainInc;
	                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
	                displayDesiredPaceOrSpeed();
	                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
	            }
	        });
	        Button button2 = (Button) findViewById(R.id.button_desired_pace_raise);
	        button2.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                mDesiredPaceOrSpeed += mMaintainInc;
	                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
	                displayDesiredPaceOrSpeed();
	                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
	            }
	        });
	        
	        if (mMaintain != PedometerSettings.M_NONE) {
	            ((TextView) findViewById(R.id.desired_pace_label)).setText(
	                    mMaintain == PedometerSettings.M_PACE
	                    ? R.string.desired_pace
	                    : R.string.desired_speed
	            );
	        }
	       */ 
	        
	     //   displayDesiredPaceOrSpeed();
	        if(!mStepValueView.getText().toString().matches("")){
	        if(Integer.parseInt(mStepValueView.getText().toString())>=10){
            	Button button=(Button)findViewById(R.id.button1);
            	button.setVisibility(View.VISIBLE);
            }
	        }
	    }
	 
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pokedex_main, menu);
		return true;
	}
	
	 @Override
	    protected void onPause() {
	        Log.i(TAG, "[ACTIVITY] onPause");
	        if (mIsRunning) {
	            unbindStepService();
	        }
	        if (mQuitting) {
	            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
	        }
	        else {
	            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
	        }

	        super.onPause();
	       // savePaceSetting();
	    }
	 
	    protected void onDestroy() {
	        Log.i(TAG, "[ACTIVITY] onDestroy");
	        super.onDestroy();
	    }
	    
	    protected void onRestart() {
	        Log.i(TAG, "[ACTIVITY] onRestart");
	        super.onDestroy();
	    }
	    

	
	    @Override
	    protected void onStop() {
	        Log.i(TAG, "[ACTIVITY] onStop");
	        if (locationClient.isConnected())
	        {
	            /*
	             * Remove location updates for a listener.
	             * The current Activity is the listener, so
	             * the argument is "this".
	             */
	        	locationClient.removeLocationUpdates(this);
	        }
	        locationClient.disconnect();
	        super.onStop();
	    }
	    
	public void getPokemon(View view){
		 mService.resetValues(); 
		 Button button=(Button) view;
		 button.setVisibility(View.INVISIBLE);
		 
		 //Get new/unlock pokemon
		 NextPokemon next =new NextPokemon();
		 Pokemon ball=next.next(currentRegions, this);

		 db.unlock(ball.getID());
		 pList.clear();
		 pList.addAll(db.getPokedex());
//		pList=db.getPokedex();
		 pokeAdp.notifyDataSetChanged();
		Intent intent=new Intent(this, PokemonScreenActivity.class);
		intent.putExtra(POKEMON_EXTRA, ball.getID());
		startActivity(intent);
	}
	
    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(PokedexMain.this,
                    StepService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(PokedexMain.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(PokedexMain.this,
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
          /*  mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");*/
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
              /**  stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);*/
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
        .setIcon(android.R.drawable.ic_menu_preferences)
        .setShortcut('8', 's')
        .setIntent(new Intent(this, Settings.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
        .setIcon(android.R.drawable.ic_lock_power_off)
        .setShortcut('9', 'q');
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                resetValues(false);
                unbindStepService();
                stopStepService();
                mQuitting = true;
                finish();
                return true;
        }
        return false;
    }
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                	
                    mStepValue = (int)msg.arg1;
                    if(mStepValue==10){
                    	Button button=(Button)findViewById(R.id.button1);
                    	button.setVisibility(View.VISIBLE);
                    }
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                  /**  mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }*/
                    break;
                case DISTANCE_MSG:
                   /** mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }*/
                    break;
                case SPEED_MSG:
/*                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }*/
                    break;
                case CALORIES_MSG:
                    /*mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }*/
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
    @Override
	public void onConnectionFailed(ConnectionResult connectionResult) 
	{
		if (connectionResult.hasResolution()) 
		{
            try 
            {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } 
            catch (IntentSender.SendIntentException e) 
            {
                // Log the error
                e.printStackTrace();
                Toast.makeText(this, 
        				"Could not connect service, regional Pokemon unavailable",  
        				Toast.LENGTH_SHORT).show();
                
                userLoc = null;
            }
        } 
		else 
		{
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
			Toast.makeText(this, 
					"Could not connect service, regional Pokemon unavailable",  
					Toast.LENGTH_SHORT).show();
			userLoc = null;
        }
	}

	@Override
	public void onConnected(Bundle connectionHint) 
	{

//		if(rType == PokedexMain.requestType.CONNECT_CLIENT)
		{
			Toast.makeText(this, 
					"Service connected",  
					Toast.LENGTH_SHORT).show();
			
			this.locationClient.requestLocationUpdates(locationReqInfo, this);
		}
//		else if(rType == PokedexMain.requestType.ADD_FENCE)
		{
			//leftovers
		}
		
	}

	@Override
	public void onDisconnected() 
	{
		Toast.makeText(this, 
				"Service disconnected, regional Pokemon unavailable",  
				Toast.LENGTH_SHORT).show();
		userLoc = null;
		
//		rType = PokedexMain.requestType.CONNECT_CLIENT;
//		requestFree = true;
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		userLoc = location;
		/*Toast.makeText(this, 
				"Location lat:" + location.getLatitude()
				+ " Location long:" + location.getLongitude(), 
				Toast.LENGTH_SHORT).show();*/
		
		NextPokemon n = new NextPokemon();
		
		currentRegions = n.updateRegions(userLoc, this);
		
		Log.i("[FENCE]", "CR:" + currentRegions.size());
		
//		if(monitorIntent == null)
//		{
//			this.addGeofences();
//		}
//		else
//		{
//			this.removeFences();
//		}
	}
		
	private boolean servicesConnected()
	{
		int resultCode = 
				GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(ConnectionResult.SUCCESS == resultCode)
		{
			//debug mode log
			Log.d("Geofence Detection", "Google Player service is available.");
			return true;
		}
		//NOT available
		else
		{
			return false;
		}
	}
	
//	private PendingIntent getTransitionPendingIntent()
//	{
//		Intent intent = new Intent(this,
//				ReceiveTransitionsIntentService.class);
//		
//		return PendingIntent.getService(this,
//				0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//	}
	
//	private boolean addGeofences()
//	{
//		if(servicesConnected() && locationClient.isConnected() && requestFree)
//		{
//			requestFree = false;
//			rType = PokedexMain.requestType.ADD_FENCE;
//			
//			if(currentRegions != null && currentRegions.size() > 0)
//			{
//				LinkedList<Geofence> list = new LinkedList<Geofence>();
//				Iterator<Entry<String, PokemonRegion>> it = currentRegions.entrySet().iterator();
//				
//				while(it.hasNext())
//					list.add(it.next().getValue().getGeofence());
//				
//				PendingIntent pInt = getTransitionPendingIntent();
//				this.monitorIntent = pInt;
//				
//				locationClient.addGeofences(list, pInt, this);
//			}
//			
//			rType = PokedexMain.requestType.CONNECT_CLIENT;
//			
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
	
//	private void removeFences()
//	{
//		if(servicesConnected() && locationClient.isConnected() && requestFree)
//		{
//			requestFree = false;
//			if(monitorIntent != null)
//			{
//				locationClient.removeGeofences(monitorIntent, this);
//			}			
//		}
//	}

//	@Override
//	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) 
//	{
//		if(statusCode == LocationStatusCodes.SUCCESS)
//			Log.i("[Geofence]", "Successfully added fences to monitor");
//		else
//			Log.e("[Geofece]", "Registering fences for monitoring was not successful");
//
//		requestFree = true;
//	}
	
//	protected void onNewIntent(Intent intent)
//	{
//		String geofenceData = intent.getStringExtra("Geofences");
//		String[] data = geofenceData.split(",");
//		Log.i("[Geofence]", "Recieved an intent");
//		if(data.length > 1)
//		{
//			int transition = Integer.parseInt(data[0]);
//			
//			if(transition == Geofence.GEOFENCE_TRANSITION_ENTER)
//			{
//				for(int i = 1; i < data.length; i++)
//					activeRegions.put(data[i], currentRegions.get(data[i]));
//				
//				Toast.makeText(this, "Entered a region", Toast.LENGTH_SHORT).show();
//			}
//			else
//			{
//				for(int i = 1; i < data.length; i++)
//					activeRegions.remove(data[i]);
//			}
//		}
//	}

//	@Override
//	public void onRemoveGeofencesByPendingIntentResult(int arg0,
//			PendingIntent arg1) 
//	{
//		requestFree = true;		
//		if(!shuttingDown)
//		{
//			addGeofences();
//		}
//	}
//
//	@Override
//	public void onRemoveGeofencesByRequestIdsResult(int statusCode,
//			String[] geofenceRequestIds) 
//	{
//		requestFree = true;	
//		if(!shuttingDown)
//		{
//			addGeofences();
//		}
//	}
}
