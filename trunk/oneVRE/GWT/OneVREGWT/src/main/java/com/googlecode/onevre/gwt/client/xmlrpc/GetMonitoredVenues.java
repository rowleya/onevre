package com.googlecode.onevre.gwt.client.xmlrpc;

import java.util.HashMap;
import java.util.Vector;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.ServerVenueManager;
import com.googlecode.onevre.gwt.client.ag.types.StringVectorJSO;
import com.googlecode.onevre.gwt.client.ag.types.VOAttribute;

public class GetMonitoredVenues implements AsyncCallback<String> {

    private ServerVenueManager serverVenueManager = null;

    private String url = null;

    public GetMonitoredVenues(ServerVenueManager serverVenueManager) {
        this.serverVenueManager = serverVenueManager;
    }

    public void getVenues(String url) {
        this.url = url;
        Vector<HashMap<String, String>> voAttr = new Vector<HashMap<String, String>>();
        Vector<VOAttribute> voAtts = Application.getUserManager().getVOAttributes();
        if (voAtts != null) {
            for (VOAttribute voAtt : Application.getUserManager().getVOAttributes()) {
                voAttr.add(voAtt.toMap());
            }
        }
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "getVenues",  new Object[]{url, voAttr},
                this);
        request.execute();
    }

    public void onFailure(Throwable error) {
        GWT.log("failed on: " + url , error);
    }

    // start venueclientui.entervenue


    public void onSuccess(String connectionsXml) {
        GWT.log("conns xml: " + connectionsXml);
//        Vector<String> connections = new Vector<String>();
        StringVectorJSO connJSO = ((StringVectorJSO) VenueClientController.getObjectDec(connectionsXml));
        for (int i = 0; i < connJSO.size(); i++) {
            String conn = connJSO.get(i);
            GWT.log("Connection [" + i + "] :" + conn);
            serverVenueManager.monitorVenue(conn);
        }
    }

}
