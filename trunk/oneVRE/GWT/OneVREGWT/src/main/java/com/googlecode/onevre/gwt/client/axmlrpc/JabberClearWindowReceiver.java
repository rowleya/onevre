package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class JabberClearWindowReceiver implements RequestReceiver {

	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		state = Application.getServerManager().getVenuefromSource(source);
		Application.getJabberManager().clear(state);
	}

	public void init(String source) {
		state = Application.getServerManager().getVenuefromSource(source);
	}

}