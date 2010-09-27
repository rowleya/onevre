package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfileJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class EnterVenueReceiver implements RequestReceiver {

//	private VenueState state = null;

	public void execute(String source, Vector<JavaScriptObject> parameters) {
		ClientProfile client = new ClientProfile((ClientProfileJSO) parameters.get(0));
		GWT.log("Enter user : " + client.getName() );
		VenueState state = Application.getServerManager().getVenuefromSource(source);
//		GWT.log("State: "+ state.toString());
		Application.getUserManager().addUser(state, client);
	}

	public void init(String source) {
//		state = Application.getServerManager().getVenuefromSource(source);
	}

}
