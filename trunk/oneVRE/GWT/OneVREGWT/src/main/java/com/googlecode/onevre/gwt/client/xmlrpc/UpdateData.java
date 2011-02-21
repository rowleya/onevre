package com.googlecode.onevre.gwt.client.xmlrpc;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;

public class UpdateData implements AsyncCallback<String> {

    public static void updateData(String venueURI, DataDescription data) {
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "updateData",
                new Object[]{venueURI, data.getId(), data.getName(), data.getDescription(), data.getExpires()},
                new UpdateData());
        request.execute();
    }

    public static void createDirectory(String venueURI, DataDescription data) {
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "addDir",
                new Object[]{
                        venueURI, data.getName(),
                        data.getDescription(),
                        "" + data.getHierarchyLevel(),
                        data.getParentId(),
                        data.getExpires()
                },
                new UpdateData());
        request.execute();
    }

    public static void deleteData(String venueURI, DataDescription data) {
        XmlRpcClient xmlrpcClient = Application.getXmlRpcClient();
        XmlRpcRequest<String> request = new XmlRpcRequest<String>(
                xmlrpcClient, "deleteData",  new Object[]{venueURI, data.getId()},
                new UpdateData());
        request.execute();
    }

    public void onFailure(Throwable paramThrowable) {

    }

    public void onSuccess(String venueStateXml) {

    }

}
