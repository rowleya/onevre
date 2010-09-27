package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerTypeJSO;
import com.googlecode.onevre.gwt.common.client.XmlResponse;
import com.googlecode.onevre.gwt.common.client.XmlResponseHandler;

public class AddTrustedServer implements AsyncCallback<String>{

	XmlResponseHandler handler = null;

	String server = null;

	public AddTrustedServer(XmlResponseHandler handler) {
		this.handler = handler;
	}

	public void addTrustedServers(String name, String url){
		server = name + " (" + url + ")";
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "addTrustedServer",
                new Object[]{name, url},
                this);
        GWT.log("execute setTrustedServers");
        request.execute();
	}

	public void onFailure(Throwable error) {
		//GWT.log("addTrustedServers failed ");
		handler.handleResponse(new XmlResponse(XmlResponse.ERROR, server, null));
	}


	public void onSuccess(String trustedServerXml) {
		GWT.log("trustedServerXml: " + trustedServerXml);
		VenueServerType ts = new VenueServerType((VenueServerTypeJSO)VenueClientController.getObjectDec(trustedServerXml));
		handler.handleResponse(new XmlResponse(XmlResponse.OK, ts,trustedServerXml));

//		ClientProfile clientProfile = new ClientProfile((ClientProfileJSO)VenueClientController.getObjectDec(clientProfileXml));
//		VenueClientController.setTrustedServers(clientProfile);
	}

}
