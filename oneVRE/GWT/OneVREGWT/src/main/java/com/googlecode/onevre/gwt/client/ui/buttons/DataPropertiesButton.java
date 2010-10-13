package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.EditDataDescriptionPanel;
import com.googlecode.onevre.gwt.client.xmlrpc.UpdateData;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class DataPropertiesButton extends ActionButton implements ClickHandler, MessageResponseHandler {

	private VenueState venueState = null;

	private DataDescription data = null;

	public DataPropertiesButton(VenueState state, DataDescription data) {
		this.venueState = state;
		this.data = data;
		setImageUrl("images/icons/fileProperties.png");
		setName("Change Properties for " + data.getName());
		setImageHeight("20px");
		getButton().addClickHandler(this);
	}

	@Override
	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

	public void onClick(ClickEvent paramClickEvent) {
		EditDataDescriptionPanel eddp = new EditDataDescriptionPanel(this);
		eddp.setDataDescription(data);
		eddp.show();
	}

	public void handleResponse(MessageResponse response) {
		if (response.getResponseCode() == MessageResponse.OK){
			DataDescription dataDescription = ((EditDataDescriptionPanel)response.getSource()).getDataDescription();
			GWT.log("DATA: " + dataDescription.toString());
			UpdateData.updateData(venueState.getUri(), dataDescription);
			Application.getDataManager().updateData(venueState, dataDescription);
		}
	}

}
