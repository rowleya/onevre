package com.googlecode.onevre.gwt.client.interfaces;

import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.ServerManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;


public interface ServerManagerInterface {

	public void setServerManager(ServerManager serverManager);

	public void updateUI();

	public void addServer(VenueServerType server);

	public void removeServer(VenueServerType server);

	public void clearServers();

	public Panel getPanel();

	public ServerVenueManagerInterface getServerVenueManager();

}
