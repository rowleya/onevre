package com.googlecode.onevre.gwt.client.ui;

import com.googlecode.onevre.gwt.client.ag.ConnectionManager;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ConnectionManagerInterface;

public class OneVREConnectionManager implements ConnectionManagerInterface {

    private ConnectionManager connectionManager = null;

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void addConnection(VenueState state, ConnectionDescription connection) {
        // TODO Auto-generated method stub

    }

    public void removeConnection(VenueState state,
            ConnectionDescription connection) {
        // TODO Auto-generated method stub

    }

    public void addStream(VenueState state, StreamDescription stream) {
        // TODO Auto-generated method stub

    }

    public void updateStream(VenueState state, StreamDescription stream) {
        // TODO Auto-generated method stub

    }

    public void removeStream(VenueState state, StreamDescription stream) {
        // TODO Auto-generated method stub

    }

}
