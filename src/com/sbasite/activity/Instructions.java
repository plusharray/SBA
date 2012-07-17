package com.sbasite.activity;

import com.sbasite.sbasites.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Instructions extends Activity implements OnClickListener {

	private Button emailButton;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);
        
        this.emailButton = (Button) findViewById(R.id.Button08);
        this.emailButton.setOnClickListener(this);

    	((Button) findViewById(R.id.Button01)).setOnClickListener(this);
    	((Button) findViewById(R.id.Button02)).setOnClickListener(this);
    	((Button) findViewById(R.id.Button03)).setOnClickListener(this);
    	((Button) findViewById(R.id.Button04)).setOnClickListener(this);
    	
	}
    	
			public void onClick(View v) {
				switch (v.getId()){
				case R.id.Button01:
					Intent searchsitenameaddressIntent = new Intent(this, SearchSiteNameAddress.class);
					startActivity(searchsitenameaddressIntent);
					break;
				case R.id.Button02:
					Intent searchcoordinatesIntent = new Intent(this, SearchCoordinates.class);
					startActivity(searchcoordinatesIntent);
					break;
				case R.id.Button03:
					Intent searchaddressIntent = new Intent(this, SearchAddress.class);
					startActivity(searchaddressIntent);
					break;
				case R.id.Button04:
					Intent aboutsbacommunicationsIntent = new Intent(this, AboutSbaCommunications.class);
					startActivity(aboutsbacommunicationsIntent);
					break;
				case R.id.Button08:
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"mobilesupport@sbasite.com"});
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SBA Sites Support Request");
				emailIntent.setType("text/plain");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				finish();
				}
			}
}
