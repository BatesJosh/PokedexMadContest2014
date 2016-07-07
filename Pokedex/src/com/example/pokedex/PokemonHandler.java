package com.example.pokedex;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



import com.example.pokedex.Pokemon.type;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite .SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;


public class PokemonHandler extends SQLiteOpenHelper{
	private static final String TAG = "PokemonDatabase";
	private static final int MAX_LOCK=5;
	public static final String KEY_ID ="id";
	public static final String KEY_NAME = "pokemon_name";
	private static final String DESCR = "description";
	private static final String EVOL = "Evolution";
	private static final String RARE = "rarity";
	private static final String TYPEI = "typeI";
	private static final String TYPEII = "typeII";
	private static final String SEX = "sex";
	public static final String UNLOCK="unlock_counter";
	private static final String DATABASE_NAME = "Pokedex";
	private static final String TABLE_NAME = "Pokemon";
	private static final String TABLE_UNLOCK= "Unlocked";
	private static final String[] ALL_COLUMNS={KEY_ID,KEY_NAME,DESCR,EVOL,RARE,TYPEI,TYPEII,SEX};
	private static final int DATABASE_VERSION =1;
	private SQLiteDatabase mDatabase;
	private static final String TABLE_CREATE = "CREATE TABLE "+ 
			TABLE_NAME+"("+KEY_ID +" INTEGER PRIMARY KEY, " +  
			KEY_NAME + " TEXT,"+DESCR+" TEXT, "+EVOL+" TEXT, "+
			RARE+" TEXT, "+TYPEI+" TEXT, "+TYPEII+" TEXT, "+SEX+" TEXT);";
	private static final String TABLE_CREATE_UNLOCK= "CREATE TABLE "+TABLE_UNLOCK+" ("+KEY_ID+" INTEGER PRIMARY KEY, "+UNLOCK +" INTEGER);";
	private Context appContext;
	PokemonHandler(Context contest){
		super(contest, DATABASE_NAME, null, DATABASE_VERSION);
		appContext=contest;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mDatabase = db;
		mDatabase.execSQL(TABLE_CREATE);
		mDatabase.execSQL(TABLE_CREATE_UNLOCK);
		//addPokemon(new Pokemon(1, "Bulbasaur", "For some time after its birth, it grows by gaining nourishment from the seed on its back.", 0, 1, Pokemon.type.GRASS, Pokemon.type.POISON , Pokemon.sex.FEMALE), mDatabase);
		//addPokemon(new Pokemon(4, "Charmander", "The flame on its tail indicates Charmander's life force. If it is healthy, the flame burns brightly.", 0, 1, Pokemon.type.FIRE, Pokemon.type.NONE, Pokemon.sex.MALE), mDatabase);
		LinkedList<Pokemon> pokedex=null;
		Log.i("Read File", "Start to read");
		try {
			Log.i("Read File", "success");
			pokedex=(LinkedList<Pokemon>) new ReadFile().readPokemonFile("pokemon.txt", appContext);
			Log.i("Read File", "pokedex size:"+ pokedex.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(pokedex!=null && pokedex.size()!=0){
			Iterator<Pokemon> it=pokedex.iterator();
			while(it.hasNext()){
				addPokemon(it.next(), mDatabase);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNLOCK);
        onCreate(db);
       // addPokemon(new Pokemon(1, "Test", "Test", 0, 1, Pokemon.type.WATER , null, Pokemon.sex.FEMALE));
		
	}
	
	public void addPokemon(Pokemon critter, SQLiteDatabase db){
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues insertValues = new ContentValues();
		insertValues.put(KEY_ID, critter.getID());
		insertValues.put(KEY_NAME, critter.getName());
		insertValues.put(DESCR, critter.getDescription());
		insertValues.put(EVOL, critter.getEvolutions());
		insertValues.put(RARE, critter.getRarity());
		insertValues.put(TYPEI,  critter.getType1().name());
		insertValues.put(TYPEII,  critter.getType2().name());
		insertValues.put(SEX, critter.getSex().name());
		db.insert(TABLE_NAME, null, insertValues);
		ContentValues unlockValues = new ContentValues();
		unlockValues.put(KEY_ID, critter.getID());
		unlockValues.put(UNLOCK, MAX_LOCK);
		db.insert(TABLE_UNLOCK, null, unlockValues);
		//db.close();
	}
	
	/**
	 * Get list of pokemon that the user has seen.
	 * @return
	 */
	public List<Pokemon> getPokedex(){
		String query= "Select p."+KEY_ID+", "+KEY_NAME+", "+DESCR+", "+RARE+", "+TYPEI+", "+TYPEII+", "+SEX+" FROM "+TABLE_NAME +" p JOIN "+ TABLE_UNLOCK +" u WHERE p."+KEY_ID+" = u."+KEY_ID+" AND u."+UNLOCK+" < 5" ;
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Pokemon> pokedex=new ArrayList<Pokemon>();
		try{
			Log.d("Testing getPokemonType", "starting query");
			//Cursor cursor=db.query(TABLE_NAME, ALL_COLUMNS, KEY_ID+" = "+ id, null, null, null, null);
			Cursor cursor=db.rawQuery(query, null);
			Log.d("Testing getPokemonType", "moving cursor");
			if(cursor!=null){
			if(cursor.moveToFirst()){
				do{
					Pokemon pokeball=new Pokemon();
					pokeball.setID(Integer.parseInt(cursor.getString(0)));
					pokeball.setName(cursor.getString(1));
					pokeball.setDescription(cursor.getString(2));
					pokeball.setRarity(Integer.parseInt(cursor.getString(3)));
					pokeball.setType1((Pokemon.type.valueOf(cursor.getString(4))));
					pokeball.setType2((Pokemon.type.valueOf(cursor.getString(5))));
					pokeball.setSex(Pokemon.sex.valueOf(cursor.getString(6)));
					pokedex.add(pokeball);
				}while (cursor.moveToNext());
					cursor.close();
			}
			}
			db.close();
		}
		catch(SQLException e){
			db.close();
			Log.d("SQL", "error reading ");
		}
		return pokedex;
	
	}
	
	/**
	 * Get list of pokemon using type
	 * 
	 * 
	 */
	public List<Pokemon> getPokemonType(String type){
		String query= "Select "+KEY_ID+", "+KEY_NAME+", "+DESCR+", "+RARE+", "+TYPEI+", "+TYPEII+", "+SEX+" FROM "+TABLE_NAME +" WHERE "+ TYPEI+" ='"+type+"' OR "+TYPEII +" ='"+type+"' ORDER BY "+RARE;
	//	SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Pokemon> pokedex=new ArrayList<Pokemon>();
		try{
			Log.d("Testing getPokemonType", "starting query");
			//Cursor cursor=db.query(TABLE_NAME, ALL_COLUMNS, KEY_ID+" = "+ id, null, null, null, null);
			Cursor cursor=db.rawQuery(query, null);
			Log.d("Testing getPokemonType", "moving cursor");
			if(cursor!=null){
			if(cursor.moveToFirst()){
				do{
					Pokemon pokeball=new Pokemon();
					pokeball.setID(Integer.parseInt(cursor.getString(0)));
					pokeball.setName(cursor.getString(1));
					pokeball.setDescription(cursor.getString(2));
					pokeball.setRarity(Integer.parseInt(cursor.getString(3)));
					pokeball.setType1((Pokemon.type.valueOf(cursor.getString(4))));
					pokeball.setType2((Pokemon.type.valueOf(cursor.getString(5))));
					pokeball.setSex(Pokemon.sex.valueOf(cursor.getString(6)));
					pokedex.add(pokeball);
				}while (cursor.moveToNext());
					cursor.close();
			}
			}
			db.close();
		}
		catch(SQLException e){
			db.close();
			Log.d("SQL", "error reading ");
		}
		return pokedex;
	
	}
	
	/**
	 * Get Pokemon based off of id
	 */
	public Pokemon getPokemon(int id){
		String query= "Select "+KEY_ID+", "+KEY_NAME+", "+DESCR+", "+RARE+", "+TYPEI+", "+TYPEII+", "+SEX+", "+EVOL+" FROM "+TABLE_NAME +" WHERE "+ KEY_ID+" = "+id;
		SQLiteDatabase db = this.getReadableDatabase();
		Pokemon pokeball=new Pokemon();
		try{
			Log.d("testing getPokemon", "starting query");
			//Cursor cursor=db.query(TABLE_NAME, ALL_COLUMNS, KEY_ID+" = "+ id, null, null, null, null);
			Cursor cursor=db.rawQuery(query, null);
			Log.d("testing getPokemon", "moving cursor");
			if(cursor!=null){
			if(cursor.moveToFirst()){
				pokeball.setID(Integer.parseInt(cursor.getString(0)));
				pokeball.setName(cursor.getString(1));
				pokeball.setDescription(cursor.getString(2));
				pokeball.setRarity(Integer.parseInt(cursor.getString(3)));
				pokeball.setType1((Pokemon.type.valueOf(cursor.getString(4))));
				pokeball.setType2(Pokemon.type.valueOf(cursor.getString(5)));
				pokeball.setSex(Pokemon.sex.valueOf(cursor.getString(6)));
				pokeball.setEvolutions(Integer.parseInt(cursor.getString(7)));
				cursor.close();
			}
			}
			else{
				pokeball=null;
			}
			db.close();
		}
		catch(SQLException e){
			db.close();
			Log.d("SQL", "error reading ");
		}
		return pokeball;
	}
	
	public void deletePokemon(Pokemon critter){
		SQLiteDatabase db =this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + "=?", new String[] {String.valueOf(critter.getID())});
		db.close();
	}
	
	public Pokemon findPokemon(int id){
		return null;
		
	}
	public void unlock(int id){
		int num=uncover(id);
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues values =new ContentValues();
		if(num!= 0)num--;
		values.put(UNLOCK, num);
		db.update(TABLE_UNLOCK, values, KEY_ID + "=?", new String[] {String.valueOf(id)});
	}
	
	public int uncover(int id){
		String query= "Select "+UNLOCK+" FROM "+TABLE_UNLOCK +" WHERE "+ KEY_ID+" = "+id;
		SQLiteDatabase db = this.getReadableDatabase();
		int show = -1;
		try{
			Log.d("testing uncover", "starting query");
			//Cursor cursor=db.query(TABLE_NAME, ALL_COLUMNS, KEY_ID+" = "+ id, null, null, null, null);
			Cursor cursor=db.rawQuery(query, null);
			Log.d("testing uncover", "moving cursor");
			if(cursor!=null){
			if(cursor.moveToFirst()){
				show=Integer.parseInt(cursor.getString(0));
				cursor.close();
				Log.d("int","show: "+ show);
			}
			}
			db.close();
		}
		catch(SQLException e){
			db.close();
			Log.d("SQL", "error reading ");
		}
		return show;
	}
	

}
