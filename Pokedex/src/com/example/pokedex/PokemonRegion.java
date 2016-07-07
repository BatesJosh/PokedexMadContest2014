package com.example.pokedex;

import com.google.android.gms.location.Geofence;

public class PokemonRegion
{
	private Geofence geofence;
	private int regionType;
	
	public PokemonRegion
			(String id,
			double latitude,
			double longitude,
			float radius,
			long expirationDuration,
			int transitionType,
			int regionType)
	{
		geofence = new Geofence.Builder()
		.setRequestId(id)
		.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
		.setCircularRegion
			(latitude, longitude, radius)
		.setExpirationDuration(Geofence.NEVER_EXPIRE)
		.build();
		
		this.regionType = regionType;
	}
	
	public String getID()
	{
		return this.geofence.getRequestId();
	}
	
	public Geofence getGeofence() 
	{
		return geofence;
	}

	public void setGeofence(Geofence geofence) 
	{
		this.geofence = geofence;
	}

	public int getRegionType() 
	{
		return regionType;
	}

	public void setRegionType(int regionType) 
	{
		this.regionType = regionType;
	}
	
	public String toString()
	{
		return this.geofence.getRequestId() + " " + regionType; 
	}
}