package com.sbasite.sbasites.activity;

import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.tasks.ags.identify.IdentifyParameters;
import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.esri.core.tasks.ags.identify.IdentifyTask;
import com.sbasite.R;
import com.sbasite.sbasites.adapter.SiteListAdapter;
import com.sbasite.sbasites.app.SBAApplication;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class SiteListActivity extends ListActivity {

	private static final String TAG = SiteListActivity.class.getSimpleName();
	private SiteListAdapter listAdapter;
	private IdentifyResult[] sites;
	protected ProgressDialog progressDialog;
	
	private MapView map;
	private IdentifyParameters params;
	private static final String DYNAMIC_MAP_SERVICE_URL = "http://mapservices.sbasite.com/ArcGIS/rest/services/Google/MobileiOS/MapServer";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        map = SBASitesActivity.map;
        
        params = new IdentifyParameters();
		params.setTolerance(20);
		params.setDPI(98);
		params.setLayers(new int[]{4});
		params.setLayerMode(IdentifyParameters.ALL_LAYERS);
		
		params.setGeometry(map.getMapBoundaryExtent());
		params.setSpatialReference(map.getSpatialReference());									
		params.setMapHeight(map.getHeight());
		params.setMapWidth(map.getWidth());
		Envelope env = new Envelope();
		map.getExtent().queryEnvelope(env);
		params.setMapExtent(env);
		params.setLayers(getSBASitesApplication().getActiveLayerIDs());
		MyIdentifyTask mTask = new MyIdentifyTask();
		mTask.execute(params);
		
        setContentView(R.layout.listview);
        
    }

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		IdentifyResult site = listAdapter.getItem(position);
		Log.d(TAG, site.toString());
	    if (null != site) {
			Intent intent = new Intent(this, SiteDetailActivity.class);
			intent.putExtra("SITE", site);
			startActivity(intent);
		}
		finish();
	    
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	protected SBAApplication getSBASitesApplication() {
		return (SBAApplication)getApplication();
	}
	
	public void setSites(IdentifyResult[] results) {
		this.sites = results;
		listAdapter = new SiteListAdapter(this, this.sites);
        setListAdapter(listAdapter);
		listAdapter.forceReload();
		
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
	}
	
	private class MyIdentifyTask extends AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {

		IdentifyTask mIdentifyTask;
		
		@Override
		protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
			IdentifyResult[] mResult = null;
			if (params != null && params.length > 0) {
				IdentifyParameters mParams = params[0];
				try {
					mResult = mIdentifyTask.execute(mParams);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return mResult;
		}

		@Override
		protected void onPostExecute(IdentifyResult[] results) {
			setSites(results);
		} 

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			progressDialog = ProgressDialog.show(SiteListActivity.this, "Loading...", "Site list is loading");
			//mIdentifyTask = new IdentifyTask("http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Earthquakes/EarthquakesFromLastSevenDays/MapServer");
			mIdentifyTask = new IdentifyTask(DYNAMIC_MAP_SERVICE_URL);
		}
	}
}
