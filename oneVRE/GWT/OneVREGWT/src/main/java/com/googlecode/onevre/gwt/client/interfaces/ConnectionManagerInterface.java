package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.ConnectionManager;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public interface ConnectionManagerInterface {

    void setConnectionManager(ConnectionManager connectionManager);

    void addConnection(VenueState state, ConnectionDescription connection);

    void removeConnection(VenueState state, ConnectionDescription connection);

    void addStream(VenueState state, StreamDescription stream);

    void updateStream(VenueState state, StreamDescription stream);

    void removeStream(VenueState state, StreamDescription stream);

}
