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

package com.googlecode.onevre.ag.agserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import java.security.cert.X509Certificate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;

import com.googlecode.onevre.ag.agsecurity.AuthorizationException;
import com.googlecode.onevre.ag.agsecurity.Subject;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.VOAttribute;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.protocols.soap.soapserver.SoapServerClient;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Utils;


/**
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenuesServlet extends SoapServerClient {

	Log log = LogFactory.getLog(this.getClass());

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

    ThreadLocal<Subject> subject = new ThreadLocal<Subject>();

    private String serverLogFile = VenueServerDefaults.venueServerLogFile;
    private PrintWriter serverLog=null;

    private int port = 0;
    private boolean securedServer = true;

    private int encryptAllMedia = 1;
    private int houseKeeperFrequency = 300;
    private String persistenceFilename = "VenueServer.dat";
    private String defaultPolicyFilename = "VenueServer.pol";
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
    String defaultPolicy = "";
    private String configLocation = "";

    String textHost ="jabber.mcs.anl.gov";
    int textPort = 5223;

    private HashMap<String, HashMap<String, String>> serverConfig = new HashMap<String, HashMap<String,String>>();

    HashMap<String, HashMap<String, String>> venuesData = new HashMap<String, HashMap<String,String>>();

    private VenueEventServer venueEventServer = null;

    private boolean dontRunInit = false;

    private DataStore dataStore = null;

    public VenuesServlet(PrintWriter serverLog){
    	this.serverLog = serverLog;
    }
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
        log.info("servletname: "+config.getServletName());
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

 //           InetAddress addr =  InetAddress.getByName(url.getHost());
            serverUri = new URI(url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Server uri: " + serverUri.toString());

        // .getResource(java.lang.String path)
        addVenues(serverConf, serverLog);

        log.info("leaving VenuesServlet INIT");
    }

    public Subject getSubject(){
    	return subject.get();
    }

    public void addVenues(HashMap<String, HashMap<String, String>> serverConfig, PrintWriter logfile) throws ServletException{
        serverLog = logfile;
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
        defaultPolicyFilename = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_DEFAULT_POLICY_FILE, defaultPolicyFilename);

        if (!persistenceFilename.startsWith("/")){
            persistenceFilename=configLocation + persistenceFilename;
        }
        try {
            venuesData = ConfigFile.read(persistenceFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (venuesData==null){
            throw new ServletException(" can't read persistence file @ " + persistenceFilename);
        }
        if (!defaultPolicyFilename.startsWith("/")){
            defaultPolicyFilename=configLocation + defaultPolicyFilename;
        }
        defaultPolicy = null;
        try {
        	defaultPolicy = Utils.readPlainFile(defaultPolicyFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (defaultPolicy==null){
            throw new ServletException(" can't read policy file @ " + defaultPolicyFilename);
        }
        log.info("ConfigLocation: " + configLocation);
        if (venueEventServer == null) {
	        venueEventServer = new VenueEventServer(serverConfig);
	        venueEventServer.start();
        }
        if (dataStore == null) {
        	dataStore = new DataStore(serverConfig);
        }
        textHost = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_TEXTSERVER_SECTION,
                VenueServerConfigParameters.TEXTSERVER_HOST, VenueServerDefaults.textHost);
        textPort = Integer.valueOf(ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_TEXTSERVER_SECTION,
                VenueServerConfigParameters.TEXTSERVER_PORT, VenueServerDefaults.textPort));
        VenueServer venueServer = new VenueServer(this,serverLog);

        for (String venueIdString : venuesData.keySet()){
            HashMap<String, String> venueConfig = venuesData.get(venueIdString);
            String vType = venueConfig.get("type");
            if ((vType==null)||(!vType.equals("Venue"))){
//                System.out.println("Skipped Venue | " + venueIdString + " | " + venueConfig.get("name") + " | " + venueConfig );
                continue;
            }
//            System.out.println("Add Venue | " + venueIdString + " | " + venueConfig.get("name") + " | " + venueConfig );
            venueConfig.put("venueId", venueIdString);
            Vector<VOAttribute> voAttributes = null;
            String voAttr = venueConfig.get("VO-Attributes");
            if (voAttr!=null){
            	voAttributes = new Vector<VOAttribute>();
            	for (String att: voAttr.split(":")){
            		voAttributes.add(new VOAttribute(att));
            	}
            }
            Venue venue = new Venue(this, venueIdString, venuesData, venueEventServer, dataStore, defaultPolicy, voAttributes, serverLog);
            serverLog.println("created Venue: "+ venueIdString + " ("+ venueConfig.get("name")+")");
            log.info("created Venue: "+ venueIdString);
            String connections = venueConfig.get("connections");
            if (connections!=null){
                venue.setConnections(connections.split(":"),venuesData, importUrl);
            }
            venue.setTextLocation(textHost,textPort);
            venues.put(venueIdString, venue);
            registerObject("/Venues/"+venueIdString,  venue);
            if (venueIdString.equals(defaultVenue)){
                registerObject("/Venues/default", venue);
            }
            log.info("registered Venue: "+ venueIdString);
            venueServer.addVenue(venue,voAttributes);
        }
        registerObject("/VenueServer",  venueServer);
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

    public ConnectionDescription addVenue(String name, String description, Vector<VOAttribute> voAttributes, ClientProfile creator, PrintWriter serverLog) {
    	String venueIdString =  Utils.generateID();

    	HashMap<String, String> venueConfig = new HashMap<String, String>();
    	venuesData.put(venueIdString,venueConfig);
    	venueConfig.put("type","Venue");
        venueConfig.put("venueId", venueIdString);
        venueConfig.put("name", name);
        venueConfig.put("description", description);
        venueConfig.put("creator", creator.getPublicId());
        Venue venue = new Venue(this, venueIdString, venuesData, venueEventServer, dataStore, defaultPolicy, voAttributes, serverLog);
        venue.setTextLocation(textHost,textPort);
        venues.put(venueIdString, venue);
        registerObject("/Venues/"+venueIdString,  venue);
        Venue defautlVenue=(Venue)findObjectForPath("/Venues/default");
        VenueState defautlVenueState = defautlVenue.getState();
        String url = defautlVenueState.getUri();
        log.info("defaultVenueURL: " + url );
        url = url.substring(0, url.indexOf("/Venues")) + "/Venues/"+venueIdString;
        VenueState venueState = venue.getState(venueIdString);
        venueState.setUri(url);
        venueState.setEventLocation(defautlVenueState.getEventLocation());
        ConnectionDescription conns [] = venue.getConnections();
        if (conns == null){
        	ConnectionDescription conn = new ConnectionDescription(venue.getState(venueIdString));
        	conns = new ConnectionDescription[]{conn};
        	defautlVenue.setConnections(conns,venuesData,false);
        	venue.setConnections(new ConnectionDescription[]{new ConnectionDescription(defautlVenue.getState())},venuesData,false);
        }
        try {
			ConfigFile.store(persistenceFilename, venuesData);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return conns[0];
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
        log.info("VenuesServlet - RequestProto: " + request.getProtocol());
        log.info("VenuesServlet - venue: " + request.getServletPath());
        log.info("VenuesServlet - venue PI: " + request.getPathInfo());
        log.info("VenuesServlet - venue PT: " + request.getPathTranslated());
        log.info("VenuesServlet - venue LN: " + request.getLocalName());
        Subject subject = new Subject();
        this.subject.set(subject);
		X509Certificate[] certificates = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
		if ((certificates!=null)&&(certificates.length>0)){
			subject.setName(certificates[0].getSubjectDN().getName().toString());
			VOMSValidator validator = new VOMSValidator(certificates).validate();
			Vector attribute = new Vector(validator.getVOMSAttributes());
			log.info("attribute: " + attribute.toString());
			for (Object attrobj : attribute){
				VOMSAttribute vomsAttribute = (VOMSAttribute)attrobj;
				String voText = (String)vomsAttribute.getFullyQualifiedAttributes().get(0);
				log.info("analyzing VO attribute: " + voText);
				VOAttribute voAttribute = new VOAttribute(voText);
				subject.setVoAttribute(voAttribute);
			}
			this.subject.set(subject);
		}
		subject = getSubject();

        URL url=new URL(request.getRequestURL().toString());
        log.info("VenuesServlet - URL: " + url);
        log.info("VenuesServlet - Set Subject: " + subject.toString());
        try{
	        venueName=request.getPathInfo().substring(1);
	        if (venueName.startsWith("Venues/")){
		        Venue venue=(Venue)findObjectForPath(request.getPathInfo());
		        if (venue==null){
		            serverLog.println("venue not found " + request.getPathInfo());
		        } else {
			        if (venue.getState().getUri()==null){
			            InetAddress addr[] =  InetAddress.getAllByName(url.getHost());
			            String hostname = "";
			            InetAddress hostaddr = null;
			            for (InetAddress add : addr){
			            	hostname = add.getCanonicalHostName();
			            	hostaddr = add;
			            	if (!hostname.equals("localhost")){
			            			break;
			            	}
			            }
			            try {
			                URI uri = new URI(url.getProtocol(),url.getUserInfo(),hostname,url.getPort(),url.getPath(),url.getQuery(),null);
			                log.info("addr:"+ hostaddr.getHostAddress() +" url:"+uri);
			                venue.getState().setUri(uri.toString());
			            } catch (URISyntaxException e) {
			                e.printStackTrace();
			            }
			        }
		        }
	        }
	        log.info("VenuesServlet - call Super");
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
		    log.info("Super success");
        } catch (AuthorizationException e){
        	e.printStackTrace();
        }

    }

    public synchronized void doGet (HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        PrintWriter http = response.getWriter();
        doPost(request, response);
        http.println("OneVRE Venue Server");
    }


    protected void removeVenue(String id) {
        log.info("Removing venue " + id);
        venues.remove(id);
    }

    /**
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        dataStore.destroy();
        try {
			ConfigFile.store(persistenceFilename, venuesData);
		} catch (IOException e) {
			e.printStackTrace();
		}
        venueEventServer.closeAll();
    }
}
