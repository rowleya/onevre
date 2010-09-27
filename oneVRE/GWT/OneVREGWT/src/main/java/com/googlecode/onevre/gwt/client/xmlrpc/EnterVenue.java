package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ag.types.VenueStateJSO;

public class EnterVenue implements AsyncCallback<String>{

	public void enter(String url){
		XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "enterVenue",  new Object[]{url},
                new EnterVenue());
        request.execute();
	}

	public void onFailure(Throwable paramThrowable) {
		// TODO Auto-generated method stub

	}

	// start venueclientui.entervenue


	public void onSuccess(String venueStateXml) {
		VenueState venueState = new VenueState((VenueStateJSO)VenueClientController.getObjectDec(venueStateXml));

		// when VenueclientUI answers

		if (venueState != null) {
/*
            var venuelist = venueState.getVenueList();
            var participants = venueState.getClients();
            var data  = venueState.getData();
            var services  = venueState.getServices();
            var applications = venueState.getApplications();
            if (venuelist != null){
                var roots=venuelist.getRoots();
                var name = venueState.getName();
                var uri = venueState.getUri();
                   document.getElementById(this.venueNameId).innerHTML = name;
                      document.getElementById(this.venueUriId).value = uri;
                   pag_venueTree =
                    new Tree("pag_venueTree", this.treeDivId, 22, "#ccccff", false,
                    true, "cursor: pointer;", this.imageLocation, false, true);
                pag_venueTree.addTreeNode(null, "pag_venueRoot",
                    imageLocation + "tree_blank.gif",
                    "", 1, null, null, null, null);
                pag_addVenuesToList(this.namespace, "pag_venueRoot", roots, this.venueClientControllerId);
                pag_venueTree.drawTree();
                pag_venueTree.refreshTree();
                if (refresh==null){
                       pag_setStatus("Starting services...");
                       this.venueClientController.getCapabilitiesXml();
                }
                add=true;
            }
            pag_createDataTree(participants,data,services,applications,this.dataDownloadUrl,this.dataUploadUrl,this.venueClientControllerId,add);
            if (refresh==null){
                //pag_xmlRpcClient.call("startApplicationQueue");
            } else {
                for (var i=0; i<applications.size();i++){
                    //pag_xmlRpcClient.call("getApplicationParticipants",applications.get(i).getUri(),applications.get(i).getId());
                }
            }
            return true;
        } else {
            alert("Error entering venue!");
            return false;*/
        }


	}

}
