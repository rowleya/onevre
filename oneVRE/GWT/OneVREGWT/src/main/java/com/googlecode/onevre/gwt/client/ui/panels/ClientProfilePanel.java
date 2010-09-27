package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ui.Icons;

public class ClientProfilePanel extends FlexTable implements ClickHandler {

	boolean active = false ;

	FlexTable activePanel = new FlexTable();
	FlexTable passivePanel = new FlexTable();

	ClientProfile clientProfile = null;

	String profileImage = null;

	public void setProfileImage(){
		String img = "images/defaultNode.png";
		if (clientProfile.getProfileType().equals("user")){
			img = "images/icons/defaultParticipant.png";
		}
		profileImage = GWT.getModuleBaseURL() + img;
		activePanel.setWidget(0, 0, new Image(profileImage));
		passivePanel.setWidget(0, 0, new Image(profileImage));
	}

	private void createPassivePanel(){
		passivePanel.setHeight("100%");
		passivePanel.setWidth("100%");
//		passivePanel.setWidget(0,0,profileImage);
		passivePanel.setText(0, 1, clientProfile.getName());
		passivePanel.getFlexCellFormatter().setWordWrap(0, 1, false);
		passivePanel.getRowFormatter().addStyleName(0, "gwt-StackPanelItem");
	}

	private void createActivePanel(){
		activePanel.setHeight("100%");
		activePanel.setWidth("100%");
//		activePanel.setWidget(0, 0, profileImage);
		activePanel.setText(0, 1, clientProfile.getName());
		passivePanel.getFlexCellFormatter().setWordWrap(0, 1, false);
		activePanel.getFlexCellFormatter().setColSpan(0, 1, 2);
		activePanel.getRowFormatter().addStyleName(0, "gwt-StackPanelItem");

		int i=1;
		String email=clientProfile.getEmail();
		if (!email.equals("")){
			activePanel.setWidget(i, 0, new Image(Icons.emailIcon));
			activePanel.setText(i, 1, "eMail: ");
			activePanel.setText(i, 2, email);
			i++;
		}
		String phone=clientProfile.getPhoneNumber();
		if (!phone.equals("")){
			activePanel.setWidget(i, 0, new Image(Icons.phoneIcon));
			activePanel.setText(i, 1, "Phone: ");
			activePanel.setText(i, 2, phone);
			i++;
		}
		String location=clientProfile.getLocation();
		if (!location.equals("")){
			activePanel.setWidget(i, 0, new Image(Icons.locationIcon));
			activePanel.setText(i, 1, "Location: ");
			activePanel.setText(i, 2, location);
			i++;
		}

	}

	public Panel getActivePanel(){
		return activePanel;
	}

	private void setPanel(){
		this.setHeight("100%");
		this.setWidth("100%");
		if (active){
			this.setWidget(0, 0, activePanel);
		} else {
			this.setWidget(0, 0, passivePanel);
		}
	}

	public ClientProfilePanel(ClientProfile clientProfile, boolean active) {
		this.active = active;
		this.clientProfile = clientProfile;
		this.addClickHandler(this);
		this.setStyleName("gwt-StackPanel gwt-Border");
		setProfileImage();
		createPassivePanel();
		createActivePanel();
		createPassivePanel();
		setPanel();
	}

	public void onClick(ClickEvent event) {
		active=!active;
		setPanel();
	}

	public void updatePanel(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
		setProfileImage();
		createPassivePanel();
		createActivePanel();
	}

}
