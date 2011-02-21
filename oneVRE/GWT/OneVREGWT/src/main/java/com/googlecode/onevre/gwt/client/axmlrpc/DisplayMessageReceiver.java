package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;
import com.googlecode.onevre.gwt.client.ag.types.MessageBox;
import com.googlecode.onevre.gwt.client.ag.types.MessageBoxJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class DisplayMessageReceiver implements RequestReceiver {

    private VenueState state = null;

    public void execute(String source, Vector<JavaScriptObject> parameters) {
//        state = Application.getServerManager().getVenuefromSource(source);
//        Application.getJabberManager().addMessage(state, message);
    }

    public void init(String source) {
        state = Application.getServerManager().getVenuefromSource(source);
    }

    @Override
    public void execute(AgEventJSO eventJSO) {
        GWT.log("EVENT: " + eventJSO.getEventObject().toString());
        MessageBox message = new MessageBox((MessageBoxJSO) eventJSO.getEventObject());
        state = Application.getServerManager().getVenuefromSource(eventJSO.getEventSource());
        Application.getMessageManager().addMessage(state, message);
    }

}
