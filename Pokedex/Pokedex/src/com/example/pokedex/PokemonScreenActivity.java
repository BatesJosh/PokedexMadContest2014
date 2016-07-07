package com.example.pokedex;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class PokemonScreenActivity extends Activity {
	private PokemonHandler db;
	private Pokemon ball;
	private TextView pokemonName;
	private TextView pokemonId;
	private TextView pokemonType1;
	private TextView pokemonType2;
	private TextView pokemonEvol;
	private TextView pokemonDescr;
	private ImageView pokemonImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pokemon_screen);
		// Show the Up button in the action bar.
		setupActionBar();
		db=new PokemonHandler(this);
		Intent intent=getIntent();
		int num=intent.getIntExtra(PokedexMain.POKEMON_EXTRA, 0);
	
		pokemonName=(TextView) findViewById(R.id.pokemon_name);
		pokemonId=(TextView) findViewById(R.id.pokemon_id);
		pokemonType1=(TextView) findViewById(R.id.pokemon_type1);
		pokemonType2=(TextView) findViewById(R.id.pokemon_type2);
		pokemonEvol=(TextView) findViewById(R.id.pokemon_evolutions);
		pokemonDescr=(TextView) findViewById(R.id.pokemon_descr);
		pokemonImg=(ImageView) findViewById(R.id.pokemon_img);

		if(num>0){
			int unlock=db.uncover(num);
			ball=db.getPokemon(num);
			int drawableImageId = getResources().getIdentifier("pokemon"+ball.getID(),"drawable", getPackageName());;
			
			pokemonImg.setImageResource(drawableImageId);
			//update_str=Context.getResources().getString(R.drawable.+ball.getID());
			pokemonName.setText("Name: "+ball.getName());
			pokemonId.setText("ID Number: "+ball.getID());
			if(unlock<4){
				pokemonType1.setText("Type 1:"+ ball.getType1());
			}
			if(unlock<3){
				pokemonType2.setText("Type 2: "+ ball.getType2());
			}
			if(unlock<2){
				if(ball.getEvolutions()==0){
					pokemonEvol.setText("Evolves into: NONE");
				}
				else{
					//Be careful that if the pokemon is not in the database, the program will crash.
					Pokemon evolPokeball=db.getPokemon(ball.getEvolutions());
					if(evolPokeball!=null){
						pokemonEvol.setText("Evolution: "+ db.getPokemon(ball.getEvolutions()).getName());
					}else{
						pokemonEvol.setText("Evolution: "+ ball.getEvolutions());
					}
				}
		
			}
			if(unlock<1){
				pokemonDescr.setText(ball.getDescription());
					
			}
			
		}
			
	
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pokemon_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
