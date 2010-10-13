package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescription;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescriptionEJSO;
import com.googlecode.onevre.gwt.client.ag.types.ConnectionDescriptionJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class RemoveConnectionReceiver implements RequestReceiver {

	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		ConnectionDescription connection = new ConnectionDescription((ConnectionDescriptionJSO) parameters.get(0));
		state = Application.getServerManager().getVenuefromSource(source);
		Application.getConnectionManager().deleteConnection(state, connection);
	}

	public void init(String source) {
		state = Application.getServerManager().getVenuefromSource(source);
	}

	@Override
	public void execute(AgEventJSO eventJSO) {
		ConnectionDescription connection = new ConnectionDescription((ConnectionDescriptionEJSO) eventJSO.getEventObject());
		state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
		Application.getConnectionManager().deleteConnection(state, connection);
	}

}
