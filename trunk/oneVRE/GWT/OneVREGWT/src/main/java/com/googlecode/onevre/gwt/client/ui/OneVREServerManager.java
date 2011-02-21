package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.ServerManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.interfaces.ServerManagerInterface;
import com.googlecode.onevre.gwt.client.interfaces.ServerVenueManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.VenueServerPanel;

public class OneVREServerManager implements ServerManagerInterface {

    private HashMap<VenueServerType, VenueServerPanel> panels = new HashMap<VenueServerType, VenueServerPanel>();

    private ServerManager serverManager = null;

    private FlexTable panel = new FlexTable();

    private int numServers = 0;

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        panel.setWidth("100%");
        panel.setHeight("100px");
    }



    public void updateUI() {
        panel.clear();
        numServers = 0;
        HashMap<VenueServerType, VenueServerPanel> oldPanels = panels;
        panels = new HashMap<VenueServerType, VenueServerPanel>();
        for (VenueServerType server : serverManager.getTrustedServers()) {
            VenueServerPanel serverPanel = oldPanels.get(server);
            if (serverPanel == null) {
                serverPanel = new VenueServerPanel(server, serverManager.getServerVenuePanel(server));
            }
            panels.put(server, serverPanel);
            panel.setWidget(numServers, 0, serverPanel);
            numServers++;
        }
    }

    public void addServer(VenueServerType server) {
        VenueServerPanel serverPanel = new VenueServerPanel(server, serverManager.getServerVenuePanel(server));
        panels.put(server, serverPanel);
        panel.setWidget(numServers, 0, serverPanel);
        numServers++;
    }

    public void removeServer(VenueServerType server) {
        VenueServerPanel serverPanel = panels.remove(server);
        if (serverPanel != null) {
            panel.remove(serverPanel);
            numServers--;
        }
    }

    public void clearServers() {
        panel.clear();
        panels.clear();
        numServers = 0;
    }

    public Panel getPanel() {
        return panel;
    }

    public ServerVenueManagerInterface getServerVenueManager() {
        OneVREServerVenueManager serverVenueManager = new OneVREServerVenueManager();
        return serverVenueManager;
    }

}
