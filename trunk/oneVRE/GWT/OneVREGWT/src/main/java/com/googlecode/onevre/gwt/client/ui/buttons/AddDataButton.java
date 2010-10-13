package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.AddDataPanel;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class AddDataButton extends ActionButton implements ClickHandler {

	private VenueState venueState = null;

	private String parent = null;

	public AddDataButton(VenueState state, String parent) {
		this.venueState = state;
		this.parent = parent;
		setImageUrl("images/icons/add.png");
		setName("Add data item");
		setImageHeight("20px");
		getButton().addClickHandler(this);
	}

	@Override
	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

	public void onClick(ClickEvent paramClickEvent) {
		AddDataPanel adp = new AddDataPanel(parent,venueState.getUri());
		adp.show();
	}


}
