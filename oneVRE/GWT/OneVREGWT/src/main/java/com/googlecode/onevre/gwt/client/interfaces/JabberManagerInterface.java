package com.googlecode.onevre.gwt.client.interfaces;

import com.googlecode.onevre.gwt.client.ag.JabberManager;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.panels.JabberPanel;

public interface JabberManagerInterface {

	public void setJabberManager(JabberManager jabberManager);

	public void setJabberPanel(VenueState state, JabberPanel jabberPanel);

	public void addMessage(VenueState state, JabberMessage message);

	public void clear(VenueState state);

}
