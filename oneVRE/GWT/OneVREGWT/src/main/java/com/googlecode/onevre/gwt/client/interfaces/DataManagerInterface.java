package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.DataManager;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.DataPanel;

public interface DataManagerInterface {

    void setDataManager(DataManager dataManager);

    void setDataPanel(VenueState state, DataPanel dataPanel);

    void addData(VenueState state, DataDescription data);

    void removeData(VenueState state, DataDescription data);

    void updateData(VenueState state, DataDescription data);

    void addDirectory(VenueState state, DataDescription data);

    void deleteDirectory(VenueState state, DataDescription data);
}
