package com.example.pokedex;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class ReadFile 
{
	private static String regex = "[;,:]";
	
	public List<Pokemon> readPokemonFile(String filename, Context current) throws IOException
	{
		LinkedList<Pokemon> l = null;
		BufferedReader in = null;
		try
		{
			in=new BufferedReader(new InputStreamReader(current.getAssets().open(filename)));
			//in = new BufferedReader(new FileReader(new File(filename)));
		}
		catch(FileNotFoundException e)
		{
			//just bail
			return null;
		}
		
		String line = in.readLine();
		
		l = new LinkedList<Pokemon>();
		Pokemon p = null;
		int fields = 0;//how many fields we have captured
				       //once we reach six
				       //fields, we have all of
				       // a pokemon's data
		while(line != null)
		{
			//its a comment
			if(line.charAt(0) == '#')
			{
				line = in.readLine();
				continue;
			}
			
			if(fields == 0)
				p = new Pokemon();
			
			String[] info = line.split(regex); 
			
			if(info[0].equalsIgnoreCase("name"))
			{
				p.setName(info[1]);
				fields++;
			}
			else if(info[0].equalsIgnoreCase("key"))
			{
				p.setID(Integer.parseInt(info[1]));
				fields++;
			}
			else if(info[0].equalsIgnoreCase("type"))
			{
				Pokemon.type t1;
				Pokemon.type t2;
				t1 = getType(info[1]);
				
				if(info.length > 2)
					t2 = getType(info[2]);
				else
					t2 = Pokemon.type.NONE;
				
				p.setType1(t1);
				p.setType2(t2);
				
				fields++;
			}
			else if(info[0].equalsIgnoreCase("evolve"))
			{
				p.setEvolutions(Integer.parseInt(info[1]));
				
				fields++;
			}
			else if(info[0].equalsIgnoreCase("rare"))
			{
				p.setRarity(Integer.parseInt(info[1]));
				
				fields++;
			}
			else if(info[0].equalsIgnoreCase("desc"))
			{
				String description = info[1];
				line = in.readLine();
				
				while(line != null && line.charAt(0) != '#')
				{
					description = description.concat(line+ "\n");
					line = in.readLine();
				}
				
				p.setDescription(description);
				fields++;
			}
			
			if(fields == 6)
			{
				p.setSex(Pokemon.sex.MALE);
				l.add(p);
				fields = 0;
			}
			line = in.readLine();
		}
		in.close();
		return l;
	}
	
	public /*List<PokemonRegion>*/ void readGeofenceFile(String filename, Context current) throws IOException
	{
		Log.i("ReadFile", "Beginning to read geofences");
		//LinkedList<PokemonRegion> l = null;
		BufferedReader in = null;
		try
		{
			in=new BufferedReader(new InputStreamReader(current.getAssets().open(filename)));
			//in = new BufferedReader(new FileReader(new File(filename)));
		}
		catch(FileNotFoundException e)
		{
			Log.e("ReadFile", "error opening file");
			//just bail
			return;
		}
		
		String line = in.readLine();
		
		//l = new LinkedList<PokemonRegion>();
		//PokemonRegion r = null;
		String[] data = new String[7];
		int peices = 0;
		PokemonRegionDataSource db = new PokemonRegionDataSource(current);
		int count = 0;
		while(line != null)
		{
			//it is a comment, skip
			if(line.charAt(0) == '#')
			{
				line = in.readLine();
				continue;
			}
			
			data[peices] = line;
			
			peices++;
			
			if(peices == 7)
			{
				db.createPokemonRegionEntry(
						data[0], 
						Double.parseDouble(data[1]), 
						Double.parseDouble(data[2]), 
						Float.parseFloat(data[3]), 
						1, 
						1, 
						Integer.parseInt(data[6]));
				
				count++;
				peices = 0;
			}
			line = in.readLine();
		}
		Log.i("ReadFile", "Added " + count + " geofences");
		//return l;
	}

	private Pokemon.type getType(String string) 
	{
		Pokemon.type t = Pokemon.type.UNKNOWN;
		
		if(string.equalsIgnoreCase("normal"))
			t = Pokemon.type.NORMAL;
		else if(string.equalsIgnoreCase("fire"))
			t = Pokemon.type.FIRE;
		else if(string.equalsIgnoreCase("water"))
			t = Pokemon.type.WATER;
		else if(string.equalsIgnoreCase("flying"))
			t = Pokemon.type.FLYING;
		else if(string.equalsIgnoreCase("grass"))
			t = Pokemon.type.GRASS;
		else if(string.equalsIgnoreCase("poison"))
			t = Pokemon.type.POISON;
		else if(string.equalsIgnoreCase("electric"))
			t = Pokemon.type.ELECTRIC;
		else if(string.equalsIgnoreCase("ground"))
			t = Pokemon.type.GROUND;
		else if(string.equalsIgnoreCase("psychic"))
			t = Pokemon.type.PSYCHIC;
		else if(string.equalsIgnoreCase("rock"))
			t = Pokemon.type.ROCK;
		else if(string.equalsIgnoreCase("ice"))
			t = Pokemon.type.ICE;
		else if(string.equalsIgnoreCase("bug"))
			t = Pokemon.type.BUG;
		else if(string.equalsIgnoreCase("dragon"))
			t = Pokemon.type.DRAGON;
		else if(string.equalsIgnoreCase("ghost"))
			t = Pokemon.type.GHOST;
		else if(string.equalsIgnoreCase("dark"))
			t = Pokemon.type.DARK;
		else if(string.equalsIgnoreCase("steel"))
			t = Pokemon.type.STEEL;
		else if(string.equalsIgnoreCase("fairy"))
			t = Pokemon.type.FAIRY;
		else if(string.equalsIgnoreCase("none"))
			t = Pokemon.type.NONE;
		
		return t;
	}
}
