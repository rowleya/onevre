package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.DataDescriptionJSO;
import com.googlecode.onevre.gwt.client.VenueClientController;

public class UploadData implements AsyncCallback<String>{

	public static void uploadData(String venueURI, DataDescription data){
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "uploadData",  new Object[]{venueURI,data.getParentId(),data.getName(),data.getDescription(),data.getExpires()},
                new UploadData());
        request.execute();
	}
	public void onFailure(Throwable paramThrowable) {

	}

	public void onSuccess(String dataDescriptionXml) {
//		DataDescription dataDescription = new DataDescription((DataDescriptionJSO)VenueClientController.getObjectDec(dataDescriptionXml));

	}

}
