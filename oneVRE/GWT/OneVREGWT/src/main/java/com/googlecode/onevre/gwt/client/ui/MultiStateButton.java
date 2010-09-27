package com.googlecode.onevre.gwt.client.ui;

import com.google.gwt.core.client.GWT;

public abstract class MultiStateButton extends ActionButton {

	String [] imageUrls;
	String [] names;

	int state = 0;

	public void setImageUrls(String [] imageUrls) {
		this.imageUrls = imageUrls;
		super.setImageUrl(imageUrls[state]);
	}

	public void setNames(String [] names) {
		this.names = names;
		super.setName(names[state]);
	}

	public void toggle(){
		state ++;
		if (state==imageUrls.length) {
			state = 0;
		}
		super.setImageUrl(imageUrls[state]);
		super.setName(names[state]);
	}

	public void onClick(){
		this.toggle();
		GWT.log(this.name + " Button pressed");
		this.action();
	}

	abstract public void action();

	}
