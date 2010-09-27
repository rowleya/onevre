package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.UserManager;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.ClientPanel;

public interface UserManagerInterface {

	public void setUserManager (UserManager userManager);

	public void addUser(VenueState state, ClientProfile user);

	public void delUser(VenueState state, ClientProfile user);

	public void modifyUser (VenueState state, ClientProfile user);

	public void updateUI(VenueState state);

	public void addLocalUser(ClientProfile user);

	public void setClientPanel(VenueState state, ClientPanel clientPanel);

}
