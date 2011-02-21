package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.UserManager;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.UserManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.ClientPanel;

public class OneVREUserManager implements UserManagerInterface {

    private UserManager userManager = null;

    private HashMap<VenueState, ClientPanel> clientPanels = new HashMap<VenueState, ClientPanel>();

    private ClientProfile localUser = null;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public Panel getUserPanel(VenueState state) {
        return clientPanels.get(state);
    }


    public void updateUI(VenueState state) {
    //  clientPanel = new ClientPanel(userManager.getUsers(state));
    }

    public void addUser(VenueState state, ClientProfile user) {
        ClientPanel clientPanel = clientPanels.get(state);
        if (clientPanel != null) {
            clientPanel.addClient(user);
        }
    }

    public void delUser(VenueState state, ClientProfile user) {
        ClientPanel clientPanel = clientPanels.get(state);
        if (clientPanel != null) {
            clientPanel.removeClient(user);
        }
    }

    public void modifyUser(VenueState state, ClientProfile user) {
        ClientPanel clientPanel = clientPanels.get(state);
        if (clientPanel != null) {
            clientPanel.updateClient(user);
        }
    }

    public void addLocalUser(ClientProfile user) {
        localUser = user;
    }

    public void setClientPanel(VenueState state, ClientPanel clientPanel) {
        clientPanels.put(state, clientPanel);
    }

}
