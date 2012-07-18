package com.sbasite.sbasites.activity;

import java.util.Map;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.map.bing.BingMapsLayer.MapStyle;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.tasks.ags.identify.IdentifyParameters;
import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.esri.core.tasks.ags.identify.IdentifyTask;
import com.sbasite.sbasites.R;
import com.sbasite.sbasites.app.SBAApplication;
import com.sbasite.sbasites.model.SearchResult;


public class SBASitesActivity extends GDActivity {

	public static MapView map;
	BingMapsLayer baseLayer;
	ArcGISDynamicMapServiceLayer dynamicLayer;
	Callout callout;
	Location location;

	IdentifyParameters params;

	// The circle area specified by search_radius and input lat/lon serves
	// searching purpose. It is also used to construct the extent which
	// map zooms to after the first GPS fix is retrieved.
	final static double SEARCH_RADIUS = 5;
	private Intent myIntent;
	private static final String APP_ID = "AvW9D5g6eoK0VY4L5oplesIcHsNTrOMdZxSD4mxY2vqRtzMLHAcEA4U90jtJBErj";
	private static final String DYNAMIC_MAP_SERVICE_URL = "http://mapservices.sbasite.com/ArcGIS/rest/services/Google/MobileiOS/MapServer";

	final static int LOADING_CANCEL = 0;
	final static int LOADING_SUCCESS = 1;
	protected static final int CHOOSE_SEARCH_RESULT = 1;
	protected static final int CHOOSE_SITE_RESULT = 2;
	protected static final int CHOOSE_LAYER = 3;
	
	private static IdentifyResult selectedSite = null;

	// Called when the activity is first created.
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.main);

		addActionBarItem(Type.LocateMyself);
		addActionBarItem(Type.Search);
		addActionBarItem(Type.Locate);

		// Retrieve the MapView from XML layout
		map = (MapView)findViewById(R.id.map);
		// Create an extent for initial extent
		Envelope env = new Envelope(-19332033.11, -3516.27, -1720941.80, 11737211.28);
		
		// Set the MapView initial extent
		map.setExtent(env);

		Boolean mapMode = getSBASitesApplication().getMapMode();
		if (mapMode) {
			baseLayer = new BingMapsLayer(APP_ID, MapStyle.AerialWithLabels);
		} else {
			baseLayer = new BingMapsLayer(APP_ID, MapStyle.Road);
		}
		map.addLayer(baseLayer);
		
		dynamicLayer = new ArcGISDynamicMapServiceLayer(
				DYNAMIC_MAP_SERVICE_URL, getSBASitesApplication().getActiveLayerIDs());
		map.addLayer(dynamicLayer);
		
		params = new IdentifyParameters();
		params.setTolerance(20);
		params.setDPI(98);
		params.setLayers(new int[]{4});
		params.setLayerMode(IdentifyParameters.ALL_LAYERS);

		callout = map.getCallout();
		callout.setStyle(R.xml.site_callout);

		map.setOnStatusChangedListener(new OnStatusChangedListener() {

			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {
				
				if (source == map && status == STATUS.INITIALIZED) {
					
					LocationService ls = map.getLocationService();
					ls.setAutoPan(false);
					ls.setLocationListener(new LocationListener() {

						// Zooms to the current location when first GPS fix
						// arrives.
						public void onLocationChanged(Location loc) {
							location = loc;
						}

						public void onProviderDisabled(String arg0) {

						}

						public void onProviderEnabled(String arg0) {
						}

						public void onStatusChanged(String arg0, int arg1,
								Bundle arg2) {

						}
					});
					ls.start();

				}

			}
		});

		map.setOnSingleTapListener(new OnSingleTapListener() {
			private static final long serialVersionUID = 1L;
			public void onSingleTap(final float x, final float y) {
				callout.hide();
				if(!map.isLoaded()){
					return;
				}
				//establish the identify parameters	
				Point identifyPoint = map.toMapPoint(x, y);				
				params.setGeometry(identifyPoint);
				params.setSpatialReference(map.getSpatialReference());									
				params.setMapHeight(map.getHeight());
				params.setMapWidth(map.getWidth());
				Envelope env = new Envelope();
				map.getExtent().queryEnvelope(env);
				params.setMapExtent(env);
				params.setLayers(getSBASitesApplication().getActiveLayerIDs());
				MyIdentifyTask mTask = new MyIdentifyTask(identifyPoint);
				mTask.execute(params);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		map.pause();
	}

	@Override
	protected void onResume() {
		super.onResume(); 
		map.unpause();

	}
	
	
	
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
		if (map.isLoaded()) {
    		if (SBAApplication.getSearchResult() != null) {
    			SearchResult result = SBAApplication.getSearchResult();
    			navigateToPoint(result.coordinates);
    		}
    	}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		myIntent = intent;
		
//		Runnable r = new Runnable() {
//			
//			@Override
//			public void run() {
//				if (map.isLoaded()) {
//		    		if (SBAApplication.getSearchResult() != null) {
//		    			SearchResult result = myIntent.getParcelableExtra(SearchListActivity.SEARCH_RESULT);
//		    			navigateToPoint(result.coordinates);
//		    		}
//            	}
//			}
//		};
//		
//		runOnUiThread(r);
	
		    
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (position) {
		case 0:
			if (null != location) {
				navigateToLocation(location);
			} else {
			
				AlertDialog.Builder dialog = new AlertDialog.Builder(SBASitesActivity.this);
				dialog.setTitle("Location Error");
				dialog.setMessage("Your location could not be found");
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int Click) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			break;
		case 1:
			onSearchRequested();
			break;
		case 2:
			Intent intent = new Intent(this, SiteListActivity.class);
			
			startActivityForResult(intent, CHOOSE_SITE_RESULT);
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	public void navigateToLocation(Location loc) {
		double locy = loc.getLatitude();
		double locx = loc.getLongitude();
		Point wgspoint = new Point(locx, locy);
		Point mapPoint = (Point) GeometryEngine
				.project(wgspoint,
						SpatialReference.create(4326),
						map.getSpatialReference());

		Unit mapUnit = map.getSpatialReference()
				.getUnit();
		double zoomWidth = Unit.convertUnits(
				SEARCH_RADIUS,
				Unit.create(LinearUnit.Code.MILE_US),
				mapUnit);
		Envelope zoomExtent = new Envelope(mapPoint,
				zoomWidth, zoomWidth);
		map.setExtent(zoomExtent);
	}
	
	public void navigateToPoint(Point point) {
		
		Point mapPoint = (Point) GeometryEngine
				.project(point,
						SpatialReference.create(4326),
						map.getSpatialReference());

		Unit mapUnit = map.getSpatialReference()
				.getUnit();
		double zoomWidth = Unit.convertUnits(
				SEARCH_RADIUS,
				Unit.create(LinearUnit.Code.MILE_US),
				mapUnit);
		Envelope zoomExtent = new Envelope(mapPoint,
				zoomWidth, zoomWidth);
		map.setExtent(zoomExtent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		menu.findItem(R.id.instructions).setIntent(
				new Intent(this, Instructions.class));
		menu.findItem(R.id.layers).setIntent(
				new Intent(this, Layers.class));
		menu.findItem(R.id.list_view).setIntent(
				new Intent(this, SiteListActivity.class));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.list_view) {
			startActivityForResult(item.getIntent(), CHOOSE_SITE_RESULT);
		} else if (item.getItemId() == R.id.layers) {
			startActivityForResult(item.getIntent(), CHOOSE_LAYER);
		} else {
			startActivity(item.getIntent());
		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data) 
	{
		if (CHOOSE_SEARCH_RESULT == requestCode && RESULT_OK == resultCode) {
			Log.d("Search", "Search result");
		} else if (CHOOSE_SITE_RESULT == requestCode && RESULT_OK == resultCode) {
			Log.d("Search", "Site result");
			/*
			String mobileKey = data.getStringExtra("MobileKey");
			Site site = Site.siteForMobileKey(getApplicationContext(), mobileKey);
			if (null != site) {
				navigateToSite(site.latitude, site.longitude, mapView);
			}
			SiteOverlayItem siteOverlay = new SiteOverlayItem(site);
			layerItemizedOverlay.addOverlay(siteOverlay);
			mapView.invalidate();
			layerItemizedOverlay.setFocus(siteOverlay);
			 */
		} else {
			super.onActivityResult(requestCode, resultCode, data);
			
			Boolean mapMode = getSBASitesApplication().getMapMode();
			if (mapMode) {
				baseLayer.setMapStyle(MapStyle.AerialWithLabels);
			} else {
				baseLayer.setMapStyle(MapStyle.Road);
			}
			baseLayer.refresh();
			
			map.removeLayer(dynamicLayer);
			dynamicLayer = new ArcGISDynamicMapServiceLayer(
					DYNAMIC_MAP_SERVICE_URL, getSBASitesApplication().getActiveLayerIDs());
			map.addLayer(dynamicLayer);
			
			dynamicLayer.refresh();
			map.invalidate();
		}
	}

	protected SBAApplication getSBASitesApplication() {
		return (SBAApplication)getApplication();
	}

	// Creates custom content view with 'Graphic' attributes
	private View loadView(String name, String code) {
		View view = LayoutInflater.from(SBASitesActivity.this).inflate(
				R.layout.site_callout_layout, null);

		final TextView title = (TextView) view.findViewById(R.id.site_title);
		title.setText(name);

		final TextView subtitle = (TextView) view.findViewById(R.id.site_subtitle);
		subtitle.setText(code);
		
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				map.getCallout().hide();
				showSiteDetails();
				return false;
			}
		});
		
		return view;

	}
	
	public void showSiteDetails() {
		Intent intent = new Intent(this, SiteDetailActivity.class);
		intent.putExtra("SITE", selectedSite);
		startActivity(intent);
	}

	private class MyIdentifyTask extends AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {

		IdentifyTask mIdentifyTask;
		Point mAnchor;
		MyIdentifyTask(Point anchorPoint) {
			mAnchor = anchorPoint;
		}
		@Override
		protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
			IdentifyResult[] mResult = null;
			if (params != null && params.length > 0) {
				IdentifyParameters mParams = params[0];
				try {
					mResult = mIdentifyTask.execute(mParams);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return mResult;
		}

		@Override
		protected void onPostExecute(IdentifyResult[] results) {
			if (results.length > 0) {
				map.getCallout().show(mAnchor, loadView((String)results[0].getAttributes().get("SiteName"), (String)results[0].getAttributes().get("SiteCode")));
				selectedSite = results[0];
			} else {
				selectedSite = null;
			}
		} 

		@Override
		protected void onPreExecute() {
			//mIdentifyTask = new IdentifyTask("http://sampleserver3.arcgisonline.com/ArcGIS/rest/services/Earthquakes/EarthquakesFromLastSevenDays/MapServer");
			mIdentifyTask = new IdentifyTask(DYNAMIC_MAP_SERVICE_URL);
		}



	}
}