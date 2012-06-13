package com.sbasite.task;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.esri.core.map.FeatureSet;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;
import com.sbasite.model.SearchResult;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class LoadSearchResultsAsyncTask extends AsyncTask<String, Void, LoadSearchResultsAsyncTask.LoadSearchResults> {

	private Context context;
	private LoadSearchResultsResponder responder;
	private static final String DYNAMIC_MAP_SERVICE_URL = "http://mapservices.sbasite.com/ArcGIS/rest/services/Google/MobileiOS/MapServer";
	
	public interface LoadSearchResultsResponder {
		public void loadingSearchResults();
		public void searchResultsLoaded(LoadSearchResults results);
	}
	
	public class LoadSearchResults {
		public ArrayList<SearchResult> searchResults;
		public LoadSearchResults(ArrayList<SearchResult> searchResults) {
			super();
			this.searchResults = searchResults;
		}
	}
	
	public LoadSearchResultsAsyncTask(Context context, LoadSearchResultsResponder responder) {
		super();
		this.context = context;
		this.responder = responder;
	}
	
	@Override
	  public void onPreExecute() {
		super.onPreExecute();
		responder.loadingSearchResults();
	}
	
	@Override
	protected void onPostExecute(LoadSearchResults result) {
		super.onPostExecute(result);
		responder.searchResultsLoaded(result);
	}

	@Override
	protected LoadSearchResults doInBackground(String... params) {
		String searchText = params[0].toString();
		
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		
		results.addAll(searchForMatchingAddresses(searchText));
		//results.addAll(searchForCoordinates(searchText));
		//results.addAll(searchForMatchingSites(searchText));
		
		LoadSearchResults loadedResults = new LoadSearchResults(results);
		
		return loadedResults;
	}
	
	private ArrayList<SearchResult> searchForMatchingAddresses(String addressInput) {
		/*
		 * Geocode search text to get address matches
		 */
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		try {
			Geocoder geoCoder = new Geocoder(context);
			List<Address> foundAdresses = geoCoder.getFromLocationName(addressInput, 10); //Search addresses
			if (foundAdresses.size() == 0) { //if no address found, display an error
				/*
				Dialog locationError = new AlertDialog.Builder(SearchListActivity.this)
				.setIcon(0)
				.setTitle("Error")
				.setPositiveButton(R.string.ok, null)
				.setMessage("Sorry, your address doesn't exist.")
				.create();
				locationError.show();
				*/
			}
			else { //else display address on map
				for (int i = 0; i < foundAdresses.size(); ++i) {
					//Save results as Longitude and Latitude
					//@todo: if more than one result, then show a select-list
					Address x = foundAdresses.get(i);
					SearchResult result = new SearchResult(x);
					results.add(result);
				}
				//navigateToLocation((lat * 1000000), (lon * 1000000), map); //display the found address
			}
		}
		catch (Exception e) {
			//@todo: Show error message
			e.printStackTrace();
		}
		
		Query siteQuery = new Query();
		siteQuery.setText(addressInput);
		QueryTask queryTask = new QueryTask(DYNAMIC_MAP_SERVICE_URL);
		FeatureSet featureSet = null;
		try {
			featureSet = queryTask.execute(siteQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		ArrayList<Site> sites = Site.sitesForSearchText(context, addressInput);
		
		for (Site site : sites) {
			SearchResult result = new SearchResult(site);
			results.add(result);
		}
		*/
		
		StringTokenizer tokenizer = new StringTokenizer(addressInput, ",");
		
		if (tokenizer.countTokens() > 1 && tokenizer.countTokens() < 3) {
			String coordLat = tokenizer.nextToken();
			String coordLong = tokenizer.nextToken().trim();
			
			if ((containsOnlyNumbers(coordLat)) && (containsOnlyNumbers(coordLong))) {
				Double latitude = Double.valueOf(coordLat);
				Double longitude = (Double.valueOf(coordLong));
				if (longitude > 0.0) {
					longitude *= -1.0;
				}
				SearchResult result = new SearchResult(latitude, longitude);
				results.add(result);
			}
		}
		
		return results;
	}
	
	public boolean containsOnlyNumbers(String str) {
		//It can't contain only numbers if it's null or empty...
		if (str == null || str.length() == 0)
			return false;
		for (int i = 0; i < str.length(); i++) {
			//If we find a non-digit character we return false.
			if ((!Character.isDigit(str.charAt(i))) && str.charAt(i) != '-')
				return false;
			//if (!Character.isDigit(str.charAt(i)))
				
		}
		return true;
	}

}
