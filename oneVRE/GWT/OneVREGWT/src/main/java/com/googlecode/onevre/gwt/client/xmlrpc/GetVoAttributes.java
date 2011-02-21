package com.googlecode.onevre.gwt.client.xmlrpc;

import java.util.Vector;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.ag.types.VectorJSO;
import com.googlecode.onevre.gwt.client.ag.types.VOAttribute;
import com.googlecode.onevre.gwt.client.ag.types.VOAttributeJSO;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.VenueClientController;

public class GetVoAttributes implements AsyncCallback<String> {

    public static void getVoAttributes() {
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "getVoAttributes",  new Object[]{},
                new GetVoAttributes());
        GWT.log("execute getVoAttributes");
        request.execute();

    }

    public void onFailure(Throwable error) {
        GWT.log("getVoAttributes failed: ", error);
    }

    public void onSuccess(String voAttributesXml) {
        GWT.log("VoAttributes: " + voAttributesXml);
        Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();
        @SuppressWarnings("unchecked")
        VectorJSO<VOAttributeJSO> vojso =
            (VectorJSO<VOAttributeJSO>) VenueClientController.getObjectDec(voAttributesXml);
        for (int i = 0; i < vojso.size(); i++) {
            VOAttribute vo = new VOAttribute(vojso.get(i));
            voAttributes.add(vo);
            GWT.log("adding VoAttribute: " + vo.toString());
        }
        Application.getUserManager().setVOAttributes(voAttributes);

    }

}
