package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfileEJSO;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfileJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class ExitVenueReceiver implements RequestReceiver {

    private VenueState state = null;

    public void execute(String source, Vector<JavaScriptObject> parameters) {

        ClientProfile client = new ClientProfile((ClientProfileJSO) parameters.get(0));
        state = Application.getServerManager().getVenuefromSource(source);
        Application.getUserManager().deleteUser(state, client);
    }

    public void init(String source) {
    }

    @Override
    public void execute(AgEventJSO eventJSO) {
        ClientProfile client = new ClientProfile((ClientProfileEJSO) eventJSO.getEventObject());
        state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
        Application.getUserManager().deleteUser(state, client);
    }

}
