package com.sbasite.sbasites.activity;

import java.util.ArrayList;

import com.sbasite.sbasites.R;
import com.sbasite.sbasites.adapter.LayerListAdapter;
import com.sbasite.sbasites.app.SBAApplication;
import com.sbasite.sbasites.model.SBALayer;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Layers extends ListActivity {

	private LayerListAdapter listAdapter;
	private ArrayList<SBALayer> layers;
	private Button hybridButton;
	private Button mapButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layers = getSBASitesApplication().getLayers();
        Log.d("Layers", layers.toString());
        listAdapter = new LayerListAdapter(this, layers);
        setContentView(R.layout.layers);
        setListAdapter(listAdapter);
        
        hybridButton = (Button) findViewById(R.id.HybridButton);
        mapButton = (Button) findViewById(R.id.MapButton);
        
        boolean mapMode = getSBASitesApplication().getMapMode();
        hybridButton.setEnabled(!mapMode);
        mapButton.setEnabled(mapMode);
        
        hybridButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getSBASitesApplication().setMapMode(true);
				hybridButton.setEnabled(false);
		        mapButton.setEnabled(true);
			}
        });
        mapButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getSBASitesApplication().setMapMode(false);
				hybridButton.setEnabled(true);
		        mapButton.setEnabled(false);
			}
        });
    }

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		SBALayer layer = listAdapter.getItem(position);
		if (null != layer) {
			layer.activated = !layer.activated;
		}
		listAdapter.forceReload();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listAdapter.forceReload();
	}
	
	protected SBAApplication getSBASitesApplication() {
		return (SBAApplication)getApplication();
	}
	
}
