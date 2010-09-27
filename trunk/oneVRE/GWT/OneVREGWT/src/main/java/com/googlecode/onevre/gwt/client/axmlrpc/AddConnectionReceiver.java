package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescriptionJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class AddConnectionReceiver implements RequestReceiver {

	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		ConnectionDescription connection = new ConnectionDescription((ConnectionDescriptionJSO) parameters.get(0));
		state = Application.getServerManager().getVenuefromSource(source);
		Application.getConnectionManager().addConnection(state, connection);
	}

	public void init(String source) {
		state = Application.getServerManager().getVenuefromSource(source);
	}

}
