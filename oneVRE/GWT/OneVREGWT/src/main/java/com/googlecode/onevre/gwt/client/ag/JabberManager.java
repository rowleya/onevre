package com.googlecode.onevre.gwt.client.ag;


import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.JabberManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.JabberPanel;
import com.googlecode.onevre.gwt.client.xmlrpc.SendJabberMessage;

public class JabberManager {

	private JabberManagerInterface ui = null;

	public JabberManager(JabberManagerInterface ui) {
		this.ui = ui;
		ui.setJabberManager(this);
	}

	public void addMessage(VenueState state, JabberMessage message) {
		ui.addMessage(state, message);
	}

	public void clear(VenueState state) {
		ui.clear(state);
	}

	public void submit(String venueUri, String message) {
		SendJabberMessage.sendJabberMessage(venueUri, message);
	}

	public void setJabberPanel(VenueState state, JabberPanel jabberPanel) {
		ui.setJabberPanel(state, jabberPanel);
	}

}
