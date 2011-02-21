package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;

public class StopMonitorVenue implements AsyncCallback<String> {

    private ServerVenueManager serverVenueManager = null;

    private VenueState state = null;

    public StopMonitorVenue(ServerVenueManager serverVenueManager) {
        this.serverVenueManager = serverVenueManager;
    }

    public void stopMonitoring(VenueState state) {
        this.state = state;
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "stopMonitoringVenue",  new Object[]{state.getUri()},
                this);
        request.execute();
    }

    public void onFailure(Throwable error) {
        GWT.log("failed on: " + state.toLog() , error);
    }

    // start venueclientui.entervenue


    public void onSuccess(String venueStateXml) {
        GWT.log("VS xml: " + venueStateXml);
        serverVenueManager.stopMonitoring(state);
    }

}
