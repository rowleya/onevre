package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ui.ActionButton;

public class MonitorVenueButton extends ActionButton implements ClickHandler{

	VenueServerType venueServerType = null;
	String venueUrl = null;

	public MonitorVenueButton(String venueUrl) {
		this.venueUrl = venueUrl;
		setImageUrl("images/display.png");
		setName("Monitor Venue");
		getButton().addClickHandler(this);
	}

	@Override
	public void action() {
		GWT.log("In monitorVenue : " + venueUrl);
	}

	public void onClick(ClickEvent paramClickEvent) {
		GWT.log("In monitorVenue : " + venueUrl);
		Application.getServerManager().monitorVenue(venueUrl);

	}


}
