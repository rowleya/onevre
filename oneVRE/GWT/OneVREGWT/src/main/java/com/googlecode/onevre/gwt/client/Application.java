/**
 * Copyright (c) 2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the and the University of Manchester nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.googlecode.onevre.gwt.client;


import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.onevre.gwt.client.ag.ApplicationManager;
import com.googlecode.onevre.gwt.client.ag.ConnectionManager;
import com.googlecode.onevre.gwt.client.ag.DataManager;
import com.googlecode.onevre.gwt.client.ag.JabberManager;
import com.googlecode.onevre.gwt.client.ag.MessageManager;
import com.googlecode.onevre.gwt.client.ag.ServerManager;
import com.googlecode.onevre.gwt.client.ag.ServiceManager;
import com.googlecode.onevre.gwt.client.ag.UserManager;
import com.googlecode.onevre.gwt.client.axmlrpc.AXmlRpcMessageReceiver;
import com.googlecode.onevre.gwt.client.ui.OneVREApplicationManager;
import com.googlecode.onevre.gwt.client.ui.OneVREConnectionManager;
import com.googlecode.onevre.gwt.client.ui.OneVREDataManager;
import com.googlecode.onevre.gwt.client.ui.OneVREJabberManager;
import com.googlecode.onevre.gwt.client.ui.OneVREMessageManager;
import com.googlecode.onevre.gwt.client.ui.OneVREServerManager;
import com.googlecode.onevre.gwt.client.ui.OneVREServiceManager;
import com.googlecode.onevre.gwt.client.ui.OneVREUserManager;
import com.googlecode.onevre.gwt.client.ui.panels.TopPanel;
import com.googlecode.onevre.gwt.client.xmlrpc.GetClientProfile;
import com.googlecode.onevre.gwt.client.xmlrpc.GetTrustedServers;
import com.googlecode.onevre.gwt.client.xmlrpc.GetVoAttributes;

public class Application implements EntryPoint {

    public static final String XMLRPC_REQUEST_SERVER = "xmlrpcRequestUrl";

    public static final String XMLRPC_RESPONSE_SERVER = "xmlrpcResponseUrl";

    public static final String APPLICATION_NAMESPACE = "pag_namespace";

    private static XmlRpcClient xmlrpcClient = null;

    private AXmlRpcMessageReceiver receiver = null;

    private static Dictionary parameters = null;

    private static UserManager userManager = null;

    private static DataManager dataManager = null;

    private static MessageManager messageManager = null;

    private static ServiceManager serviceManager = null;

	private static ApplicationManager applicationManager = null;

	private static JabberManager jabberManager = null;

	private static ServerManager serverManager = null;

	private static ConnectionManager connectionManager = null;

    public static UserManager getUserManager(){
    	return userManager;
    }

    public static JabberManager getJabberManager (){
    	return jabberManager;
    }

    public static MessageManager getMessageManager (){
    	return messageManager;
    }

	public static DataManager getDataManager() {
		return dataManager;
	}

	public static ServiceManager getServiceManager(){
		return serviceManager;
	}

	public static ConnectionManager getConnectionManager(){
		return connectionManager;
	}

	public static ServerManager getServerManager(){
		return serverManager;
	}

	public static ApplicationManager getApplicationManager(){
		return applicationManager;
	}

    public static String getParam(String name) {
        return parameters.get(name);
    }
    public static native String encodeURIComponent(String comp)/*-{
		return encodeURIComponent(comp);
	}-*/;

    public static XmlRpcClient getXmlRpcClient() {
        return xmlrpcClient;
    }

    protected String getUrl() {
        String url = GWT.getHostPageBaseURL();
        String paramUrl = parameters.get("url");
        if (paramUrl.startsWith("/")) {
            paramUrl = paramUrl.substring(1);
        }
        if (!paramUrl.endsWith("/")) {
            paramUrl += "/";
        }
        return url + paramUrl;
    }


    public void onModuleLoad() {

    	parameters = Dictionary.getDictionary("Parameters");

    	userManager = new UserManager(new OneVREUserManager());
    	dataManager = new DataManager(new OneVREDataManager());
    	serviceManager = new ServiceManager(new OneVREServiceManager());
    	serverManager = new ServerManager(new OneVREServerManager());
    	applicationManager  = new ApplicationManager (new OneVREApplicationManager());
    	connectionManager = new ConnectionManager(new OneVREConnectionManager());
    	jabberManager = new JabberManager(new OneVREJabberManager());
    	messageManager = new MessageManager(new OneVREMessageManager());

    	VenueClientController.setVenueClientController(Application.getParam("pag_venueClientControllerId"));

        String xmlrpcServer = getParam(XMLRPC_REQUEST_SERVER);
        if (xmlrpcServer.startsWith("/")) {
            xmlrpcServer = xmlrpcServer.substring(1);
        }
        xmlrpcServer+="?namespace="+encodeURIComponent(Application.getParam(Application.APPLICATION_NAMESPACE));
        xmlrpcClient = new XmlRpcClient(xmlrpcServer);
        xmlrpcClient.setDebugMode(true);
        xmlrpcClient.setTimeoutMillis(120000);
        GetVoAttributes.getVoAttributes();
        GetTrustedServers.getTrustedServers();
        receiver = new AXmlRpcMessageReceiver();
        receiver.start();
        GetClientProfile.getClientProfile();

        DockPanel applicationPanel = new DockPanel();
        applicationPanel.setWidth("100%");
        applicationPanel.setHeight("100%");
        applicationPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        applicationPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        FlexTable  flextable = new FlexTable();
        flextable.setCellPadding(1);
        flextable.setCellSpacing(0);
        flextable.setWidth("100%");


  /*      parameters = Dictionary.getDictionary("Parameters");
        GWT.log("found " + parameters.keySet().size() + " Parameters");
        int i=0;
        for (String par : parameters.keySet()){
        	String val = parameters.get(par);
        	GWT.log("Parameter "+ i +" " + par + " = " + val  );
        	flextable.setText(i, 0, String.valueOf(i));
			flextable.setText(i, 1, par);
			flextable.setText(i, 2, val);
			i++;

        }
*/
        RootPanel.get("pag-content").add(applicationPanel);

        TopPanel buttonPanel = new TopPanel();
        applicationPanel.add(buttonPanel.getPanel(),DockPanel.NORTH);
        Panel venueServerPanel = getServerManager().getUiPanel();
        applicationPanel.add(venueServerPanel,DockPanel.CENTER);

//        topPanel.add(new Label("OneVRE"),DockPanel.CENTER);
//        topPanel.add(flextable,DockPanel.CENTER);
    }


}
