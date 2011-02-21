package com.googlecode.onevre.gwt.client.ag;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ServerManagerInterface;

public class ServerManager {

    private Vector<VenueServerType> trustedServers = new Vector<VenueServerType>();

    private HashMap<VenueServerType, ServerVenueManager> serverVenueManagers =
        new HashMap<VenueServerType, ServerVenueManager>();

    private ServerManagerInterface ui = null;

    public ServerManager(ServerManagerInterface ui) {
        this.ui = ui;
        ui.setServerManager(this);
    }

    public void addServer(VenueServerType server) {
        GWT.log("adding server " + server.getName());
        trustedServers.add(server);
        ServerVenueManager serverVenueManager = new ServerVenueManager(server, ui.getServerVenueManager());
        serverVenueManagers.put(server, serverVenueManager);
        ui.addServer(server);
    }

    public void deleteServer(VenueServerType server) {
        GWT.log("deleting server " + server.getName());
        trustedServers.remove(server);
        serverVenueManagers.remove(server);
        ui.removeServer(server);
    }

    public Vector<VenueServerType> getTrustedServers() {
        return trustedServers;
    }

    public Panel getServerVenuePanel(VenueServerType server) {
        ServerVenueManager serverVenueManager = serverVenueManagers.get(server);
        Panel panel = serverVenueManager.getUiPanel();
        return panel;
    }

    public void monitorVenue(String venueUrl) {
        VenueServerType server = new VenueServerType(venueUrl);
        ServerVenueManager serverVenueManager = serverVenueManagers.get(server);
        if (serverVenueManager != null) {
            serverVenueManager.monitorVenue(venueUrl);
        }
    }
    public void stopMonitoring(VenueState state) {
        VenueServerType server = new VenueServerType(state.getUri());
        ServerVenueManager serverVenueManager = serverVenueManagers.get(server);
        if (serverVenueManager != null) {
            serverVenueManager.stopMonitoring(state);
        }
    }

    public void setTrustedServers(Vector<VenueServerType> trustedServers) {
        this.trustedServers = trustedServers;
        for (VenueServerType server : trustedServers) {
            if (serverVenueManagers.get(server) == null) {
                ServerVenueManager serverVenueManager =
                    new ServerVenueManager(server, ui.getServerVenueManager());
                serverVenueManagers.put(server, serverVenueManager);
            }
        }
        ui.updateUI();
        GWT.log("serverVenueManagers = " + serverVenueManagers);
    }

    public Panel getUiPanel() {
        return ui.getPanel();
    }

    public VenueState getVenuefromSource(String source) {
        VenueServerType vst = new VenueServerType(source);
        ServerVenueManager serverVenueManager = serverVenueManagers.get(vst);
        GWT.log("serverVenueManagers (searching: " + vst.toString() + ") :" + serverVenueManagers.toString());

        if (serverVenueManager != null) {
            GWT.log(serverVenueManager.toString());
            return serverVenueManager.getState(source);
        }
        return null;
    }
}
