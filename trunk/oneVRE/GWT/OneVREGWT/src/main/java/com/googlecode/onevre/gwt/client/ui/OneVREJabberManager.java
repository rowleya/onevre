package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.googlecode.onevre.gwt.client.ag.JabberManager;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.JabberManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.JabberPanel;

public class OneVREJabberManager implements JabberManagerInterface {


    private JabberManager jabberManager = null;

    private HashMap<VenueState, JabberPanel> jabberPanels = new HashMap<VenueState, JabberPanel>();

    public void setJabberManager(JabberManager jabberManager) {
        this.jabberManager = jabberManager;
    }

    public void setJabberPanel(VenueState state, JabberPanel jabberPanel) {
        jabberPanels.put(state, jabberPanel);
    }

    public void addMessage(VenueState state, JabberMessage message) {
        JabberPanel jabberPanel = jabberPanels.get(state);
        if (jabberPanel != null) {
            jabberPanel.addMessage(message);
        }
    }

    public void clear(VenueState state) {
        JabberPanel jabberPanel = jabberPanels.get(state);
        if (jabberPanel != null) {
            jabberPanel.clear();
        }
    }

}
