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

	private static HashMap<String, RequestReceiver> RequestMappings = new HashMap<String, RequestReceiver>();

	static {
		RequestMappings.put("eventEnterVenue", new EnterVenueReceiver());
		RequestMappings.put("eventExitVenue", new ExitVenueReceiver());
		RequestMappings.put("eventModifyUser",new ModifyUserReceiver());

		RequestMappings.put("eventAddData", new AddDataReceiver());
		RequestMappings.put("eventUpdateData", new UpdateDataReceiver());
		RequestMappings.put("eventRemoveData", new RemoveDataReceiver());
		RequestMappings.put("eventAddDirectory", new AddDirectoryReceiver());
		RequestMappings.put("eventRemoveDirectory", new RemoveDirectoryReceiver());

		RequestMappings.put("eventAddService", new AddServiceReceiver());
		RequestMappings.put("eventUpdateService", new UpdateServiceReceiver());
		RequestMappings.put("eventRemoveService", new RemoveServiceReceiver());

		RequestMappings.put("eventAddConnection", new AddConnectionReceiver());
		RequestMappings.put("eventRemoveConnection", new RemoveConnectionReceiver());

		RequestMappings.put("eventAddStream", new AddStreamReceiver());
		RequestMappings.put("eventModifyStream", new ModifyStreamReceiver());
		RequestMappings.put("eventRemoveStream", new RemoveStreamReceiver());

		RequestMappings.put("jabberClearWindow", new JabberClearWindowReceiver());
		RequestMappings.put("jabberAddMessage", new JabberAddMessageReceiver());

		RequestMappings.put("setUploadStatus", new SetUploadStatusReceiver());
		RequestMappings.put("showUploadStatus", new ShowUploadStatusReceiver());
		RequestMappings.put("hideUploadStatus", new HideUploadStatusReceiver());
		RequestMappings.put("displayMessage", new DisplayMessageReceiver());
	}

    private boolean done = false;

    private String serverUrl=null;


    private void getUpdate() {
		RequestBuilder builder =  new RequestBuilder(RequestBuilder.GET,serverUrl);
    	try {
    		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
    		builder.sendRequest("getNextResponse", this);
		} catch (RequestException e) {
			onError(null, e);
		}
    }

    public AXmlRpcMessageReceiver() {
        this.serverUrl =  Application.getParam(Application.XMLRPC_RESPONSE_SERVER)
        			   + "?namespace="+Application.encodeURIComponent(Application.getParam(Application.APPLICATION_NAMESPACE));
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
        if (event.getEventName().equals("none")){
        	getUpdate();
        	return;
        }
        if (event.getEventName().equals("done")){
        	return;
        }
        RequestReceiver receiver = RequestMappings.get(event.getEventName());
        if (receiver == null){
        	GWT.log("Unknown Event Method :" + event.getEventName());
        	return;
        }
        receiver.execute(event);
        getUpdate();
	}

}
