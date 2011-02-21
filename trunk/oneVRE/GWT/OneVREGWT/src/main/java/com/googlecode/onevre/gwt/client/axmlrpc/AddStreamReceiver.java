package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescription;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescriptionEJSO;
import com.googlecode.onevre.gwt.client.ag.types.StreamDescriptionJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class AddStreamReceiver implements RequestReceiver {

    private VenueState state = null;

    public void execute(String source, Vector<JavaScriptObject> parameters) {
        StreamDescription stream = new StreamDescription((StreamDescriptionJSO) parameters.get(0));
        state = Application.getServerManager().getVenuefromSource(source);
        Application.getConnectionManager().addStream(state, stream);
    }

    public void init(String source) {
        state = Application.getServerManager().getVenuefromSource(source);
    }

    @Override
    public void execute(AgEventJSO eventJSO) {
        StreamDescription stream = new StreamDescription((StreamDescriptionEJSO) eventJSO.getEventObject());
        state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
        Application.getConnectionManager().addStream(state, stream);
    }

}
