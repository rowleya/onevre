package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.ServerVenueManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.VenuePanel;

public class OneVREServerVenueManager implements ServerVenueManagerInterface {

    private ServerVenueManager serverVenueManager = null;

    private VerticalPanel panel = new VerticalPanel();

    private HashMap<VenueState, VenuePanel> panelMap = new HashMap<VenueState, VenuePanel>();

    public void setServerVenueManager(ServerVenueManager serverVenueManager) {
        this.serverVenueManager = serverVenueManager;
        panel.setWidth("100%");
    }

    public void addVenue(VenueState state) {
        VenuePanel vp = new VenuePanel(state, serverVenueManager);
        panelMap.put(state, vp);
        panel.add(vp);
    }

    public Panel getPanel() {
        return panel;
    }

    public void removeVenue(VenueState state) {
        VenuePanel vp = panelMap.remove(state);
        if (vp != null) {
            panel.remove(vp);
        }
        // TODO Auto-generated method stub

    }

}
