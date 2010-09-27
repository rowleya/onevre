package com.googlecode.onevre.gwt.client.axmlrpc;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;


import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;


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
	}

    private boolean done = false;

    private int count = 0;

    private Application application = null;

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

    public AXmlRpcMessageReceiver(Application application) {
        this.application = application;
        count = 0;
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
        GWT.log("AXmlRpcMessageReceiver.onResponseReceived: "+ response.getText());
		Document doc = null;
        try {
            doc = XMLParser.parse(response.getText());
        } catch (DOMParseException e) {
        	return;
        }
        Element methodCall = doc.getDocumentElement();
        if (methodCall.getNodeName().equals("empty")){
        	getUpdate();
        	return;
        }
        if (methodCall.getNodeName().equals("done")){
        	return;
        }
        if (!methodCall.getNodeName().equals("methodCall")){
        	GWT.log("The document element must be named \"methodCall\" "+
                    "(this is "+methodCall.getNodeName()+")");
            return;
        }
        String methodName = methodCall.getElementsByTagName("methodName").item(0).getFirstChild().toString();
        GWT.log(methodName);
        RequestReceiver receiver = RequestMappings.get(methodName);
        if (receiver == null){
        	GWT.log("Unknown Request Method :" + methodName );
        	return;
        }
        count = 0;
        NodeList paramsL = methodCall.getElementsByTagName("params");
        NodeList paramList = paramsL.item(0).getChildNodes();
        Vector<JavaScriptObject> parameters = new Vector<JavaScriptObject>();
        String source = null;
        for (int i = 0 ; i<paramList.getLength() ;i++ ){
        	Node param= paramList.item(i).getFirstChild();
        	String pString = param.getFirstChild().toString();
        	pString = pString.replaceAll("&semi;", ";");
        	GWT.log("parsing: " + pString);
        	if (source==null) {
        		source = VenueClientController.getStringDec(pString);
        		GWT.log("Event Source =" + source);
        	} else {
        		JavaScriptObject obj = VenueClientController.getObjectDec(pString);
	        	//GWT.log(obj.toString());
	        	parameters.add(obj);
        	}
        }
        GWT.log("Event Source =" + source);
        receiver.init(source);
        receiver.execute(source, parameters);
 		if (!done) {
			getUpdate();
		}
	}



}
