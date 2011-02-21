package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.user.client.ui.FlexTable;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;

public class ClientPanel extends FlexTable {

    private HashMap<ClientProfile, ClientProfilePanel> profilePanels =
        new HashMap<ClientProfile, ClientProfilePanel>();

    public ClientPanel(Vector<ClientProfile> clientProfiles) {
        this.setWidth("100%");
        this.setHeight("100%");
        int i = 0;
        for (ClientProfile clientProfile : clientProfiles) {
            ClientProfilePanel panel =  new ClientProfilePanel(clientProfile, false);
            profilePanels.put(clientProfile, panel);
            this.setWidget(i, 0, panel);
            i++;
        }
    }

    public void addClient(ClientProfile user) {
        profilePanels.remove(user);
        ClientProfilePanel profilePanel = new ClientProfilePanel(user, false);
        profilePanels.put(user, profilePanel);
        this.setWidget(profilePanels.size(), 0, profilePanel);
    }

    public void removeClient(ClientProfile user) {
        ClientProfilePanel profilePanel = profilePanels.remove(user);
        if (profilePanel != null) {
            this.remove(profilePanel);
        }
    }

    public void updateClient(ClientProfile user) {
        ClientProfilePanel profilePanel = profilePanels.get(user);
        profilePanel.updatePanel(user);
    }

}
