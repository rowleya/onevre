package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ui.Icons;

import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class EditClientProfilePanel extends ModalPopup<DockPanel> implements ClickHandler {

	TextBox nameInput = new TextBox();
	TextBox emailInput = new TextBox();
	TextBox phoneInput = new TextBox();
	TextBox locationInput = new TextBox();
	TextBox homeInput = new TextBox();
	ListBox profileInput = new ListBox();
	PushButton cancel = new PushButton("Cancel",this);
	PushButton ok = new PushButton("Set Profile Values",this);

	ClientProfile clientProfile = null;

	MessageResponseHandler handler = null;

	boolean changed = false;

	public void setClientProfile(ClientProfile cp){
		clientProfile = cp;
		changed = false;
		nameInput.setText(clientProfile.getName());
		emailInput.setText(clientProfile.getEmail());
		phoneInput.setText(clientProfile.getPhoneNumber());
		locationInput.setText(clientProfile.getLocation());
		homeInput.setText(clientProfile.getHomeVenue());
		if (clientProfile.getProfileType().equals("user")){
			profileInput.setSelectedIndex(0);
		} else {
			profileInput.setSelectedIndex(1);
		}
	}

	public boolean hasChanged(){
		return changed;
	}

	public ClientProfile getClientProfile(){
		return clientProfile;
	}


	public EditClientProfilePanel(MessageResponseHandler handler) {
		super(new DockPanel());
		DockPanel panel= getWidget();
		this.handler = handler;
		Image img = new Image(GWT.getModuleBaseURL() + "images/person.png");
		panel.add(img,DockPanel.WEST);
		panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
		Grid profilepanel = new Grid (6,3);
		profilepanel.setWidget(0,0, new Image(Icons.participantIcon));
		profilepanel.setText(0,1, "Name: *");
		profilepanel.setWidget(0, 2, nameInput);
		profilepanel.setWidget(1,0, new Image(Icons.emailIcon));
		profilepanel.setText(1,1, "E-Mail: *");
		profilepanel.setWidget(1, 2, emailInput);
		profilepanel.setWidget(2,0, new Image(Icons.phoneIcon));
		profilepanel.setText(2,1, "Phone Number:");
		profilepanel.setWidget(2, 2, phoneInput);
		profilepanel.setWidget(3,0, new Image(Icons.locationIcon));
		profilepanel.setText(3,1, "Location:");
		profilepanel.setWidget(3, 2, locationInput);
		profilepanel.setWidget(4,0, new Image( Icons.homeIcon));
		profilepanel.setText(4,1, "Home Venue: *");
		profilepanel.setWidget(4, 2, homeInput);
		profileInput.insertItem("User",0);
		profileInput.insertItem("Node",1);
		profilepanel.setWidget(5,0, new Image(Icons.hostIcon));
		profilepanel.setText(5,1, "Profile Type: *");
		profilepanel.setWidget(5, 2, profileInput);
		panel.add(profilepanel,DockPanel.CENTER);

		DockPanel buttons  = new DockPanel();
		buttons.add(ok, DockPanel.EAST);
		buttons.add(cancel, DockPanel.WEST);
		panel.add(buttons,DockPanel.SOUTH);
        panel.setWidth("100%");
	}

	public void onClick(ClickEvent event) {
		if (event.getSource().equals(ok)){
			clientProfile.setName(nameInput.getText());
			clientProfile.setEmail(emailInput.getText());
			clientProfile.setPhoneNumber(phoneInput.getText());
			clientProfile.setLocation(locationInput.getText());
			clientProfile.setHomeVenue(homeInput.getText());
			clientProfile.setProfileType(profileInput.getItemText(profileInput.getSelectedIndex()));
			changed=true;
			handler.handleResponse(new MessageResponse(MessageResponse.OK,this));
		} else {
			handler.handleResponse(new MessageResponse(MessageResponse.CANCEL,this));
		}
		hide();
	}

}
