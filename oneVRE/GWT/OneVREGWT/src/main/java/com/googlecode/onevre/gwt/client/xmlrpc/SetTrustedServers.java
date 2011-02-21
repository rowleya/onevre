package com.googlecode.onevre.gwt.client.xmlrpc;

import java.util.Vector;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.Application;

public class SetTrustedServers implements AsyncCallback<String> {

    public static void setTrustedServers() {
        Vector<VenueServerType> trustedServers = Application.getServerManager().getTrustedServers();
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "setTrustedServers",
                new Object[]{trustedServers},
                new SetTrustedServers());
        GWT.log("execute setTrustedServers");
        request.execute();
    }

    public void onFailure(Throwable error) {
        GWT.log("setTrustedServers failed: ", error);
    }


    public void onSuccess(String trustedServersXml) {
        GWT.log("trusted Servers: " + trustedServersXml);
//        ClientProfile clientProfile =
//            new ClientProfile((ClientProfileJSO)VenueClientController.getObjectDec(clientProfileXml));
//        VenueClientController.setTrustedServers(clientProfile);
    }

}
