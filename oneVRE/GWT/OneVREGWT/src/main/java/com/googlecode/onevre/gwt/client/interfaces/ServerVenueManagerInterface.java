package com.googlecode.onevre.gwt.client.interfaces;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public interface ServerVenueManagerInterface {

	void setServerVenueManager(ServerVenueManager serverVenueManager);

	void addVenue(VenueState state);

	void removeVenue(VenueState state);

	Panel getPanel();

}
