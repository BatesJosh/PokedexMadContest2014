package com.example.pokedex;

import java.util.Iterator;
import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ReceiveTransitionsIntentService extends IntentService  
{

	public ReceiveTransitionsIntentService() 
	{
		super("com.example.pokedex.ReceiveTransitionsIntentService");
	}

	protected void onHandleIntent(Intent intent) 
	{
		Log.i("[IntentServ]", "Recieved an intent");
		if(LocationClient.hasError(intent))
		{
			int errorCode = LocationClient.getErrorCode(intent);
			
			Log.e("com.example.pokedex.ReceiveTransitionsIntentService",
                    "Location Services error: " +
                    Integer.toString(errorCode));
		}
		else
		{
			int transitionType =
                    LocationClient.getGeofenceTransition(intent);
			
			if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER
				||
				transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
			{
				List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
				String triggeredFences = "";
				
				Iterator<Geofence> it = triggerList.iterator();
				while(it.hasNext())
				{
					Geofence present = it.next();
					triggeredFences += present.getRequestId();
					triggeredFences += ",";
				}
				triggeredFences = Integer.toString(transitionType) + "," + triggeredFences;
				triggeredFences = triggeredFences.substring(0, triggeredFences.length()-2);
				Intent out = new Intent(this, PokedexMain.class);
				out.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				out.putExtra("Geofences", triggeredFences);
				
				startActivity(out);
				
			}
			else
			{
				Log.e("com.example.pokedex.ReceiveTransitionsIntentService",
	                    "Geofence invalid transition type: " +
	                    Integer.toString(transitionType));
			}
		}
	}
}
