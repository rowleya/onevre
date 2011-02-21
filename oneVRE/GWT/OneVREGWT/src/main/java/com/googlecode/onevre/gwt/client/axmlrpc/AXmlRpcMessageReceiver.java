package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;


import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.AgEventJSO;


public class AXmlRpcMessageReceiver implements RequestCallback {

    private static HashMap<String, RequestReceiver> requestMappings = new HashMap<String, RequestReceiver>();

    static {
        requestMappings.put("eventEnterVenue", new EnterVenueReceiver());
        requestMappings.put("eventExitVenue", new ExitVenueReceiver());
        requestMappings.put("eventModifyUser", new ModifyUserReceiver());

        requestMappings.put("eventAddData", new AddDataReceiver());
        requestMappings.put("eventUpdateData", new UpdateDataReceiver());
        requestMappings.put("eventRemoveData", new RemoveDataReceiver());
        requestMappings.put("eventAddDirectory", new AddDirectoryReceiver());
        requestMappings.put("eventRemoveDirectory", new RemoveDirectoryReceiver());

        requestMappings.put("eventAddService", new AddServiceReceiver());
        requestMappings.put("eventUpdateService", new UpdateServiceReceiver());
        requestMappings.put("eventRemoveService", new RemoveServiceReceiver());

        requestMappings.put("eventAddConnection", new AddConnectionReceiver());
        requestMappings.put("eventRemoveConnection", new RemoveConnectionReceiver());

        requestMappings.put("eventAddStream", new AddStreamReceiver());
        requestMappings.put("eventModifyStream", new ModifyStreamReceiver());
        requestMappings.put("eventRemoveStream", new RemoveStreamReceiver());

        requestMappings.put("jabberClearWindow", new JabberClearWindowReceiver());
        requestMappings.put("jabberAddMessage", new JabberAddMessageReceiver());

        requestMappings.put("setUploadStatus", new SetUploadStatusReceiver());
        requestMappings.put("showUploadStatus", new ShowUploadStatusReceiver());
        requestMappings.put("hideUploadStatus", new HideUploadStatusReceiver());
        requestMappings.put("displayMessage", new DisplayMessageReceiver());
    }

    private boolean done = false;

    private String serverUrl = null;


    private void getUpdate() {
        RequestBuilder builder =  new RequestBuilder(RequestBuilder.GET, serverUrl);
        try {
            builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
            builder.sendRequest("getNextResponse", this);
        } catch (RequestException e) {
            onError(null, e);
        }
    }

    public AXmlRpcMessageReceiver() {
        this.serverUrl =  Application.getParam(Application.XMLRPC_RESPONSE_SERVER)
                       + "?namespace=" + Application.encodeURIComponent(
                               Application.getParam(Application.APPLICATION_NAMESPACE));
        GWT.log("AXML msg serverURL = " + serverUrl);
    }

    public void start() {
        done = false;
        getUpdate();
    }

    public void stop() {
        done = true;
    }

    public void onError(Request request, Throwable error) {
        GWT.log("failed request " + request.toString(), error);
    }

    public void onResponseReceived(Request request, Response response) {
        if (done) {
            return;
        }
        AgEventJSO event = AgEventJSO.parse(response.getText());
        if (event.getEventName().equals("none")) {
            getUpdate();
            return;
        }
        if (event.getEventName().equals("done")) {
            return;
        }
        RequestReceiver receiver = requestMappings.get(event.getEventName());
        if (receiver == null) {
            GWT.log("Unknown Event Method :" + event.getEventName());
            return;
        }
        receiver.execute(event);
        getUpdate();
    }

}
