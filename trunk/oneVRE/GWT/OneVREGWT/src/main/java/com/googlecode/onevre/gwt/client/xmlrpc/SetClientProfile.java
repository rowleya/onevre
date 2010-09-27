package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfileJSO;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;

public class SetClientProfile implements AsyncCallback<String>{

	public static void setClientProfile(){
		ClientProfile clientProfile = Application.getUserManager().getLocalUser();
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "setClientProfile",
                new Object[]{
                		clientProfile.getName(), clientProfile.getEmail(),
                		clientProfile.getPhoneNumber(), clientProfile.getLocation(),
                		clientProfile.getHomeVenue(), clientProfile.getProfileType()},
                new SetClientProfile());
        GWT.log("execute setClientProfile");
        request.execute();
	}

	public void onFailure(Throwable error) {
		GWT.log("getClientProfile failed: ", error);
	}


	public void onSuccess(String clientProfileXml) {
		GWT.log("clientprofile: " + clientProfileXml);
		ClientProfile clientProfile = new ClientProfile((ClientProfileJSO)VenueClientController.getObjectDec(clientProfileXml));
		VenueClientController.setClientProfile(clientProfile);
	}

}
