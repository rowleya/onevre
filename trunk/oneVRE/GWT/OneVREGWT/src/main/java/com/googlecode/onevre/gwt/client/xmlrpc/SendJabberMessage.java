package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;

public class SendJabberMessage implements AsyncCallback<String> {

    public static void sendJabberMessage(String url, String message) {
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "sendJabberMessage",  new Object[]{url, message},
                new SendJabberMessage());
        request.execute();
    }

    public void onFailure(Throwable error) {
        GWT.log("sendJabberMessage failed: ", error);
    }

    // start venueclientui.entervenue


    public void onSuccess(String venueStateXml) {
        GWT.log("VS xml: " + venueStateXml);
    }

}
