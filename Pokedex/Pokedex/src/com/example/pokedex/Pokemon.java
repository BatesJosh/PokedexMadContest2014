package com.example.pokedex;




public class Pokemon implements Comparable<Pokemon>
{
	/**
	 * All possible types a Pokemon can be
	 * plus unknown
	 * @author Dan
	 *	@modified by Josh 
	 */
	public static enum type {NORMAL, FIRE, FIGHTING, WATER, FLYING, GRASS,
		POISON, ELECTRIC, GROUND, PSYCHIC, ROCK, ICE, BUG, DRAGON, GHOST,
		DARK, STEEL, FAIRY, UNKNOWN, NONE};
		
	public static enum sex {MALE, FEMALE, UNKNOWN};

	private String name;			//Name
	private int key;
	private String description;
	private int evolutions; 	//name of the Pokemon that this
									//Pokemon evolves into
									//usually will be one or null
	
	private int rarity;				//Rare value, low value = more common, 
									//high value = more rare
	
	private type[] type=new type[2];				//water, fire, rock, ect
	
	
	private sex sex;				//Some Pokemon have different portraits 
									//based on the sex, perhaps different
									//stats as well

	/**
	 * Pokemon constructor that takes all fields
	 * @param key
	 * @param name
	 * @param description
	 * @param evolutions
	 * @param rarity
	 * @param type1
	 * @param type2
	 * @param sex
	 */
	public Pokemon(int id, String name, String description, int evolutions,
			int rarity, Pokemon.type type1, Pokemon.type type2,  sex sex) 
	{
		super();
		this.key=id;
		this.name = name;
		this.description = description;
		this.evolutions = evolutions;
		this.rarity = rarity;
		this.type[0]=type1;
		this.type[1]= type2;
	
		this.sex = sex;
	}

	public Pokemon(){
		
	}
	
	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public int getEvolutions() 
	{
		return evolutions;
	}

	public void setEvolutions(int evolutions) 
	{
		this.evolutions = evolutions;
	}

	public int getRarity() 
	{
		return rarity;
	}

	public void setRarity(int rarity) 
	{
		this.rarity = rarity;
	}

	public type getType1() 
	{
		return type[0];
	}
	
	public type getType2() 
	{
		return type[1];
	}

	public void setType1(type type)
	{
		this.type[0] = type;
	}

	public void setType2(type type){
		this.type[1]=type;
	}
	
	public sex getSex() 
	{
		return sex;
	}

	public void setSex(sex sex) 
	{
		this.sex = sex;
	}
	
	public String toString() 
	{
		return "Pokemon [name=" + name + "]";
	}

	public int compareTo(Pokemon other) 
	{
		//this one is less common
		if(this.rarity < other.rarity)
		{
			return -1;
		}
		//this one is more common or they are the same
		else
		{
			return 1;
		}
	}

	public int getID() {
		// TODO Auto-generated method stub
		return key;
	}
	
	public void setID(int num){
		this.key=num;
	}
}
