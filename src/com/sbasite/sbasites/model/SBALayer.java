package com.sbasite.sbasites.model;

import com.sbasite.R;

public class SBALayer {

	public int id;
	public String name;
	public boolean activated = true;
	public int iconID;

	public SBALayer(int id) {
		this.id = id;
		switch (id) {
			case 0:  
				this.name = "New Construction";
				this.iconID = R.drawable.new_construction;
				break;
			case 1:  
				this.name = "SBA Owned";
				this.iconID = R.drawable.owned;
				break;
			case 2:  
				this.name = "SBA Managed";
				this.iconID = R.drawable.managed;
				break;
			case 3:  
				this.name = "Canada";
				this.iconID = R.drawable.canada;
				break;
			case 4:  
				this.name = "Central America";
				this.iconID = R.drawable.central_america;
				break;
		}
	}
}