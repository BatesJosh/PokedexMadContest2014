package com.example.pokedex;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PokemonRegionDBHelper extends SQLiteOpenHelper
{
	public static final String LOCATION_ID = "loc_id";
	public static final String LOCATION_LATITUDE = "latitude";
	public static final String LOCATION_LONGITUDE = "longitude";
	public static final String LOCATION_RADIUS = "radius";
	public static final String LOCATION_EXPIRATION_DURATION = "expiration_duration";
	public static final String LOCATION_TRANSITION_TYPE = "transition_type";
	public static final String LOCATION_REGION_TYPE = "region_type";
	public static final String GEOLOCATION_TABLE = "geolocation";
	
	private static final String DATABASE_NAME = "PokemonRegionStor.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ GEOLOCATION_TABLE + "(" + LOCATION_ID + " TEXT PRIMARY KEY, "
			+ LOCATION_LATITUDE + " REAL, " + LOCATION_LONGITUDE + " REAL, "
			+ LOCATION_RADIUS + " REAL, " + LOCATION_EXPIRATION_DURATION + " INTEGER, "
			+ LOCATION_TRANSITION_TYPE + " INTEGER, "
			+ LOCATION_REGION_TYPE + " INTEGER);";
	
	
	//private Context appContext;
	
	public PokemonRegionDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//appContext = context;
	}
	
	public boolean regionTableExists(SQLiteDatabase database)
	{
		if(!database.isOpen())
			return false;
		
		Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + GEOLOCATION_TABLE + ";", null);
		
		int count = cursor.getCount();
		Log.i("GeofenceDB", "Has " + count + " Rows");
		if(count < 5)
		{
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}
	
	public void onCreate(SQLiteDatabase database)
	{
		Log.d(null,"IN ONCREATE");
		database.execSQL(DATABASE_CREATE);
		
		/* populate = new ReadFile();
		try 
		{
			populate.readGeofenceFile("geofence.txt", appContext);
		}
		catch (IOException e) 
		{
			Log.e("Geofence database", "Error populating database");
		}*/
	}
	
	//CLEARS DATABASE
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(PokemonRegionDBHelper.class.getName(), 
				"upgrading database from version " + oldVersion 
				+ " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + GEOLOCATION_TABLE);
		onCreate(db);
	}
}