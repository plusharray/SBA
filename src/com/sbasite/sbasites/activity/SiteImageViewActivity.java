package com.sbasite.sbasites.activity;

import java.net.MalformedURLException;
import java.net.URL;

import com.sbasite.sbasites.R;
import com.sbasite.sbasites.task.LoadImageAsyncTask;
import com.sbasite.sbasites.task.LoadImageAsyncTask.LoadImageAsyncTaskResponder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class SiteImageViewActivity extends Activity implements LoadImageAsyncTaskResponder {
	
	private String siteCode;
	private ImageView image;
	protected ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		siteCode = getIntent().getStringExtra("SiteCode");
		setupViews();
	}

	private void setupViews() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.site_image_view_activity);
		image = (ImageView)findViewById(R.id.ImageView_SiteImageFullScreen);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			String urlString = String.format("http://map.sbasite.com/Mobile/GetImage?SiteCode=%s&width=600&height=600", siteCode);
			URL requestURL = new URL(urlString);
			new LoadImageAsyncTask(this).execute(requestURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void imageLoading() {
		setProgressBarIndeterminateVisibility(true);
		progressDialog = ProgressDialog.show(SiteImageViewActivity.this, "Loading...", "Image is loading");
	}

	public void imageLoadCancelled() {
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
	}

	public void imageLoaded(Drawable drawable) {
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
		image.setImageDrawable(drawable);
		image.setVisibility(View.VISIBLE);
	}

}
