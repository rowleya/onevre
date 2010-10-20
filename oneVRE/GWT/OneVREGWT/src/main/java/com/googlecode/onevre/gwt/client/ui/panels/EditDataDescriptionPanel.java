package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ui.Icons;

import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class EditDataDescriptionPanel extends ModalPopup<DockPanel> implements ClickHandler {

	TextBox nameInput = new TextBox();
	TextArea descriptionInput = new TextArea();
	DateBox dateInput = new DateBox();
//	TextBox  = new TextBox();
	TextBox locationInput = new TextBox();
	TextBox homeInput = new TextBox();
	ListBox profileInput = new ListBox();
	PushButton cancel = new PushButton("Cancel",this);
	PushButton clear = new PushButton("Clear",this);
	PushButton ok = new PushButton("Set Data Description",this);
	FlexTable profilepanel = new FlexTable();

	DataDescription dataDescription = null;

	MessageResponseHandler handler = null;


	boolean changed = false;

	private static String DATE_FORMAT = "EEE, MMM d, yyyy, HH:mm:ss";

	public void setDataDescription(DataDescription dataDescription){
		this.dataDescription = dataDescription;
		changed = false;
		nameInput.setText(dataDescription.getName());
		descriptionInput.setText(dataDescription.getDescription());
		String expiry = dataDescription.getExpires();
		if (expiry != null){
			Image timeout = new Image(Icons.timeoutIcon);
			timeout.setHeight("22px");
			profilepanel.setWidget(2,0, timeout);
			profilepanel.setText(2,1, "expires:");
			profilepanel.setWidget(2, 2, dateInput);
			profilepanel.setWidget(2,3,clear);
			dateInput.setValue(null);
			Date date = new Date();
			try {
				date =	DateTimeFormat.getFormat(DATE_FORMAT).parse(dataDescription.getExpires());
				dateInput.setValue(date);
			} catch (IllegalArgumentException e) {
				GWT.log("Expiry:" + dataDescription.getExpires() + " not parsable as date");
			}
			dateInput.setVisible(true);
		} else {
			dateInput.setVisible(false);
		}
	}

	public boolean hasChanged(){
		return changed;
	}

	public DataDescription getDataDescription(){
		return dataDescription;
	}

	public EditDataDescriptionPanel(MessageResponseHandler handler) {
		super(new DockPanel());
		DockPanel panel= getWidget();
		this.handler = handler;
		Image img = new Image(Icons.filePropertiesIcon);
		img.setHeight("64px");
		panel.add(img,DockPanel.WEST);
		panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
//		Grid profilepanel = new Grid (3,3);
		profilepanel.setWidget(0,0, new Image(Icons.fileIcon));
		profilepanel.setText(0,1, "Name: *");
		profilepanel.setWidget(0, 2, nameInput);
		profilepanel.getFlexCellFormatter().setColSpan(0,2,2);
		Image docInfo = new Image(Icons.docInfoIcon);
		docInfo.setHeight("22px");
		profilepanel.setWidget(1,0, docInfo);
		profilepanel.setText(1,1, "Descritpion: *");
		descriptionInput.setVisibleLines(3);
		profilepanel.setWidget(1, 2, descriptionInput);
		profilepanel.getFlexCellFormatter().setColSpan(1,2,2);
		panel.add(profilepanel,DockPanel.CENTER);

		DockPanel buttons  = new DockPanel();
		buttons.add(ok, DockPanel.EAST);
		buttons.add(cancel, DockPanel.WEST);
		panel.add(buttons,DockPanel.SOUTH);
        panel.setWidth("100%");
	}

	public void onClick(ClickEvent event) {
		if (event.getSource().equals(clear)){
			dateInput.setValue(null);
			return;
		}
		if (event.getSource().equals(ok)){
			dataDescription.setName(nameInput.getText());
			dataDescription.setDescription(descriptionInput.getText());
			GWT.log("has DateInput :" + dateInput.isVisible());
			if (dateInput.isVisible()){
				String expString = "";
				Date date = dateInput.getValue();
				if (date!=null){
					expString = DateTimeFormat.getFormat(DATE_FORMAT).format(date);
				}
				dataDescription.setExpires(expString);
			}
			changed=true;
			handler.handleResponse(new MessageResponse(MessageResponse.OK,this));
		} else {
			handler.handleResponse(new MessageResponse(MessageResponse.CANCEL,this));
		}
		hide();
	}

}
