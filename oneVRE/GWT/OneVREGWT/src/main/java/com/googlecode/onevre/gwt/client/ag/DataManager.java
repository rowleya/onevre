package com.googlecode.onevre.gwt.client.ag;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.DataManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.DataPanel;

public class DataManager {

	private HashMap<VenueState,Vector<DataDescription>> dataTree = new HashMap<VenueState, Vector<DataDescription>>();

	private DataManagerInterface ui = null;

	public Vector<DataDescription> addVenue(VenueState state){
		Vector<DataDescription> dataItems = new Vector<DataDescription>();
		dataTree.put(state, dataItems);
		return dataItems;
	}

	public DataManager(DataManagerInterface ui) {
		this.ui = ui;
		ui.setDataManager(this);
	}

	public void addData(VenueState state, DataDescription data) {
		Vector<DataDescription> dataItems = dataTree.get(state);
		if (dataItems == null){
			dataItems = addVenue(state);
		}
		dataItems.add(data);
		ui.addData(state, data);
	}

	public void deleteData(VenueState state, DataDescription data) {
		Vector<DataDescription> dataItems = dataTree.get(state);
		if (dataItems != null){
			dataItems.remove(data);
			ui.removeData(state, data);
		}
	}

	public void updateData(VenueState state, DataDescription data) {
		Vector<DataDescription> dataItems = dataTree.get(state);
		if (dataItems != null){
			int idx = dataItems.indexOf(data);
			dataItems.remove(data);
			dataItems.insertElementAt(data, idx);
			ui.updateData(state,data);
		}
	}

	public void addDirectory(VenueState state, DataDescription data) {
		Vector<DataDescription> dataItems = dataTree.get(state);
		if (dataItems != null){
			dataItems.add(data);
			ui.addDirectory(state,data);
		}
	}

	public void deleteDirectory(VenueState state, DataDescription data) {
		Vector<DataDescription> dataItems = dataTree.get(state);
		if (dataItems != null){
			dataItems.remove(data);
			ui.deleteDirectory(state,data);
		}
	}

	public void setDataPanel(VenueState state, DataPanel dataPanel) {
		dataTree.put(state, state.getData());
		ui.setDataPanel(state,dataPanel);
	}

}
