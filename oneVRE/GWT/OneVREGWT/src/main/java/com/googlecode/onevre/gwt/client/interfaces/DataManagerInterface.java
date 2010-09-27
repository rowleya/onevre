package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.DataManager;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.DataPanel;

public interface DataManagerInterface {

	public void setDataManager (DataManager dataManager);

	public void setDataPanel(VenueState state, DataPanel dataPanel);

	public void addData(VenueState state, DataDescription data);

	public void removeData(VenueState state, DataDescription data);

	public void updateData(VenueState state, DataDescription data);

	public void addDirectory(VenueState state, DataDescription data);

	public void deleteDirectory(VenueState state, DataDescription data);
}
