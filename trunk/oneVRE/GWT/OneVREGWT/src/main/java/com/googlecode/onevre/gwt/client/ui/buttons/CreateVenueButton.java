package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.Icons;
import com.googlecode.onevre.gwt.client.ui.panels.CreateVenuePanel;
import com.googlecode.onevre.gwt.client.xmlrpc.SetClientProfile;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class CreateVenueButton extends ActionButton implements ClickHandler, MessageResponseHandler {

	VenueServerType venueServerType = null;
	CreateVenuePanel cvp = null;

	public CreateVenueButton() {
		this.venueServerType = venueServerType;
		setImageUrl("images/addVenue.png");

		setImageHeight("22px");
		setName("Create new Venue");
		this.getButton().setHeight("22px");
		this.getButton().setWidth("22px");
		this.getButton().addClickHandler(this);
		cvp = new CreateVenuePanel(Application.getUserManager().getVOAttributes());
	}

	public VenueServerType getVenueServerType(){
		return venueServerType;
	}

	@Override
	public void action() {
		GWT.log("in VenueTreeButton action");
		cvp.show();
	}

	public void handleResponse(MessageResponse response) {
		if (response.getResponseCode()==MessageResponse.OK){
//			ClientProfile clientProfile = ((CreateVenuePanel)response.getSource()).getClientProfile();
//			Application.getUserManager().setLocalUser(clientProfile);
			SetClientProfile.setClientProfile();
		}
	}

	public void onClick(ClickEvent arg0) {
		this.action();
	}

}
