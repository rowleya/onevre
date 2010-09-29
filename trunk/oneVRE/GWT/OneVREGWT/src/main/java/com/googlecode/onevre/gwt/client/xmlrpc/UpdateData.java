package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ag.types.VenueStateJSO;

public class UpdateData implements AsyncCallback<String>{

	public static void updateData(String venueURI, DataDescription data){
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "updateData",  new Object[]{venueURI,data.getId(),data.getName(),data.getDescription(),data.getExpires()},
                new UpdateData());
        request.execute();
	}

	public void onFailure(Throwable paramThrowable) {

	}

	public void onSuccess(String venueStateXml) {

	}

}
