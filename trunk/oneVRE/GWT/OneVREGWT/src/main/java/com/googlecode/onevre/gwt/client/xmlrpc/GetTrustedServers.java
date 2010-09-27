package com.googlecode.onevre.gwt.client.xmlrpc;

import java.util.Vector;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.ag.types.VectorJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerTypeJSO;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;

public class GetTrustedServers implements AsyncCallback<String>{

	public static void getTrustedServers(){
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "getTrustedServers",  new Object[]{},
                new GetTrustedServers());
        GWT.log("execute getTrustedServers");
        request.execute();

	}

	public void onFailure(Throwable error) {
		GWT.log("getTrustedServers failed: ", error);
	}

	public void onSuccess(String trustedServersXml) {
		GWT.log("trustedServers: " + trustedServersXml);
		Vector<VenueServerType> trustedServers = new Vector<VenueServerType>();
		VectorJSO<VenueServerTypeJSO> tsjso = (VectorJSO<VenueServerTypeJSO>)VenueClientController.getObjectDec(trustedServersXml);
		for (int i = 0; i<tsjso.size(); i++) {
			VenueServerType ts = new VenueServerType(tsjso.get(i));
			trustedServers.add(ts);
			GWT.log("adding trustedServer: " + ts);
		}
		Application.getServerManager().setTrustedServers(trustedServers);

	}

}
