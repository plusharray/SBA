package com.sbasite.sbasites.app;

import java.util.ArrayList;

import com.sbasite.sbasites.model.SBALayer;
import com.sbasite.sbasites.model.SearchResult;
import com.sbasite.sbasites.model.Site;

import greendroid.app.GDApplication;

public class SBAApplication extends GDApplication {



	private static ArrayList<SBALayer> layers = new ArrayList<SBALayer>();
	private static final ArrayList<Site> sites = new ArrayList<Site>();
	private static Boolean mapMode = false;
	private static SearchResult searchResult = null;

	@Override
	public void onCreate() {
		super.onCreate();
		for (int i = 0; i < 5; i++) {
			layers.add(new SBALayer(i));
		}
	}
	
	public static SearchResult getSearchResult() {
		return searchResult;
	}

	public static void setSearchResult(SearchResult searchResult) {
		SBAApplication.searchResult = searchResult;
	}

	public ArrayList<SBALayer> getLayers() {
		// TODO Auto-generated method stub
		return layers;
	}

	public int[] getActiveLayerIDs() {
		ArrayList<SBALayer> activeLayers = new ArrayList<SBALayer>();
		for (SBALayer layer : layers) {
			if (layer.activated) {
				activeLayers.add(layer);
			}
		}
		int[] ids = new int[activeLayers.size()];
		for (int i = 0; i < activeLayers.size(); i++) {
			SBALayer layer = activeLayers.get(i);
			ids[i] = layer.id;
		}
		return ids;
	}

	public boolean getMapMode() {
		// TODO Auto-generated method stub
		return mapMode;
	}

	public void setMapMode(boolean b) {
		// TODO Auto-generated method stub
		mapMode = b;
	}

	public ArrayList<Site> getCurrentSites() {
		// TODO Auto-generated method stub
		return sites;
	}

}
