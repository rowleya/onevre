package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.ConnectionManager;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public interface ConnectionManagerInterface {

	public void setConnectionManager(ConnectionManager connectionManager);

	public void addConnection(VenueState state, ConnectionDescription connection);

	public void removeConnection(VenueState state, ConnectionDescription connection);

	public void addStream(VenueState state, StreamDescription stream);

	public void updateStream(VenueState state, StreamDescription stream);

	public void removeStream(VenueState state, StreamDescription stream);

}
