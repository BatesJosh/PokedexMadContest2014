package com.example.pokedex;
import java.util.List;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{
	
	ListView fenceListView;
	TextView note1;
	TextView note2;
	Button refresh;
	
	//GooglePlayServices service;
	
	//List<PokemonRegion> fenceList;
	PokemonRegionDataSource source;
	ArrayAdapter<PokemonRegion> listdata;

	LocationClient locClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fenceListView = (ListView)findViewById(R.id.listView1);
		note1 = (TextView)findViewById(R.id.textView2);
		note2 = (TextView)findViewById(R.id.textView3);
		refresh = (Button)findViewById(R.id.button1);
		refresh.setOnClickListener(this);
		
		
		
		if(servicesConnected())
		{
	//		locClient.connect();
			Log.d(null,"SHOULD BE CALLING ONCREATE SOON");
			source = new PokemonRegionDataSource(this);
			//source.openForWriting();
			
			/*source.createPokemonRegionEntry("ISU_QUAD",
					40.5075422, -88.9916847, 100, 
					Geofence.NEVER_EXPIRE, 
					Geofence.GEOFENCE_TRANSITION_ENTER,
					1);*/
			
			//source.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) 
	{
		Log.d(null,"CLICKED!!!");
		if(v.equals(refresh))
		{
			Log.d(null,"OUR REFRESH BUTTON HAS BEEN CLICKED");
			new loadViewsFromDatabase().execute();
			/*List<PokemonRegion> fenceList = source.getAllPokemonRegions();
			listdata = new ArrayAdapter<PokemonRegion>(null, android.R.layout.simple_list_item_1);
			listdata.addAll(fenceList);
			fenceListView.setAdapter(listdata);	*/
		}
		
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
			Log.d("Geofence Detection", "Google Player service is unavailable.");
			return false;
		}
	}
	

	
	private class loadViewsFromDatabase extends AsyncTask<Void, Void, List<PokemonRegion>> 
	{
		
		protected List<PokemonRegion> doInBackground(Void... params) 
		{
			//Log.d(null,"SHOULD BE CALLING A QUERY");
		//	Location location = new Location("Google");
			List<PokemonRegion> fenceList = source.getAllPokemonRegions();
			return fenceList;
		}
		
		protected void onPostExecute(List<PokemonRegion> fenceList)
		{
			Log.d(null,"GOT QUERY, LOADING DATA");
			listdata = new ArrayAdapter<PokemonRegion>(getBaseContext(), android.R.layout.simple_list_item_1);
			Log.d(null,"DOING ARRAY ADAPTER");
			listdata.addAll(fenceList);
			Log.d(null,"SETTING LIST VIEW");
			fenceListView.setAdapter(listdata);	
			Log.d(null,"LIST VIEW IS SET WTF");
		}
	}
}
