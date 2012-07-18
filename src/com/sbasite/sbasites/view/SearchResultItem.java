package com.sbasite.sbasites.view;

import com.sbasite.R;
import com.sbasite.sbasites.model.SearchResult;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchResultItem extends RelativeLayout {

	private TextView      siteName;
	private TextView      siteCode;

	public SearchResultItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setResult(SearchResult result) {
		findViews();
		siteName.setText(result.title);
		siteCode.setText(result.coordinates.toString());
	}

	private void findViews() {
		siteName = (TextView) findViewById(R.id.search_result_title);
		siteCode = (TextView) findViewById(R.id.search_result_subtitle);
	}
}
