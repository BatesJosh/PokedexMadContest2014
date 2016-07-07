package com.example.pokedex;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Checks to see if google play services are available, 
 * see: http://developer.android.com/training/location/geofencing.html
 * @author Dan
 *
 */
public class GooglePlayServices extends FragmentActivity
{
	//Any global constants go here, currently none
	
	/*
	 * define a request code to sent to Google Play services
	 * returned in Activity.onActivityResult
	 */
	private final static int
		CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	//DialogFragment that displays error dialog
	public static class ErrorDialogFragment extends DialogFragment
	{
		private Dialog mDialog;
		
		//default constructor
		public ErrorDialogFragment()
		{
			super();
			mDialog = null;
		}
		
		//set the dialog to display
		public void setDialog(Dialog dialog)
		{
			mDialog = dialog;
		}
		
		//return a dialog to the DialogFragment
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			return mDialog;
		}
	}
	
	/*
	 * handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	protected void onActivityResult
				(int requestCode, int resultCode, Intent data)
	{
		//do something based on request code
		switch(requestCode)
		{
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
		{
			switch(resultCode)
			{
			case Activity.RESULT_OK:
			{
				//try again
				break;
			}
			}
			break;
		}
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
			return false;
		}
	}
}