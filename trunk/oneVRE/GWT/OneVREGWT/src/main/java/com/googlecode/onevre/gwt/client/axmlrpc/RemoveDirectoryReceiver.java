package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.DataDescriptionJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class RemoveDirectoryReceiver implements RequestReceiver {

	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		DataDescription data = new DataDescription ((DataDescriptionJSO) parameters.get(0));
		state = Application.getServerManager().getVenuefromSource(source);
		Application.getDataManager().deleteDirectory(state, data);
	}

	public void init(String source) {
		state = Application.getServerManager().getVenuefromSource(source);
	}

}
