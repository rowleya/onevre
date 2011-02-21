package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.UserManager;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.ClientPanel;

public interface UserManagerInterface {

    void setUserManager(UserManager userManager);

    void addUser(VenueState state, ClientProfile user);

    void delUser(VenueState state, ClientProfile user);

    void modifyUser(VenueState state, ClientProfile user);

    void updateUI(VenueState state);

    void addLocalUser(ClientProfile user);

    void setClientPanel(VenueState state, ClientPanel clientPanel);

}
