package com.example.pokedex;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

public class PokedexAdapter extends ArrayAdapter<Pokemon> {
	int resource;
	ArrayList<Pokemon> pokedex;
	public PokedexAdapter(Context context, int resource, List<Pokemon> items) {
		super(context, resource, items);
		// TODO Auto-generated constructor stub
		this.resource=resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LinearLayout pokedexView;
		Pokemon ball= getItem(position);
		if(convertView==null){
			pokedexView = new LinearLayout(getContext());
			String inflater= Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi=(LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, pokedexView, true);
		}else
		{
			pokedexView=(LinearLayout) convertView;
		}
		TextView idText=(TextView)pokedexView.findViewById(R.id.pokemonId);
		TextView nameText=(TextView)pokedexView.findViewById(R.id.pokemonName);
		idText.setText(" "+ball.getID()+" ");
		nameText.setText(ball.getName());
		
		return pokedexView;
		
	}
	
	
	

}
