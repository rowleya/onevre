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
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.buttons.AddDataButton;
import com.googlecode.onevre.gwt.client.ui.buttons.CreateFolderButton;
import com.googlecode.onevre.gwt.client.ui.buttons.DataPropertiesButton;
import com.googlecode.onevre.gwt.client.ui.buttons.DeleteDataButton;
import com.googlecode.onevre.gwt.client.ui.widgets.FileIcon;
import com.googlecode.onevre.gwt.client.xmlrpc.UpdateData;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class DataPanel extends FlexTable implements ClickHandler{

	Vector<DataDescription> dataDescriptions = new Vector<DataDescription>();

	VenueState venueState = null;

	NamedFrame targetFrame = new NamedFrame("victim");

	HashMap<Widget,DataDescription> dataEntrys =  new HashMap<Widget, DataDescription>();
	HashMap<String,Widget> widgetEntrys =  new HashMap<String,Widget>();
	HashMap<String, FlexTable> folders = new HashMap<String, FlexTable>();


	public void mapDataToWidget(Widget widget,DataDescription data){
		dataEntrys.put(widget, data);
		widgetEntrys.put(data.getId(), widget);
	}

	public void removeDataMappings(DataDescription data){
		Widget widget = widgetEntrys.remove(data.getId());
		if (widget!=null){
			dataEntrys.remove(widget);
		}
	}

	public int getRow(DataDescription data){
		FlexTable table = folders.get(data.getParentId());
		if (table == null){
			return -1;
		}
		Widget widget = widgetEntrys.get(data.getId());
		int nrows = table.getRowCount();
		GWT.log("getRow nrows: " +  nrows);
		int ret = -1;
		for (int i=0; i< nrows; i++){
			Widget w = table.getWidget(i, 0);
			GWT.log("r: " + i + " w: " + w + " = " + widget);
			if (table.getWidget(i, 0) == widget){
				return i;
			}
		}
		return ret;
	}

	public String formatDate(String dateString){
		String ret = "";
		Date date = null;
		try {
			date = DateTimeFormat.getFormat("EEE, MMM d, yyyy, HH:mm:ss").parse(dateString);
			return DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss").format(date);
		} catch (IllegalArgumentException e) {
			GWT.log("Date: " + dateString  + " not parsable as date");
		}
		return ret;
	}


	public Widget addValue(DataDescription data){
		FlexTable table = folders.get(data.getParentId());
		if (table == null){
			table = new FlexTable();
			table.addClickHandler(this);
			folders.put(data.getParentId(),table);
			table.setWidth("100%");
			table.setText(0, 0, "Orphaned Data");
		}
		int i=table.getRowCount();
		GWT.log("addValue " + i + " : " + data.toString());
		String lm = data.getLastModified();
		if (!lm.equals("")){
			FileIcon file = new FileIcon(data.getObjectType(),data.getType());
			table.setWidget(i,0,file);
			table.getFlexCellFormatter().setRowSpan(i, 0 , 2);
			file.setTitle(data.getName());
			table.setText(i, 1, data.getName());
			String expiry = data.getExpires();
			String dateString = formatDate(data.getLastModified());
			table.setText(i, 2, dateString);
			if ((expiry != null) && (!expiry.equals(""))) {
				this.setText(0, 2, "Expires");
				dateString = formatDate(expiry);
				table.setText(i, 3, dateString);
			}
			table.setText(i+1,0,data.getDescription());
//			this.setText(i+1,2,"");
			table.getFlexCellFormatter().setColSpan(i+1, 0, 3);
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

			if (data.getObjectType().equals(DataDescription.TYPE_DIR)){
				iconPanel.add(new AddDataButton(venueState,data.getId(),targetFrame).getButton());
				iconPanel.add(new CreateFolderButton(venueState,data.getId(),data.getHierarchyLevel()+1).getButton());
				FlexTable flextable = folders.get(data.getId());
				if (flextable == null){
					flextable = new FlexTable();
					flextable.addClickHandler(this);
					folders.put(data.getId(),flextable);
				}
				flextable.setWidth("100%");
				flextable.setText(0, 0, data.getDescription());
				flextable.getFlexCellFormatter().setColSpan(0, 0, 5);
			}
			iconPanel.add(new DataPropertiesButton(venueState, data).getButton());
			iconPanel.add(new DeleteDataButton(venueState, data).getButton());
			table.setWidget(i,4,iconPanel);
			table.getFlexCellFormatter().setHorizontalAlignment(i,4,HorizontalPanel.ALIGN_RIGHT);
			table.getFlexCellFormatter().setRowSpan(i, 4, 2);
			return file;
		}
		return null;
	}


	public DataPanel(VenueState venueState) {
		//setBorderWidth(1);
		this.venueState = venueState;
		this.dataDescriptions = venueState.getData();
		this.setWidth("100%");
		this.setHeight("100%");
		this.getRowFormatter().setStyleName(0,"gwt-PushButton gwt-PushButton-up");
		this.setText(0,0, "Name");
		this.getFlexCellFormatter().setColSpan(0, 0, 2);
		this.setText(0, 1, "Last Modified" );
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(new AddDataButton(venueState,"-1",targetFrame).getButton());
		buttonPanel.add(new CreateFolderButton(venueState,"-1",0).getButton());
		this.setWidget(0,3,buttonPanel);
		this.setWidget(0, 4, targetFrame);
		targetFrame.setVisible(false);
		this.getCellFormatter().setHorizontalAlignment(0, 3, HorizontalPanel.ALIGN_RIGHT);
//		int i = 1;
		folders.put("-1",this);
		for (DataDescription dataDescription : dataDescriptions){
//			if (dataDescription.getParentId().equals("-1")){
				mapDataToWidget(addValue(dataDescription),dataDescription);
//				i+=2;
//			}
		}
		addClickHandler(this);
	}

	public void addData(DataDescription data) {
		//int i = dataDescriptions.size()+1;
		dataDescriptions.add(data);
		FlexTable table = folders.get(data.getParentId());
		if (table==null){
			return;
		}
		int i = getRowCount();
		Widget w = addValue(data);
		if (w!=null){
			mapDataToWidget(w, data);
		}
		Widget w2 = widgetEntrys.get(data.getId());
		GWT.log("addDataWidget:" + w2);
		getRow(data);
	}


	public void removeData(DataDescription data) {
		FlexTable table = folders.get(data.getParentId());
/*		GWT.log("removeData: "+ data.toString());
		int row = getRow(data);
		GWT.log("deleteRow:"+ row);
*/		int i = dataDescriptions.indexOf(data);
		if (i!=-1){
			int row = getRow(data);
			GWT.log("deleteRow:"+ row);
			if (i !=-1 ){
				table.removeRow(row+1);
				table.removeRow(row);
				dataDescriptions.remove(i);
				removeDataMappings(data);
			}
		}
	}

	private void modifyTable(int row, DataDescription data){
		FlexTable table = folders.get(data.getParentId());
		if (table== null) {
			return;
		}
		FileIcon file = (FileIcon) table.getWidget(row,0);
		file.setMimeType(data.getType());
		table.setText(row, 1, data.getName());
		String expiry = data.getExpires();
		String dateString = formatDate(data.getLastModified());
		table.setText(row, 2, dateString);
		if ((expiry != null) && (!expiry.equals(""))) {
			this.setText(0, 2, "Expires");
			dateString = formatDate(expiry);
			table.setText(row, 3, dateString);
		}
		if (file.isOpen()){
			FlexTable flexTable = folders.get(data.getId());
			flexTable.setText(0, 0, data.getDescription());
		} else {
			table.setText(row+1,0,data.getDescription());
		}

		HorizontalPanel iconPanel = new HorizontalPanel();
		if (data.getObjectType().equals(DataDescription.TYPE_DIR)){
			iconPanel.add(new AddDataButton(venueState,data.getId(),targetFrame).getButton());
			iconPanel.add(new CreateFolderButton(venueState,data.getId(),data.getHierarchyLevel()+1).getButton());
		}
		iconPanel.add(new DataPropertiesButton(venueState, data).getButton());
		iconPanel.add(new DeleteDataButton(venueState, data).getButton());
		table.setWidget(row,4,iconPanel);
	}

	public void updateData(DataDescription data) {
		int i = dataDescriptions.indexOf(data);
		if (i!=-1){
			int row = getRow(data);
			GWT.log(" updateData Row:"+row);
			dataDescriptions.setElementAt(data, i);
			if (row > 0){
				modifyTable(row,data);
			}
		}
	}

	public void addDirectory(DataDescription data) {
		int i = dataDescriptions.size()+1;
		dataDescriptions.add(data);
		Widget w = addValue(data);
		if (w!=null){
			mapDataToWidget(w, data);
		}
	}

	public void removeDirectory(DataDescription data) {
		// TODO Auto-generated method stub

	}

	public void openDir(DataDescription data){
		int row = getRow(data);
		FlexTable parentTable = folders.get(data.getParentId());
		FileIcon widget = (FileIcon) widgetEntrys.get(data.getId());
		widget.toggle();
		if (widget.isOpen()){
			FlexTable flextable = folders.get(data.getId());
/*			if (flextable==null){
				flextable = new FlexTable();
				flextable.addClickHandler(this);
				folders.put(data.getId(),flextable);
				// flextable.setBorderWidth(1);
				flextable.setWidth("100%");
				flextable.setText(0, 0, data.getDescription());
				flextable.getFlexCellFormatter().setColSpan(0, 0, 5);

				int i=1;
				for (DataDescription dataDescription : dataDescriptions){
					if (dataDescription.getParentId().equals(data.getId())){
						mapDataToWidget(addValue(dataDescription),dataDescription);
//						i+=2;
					}
				}
			}
*/			parentTable.getRowFormatter().addStyleName(row, "gwt-PushButton gwt-PushButton-up");
			parentTable.getFlexCellFormatter().setRowSpan(row, 0, 1);
			parentTable.getFlexCellFormatter().setRowSpan(row, 4, 1);
			parentTable.setText(row+1,0,"");
			parentTable.getFlexCellFormatter().setColSpan(row+1, 0, 1);
			parentTable.getFlexCellFormatter().setColSpan(row+1, 1, 4);

			parentTable.setWidget(row+1, 1, flextable);
		} else {
			parentTable.getFlexCellFormatter().setRowSpan(row, 0 , 2);
			parentTable.getFlexCellFormatter().setRowSpan(row, 4, 2);
			parentTable.getFlexCellFormatter().setColSpan(row+1, 0, 3);
			parentTable.getFlexCellFormatter().setColSpan(row+1, 1, 1);
			parentTable.setText(row+1,0,data.getDescription());
			parentTable.setText(row+1,1,"");
			parentTable.getRowFormatter().removeStyleName(row, "gwt-PushButton gwt-PushButton-up");
		}

	}


	public DataDescription getDataFromEvent(FlexTable table, ClickEvent event){
	GWT.log(event.toDebugString());
		Cell cell = table.getCellForEvent(event);
		int row = cell.getRowIndex();
		if (row==0){
			return null;
		}
		Widget w = getWidget(row, 0);
		if (w == null){
			w = getWidget(row-1, 0);
		}
		if (w == null){
			return null;
		}
		DataDescription data = dataEntrys.get(w);
		if (data.getObjectType().equals(DataDescription.TYPE_DIR)){
		  	FlexTable subtable = folders.get(data.getId());
		  	if (subtable!=null){
			/*  	DataDescription dd = getDataFromEvent(subtable, event);
			  	if (dd!=null) {
			  		return dd;
		  		}*/
	  		}
		}
		return data;

	}

	public void onClick(ClickEvent event) {
/*		Cell cell = getCellForEvent(event);
		GWT.log("Cell numbers: (" + getCellCount(cell.getRowIndex()) + ", " +getRowCount() +")");
		GWT.log("Cell clicked: (" + cell.getCellIndex() + ", " +cell.getRowIndex() +")");
		int row = cell.getRowIndex();
		GWT.log("cell.getRowIndex():"+row);
		if (row==0){
			return;
		}
		Widget w = getWidget(row, 0);
		if (w == null){
			w = getWidget(row-1, 0);
		}
		if (w == null){
			return;
		}*/
//		FlexTable t = (FlexTable)event.getSource();
//		GWT.log("Event table(1,2) :" + t.getText(1, 2));
//		DataDescription data = getDataFromEvent(this, event);
		FlexTable table = (FlexTable)event.getSource();
		Cell cell = table.getCellForEvent(event);
		int row = cell.getRowIndex();
		if (row==0){
			return;
		}
		Widget w = table.getWidget(row, 0);
		if (w == null){
			w = table.getWidget(row-1, 0);
		}
		if (w == null){
			return ;
		}
		DataDescription data = dataEntrys.get(w);
		event.stopPropagation();
//		Cell cell = table.getCellForEvent(event);
		GWT.log("Data selected: "+ data.toString());
		if (cell.getCellIndex()!= (table.getCellCount(cell.getRowIndex())-1)){
			if (data.getObjectType().equals(DataDescription.TYPE_DIR)){
				openDir(data);
				return;
			}
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
			UpdateData.createDirectory(venueState.getUri(), dataDescription);
			Application.getDataManager().updateData(venueState, dataDescription);
		}
	}
}
