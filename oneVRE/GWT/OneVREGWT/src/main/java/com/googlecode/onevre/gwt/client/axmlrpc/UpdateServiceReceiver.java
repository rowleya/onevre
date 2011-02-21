package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.ServiceDescription;
import com.googlecode.onevre.gwt.client.ag.types.ServiceDescriptionEJSO;
import com.googlecode.onevre.gwt.client.ag.types.ServiceDescriptionJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class UpdateServiceReceiver implements RequestReceiver {

    private VenueState state = null;

    public void execute(String source, Vector<JavaScriptObject> parameters) {
        ServiceDescription service = new ServiceDescription((ServiceDescriptionJSO) parameters.get(0));
        state = Application.getServerManager().getVenuefromSource(source);
        Application.getServiceManager().updateService(state, service);
    }

    public void init(String source) {
        state = Application.getServerManager().getVenuefromSource(source);
    }

    @Override
    public void execute(AgEventJSO eventJSO) {
        ServiceDescription service = new ServiceDescription((ServiceDescriptionEJSO) eventJSO.getEventObject());
        state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
        Application.getServiceManager().updateService(state, service);
    }

}
