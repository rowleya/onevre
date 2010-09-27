package com.googlecode.onevre.gwt.client.xmlrpc;

import java.util.Vector;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.types.VectorJSO;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ag.types.VenueTreeItem;
import com.googlecode.onevre.gwt.client.ag.types.VenueTreeItemJSO;
import com.googlecode.onevre.gwt.client.ui.panels.VenueTreePanel;

public class GetConnections implements AsyncCallback<String>{

	private VenueTreePanel venueTreePanel = null;

	private String url = null;

	public GetConnections(VenueTreePanel venueTreePanel){
		this.venueTreePanel = venueTreePanel;
	}

	public void getConnections(String url){
		this.url = url;
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "getVenueTree",  new Object[]{url},
                this);
        request.execute();
	}

	public void onFailure(Throwable error) {
		GWT.log("failed on: " + url , error);
	}

	// start venueclientui.entervenue


	public void onSuccess(String venueTreeXml) {
		VenueServerType venueServerType = venueTreePanel.getVenueServerType();
		GWT.log("VS xml: " + venueTreeXml);
		Vector<VenueTreeItem> venueTree = new Vector<VenueTreeItem>();
		VectorJSO<VenueTreeItemJSO> vtjso = (VectorJSO<VenueTreeItemJSO>)VenueClientController.getObjectDec(venueTreeXml);
		for (int i = 0; i<vtjso.size(); i++) {
			VenueTreeItem vti = new VenueTreeItem(vtjso.get(i));
			venueTree.add(vti);
		}
		venueTreePanel.updateTree(venueTree);
	}

}
