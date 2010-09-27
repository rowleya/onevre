package com.googlecode.onevre.gwt.client.ag;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.UserManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.ClientPanel;

public class UserManager {

	private HashMap<VenueState, Vector<ClientProfile>> users = new HashMap<VenueState, Vector<ClientProfile>>();
//	private Vector<ClientProfile> users = new Vector<ClientProfile>();

	private ClientProfile localUser = null;

	private UserManagerInterface ui = null;

	public UserManager(UserManagerInterface ui){
		this.ui = ui;
		ui.setUserManager(this);
	}

	public Vector<ClientProfile> addVenue(VenueState state){
		Vector<ClientProfile> userList = new Vector<ClientProfile>();
		users.put(state, userList);
		return userList;
	}

	public void addUser(VenueState state, ClientProfile user){
		Vector<ClientProfile> userList = users.get(state);
		if (userList==null){
			userList = addVenue(state);
		}
		userList.add(user);
		ui.addUser(state, user);
	}

	public void setLocalUser(ClientProfile user){
		this.localUser = user;
		ui.addLocalUser(user);
	}

	public Vector<ClientProfile> getUsers(VenueState state){
		Vector<ClientProfile> userList = users.get(state);
		return userList;
	}

	public void deleteUser(VenueState state, ClientProfile user) {
		Vector<ClientProfile> userList = users.get(state);
		if (userList!=null){
			userList.remove(user);
			ui.delUser(state, user);
		}
	}

	public void updateUser(VenueState state, ClientProfile user) {
		Vector<ClientProfile> userList = users.get(state);
		if (userList!=null){
			int idx = userList.indexOf(user);
			userList.remove(user);
			userList.insertElementAt(user, idx);
			ui.modifyUser(state, user);
		}
	}

	public ClientProfile getLocalUser() {
		return localUser;
	}

	public void setClientPanel(VenueState state, ClientPanel clientPanel) {
		users.put(state, state.getClients());
		ui.setClientPanel(state,clientPanel);
	}

}
