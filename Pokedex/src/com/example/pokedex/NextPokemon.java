package com.example.pokedex;  
    
import java.util.Collection;  
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Random; 
  
import android.content.Context;  
import android.location.Location;  
import android.util.Log; 
    
public class NextPokemon   
{     
    /**  
     * Chooses what Pokemon will appear next  
     * @return the next Pokemon to appear  
     */
    public Pokemon next(HashMap<String, PokemonRegion> map, Context c)  
    {  
        PokemonHandler handle = new PokemonHandler(c);  
        List<Pokemon> pList = null;  
        String[] types = null;  
        Random r = new Random(); 
        if(map.isEmpty())  
        {  
            types = new String[1];  
            types[0] = "NORMAL";  
        }  
        else
        {  
            Collection<PokemonRegion> collection = map.values();  
            PokemonRegion[] regions = new PokemonRegion[collection.size()];  
            regions = collection.toArray(regions);  
                
            int index = r.nextInt(regions.length);  
                
            int type = regions[index].getRegionType();  
                
            types = getPokemonTypesFromRegion(type);  
            for(int i = 0; i < types.length; i++) 
                Log.i("[TYPES]", types[i]); 
        }  
          
        int index2 = r.nextInt(types.length); 
        pList = handle.getPokemonType(types[index2]);  
          
        Log.i("[SELECTED]", types[index2]); 
            
        int index3 = index(pList.size())[0];  
        index3 %= pList.size();  
            
        return pList.get(index3);  
            
    }  
        
    private String[] getPokemonTypesFromRegion(int type)   
    {  
        String[] types = null;  
//Region type(Field=0, Forest=1, Water=2, Urban=3, Cave=4, Cemetery=5, Mountain=6, Desert=7, Water/forest=8)  
        if(type == 0)  
        {  
            types = new String[6];  
            types[0] = "GRASS";  
            types[1] = "GROUND";  
            types[2] = "NORMAL";  
            types[3] = "BUG";  
            types[4] = "FLYING";  
            //types[5] = "FIGHTING";  
            types[5] = "ELECTRIC";  
        }  
        else if(type == 1)  
        {  
            types = new String[4];  
            types[0] = "BUG";  
            types[1] = "GRASS";  
            types[2] = "POISON";  
            //types[3] = "FAIRY";  
            types[3] = "FLYING";  
           // types[5] = "PSYCHIC";  
        }  
        else if(type == 2)  
        {  
            types = new String[1];  
            types[0] = "WATER";  
        }  
        else if(type == 3)  
        {  
            types = new String[2];  
            //types[0] = "DARK";  
            //types[1] = "STEEL";  
            types[0] = "POISON";  
            //types[3] = "GHOST";  
            //types[4] = "FIGHTING";  
            types[1] = "ELECTRIC";  
        }  
        else if(type == 4)  
        {  
            types = new String[1];  
            //types[0] = "DARK";  
            types[0] = "GROUND";  
            //types[2] = "ROCK";  
            //types[3] = "DRAGON";  
            //types[4] = "PSYCHIC";  
        }  
        else if(type == 5)  
        {  
            types = new String[1];  
            //types[0] = "DARK";  
            //types[1] = "GHOST";  
            //types[2] = "FAIRY";  
            types[0] = "FLYING";  
            //types[4] = "PSYCHIC";  
        }  
        else if(type == 6)  
        {  
            types = new String[3];  
            types[0] = "FIRE";  
            //types[1] = "ROCK";  
            types[1] = "GROUND";  
            types[2] = "FLYING";  
            //types[4] = "FIGHTING";  
            //types[5] = "DRAGON";  
        }  
        else if(type == 7)  
        {  
            types = new String[3];  
            types[0] = "FIRE";  
            types[1] = "GROUND";  
            types[2] = "BUG";  
            //types[3] = "STEEL";  
        }  
        else if(type == 8)  
        {  
            types = new String[5];  
            types[0] = "BUG";  
            types[1] = "GRASS";  
            types[2] = "POISON";  
            //types[3] = "FAIRY";  
            types[3] = "FLYING";  
           // types[5] = "PSYCHIC";  
            types[46] = "WATER";  
        }  
        else
        {  
            types = new String[1];  
            types[0] = "NORMAL";  
        }  
        return types;  
    }  
    
    public HashMap<String, PokemonRegion> updateRegions(Location loc, Context c)  
    {  
        HashMap<String, PokemonRegion> nextPossible = new HashMap<String, PokemonRegion>();  
            
        if(loc != null)  
        {  
            PokemonRegionDataSource source = new PokemonRegionDataSource(c);  
                
            List<PokemonRegion> rs = source.getClosePokemonRegions(loc);  
                
            Iterator<PokemonRegion> it = rs.iterator();  
                
            while(it.hasNext())  
            {  
                PokemonRegion next = it.next();  
                    
                nextPossible.put(next.getID(), next);  
            }  
        }  
            
        return nextPossible;  
    }  
        
        
    private int[] index(int range)  
    {  
        //get normal samples  
        double[] samples = pBoxMuller();  
            
        int indices[] = new int[2];  
            
        //get a whole number that can be used as an index  
        indices[0] = Math.abs((int)(samples[0] * range));  
        indices[1] = Math.abs((int)(samples[1] * range));  
            
        return indices;  
    }     
        
    /*  
     * Pseudo-normal distribution function  
     * generates two numbers per call  
     */
    private double[] pBoxMuller()  
    {  
            
        double[] samples = new double[2];  
            
        double u;  
        double v;  
        double s;  
                    
        do
        {  
            u = Math.random() * 2.0 - 1.0; //[-1, +1)  
            v = Math.random() * 2.0 - 1.0;  
    
            s = u * u + v * v;  
        }  
        while(s == 0 || s >= 1);   
            
        samples[0] = u * Math.sqrt((-2.0 * Math.log(s)) / s);  
        samples[1] = v * Math.sqrt((-2.0 * Math.log(s)) / s);  
            
        //System.out.println(samples[0] + "\n"+ samples[1]);  
            
        return samples;  
    }  
}
