package com.googlecode.onevre.gwt.client.ag;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ServerVenueManagerInterface;
import com.googlecode.onevre.gwt.client.xmlrpc.GetMonitoredVenues;
import com.googlecode.onevre.gwt.client.xmlrpc.MonitorVenue;

public class ServerVenueManager {

	private Vector<VenueState> states = new Vector<VenueState>();

	private ServerVenueManagerInterface ui = null;

	private MonitorVenue monitorVenue = null;
	private GetMonitoredVenues getMonitoredVenues =  null;
	private VenueServerType serverType = null;

	public ServerVenueManager(VenueServerType serverType, ServerVenueManagerInterface ui){
		this.ui=ui;
		monitorVenue = new MonitorVenue(this);
		if (!serverType.isManagable()){
			monitorVenue.monitor(serverType.toUrl());
		} else {
			getMonitoredVenues = new GetMonitoredVenues(this);
			getMonitoredVenues.getVenues(serverType.toServerUrl());
		}
		this.serverType = serverType;
		ui.setServerVenueManager(this);
	}

	public void monitorVenue(String url){
		monitorVenue.monitor(url);
	}

	public void addVenueState(VenueState state){
		GWT.log("adding VS " + state.toLog());
		states.add(state);
		ui.addVenue(state);
	}

	public Panel getUiPanel() {
		return ui.getPanel();
	}

	public VenueState getState(String source) {
		GWT.log ("states (searching : " + source + ") :" + states.toString() );
		for (VenueState venueState: states){
			if (source.equals(venueState.getUri())){
				return venueState;
			}
		}
		return null;
	}

	public void stopMonitoring(VenueState state) {
		states.remove(state);
		ui.removeVenue(state);
		// TODO Auto-generated method stub

	}

/*	public void setLocalUser(ClientProfile user){
		this.localUser = user;
		ui.addLocalUser(user);
	}

	public Vector<ClientProfile> getUsers(){
		return users;
	}

	public void deleteUser(ClientProfile user) {
		users.remove(user);
		ui.delUser(user);
	}

	public void updateUser(ClientProfile user) {
		int idx = users.indexOf(user);
		users.remove(user);
		users.insertElementAt(user, idx);
		ui.modifyUser(user);

	}

	public ClientProfile getLocalUser() {
		return localUser;
	}
*/
}
