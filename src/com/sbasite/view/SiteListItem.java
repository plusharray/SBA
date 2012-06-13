package com.sbasite.view;

import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.sbasite.R;
import com.sbasite.model.SBALayer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SiteListItem extends RelativeLayout {

	private ImageView     pinIconView;
	private TextView      siteName;
	private TextView      siteCode;

	public SiteListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSite(IdentifyResult site) {
		findViews();
		if (null != site) {
			siteName.setText((String)site.getAttributes().get("SiteName"));
			siteCode.setText((String)site.getAttributes().get("SiteCode"));
			SBALayer layer = new SBALayer(site.getLayerId());
			Drawable icon = getResources().getDrawable(layer.iconID);
			pinIconView.setImageDrawable(icon);
		}
	}

	private void findViews() {
		pinIconView = (ImageView) findViewById(R.id.site_pin_icon);
		siteName = (TextView) findViewById(R.id.site_site_name);
		siteCode = (TextView) findViewById(R.id.site_site_code);
	}

}
