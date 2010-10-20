package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessageJSO;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatus;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatusJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class ShowUploadStatusReceiver implements RequestReceiver {

	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		JabberMessage message = new JabberMessage ((JabberMessageJSO) parameters.get(0));
		GWT.log("Jabber Message: " + message.toString());
		state = Application.getServerManager().getVenuefromSource(source);
		Application.getJabberManager().addMessage(state, message);
	}

	public void init(String source) {
		state = Application.getServerManager().getVenuefromSource(source);
	}

	@Override
	public void execute(AgEventJSO eventJSO) {
		UploadStatus uploadStatus = new UploadStatus((UploadStatusJSO) eventJSO.getEventObject());
		state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
		Application.getMessageManager().showUpload(state, uploadStatus);
	}

}
