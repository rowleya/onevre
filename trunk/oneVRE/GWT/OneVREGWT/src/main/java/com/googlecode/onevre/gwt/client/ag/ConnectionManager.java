package com.googlecode.onevre.gwt.client.ag;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ConnectionManagerInterface;

public class ConnectionManager {

    private HashMap<VenueState, Vector<ConnectionDescription>> connections =
        new HashMap<VenueState, Vector<ConnectionDescription>>();
    private HashMap<VenueState, Vector<StreamDescription>> streams =
        new HashMap<VenueState, Vector<StreamDescription>>();

    private ConnectionManagerInterface ui = null;

    public void addVenue(VenueState state) {
        Vector<ConnectionDescription> connectionList = new Vector<ConnectionDescription>();
        Vector<StreamDescription> streamList = new Vector<StreamDescription>();
        connections.put(state, connectionList);
        streams.put(state, streamList);
    }

    public ConnectionManager(ConnectionManagerInterface ui) {
        this.ui = ui;
        ui.setConnectionManager(this);
    }

    public void addConnection(VenueState state, ConnectionDescription connection) {
        if (!connections.containsKey(state)) {
            addVenue(state);
        }
        Vector<ConnectionDescription> connectionList = connections.get(state);
        connectionList.add(connection);
        ui.addConnection(state, connection);
    }

    public void deleteConnection(VenueState state, ConnectionDescription connection) {
        Vector<ConnectionDescription> connectionList = connections.get(state);
        if (connectionList != null) {
            connectionList.remove(connection);
            ui.removeConnection(state, connection);
        }
    }

    public void addStream(VenueState state, StreamDescription stream) {
        if (!streams.containsKey(state)) {
            addVenue(state);
        }
        Vector<StreamDescription> streamList = streams.get(state);
        streamList.add(stream);
        ui.addStream(state, stream);
    }

    public void updateStream(VenueState state, StreamDescription stream) {
        Vector<StreamDescription> streamList = streams.get(state);
        if (streamList != null) {
            int idx = streamList.indexOf(stream);
            streamList.remove(stream);
            streamList.insertElementAt(stream, idx);
            ui.updateStream(state, stream);
        }
    }

    public void deleteStream(VenueState state, StreamDescription stream) {
        Vector<StreamDescription> streamList = streams.get(state);
        if (streamList != null) {
            streamList.remove(stream);
            ui.removeStream(state, stream);
        }
    }

}
