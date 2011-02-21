package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ag.types.VenueStateJSO;

public class MonitorVenue implements AsyncCallback<String> {

    private ServerVenueManager serverVenueManager = null;

    private String url = null;

    public MonitorVenue(ServerVenueManager serverVenueManager) {
        this.serverVenueManager = serverVenueManager;
    }

    public void monitor(String url) {
        this.url = url;
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "monitorVenue",  new Object[]{url},
                this);
        request.execute();
    }

    public void onFailure(Throwable error) {
        GWT.log("failed on: " + url , error);
    }

    // start venueclientui.entervenue


    public void onSuccess(String venueStateXml) {
        GWT.log("VS xml: " + venueStateXml);
        VenueState venueState = new VenueState((VenueStateJSO) VenueClientController.getObjectDec(venueStateXml));
        GWT.log("VS log: " + venueState.toLog());
        GWT.log("VenueState: " + venueState.toString());

        if (venueState != null) {
            serverVenueManager.addVenueState(venueState);
        }
    }

}
