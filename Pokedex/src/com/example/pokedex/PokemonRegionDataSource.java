package com.example.pokedex;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class PokemonRegionDataSource
{
	private static final double CLOSE = 0.0217391;
	
	//database fields
	private SQLiteDatabase db;
	private PokemonRegionDBHelper dbHelper;
	private String[] allColumns = 
			{PokemonRegionDBHelper.LOCATION_ID,
			PokemonRegionDBHelper.LOCATION_LATITUDE,
			PokemonRegionDBHelper.LOCATION_LONGITUDE,
			PokemonRegionDBHelper.LOCATION_RADIUS,
			PokemonRegionDBHelper.LOCATION_EXPIRATION_DURATION,
			PokemonRegionDBHelper.LOCATION_TRANSITION_TYPE,
			PokemonRegionDBHelper.LOCATION_REGION_TYPE };
	
	public PokemonRegionDataSource(Context context)
	{
		dbHelper = new PokemonRegionDBHelper(context);
	}
	
	public void openForWriting() throws SQLException
	{
		db = dbHelper.getWritableDatabase();
	}
	
	public void openForReading() throws SQLException
	{
		db = dbHelper.getReadableDatabase();	
	}
	
	public boolean tableExists()
	{
		db = dbHelper.getReadableDatabase();
		boolean test = dbHelper.regionTableExists(db);
		
		dbHelper.close();
		return test;
	}
	
	public void close()
	{
		dbHelper.close();
	}
	
	public void createPokemonRegionEntry(String id, double lat,
			double lon, float rad, long exp, int type, int rType)
	{
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
           
        values.put(PokemonRegionDBHelper.LOCATION_ID, id);
        values.put(PokemonRegionDBHelper.LOCATION_LATITUDE, lat);
        values.put(PokemonRegionDBHelper.LOCATION_LONGITUDE, lon);
        values.put(PokemonRegionDBHelper.LOCATION_RADIUS, rad);
        values.put(PokemonRegionDBHelper.LOCATION_EXPIRATION_DURATION, exp);
        values.put(PokemonRegionDBHelper.LOCATION_TRANSITION_TYPE, type);
        values.put(PokemonRegionDBHelper.LOCATION_REGION_TYPE, rType);
           
        db.insert(PokemonRegionDBHelper.GEOLOCATION_TABLE, null, values);
       
        db.close();
	}
	
	public void deletePokemonRegionEntry(String id)
	{
		db.delete(PokemonRegionDBHelper.GEOLOCATION_TABLE, 
				PokemonRegionDBHelper.LOCATION_ID + " = " + id, null);
	}
	
	public List<PokemonRegion> getAllPokemonRegions()
	{
		Log.d(null,"IN THE QUERY");
		db = dbHelper.getReadableDatabase();
		ArrayList<PokemonRegion> fenceList = new ArrayList<PokemonRegion>();
		
		
		String selectQuery = "SELECT * FROM " + 
				PokemonRegionDBHelper.GEOLOCATION_TABLE;
		
		
		Log.d(null,"RUNNING QUERY");
		Cursor cursor = db.rawQuery(selectQuery, null);
				
				/*db.query
				(PokemonRegionDBHelper.GEOLOCATION_TABLE,
				allColumns,
				null, null, null, null, null);*/
		Log.d(null,"QUERY DONE DOING CURSOR");
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			fenceList.add(cursorToPokemonRegion(cursor));
			Log.d(null,"GOT A ROW");
			cursor.moveToNext();
		}
		Log.d(null,"DONE WITH CURSOR");
		cursor.close();
		db.close();
		return fenceList;
	}
	
	public List<PokemonRegion> getClosePokemonRegions(Location location)
	{
		db = dbHelper.getReadableDatabase();
		ArrayList<PokemonRegion> fenceList = new ArrayList<PokemonRegion>();
		
		double curLat = location.getLatitude();
		double curLon = location.getLongitude();
		
		//setting up ranges to check in database
		double latPlusClose = curLat + CLOSE;
		double latMinusClose = curLat - CLOSE;
		double lonPlusClose = curLon + CLOSE;
		double lonMinusClose = curLon - CLOSE;
		
		String[] selectionArgs = 
				{String.valueOf(latMinusClose),
				String.valueOf(latPlusClose),
				String.valueOf(lonMinusClose),
				String.valueOf(lonPlusClose)};
		
		Cursor cursor = db.query
				(PokemonRegionDBHelper.GEOLOCATION_TABLE, 
				allColumns, 
				PokemonRegionDBHelper.LOCATION_LATITUDE + " >= ? AND " +
				PokemonRegionDBHelper.LOCATION_LATITUDE + " <= ? AND " +
				PokemonRegionDBHelper.LOCATION_LONGITUDE + " >= ? AND " +
				PokemonRegionDBHelper.LOCATION_LONGITUDE + " <= ?", 
				selectionArgs, 
				null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			fenceList.add(cursorToPokemonRegion(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return fenceList;
	}
	
	private PokemonRegion cursorToPokemonRegion(Cursor cursor)
	{
		return new PokemonRegion
				(cursor.getString(0),
				cursor.getDouble(1),
				cursor.getDouble(2), 
				cursor.getFloat(3),
				cursor.getLong(4),
				cursor.getInt(5),
				cursor.getInt(6));
	}
}