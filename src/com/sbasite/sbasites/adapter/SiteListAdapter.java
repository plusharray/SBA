package com.sbasite.sbasites.adapter;

import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.sbasite.R;
import com.sbasite.sbasites.view.SiteListItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SiteListAdapter extends BaseAdapter {
	
	private Context context;
	public IdentifyResult[] sites;

	public SiteListAdapter(Context context, IdentifyResult[] sites) {
		super();
		this.context = context;
		this.sites = sites;
	}

	public int getCount() {
		return sites.length;
	}

	public IdentifyResult getItem(int index) {
		if ((null != sites) && (sites.length > 0)) {
			return sites[index];
		} else {
			return null;
		}
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		SiteListItem siteListItem;
		if (null == convertView) {
			siteListItem = (SiteListItem)View.inflate(context, R.layout.site_list_item, null);
		} else {
			siteListItem = (SiteListItem)convertView;
		}
		siteListItem.setSite(sites[position]);
		return siteListItem;
	}

	public void forceReload() {
		notifyDataSetChanged();
	}
}
