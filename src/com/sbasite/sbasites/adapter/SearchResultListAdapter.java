package com.sbasite.sbasites.adapter;

import java.util.ArrayList;

import com.sbasite.sbasites.R;
import com.sbasite.sbasites.model.SearchResult;
import com.sbasite.sbasites.view.SearchResultItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchResultListAdapter extends BaseAdapter {

	//private static final String TAG = SearchResultListAdapter.class.getSimpleName();
	private Context context;
	private ArrayList<SearchResult> results;
	
	public SearchResultListAdapter(Context context, ArrayList<SearchResult> results) {
		super();
		this.context = context;
		this.results = results;
	}

	public int getCount() {
		return results.size();
	}

	public SearchResult getItem(int index) {
		return (null == results) ? null : results.get(index);
	}

	public long getItemId(int index) {
		return index;
	}
	
	 public View getView(int position, View convertView, ViewGroup parent) {
		 
		 SearchResultItem searchListItemView;
		if (null == convertView) {
			searchListItemView = (SearchResultItem)View.inflate(context, R.layout.search_result_list_item, null);
		} else {
			searchListItemView = (SearchResultItem)convertView;
		}
		searchListItemView.setResult(results.get(position));
		return searchListItemView;
	}

	 public void forceReload() {
			notifyDataSetChanged();
	}
}
