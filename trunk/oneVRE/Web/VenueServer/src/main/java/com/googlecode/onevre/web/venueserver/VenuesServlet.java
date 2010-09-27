/*
 * @(#)VenuesServlet.java
 * Created: 10 Dec 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 */

package com.googlecode.onevre.web.venueserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.onevre.ag.agserver.DataStore;
import com.googlecode.onevre.ag.agserver.Venue;
import com.googlecode.onevre.ag.agserver.VenueEventServer;
import com.googlecode.onevre.ag.agserver.VenueServerConfigParameters;
import com.googlecode.onevre.ag.agserver.VenueServerDefaults;
import com.googlecode.onevre.protocols.soap.soapserver.SoapServerClient;
import com.googlecode.onevre.utils.ConfigFile;


/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenuesServlet extends SoapServerClient {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final int SEND_DELAY_TIME = 100;

    private static final int DEFAULT_TTL = 127;

    private static final String DEFAULT_HOST = "224.0.24.126";

    private static final int DEFAULT_PORT = 8000;

    private static final boolean DEFAULT_SECURITY = true;

    private static final long DEFAULT_REGISTRY_DELAY = 120000;

    private HashMap < String, Venue> venues = new HashMap < String, Venue > ();

    private String multicastAddress = null;

    private String venueName=null;

    private String serverLogFile = VenueServerDefaults.venueServerLogFile;

    private int port = 0;
    private boolean securedServer = true;

    private int encryptAllMedia = 1;
    private int houseKeeperFrequency = 300;
    private String persistenceFilename = "VenueServer.dat";
    private String serverPrefix = "VenueServer";
    private String venuePathPrefix = "Venues";
    private String dataStorageLocation = "Data";
    private String keyStore = "";
    private String storePasswd = "";
    private String backupServer = "";
/*    private String addressAllocationMethod = MulticastAddressAllocator.RANDOM;
    private String baseAddress = MulticastAddressAllocator.SDR_BASE_ADDRESS;
    private int addressMask = MulticastAddressAllocator.SDR_MASK_SIZE;
*/    private String authorizationPolicy = null;
    private String performanceReportFile = "";
    private int performanceReportFrequency = 0;

    private String configFile = "";

    private String configLocation = "";

    private HashMap<String, HashMap<String, String>> serverConfig = new HashMap<String, HashMap<String,String>>();

    private VenueEventServer venueEventServer = null;

    private boolean dontRunInit = false;

    private DataStore dataStore = null;
    /**
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
        if (dontRunInit) {
            return;
        }
        dontRunInit = true;
        System.out.println("entering VenuesServlet INIT");
        ServletConfig config = getServletConfig();
        HashMap<String, HashMap<String, String>> serverConf = new HashMap<String, HashMap<String,String>>();
        System.out.println("servletname: "+config.getServletName());
        String configFile = config.getInitParameter("ServerConfig");
        if (configFile!=null){
            configLocation = (new File((new File(configFile)).getParent())).getAbsolutePath()+"/";
            try {
                serverConf = ConfigFile.read(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
             throw new ServletException("No VenueServer config file specified");
        }
        HashMap<String, String> serverCfg = serverConf.get(VenueServerConfigParameters.VENUE_SERVER_SECTION);
        if (serverCfg==null){
            throw new ServletException("VenueServer config file needs [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        serverCfg.put(VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION, configLocation);
        if ((serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_FILE)==null)
                ||(serverCfg.get(VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD)==null)){
            throw new ServletException("VenueServer config file needs \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_FILE + "\", \"" +
                    VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD + "\", \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_FILE	+ "\" and \"" +
                    VenueServerConfigParameters.SSL_TRUSTSTORE_PASSWORD + "\" parameters in [" +
                    VenueServerConfigParameters.VENUE_SERVER_SECTION + "] Section");
        }
        URI serverUri=null;
        try {
            URL url = config.getServletContext().getResource("/");

            InetAddress addr =  InetAddress.getByName(url.getHost());
            serverUri = new URI(url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server uri: " + serverUri.toString());

        // .getResource(java.lang.String path)

        PrintWriter serverLog=null;
        try {
            serverLogFile = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_SECTION,
                    VenueServerConfigParameters.VENUE_SERVER_LOG_FILE, serverLogFile);
            serverLog = new PrintWriter(configLocation + serverLogFile);
        } catch (FileNotFoundException e) {
            throw new ServletException(e);
        }
        addVenues(serverConf, serverLog);
        System.out.println("leaving VenuesServlet INIT");
    }


    public void addVenues(HashMap<String, HashMap<String, String>> serverConfig, PrintWriter serverLog) throws ServletException{
        dontRunInit = true;
        String defaultVenue = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_DEFAULT_VENUE, "");
        String importUrl = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_IMPORT_HOST, "");
        configLocation = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION,"");
        persistenceFilename = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_VENUE_LIST, persistenceFilename);
        if (!persistenceFilename.startsWith("/")){
            persistenceFilename=configLocation + persistenceFilename;
        }
        HashMap<String, HashMap<String, String>> venuesData = null;
        try {
            venuesData = ConfigFile.read(persistenceFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (venuesData==null){
            throw new ServletException(" can't read persistence file @ " + persistenceFilename);
        }
        System.out.println("ConfigLocation: " + configLocation);
        if (venueEventServer == null) {
	        venueEventServer = new VenueEventServer(serverConfig);
	        venueEventServer.start();
        }
        if (dataStore == null) {
        	dataStore = new DataStore(serverConfig);
        }
        String textHost = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_TEXTSERVER_SECTION,
                VenueServerConfigParameters.TEXTSERVER_HOST, VenueServerDefaults.textHost);
        int textPort = Integer.valueOf(ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_TEXTSERVER_SECTION,
                VenueServerConfigParameters.TEXTSERVER_PORT, VenueServerDefaults.textPort));

        for (String venueIdString : venuesData.keySet()){
            HashMap<String, String> venueConfig = venuesData.get(venueIdString);
            String vType = venueConfig.get("type");
            if ((vType==null)||(!vType.equals("Venue"))){
//                System.out.println("Skipped Venue | " + venueIdString + " | " + venueConfig.get("name") + " | " + venueConfig );
                continue;
            }
//            System.out.println("Add Venue | " + venueIdString + " | " + venueConfig.get("name") + " | " + venueConfig );
            venueConfig.put("venueId", venueIdString);
            Venue venue = new Venue(venueIdString, venuesData, venueEventServer, dataStore, serverLog);
            String connections = venueConfig.get("connections");
            if (connections!=null){
                venue.setConnections(connections.split(":"),venuesData, importUrl);
            }
            registerObject("/"+venueIdString,  venue);
            venue.setTextLocation(textHost,textPort);
            venues.put(venueIdString, venue);
            registerObject("/"+venueIdString,  venue);
            if (venueIdString.equals(defaultVenue)){
                registerObject("/default", venue);
            }
        }
/*        if (multicastAddress == null) {
            multicastAddress = DEFAULT_HOST;
        }
      port = DEFAULT_PORT;
        if (portString != null) {
            port = Integer.parseInt(portString);
        }
        long registryDelay = DEFAULT_REGISTRY_DELAY;
        if (registryDelayString != null) {
            registryDelay = Long.parseLong(registryDelayString);
        }
        securedServer = DEFAULT_SECURITY;
        if (secureString != null){
            securedServer = Boolean.parseBoolean(secureString);
        }
        try {
            SoapServer soapServer = new SoapServer(port,securedServer);
            this.setServer(soapServer);
        } catch (IOException e) {
            throw new ServletException(e);
        }*/
/*        MulticastNetworkLocation location = new MulticastNetworkLocation();
        location.setHost(multicastAddress);
        location.setPort(port);
        location.setTtl(DEFAULT_TTL);
        try {
            URL pagUrl = new URL(pagServer);
        } catch (Exception e) {
            throw new ServletException(e);
        }*/
    }

    /**
     *
     * @throws IOException
     * @see javax.servlet.http.HttpServlet#doGet(
     *     javax.servlet.http.HttpServletRequest,
     *     javax.servlet.http.HttpServletResponse)
     */
     public synchronized void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
 // receive SOAP request
 // decode SOAP
 // trigger
        System.out.println("VenuesServlet - RequestProto: " + request.getProtocol());
        System.out.println("VenuesServlet - venue: " + request.getServletPath());
        System.out.println("VenuesServlet - venue PI: " + request.getPathInfo());
        System.out.println("VenuesServlet - venue PT: " + request.getPathTranslated());
        System.out.println("VenuesServlet - venue LN: " + request.getLocalName());
        System.out.println("VenuesServlet - venue M : " + request.getMethod());
        System.out.println("VenuesServlet - venue url: " + request.getRequestURL().toString());

        URL url=new URL(request.getRequestURL().toString());
        venueName=request.getPathInfo().substring(1);
        Venue venue=(Venue)findObjectForPath(request.getPathInfo());
        if (venue==null){
            System.out.println("venue not found " + request.getPathInfo());
        }
        if (venue.getState().getUri()==null){
            InetAddress addr =  InetAddress.getByName(url.getHost());
            try {
                URI uri = new URI(url.getProtocol(),url.getUserInfo(),addr.getCanonicalHostName(),url.getPort(),url.getPath(),url.getQuery(),null);
                venue.getState().setUri(uri.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("VenuesServlet - contentLength:"+request.getContentLength());
//        System.out.println("VenuesServlet - contentType:"+request.getContentType());
//        BufferedReader rd=request.getReader();
//        String text="";
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            text += line;
//        }
//        System.out.println("VenueServlet - SOAP Message from: "+ venueName);
//        System.out.println("---------------------------------------------------------------------------");
//        System.out.println(text);
//        System.out.println("---------------------------------------------------------------------------");

        super.doPost(request, response);

    }

    public synchronized void doGet (HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        PrintWriter http = response.getWriter();
        http.println("OneVRE Venue Server");
    }


    protected void removeVenue(String id) {
        System.err.println("Removing venue " + id);
        venues.remove(id);
    }

    /**
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        dataStore.destroy();
        venueEventServer.closeAll();
    }
}
