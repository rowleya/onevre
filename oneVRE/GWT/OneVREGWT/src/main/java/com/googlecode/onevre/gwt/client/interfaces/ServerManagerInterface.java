package com.googlecode.onevre.gwt.client.interfaces;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.ServerManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;


public interface ServerManagerInterface {

    void setServerManager(ServerManager serverManager);

    void updateUI();

    void addServer(VenueServerType server);

    void removeServer(VenueServerType server);

    void clearServers();

    Panel getPanel();

    ServerVenueManagerInterface getServerVenueManager();

}
