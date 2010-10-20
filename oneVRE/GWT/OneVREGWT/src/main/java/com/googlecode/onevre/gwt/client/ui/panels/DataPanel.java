package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.Icons;
import com.googlecode.onevre.gwt.client.ui.buttons.AddDataButton;
import com.googlecode.onevre.gwt.client.ui.buttons.CreateFolderButton;
import com.googlecode.onevre.gwt.client.ui.buttons.DataPropertiesButton;
import com.googlecode.onevre.gwt.client.ui.buttons.DeleteDataButton;
import com.googlecode.onevre.gwt.client.xmlrpc.UpdateData;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class DataPanel extends FlexTable implements ClickHandler{

	Vector<DataDescription> dataDescriptions = new Vector<DataDescription>();

	VenueState venueState = null;

	NamedFrame targetFrame = new NamedFrame("victim");

	HashMap<DataDescription, Widget> dataEntrys =  new HashMap<DataDescription, Widget>();

	public void addValue(int i, DataDescription data){

		GWT.log("addValue " + i + " : " + data.toString());
		String lm = data.getLastModified();
		if (!lm.equals("")){
			Image file = new Image(Icons.fileIcon);
			this.setWidget(2*i,0,file);
			file.setTitle(data.getName());
			this.setText(2*i, 1, data.getName());
			String expiry = data.getExpires();
			String dateString = "";
			Date date = null;
			try {
				date = DateTimeFormat.getFormat("EEE, MMM d, yyyy, HH:mm:ss").parse(data.getLastModified());
				dateString = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(date);
			} catch (IllegalArgumentException e) {
				GWT.log("LastModified: " + data.getLastModified() + " not parsable as date");
			}
			this.setText(2*i, 2, dateString);
			dateString = "";
			if ((expiry != null) && (!expiry.equals(""))) {
				this.setText(0, 2, "Expires");
				try {
					date = DateTimeFormat.getFormat("EEE, MMM d, yyyy, HH:mm:ss").parse(expiry);
					dateString = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(date);

				} catch (IllegalArgumentException e) {
					GWT.log("Expiry:" +expiry + " not parsable as date");
				}
			}
			this.setText(2*i, 3, dateString);
			this.setText(2*i+1,1,data.getDescription());
			this.setText(2*i+1,2,"");
			this.getFlexCellFormatter().setColSpan(2*i+1, 1, 3);
			HorizontalPanel iconPanel = new HorizontalPanel();
			/*
			Image propIcon = new Image(Icons.filePropertiesIcon);
			propIcon.setHeight("32px");
			propIcon.setTitle("Change Properties for " + data.getName());
			iconPanel.add(propIcon);

			Image delIcon = new Image(Icons.closeIcon);
			delIcon.setTitle("Delete " + data.getName());
			delIcon.setHeight("32px");
			*/
			iconPanel.add(new DataPropertiesButton(venueState, data).getButton());
			iconPanel.add(new DeleteDataButton(venueState, data).getButton());
			this.setWidget(2*i,4,iconPanel);
			this.getFlexCellFormatter().setRowSpan(2*i, 4, 2);
		}
	}


	public DataPanel(VenueState venueState) {
		this.venueState = venueState;
		this.dataDescriptions = venueState.getData();
		this.setWidth("100%");
		this.setHeight("100%");
		this.getRowFormatter().setStyleName(0,"gwt-PushButton gwt-PushButton-up");
		this.setText(0,0, "Name");
		this.getFlexCellFormatter().setColSpan(0, 0, 2);
		this.setText(0, 1, "Last Modified" );
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(new AddDataButton(venueState,null,targetFrame).getButton());
		buttonPanel.add(new CreateFolderButton(venueState).getButton());
		this.setWidget(0,3,buttonPanel);
		this.setWidget(0, 4, targetFrame);
		targetFrame.setVisible(false);
		this.getCellFormatter().setHorizontalAlignment(0, 3, HorizontalPanel.ALIGN_RIGHT);
		int i = 1;
		for (DataDescription dataDescription : dataDescriptions){
			addValue(i, dataDescription);
			i++;
		}
		addClickHandler(this);
	}

	public void addData(DataDescription data) {
		int i = dataDescriptions.size()+1;
		dataDescriptions.add(data);
		addValue(i, data);
	}

	public void removeData(DataDescription data) {
		GWT.log("DataId :"  + data.getId() + " name: " + data.getName());
		GWT.log("dataDescriptions: " + dataDescriptions.toString());
		int i = dataDescriptions.indexOf(data);
		if (i !=-1 ){
			GWT.log("dataentry:" + i);
			this.removeRow(2*i + 2);
			this.removeRow(2*i + 2);
			dataDescriptions.remove(i);
		}
	}

	public void updateData(DataDescription data) {
		int i = dataDescriptions.indexOf(data);
		if (i!=-1){
			dataDescriptions.setElementAt(data, i);
			addValue(i+1, data);
		}
	}

	public void addDirectory(DataDescription data) {
		// TODO Auto-generated method stub

	}

	public void removeDirectory(DataDescription data) {
		// TODO Auto-generated method stub

	}


	public void onClick(ClickEvent event) {
		Cell cell = getCellForEvent(event);
		GWT.log("Cell clicked: (" + cell.getCellIndex() + ", " +cell.getRowIndex() +")");
		int row = (cell.getRowIndex()-2) / 2;
		if (row ==-1) return;
		DataDescription data = dataDescriptions.get(row);
		if (cell.getCellIndex()!= (getCellCount(cell.getRowIndex())-1)){
			GWT.log("Data selected: "+ data.getName());
			String baseurl = GWT.getModuleBaseURL();
			if (baseurl.endsWith("/")){
				baseurl=baseurl.substring(0,baseurl.length()-1);
			}
			String url = baseurl + Application.getParam("pag_dataDownloadUrl");
			String postRequest = url + "?namespace=" + Application.getParam("pag_namespace");
		    postRequest += "&file=" + data.getId() + "&venue=" + venueState.getUri();
		    postRequest += "&selection=dataid";
		    GWT.log("download " + postRequest );
		    Window.open(postRequest, "download", "");
		 }

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
