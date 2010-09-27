package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.xmlrpc.StopMonitorVenue;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class CloseButton extends ActionButton implements ClickHandler {

	private VenueState state = null;

	private ServerVenueManager serverVenueManager = null;

	public CloseButton(VenueState state, ServerVenueManager serverVenueManager) {
		this.state = state;
		this.serverVenueManager = serverVenueManager;
		setImageUrl("images/close.png");
		setName("Stop Monitoring");
		setImageHeight("16px");
		getButton().addClickHandler(this);
	}

	@Override
	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

	public void onClick(ClickEvent paramClickEvent) {

		new StopMonitorVenue(serverVenueManager).stopMonitoring(state);
	}

}
