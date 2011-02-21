package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.JabberManager;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.JabberPanel;

public interface JabberManagerInterface {

    void setJabberManager(JabberManager jabberManager);

    void setJabberPanel(VenueState state, JabberPanel jabberPanel);

    void addMessage(VenueState state, JabberMessage message);

    void clear(VenueState state);

}
