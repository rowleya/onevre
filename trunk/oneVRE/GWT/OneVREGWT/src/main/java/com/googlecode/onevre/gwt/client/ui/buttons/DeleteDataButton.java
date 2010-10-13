package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.xmlrpc.UpdateData;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class DeleteDataButton extends ActionButton implements ClickHandler {

	private VenueState state = null;

	private DataDescription data = null;

	public DeleteDataButton(VenueState state, DataDescription data) {
		this.state = state;
		this.data = data;
		setImageUrl("images/icons/close.png");
		setName("Delete " + data.getName());
		setImageHeight("20px");
		getButton().addClickHandler(this);
	}

	@Override
	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

	public void onClick(ClickEvent paramClickEvent) {
		UpdateData.deleteData(state.getUri(), data);
	}

}
