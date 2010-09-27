package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.googlecode.onevre.gwt.client.ag.DataManager;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.DataManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.DataPanel;

public class OneVREDataManager implements DataManagerInterface {

	DataManager dataManager = null;

	HashMap<VenueState, DataPanel> dataPanels = new HashMap<VenueState, DataPanel>();

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void setDataPanel(VenueState state, DataPanel dataPanel) {
		dataPanels.put(state, dataPanel);
	}

	public void addData(VenueState state, DataDescription data) {
		DataPanel dataPanel = dataPanels.get(state);
		if (dataPanel!=null){
			dataPanel.addData(data);
		}
	}

	public void removeData(VenueState state, DataDescription data) {
		DataPanel dataPanel = dataPanels.get(state);
		if (dataPanel!=null){
			dataPanel.removeData(data);
		}
	}

	public void updateData(VenueState state, DataDescription data) {
		DataPanel dataPanel = dataPanels.get(state);
		if (dataPanel!=null){
			dataPanel.updateData(data);
		}
	}

	public void addDirectory(VenueState state, DataDescription data) {
		DataPanel dataPanel = dataPanels.get(state);
		if (dataPanel!=null){
			dataPanel.addDirectory(data);
		}
	}

	public void deleteDirectory(VenueState state, DataDescription data) {
		DataPanel dataPanel = dataPanels.get(state);
		if (dataPanel!=null){
			dataPanel.removeDirectory(data);
		}
	}

}
