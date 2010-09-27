package com.googlecode.onevre.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public abstract class ActionButton {


	Image image = new Image();

	PushButton button = new PushButton(image);

	String name;

	public void setImageUrl(String imageUrl) {
		this.image.setUrl(GWT.getModuleBaseURL() + imageUrl);
	}

	public void setImageHeight(String height) {
		this.image.setHeight(height);
	}

	public void setName(String name) {
		button.setTitle(name);
		this.name = name;
	}

	public PushButton getButton() {
		return button;
	}

	public String getName(){
		return this.name;
	}

	public void onClick(){
		GWT.log(this.name + " Button pressed");
		this.action();
	}

	abstract public void action();

	}
