package com.sbasite.sbasites.activity;

import java.util.ArrayList;

import com.sbasite.R;
import com.sbasite.sbasites.adapter.SearchResultListAdapter;
import com.sbasite.sbasites.app.SBAApplication;
import com.sbasite.sbasites.model.SearchResult;
import com.sbasite.sbasites.task.LoadSearchResultsAsyncTask;
import com.sbasite.sbasites.task.LoadSearchResultsAsyncTask.LoadSearchResults;
import com.sbasite.sbasites.task.LoadSearchResultsAsyncTask.LoadSearchResultsResponder;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class SearchListActivity extends ListActivity implements LoadSearchResultsResponder {

	//private static final String TAG = SearchListActivity.class.getSimpleName();
	public static final String SEARCH_RESULT = "search_result";
	private SearchResultListAdapter listAdapter;
	private String query=null;
	protected ProgressDialog progressDialog;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //searchString = getIntent().getStringExtra("search_text");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search_list_activity);
        
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          query = intent.getStringExtra(SearchManager.QUERY);
          new LoadSearchResultsAsyncTask(this, this).execute(query);
        }
        
        setUpViews();
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          query = intent.getStringExtra(SearchManager.QUERY);
          new LoadSearchResultsAsyncTask(this, this).execute(query);
        }
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		SearchResult result = listAdapter.getItem(position); 
		if (null != result) {
			SBAApplication.setSearchResult(result);
//			Intent intent = new Intent(this, SBASitesActivity.class);
//			intent.putExtra(SEARCH_RESULT, result);
//			startActivity(intent);
		}
		finish();
	}

	private void setUpViews() {
			
	}

	public void loadingSearchResults() {
		setProgressBarIndeterminateVisibility(true);
		progressDialog = ProgressDialog.show(
				SearchListActivity.this,
				"Searching...",
				"Performing search for '" + query + "'"
		);
	}

	public void searchResultsLoaded(LoadSearchResults results) {
		ArrayList<SearchResult> searchResults = results.searchResults;
		listAdapter = new SearchResultListAdapter(this, searchResults);
		setListAdapter(listAdapter);
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
		listAdapter.forceReload();
	}

	protected SBAApplication getSBASitesApplication() {
		return (SBAApplication)getApplication();
	}
}
