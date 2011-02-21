/*
 * @(#)VenueClientUI.java
 * Created: 15-May-2007
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

package com.googlecode.onevre.web.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.jivesoftware.smack.XMPPException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.googlecode.onevre.ag.agbridge.RegistryClient;
import com.googlecode.onevre.ag.agclient.ApplicationEventListener;
import com.googlecode.onevre.ag.agclient.ClientUpdateThread;
import com.googlecode.onevre.ag.agclient.VenueClientEventListener;
import com.googlecode.onevre.ag.agclient.interfaces.ApplicationListener;
import com.googlecode.onevre.ag.agclient.venue.EventClient;
import com.googlecode.onevre.ag.agclient.venue.JabberClient;
import com.googlecode.onevre.ag.agclient.venue.MessageBox;
import com.googlecode.onevre.ag.agclient.venue.UploadStatus;
import com.googlecode.onevre.ag.common.interfaces.SharedApplication;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VOAttribute;
import com.googlecode.onevre.ag.types.VenueList;
import com.googlecode.onevre.ag.types.VenueServerType;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.VenueTreeItem;
import com.googlecode.onevre.ag.types.application.AppParticipantDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.server.Venue;
import com.googlecode.onevre.ag.types.server.VenueServer;
import com.googlecode.onevre.protocols.events.eventserver.AgEvent;
import com.googlecode.onevre.protocols.events.eventserver.AgEventServer;
import com.googlecode.onevre.protocols.gsi.CredentialMappings;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLDeserializer;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.utils.TimeoutListener;
import com.googlecode.onevre.utils.Utils;
import com.googlecode.onevre.web.common.Defaults;
import com.googlecode.onevre.web.servlet.ServicesList;


/**
 * Keeps track of the client ui state.
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueClientUI implements ApplicationListener, TimeoutListener,
        HttpSessionBindingListener {

    private Log log = LogFactory.getLog(this.getClass());

    // The default port for the jabber connection
    private static final int DEFAULT_JABBER_PORT = 5223;

    // String that separates a url and venue name in the My Venues preference
    private static final String MY_VENUES_PREF_URL_SEPARATOR = "=";

    // String that separates venues from other venues in the My Venues pref
    private static final String MY_VENUES_PREF_VENUE_SEPARATOR = ";";

    // The string to display when the user is not in a venue
    private static final String NOT_IN_VENUE_STRING = "You are not in a venue";

    // The profile of the current client
    private ClientProfile clientProfile = null;

    // The uri of the current venue
    private String currentVenueUri = null;

    // The name of the current venue
    private String currentVenueName = NOT_IN_VENUE_STRING;

    // A link to the current venue

    // The state of the current venue

    // The connection id in the current venue

    // A threads that keep the clients updated in the venue
    private HashMap<String, ClientUpdateThread> clientUpdateThreads = new HashMap<String, ClientUpdateThread>();

    private JabberClient jabberClient = null;

    // The Jabber Client
    private HashMap<String, JabberClient> jabberClients = new HashMap<String, JabberClient>();


    private Vector<String> dataConnections = new Vector<String>();

    // The Application Client
    private Vector<EventClient> applicationClients = null;

    private HashMap<String, EventClient> venueEventClients = new HashMap<String, EventClient>();

    private HashMap<String, Venue> venues = new HashMap<String, Venue>();

    private HashMap<String, VenueState> venueStates = new HashMap<String, VenueState>();

    private HashMap<String, String> venueConnIds = new HashMap<String, String>();

    private HashMap<String, String> applicationMonitorPrivateTokens = null;

    private HashMap<String, Vector<String>> applicationPrivateTokens = null;

    private Vector<VenueServerType> trustedServers = new Vector<VenueServerType>();

    private boolean inVenue = false;

    private String sessionId = null;

    // The current list of venues
    private VenueList venueList = null;

    // A map of user selected venues
    private HashMap<String, VenueTreeItem> myVenues = new HashMap<String, VenueTreeItem>();


    // A list of previous venues
    private LinkedList<String> previousVenues = new LinkedList<String>();

    // The current streams
    private StreamDescription[] currentStreams = new StreamDescription[0];

    // The xml rpc server to add requests to
    private AgEventServer agEventServer = null;

    // The current status
    private String status = "";

    private boolean updateJabber = false;

    private CredentialMappings credential = null;

    /**
     * Creates a new VenueClientUI
     * @param clientProfile The profile of the connecting client
     * @param xmlRpcServer The XML-RPC server
     * @param myVenuesPreference The myVenues preference item
     */
    public VenueClientUI(ClientProfile clientProfile,
            AgEventServer agEventServer, String myVenuesPreference, String trustedServersPreference, String sessionId) {
        this.clientProfile = clientProfile;
        this.sessionId = sessionId;
        this.currentVenueUri = clientProfile.getHomeVenue();
        this.agEventServer = agEventServer;
        setMyVenues(myVenuesPreference);
        setTrustedServers(Defaults.getTrustedServerFile());
        try {
            this.venueList = new VenueList();
            venueList.setCurrentVenueUrl(currentVenueUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new DoShutdown(this));
    }

    /**
     * Sets the current status of an upload
     * @param filename The file being uploaded
     * @param total The total size of the file
     * @param current The current size of the file
     */
    public void setUploadStatus(String venueUri, String filename, long total, long current) {
        UploadStatus object = new UploadStatus(filename, total, current);
        agEventServer.addEvent(new AgEvent(venueUri, "setUploadStatus", object));
    }

    private String uriFromVenueStateUri(String vsUri) {
        for (String uri : venueStates.keySet()) {
            VenueState vs = venueStates.get(uri);
            if ((vs != null) && vsUri.equals(vs.getUri())) {
                return uri;
            }
        }
        return null;
    }

    /**
     * Shows the upload status dialog
     *
     */
    public void showUploadStatus(String venueUri, String filename, long total) {
        UploadStatus object = new UploadStatus(filename, total, 0);
        agEventServer.addEvent(new AgEvent(venueUri, "showUploadStatus", object));
    }

    /**
     * Hides the upload status dialog
     *
     */
    public void hideUploadStatus(String venueUri) {
        agEventServer.addEvent(new AgEvent(venueUri, "hideUploadStatus", null));
    }

    /**
     * Displays a message to the client
     * @param message The message to display
     */
    public void displayMessage(String venueUri, String type, String message) {
        agEventServer.addEvent(new AgEvent(venueUri, "displayMessage", new MessageBox(type, message)));
    }

    private void setMyVenues(String myVenuesPreference) {
        myVenues.clear();
        String[] venueNames = myVenuesPreference.split(
                MY_VENUES_PREF_VENUE_SEPARATOR);
        for (int i = 0; i < venueNames.length; i++) {
            String[] venue = venueNames[i].split(MY_VENUES_PREF_URL_SEPARATOR);
            if (venue.length > 1) {
                myVenues.put(venue[0], new VenueTreeItem(venue[0], venue[1]));
            }
        }
    }

    private VenueServerType getVenueServer(String name, String server) throws SoapException, IOException {
        String urlString = "";
        if (!server.contains("://")) {
            urlString = "https://";
        }
        urlString += server;
        URL url = new URL(urlString);
        VenueServerType venueServer = new VenueServerType();
        venueServer.setName(name);
        venueServer.setPortNumber(url.getPort());
        if (url.getPath() != "") {
            venueServer.setDefaultVenue(url.getPath());
        }
        venueServer.setProtocol(url.getProtocol());
        venueServer.setUrl(url.getHost());
        Venue venue = new Venue(venueServer.getDefaultVenueUrl(), isOneVREVenue(venueServer.getDefaultVenueUrl()));
           venue.setGSScredential(credential);
        String version = venue.getVersion();
        if (version != null) {
            venueServer.setVersion(version);
        }
        VenueState state = venue.getState();
        if (state != null) {
            venueServer.setDefaultVenueId(state.getUniqueId());
            url = new URL(state.getUri());
            log.info("URL Path: " + url.getPath());
            venueServer.setDefaultVenue(url.getPath());
        }
        log.info("Add trusted Server:" + venueServer.toLog());

        return venueServer;
    }


    private void setTrustedServers(String trustedServersFile) {
        trustedServers.clear();
        DOMParser parser = new DOMParser();
        try {
            parser.parse(new InputSource(getClass().getResourceAsStream("/" + trustedServersFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = parser.getDocument();
        NodeList serverList = document.getElementsByTagName("server");
        for (int i = 0; i < serverList.getLength(); i++) {
            Node serverNode = serverList.item(i);
            String server = serverNode.getTextContent();
            String name = "";
            try {
                name = serverNode.getAttributes().getNamedItem("name").getNodeValue();
            } catch (NullPointerException e) {
                name = "";
            }
            try {
                  VenueServerType venueServer = getVenueServer(name, server);
                log.info("Add trusted Server:" + venueServer.toLog());
                trustedServers.add(venueServer);
            } catch (Exception e) {
                log.error("Server : " + name + " = " + server + " is not responding ");
            }
        }
    }

    static {
        System.setProperty("java.protocol.handler.pkgs",
                "javax.net.ssl");
        System.setProperty("java.protocol.handler.pkgs",
                "net.sf.ufsc");

    }

    /**
     * Gets the user venues as a string that can be stored as a portlet preferences
     * @return The myVenues string
     */
    public String getMyVenuesPreference() {
        String myVenuesPreference = "";
        Iterator<String> iterator = myVenues.keySet().iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            String name = myVenues.get(url).getName();
            myVenuesPreference += url + MY_VENUES_PREF_URL_SEPARATOR + name
                + MY_VENUES_PREF_VENUE_SEPARATOR;
        }
        return myVenuesPreference;
    }

    public void setTrustedServers(Vector<VenueServerType> trServers) {
        for (VenueServerType server : trServers) {
            if (!trustedServers.contains(server)) {
                log.info("Adding trusted Server:" + server);
                trustedServers.add(server);
            }
        }
    }

/*    public String getTrustedServersPreference() {
        String trustedServersPreference = "";
        String sep="";
        for (String server : trustedServers) {
            trustedServersPreference += sep + server;
            sep=";";
        }
        return trustedServersPreference;
    }
*/
    /**
     * Gets the current list of my venues urls
     * @return The venue urls
     */
    public Vector<String> getMyVenuesUrls() {
        return new Vector<String>(myVenues.keySet());
    }

    /**
     * Gets the name of a myVenue
     * @param url The url of the venue
     * @return The name of the venue
     */
    public String getMyVenueName(String url) {
        return myVenues.get(url).getName();
    }


    /**
     * Adds a venue to the user venues
     * @param url The url of the venue
     * @param name The name of the venue
     * @return the changed list
     */
    public VenueList addMyVenue(String url, String name) {
        VenueTreeItem item = new VenueTreeItem(name, url);
        myVenues.put(url, item);
        if (venueList.getMode() == VenueList.MODE_MY_VENUES) {
            venueList.addRoot(item);
        }
       return venueList;
    }

    /**
     * Removes a venue from the user venues
     * @param url The url of the venue to remove
     * @return the changed list
     */
    public VenueList removeMyVenue(String url) {
        VenueTreeItem item = myVenues.remove(url);
        if (venueList.getMode() == VenueList.MODE_MY_VENUES) {
            if (item != null) {
                venueList.removeRoot(item);
            }
        }
        return venueList;
    }

    /**
     * Changes a venue in the list of venues
     * @param oldUrl The old url of the node
     * @param url The new url of the node
     * @param name The name of the venue
     * @return The new list of venues
     */
    public VenueList changeMyVenue(String oldUrl, String url, String name) {
        VenueTreeItem item = myVenues.remove(oldUrl);
        item.setName(name);
        item.setUri(url);
        myVenues.put(url, item);
        return venueList;
    }

    private void removeVenueConnections(String id) {
        VenueTreeItem item = venueList.findVenue(id);
        if (item != null) {
            Vector<VenueTreeItem> connections = item.getConnections();
            if (connections != null) {
                for (int i = 0; i < connections.size(); i++) {
                    removeVenueConnections(connections.get(i).getId());
                }
            }
            venueList.removeVenue(id);
        }
    }


    /**
     * Enters a venue
     *
     * @param uri The uri of the venue to enter
     * @return The state of the venue
     * @throws Exception
     */
    public VenueState enterVenue(String uri) throws Exception {
        try {
            int venueListMode = venueList.getMode();
            exitVenue();
            applicationClients = new Vector<EventClient>();
            applicationMonitorPrivateTokens = new HashMap<String, String>();
            applicationPrivateTokens = new HashMap<String, Vector<String>>();
            currentVenueUri = uri;
            Venue currentVenue = new Venue(uri, isOneVREVenue(uri));
            currentVenue.setGSScredential(credential);
            venues.put(uri, currentVenue);
            String connectionId = currentVenue.enter(clientProfile);
            venueConnIds.put(uri, connectionId);
            ClientUpdateThread clientUpdateThread = new ClientUpdateThread(currentVenue,
                    connectionId);
            clientUpdateThreads.put(uri, clientUpdateThread);
            final VenueState currentVenueState = currentVenue.getState();
            venueStates.put(uri, currentVenueState);
//            System.out.println(currentVenueState.toString());
            currentVenueName = currentVenueState.getName();
            venueList = new VenueList(currentVenueState);
            currentVenueState.setVenueList(venueList);
            setVenueListMode(venueListMode);

            EventClient eventClient = new EventClient(
                    new VenueClientEventListener(currentVenueState,
                            currentVenueState.getEventLocation(), this, agEventServer),
                    connectionId, currentVenueState.getUniqueId());
            venueEventClients.put(uri, eventClient);
            Defaults.writeLog("Enter Venue: " + currentVenueState.toLog() + "; Client: " + clientProfile.toLog());

            final URI venueuri = new URI(uri);

            Thread jabberThread = new Thread() {
                public void run() {
                    String jabberRoom =
                        currentVenueName.toLowerCase().replaceAll(
                        "[ \"&'/:<>@]", "-") + "(" + venueuri.getHost() + ")";
                    String nickname = clientProfile.getName();
                    String jabberHost = "jabber.mcs.anl.gov";
                    int jabberPort = DEFAULT_JABBER_PORT;

                    String[] location =
                        currentVenueState.getTextLocation().split(":");
                    if (location[0].length() != 0) {
                        jabberHost = location[0];
                    }
                    if (location[1].length() != 0) {
                        jabberPort = Integer.parseInt(location[1]);
                    }
                    //String conferenceHost = jabberHost.replaceFirst("jabber",
                    //        "conference");
                    //jabberRoom += "@" + conferenceHost;
//                    System.err.println("Nick : " + nickname);
//                    System.err.println("Host : " + jabberHost);
//                    System.err.println("Port : " + jabberPort);
//                    System.err.println("Room : " + jabberRoom);
                    try {
                        jabberClient = new JabberClient(jabberHost, jabberPort, true,
                            currentVenueState.getUri(), jabberRoom, nickname, agEventServer);
                        jabberClients.put(currentVenueState.getUri(), jabberClient);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            jabberClients.put(currentVenueState.getUri(), jabberClient);
            this.jabberClient = jabberClients.get(currentVenueUri);
            jabberThread.start();
            agEventServer.setListener(this);
        } catch (Exception e) {
            exitVenue();
            throw e;
        }
        inVenue = true;
        return venueStates.get(uri);
    }
    /**
     * Enters a venue
     *
     * @param uri The uri of the venue to enter
     * @return The state of the venue
     * @throws MalformedURLException
     * @throws Exception
     */
    public VenueState monitorVenue(String uri) throws MalformedURLException  {
        Venue currentVenue = new Venue(uri, isOneVREVenue(uri));
        boolean managable = isOneVREVenue(uri);
        log.info("IN MONITOR VENUE");

        if (credential != null) {
            log.info("Monitor " + credential.getDN() + " : " + credential.getVoAttributes().toString());
        } else {
            log.info("Monitor no credential provided");
        }
           currentVenue.setGSScredential(credential);
        /*        String version="";
        try {
            version = currentVenue.getVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("VenueServer Version: " + version );
        if (version.contains("OneVRE")) {
            currentVenue.setGSScredential(credential);
        }*/
        venues.put(uri, currentVenue);
        VenueState state = null;
        try {
            state = currentVenue.getState();
            uri = state.getUri();
            venues.put(uri, currentVenue);
            venueStates.put(uri, state);
            applicationClients = new Vector<EventClient>();
            applicationMonitorPrivateTokens = new HashMap<String, String>();
            applicationPrivateTokens = new HashMap<String, Vector<String>>();
            currentVenueUri = uri;
            String connectionId = Utils.generateID();
            if (managable) {
                connectionId = currentVenue.monitor(clientProfile);
                venueConnIds.put(uri, connectionId);
            }
            log.info("VenueState of " + uri + " : " +  state);
            EventClient eventClient = new EventClient(
                    new VenueClientEventListener(state, state.getEventLocation(), this, agEventServer),
                    connectionId, state.getUniqueId());
            log.info("eventClient started");
            venueEventClients.put(uri, eventClient);

            /*            Venue venue = new Venue(uri);
            String version = venue.getVersion();

            if (version.contains("OneVRE")) {
                connectionId = venue.monitor(clientProfile);
                Vector
            }
            connectionId = currentVenue.enter(clientProfile);
            clientUpdateThread = new ClientUpdateThread(currentVenue,
                    connectionId);
            currentVenueState = currentVenue.getState();
//            System.out.println(currentVenueState.toString());
            currentVenueName = currentVenueState.getName();
            venueList = new VenueList(currentVenueState);
            currentVenueState.setVenueList(venueList);
            setVenueListMode(venueListMode);

            eventClient = new EventClient(new VenueClientEventListener(currentVenueState,
                    currentVenueState.getEventLocation(),
                    this,xmlRpcServer), connectionId,
                    currentVenueState.getUniqueId());
            Defaults.writeLog("Enter Venue: " + currentVenueState.toLog() + "; Client: " + clientProfile.toLog() );
*/
            final URI venueuri = new URI(uri);
            final VenueState jabberState = state;
            Thread jabberThread = new Thread() {
                public void run() {
                    String jabberRoom = jabberState.getName().toLowerCase().replaceAll(
                        "[ \"&'/:<>@]", "-") + "(" + venueuri.getHost() + ")";
                    String nickname = clientProfile.getName();
                    String jabberHost = "jabber.mcs.anl.gov";
                    int jabberPort = DEFAULT_JABBER_PORT;

                    String[] location =
                        jabberState.getTextLocation().split(":");
                    if (location[0].length() != 0) {
                        jabberHost = location[0];
                    }
                    if (location[1].length() != 0) {
                        jabberPort = Integer.parseInt(location[1]);
                    }
                    //String conferenceHost = jabberHost.replaceFirst("jabber",
                    //        "conference");
                    //jabberRoom += "@" + conferenceHost;
                    log.info("Nick : " + nickname);
                    log.info("Host : " + jabberHost);
                    log.info("Port : " + jabberPort);
                    log.info("Room : " + jabberRoom);
                    try {
                        jabberClient = new JabberClient(jabberHost, jabberPort, true,
                            jabberState.getUri(), jabberRoom, nickname, agEventServer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
           jabberThread.start();
           jabberClients.put(uri, jabberClient);
           agEventServer.setListener(this);
        } catch (SoapException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
//            stopMonitorVenue();
//            throw e;
        }
        return state;
    }

    public boolean isOneVREVenue(String venueUri) {
        for (VenueServerType st : trustedServers) {
            if (venueUri.startsWith(st.getBaseUrl())) {
                if (st.getVersion().contains("OneVRE")) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean stopMonitoringVenue(String venueUri)  {
        String uri = uriFromVenueStateUri(venueUri);
        if (uri == null) {
            return false;
        }
        String connId = venueConnIds.get(uri);
        EventClient evclient = venueEventClients.remove(uri);
        if (evclient != null) {
            evclient.close();
        }
        JabberClient jabber = jabberClients.remove(uri);
        if (jabber != null) {
            jabber.close();
        }
        Venue venue = venues.get(uri);
        try {
            venue.exit(connId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean uploadData(String venueUri, final String parentId,
            final String filename, final String description, final String expires) {

        /*
        String uri = uriFromVenueStateUri(venueUri);

        final String namespace;

        final File file = new File(filename);
        Thread uploader = new Thread() {
            public void run() {
                    Part[] parts = new Part[4];
                    parts[0] = new StringPart("namespace", namespace);
                    parts[1] = new StringPart("parentId", parentId);
                    parts[2] = new StringPart("description", description);
                    parts[3] = new StringPart("expires", expires);
                    parts[4] = new FilePart(file.getName(),file);
                    System.out.println("Uploading " + parts.length + " files");

                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        Utils.addSslConnection(connection);
                        PostMethod filePost = new PostMethod(urlString);
                        RequestEntity requestEntity = new MultipartRequestEntity(parts,
                                filePost.getParams());
                        connection.setRequestMethod("POST");
                        connection.addRequestProperty("Content-Type", requestEntity.getContentType());
                        connection.addRequestProperty("Content-Length",
                                String.valueOf(requestEntity.getContentLength()));
                        connection.addRequestProperty("Cookie","JSESSIONID=" + sessionId);
                     //   connection.connect();
                        OutputStream os = connection.getOutputStream();
                        System.out.println("writing files to "+ connection.getURL());
                        requestEntity.writeRequest(os);
                        os.flush();
                        os.close();
                        int response=connection.getResponseCode();
                        System.out.println("response :" +connection.getResponseMessage() + "( "+ response+")");
                        if(connection != null) {
                            connection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        uploader.start();

        try {
            venue.updateData(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
*/
        return true;
    }

    public boolean updateData(String uri, String dataID, String name, String description, String expires) {
        Venue venue = venues.get(uri);
        DataDescription dataItem = new DataDescription();
        dataItem.setId(dataID);
        Vector<DataDescription> items = venueStates.get(uri).getData();
        DataDescription data = items.get(items.indexOf(dataItem));
        data.changeName(name);
        data.setDescription(description);
        data.setExpires(expires);
        try {
            venue.updateData(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateData(String venueUri, String uri, String fileName,
            String parentId, String description, String expiry, long fileSize) {
        Venue venue = venues.get(venueUri);
           try {
            VenueState venueState = venue.getState();
            DataDescription data = new DataDescription();
            boolean found = false;
            log.info("searching: " + uri + " " + fileName);
            for (DataDescription dataDesc : venueState.getData()) {
                log.info("data: " + dataDesc.getId() + " " + dataDesc.getName() + "\n" + dataDesc.getUri());
                if (dataDesc.getName().equals(fileName)) {
                    data = dataDesc;
                    found = true;
                    break;
                }
            }
            if (!found) {
                data.setId(Utils.generateID());
            }
            data.setObjectType(DataDescription.TYPE_FILE);
            data.setName(fileName);
            data.setParentId(parentId);
            data.setDescription(description);
            data.setExpires(expiry);
            data.setSize("" + fileSize);
            venue.updateData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addDir(String venueUri, String name,
            String description, String level, String parentId,  String expiry) throws Exception {
        Venue venue = venues.get(venueUri);
        venue.addDir(name, description, level, parentId);
//        new DataDescription();
    }


    /**
     * Starts the application queued for the applications in the Venue. This is part
     * of the Application Monitor built in to the venueclient.
     * @return The list of Application Descriptions
     * @throws Exception
     */
    public Vector<ApplicationDescription> startApplicationQueue(String uri) throws Exception {
        Vector<ApplicationDescription> appdsc = new Vector<ApplicationDescription>();
        try {
            appdsc = venueStates.get(uri).getApplications();
            for (int i = 0; i < appdsc.size(); i++) {
                log.info("Starting application monitoring for " + appdsc.get(i));
                addApplication(appdsc.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appdsc;
    }

    public Vector<ApplicationDescription> startApplicationQueue() throws Exception {
        return startApplicationQueue(currentVenueUri);
    }


    /**
     * Gets the List of participants of a shared Application
     * @param appUri The URI of the shared Application
     * @param appId The Application Id
     * @return List of Participants
     */
    public Vector<AppParticipantDescription> getApplicationParticipants(String appUri, String appId) {
         Vector<AppParticipantDescription> appPv = new Vector<AppParticipantDescription>();
        try {
            SharedApplication sharedApplication = new SharedApplication(appUri);
            AppParticipantDescription[] appParticipantDescriptions =
                sharedApplication.getParticipants(applicationMonitorPrivateTokens.get(appId));
            for (int i = 0; i < appParticipantDescriptions.length; i++) {
                appParticipantDescriptions[i].setAppId(appId); // should be there in first place
                appPv.add(appParticipantDescriptions[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appPv;
    }

    /**
     * method to upload data into the venue
     * @param filename name of the file to upload
     * @return URI to access the data
     * @throws URISyntaxException
     */
    public URI uploadDataItem(String venueUrl, String parentId, String filename) throws URISyntaxException {
        log.info("Upload file " + filename + " to " + venueUrl);
        log.info("search in " + venues.toString());
        Venue currentVenue = venues.get(venueUrl);
        VenueState currentVenueState = venueStates.get(venueUrl);
        if (!parentId.equals("-1")) {
            DataDescription data = new DataDescription();
            data.setId(parentId);
            Vector<DataDescription> dataDescriptions = currentVenueState.getData();
            int parentIndex = dataDescriptions.indexOf(data);
            data = dataDescriptions.get(parentIndex);
            String fname = data.getUri().replaceFirst(currentVenueState.getDataLocation(), "");
            filename = fname + "/" + filename;
            log.info("Parent File Name: " + fname);
        }
        String connId = venueConnIds.get(venueUrl);
        URI u = null;
        try {
            if (connId == null) {
                connId = currentVenue.enter(clientProfile);
                venueConnIds.put(venueUrl, connId);
                dataConnections.add(connId);
            }
            u = new URI(currentVenue.getUploadDescriptor() + "/" + filename);
        } catch (Exception e) {
            e.printStackTrace();
            u = new URI(currentVenueState.getDataLocation() + "/" + filename);
            log.info("Using Datalocation: " + u);
        }
        URI uri = new URI(u.getScheme(), currentVenueState.getUniqueId() + ":" + connId,
                u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getFragment());
        return uri;
    }

    /**
     * downloads a data item from a venue
     * @param filedesc File description
     * @param isFilename flag if the file description contains
     * the name (true) or the id (false) of the data item
     * @return the DataDescription of the data item
     */
    public DataDescription downloadDataItem(String filedesc, boolean isFilename, String venueUrl) {

        Vector<DataDescription> venueData = new Vector<DataDescription>();
        String venueId = "";
        String connId = null;
        Venue venue = venues.get(venueUrl);
        VenueState venueState = venueStates.get(venueUrl);
        if (venueState != null) {
            connId = venueConnIds.get(venueUrl);
            venueData = venueState.getData();
            venueId = venueState.getUniqueId();
        }
        DataDescription dataItem = null;
        String description = null;
        log.info("search Item: " + filedesc + " fn: " + isFilename);
        for (DataDescription item : venueData) {
            if (isFilename) {
                log.info("testing: " + item.getName());
                description = item.getName();
            } else {
                log.info("testing: " + item.getId());
                description = item.getId();
            }
            if (description.equals(filedesc)) {
                dataItem = item;
                log.info("found: " + item.getName() + " u: " + item.getUri());
                break;
            }
        }
        try {
            URI u = new URI(dataItem.getUri().replaceAll(" ", "%20"));
            log.info("URL:" + u.toString());
            if (connId == null) {
                // we are talking to an AG3 server rather than an OneVRE server
                connId = venue.enter(clientProfile);
                venueConnIds.put(venueUrl, connId);
                dataConnections.add(connId);
            }
            URI uri = new URI(u.getScheme(), venueId + ":" + connId,
                    u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getFragment());
            dataItem.setUri(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataItem;
    }

    public void closeDataConnection(String venueUrl) {
        String connectionId = venueConnIds.get(venueUrl);
        if (dataConnections.contains(connectionId)) {
            Venue venue = venues.get(venueUrl);
            venueConnIds.remove(venueUrl);
            dataConnections.remove(connectionId);
            try {
                venue.exit(connectionId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * deletes a data item from the venue
     * @param dataId the id of the data item
     */
    public void deleteData(String venueUri, String dataId) {

        Vector<DataDescription> venueData = venueStates.get(venueUri).getData();
        DataDescription dataItem = null;
        for (int i = 0; i < venueData.size(); i++) {
            if (venueData.get(i).getId().equals(dataId)) {
                dataItem = venueData.get(i);
                break;
            }
        }
        if (dataItem != null) {
            try {
                venues.get(venueUri).removeData(dataItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * joins a shared application
     * @param applicationId the Id of the application
     * @return the HTML code to be integrated in the ui of the ApplicationMonitor
     */
/*    public String joinApplication(String applicationId) {
        System.out.println("In join Application: " + applicationId);
        System.out.println("VS:"+ currentVenueState);
        Vector<ApplicationDescription> appvect=currentVenueState.getApplications();
        ApplicationDescription application=null;
        SharedAppState appState=null;
        for (int i=0; i<appvect.size(); i++) {
            log.info("Application ["+i+"]: " + appvect.get(i).getId());
            if (applicationId.equals(appvect.get(i).getId())) {
                application=appvect.get(i);
                break;
            }
        }
        log.info("join Application: " + application.getId() + " URI:" + application.getUri());
        String privateToken=null;
        try {
    //        xmlRpcServer.addRequest("ApplicationEvent", new Object[]{application,event});
            SharedApplication app=new SharedApplication(application.getUri());
            HashMap<String, String> ids = app.join(clientProfile);
            privateToken = ids.get("privateId");
            log.debug("private token "+ privateToken);
            HashMap<String, Object> dataChannel= app.getDataChannel(privateToken);
              log.debug("DC: " + dataChannel);
            applicationClients.add(
                new EventClient(
                    new ApplicationEventListener(
                        application,dataChannel.get("address")+":"+dataChannel.get("port"),xmlRpcServer
                    ),
                    ids.get("publicId"),(String)dataChannel.get("channelId")
                )
              );
            log.debug("APT: " + applicationPrivateTokens);
            Vector<String> apt = applicationPrivateTokens.get(application.getId());
            if (apt == null) {
                apt=new Vector<String>();
                applicationPrivateTokens.put(application.getId(),apt);
            }
            apt.add(privateToken);
            appState=app.getState(privateToken);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // start shared application connector
        // register application connector to eventclient
        String venueDataLocation=currentVenueState.getDataLocation();
        xmlRpcServer.addRequest("eventStartApplication",
            new Object[]{application, clientProfile, appState, venueDataLocation});
        String applicationUI="";
        return applicationUI;
    }
*/
    /**
     * expands the Venue tree for a given venue
     * @param venueId the id of the venue to expand
     * @return the VenueTreeItem of the expanded venue
     */
    public VenueTreeItem expandVenue(String venueId) {
        VenueTreeItem venue = venueList.findVenue(venueId);
        if (venue != null) {
            try {
                venue.setExpanded(true);
                if (venue.getConnections() == null) {
                    venue.setConnections(getConnections(venueId));
                } else {
                    // workaround for questionable JS in xmlrpcHandler.js method
                    // this.expandVenueResponse=function(venueXml)
                    VenueTreeItem venuesJs = new VenueTreeItem(venue.getName(), venue.getUri());
                    venuesJs.setExpanded(true);
                    venuesJs.setId(venue.getId());
                    return venuesJs;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
           return venue;
    }

    /**
     * changes the selection mode of the venue:
     * there are currently 3 modes of selecting the venue:
     * <ul>
     * <li>All Venues (1)</li>
     * <li>Exits (2)</li>
     * <li>My Venues (3)</li>
     * </ul>
     * @param mode integer value of the new selection mode
     * @return the list of venues to be shown
     */
    public VenueList changeVenueSelection(String mode) {
        int listMode = Integer.parseInt(mode);
        try {
            setVenueListMode(listMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return venueList;
    };

    /**
     * Collapses the subtree of a venue
     * @param venueId The Id of the venue-subtree to collapse
     * @return The set of venues visited
     */
    public String collapseVenue(String venueId) {
        VenueTreeItem venue = venueList.findVenue(venueId);
        venue.collapse();
        return venueId;
    }

    /**
     * Gets the set of venues visited
     * @return The set of venues visited
     */
    public String getPreviousVenues() {
        Iterator<String> iter = previousVenues.iterator();
        String prevVenues = "";
        while (iter.hasNext()) {
            prevVenues += "\"" + iter.next() + "\"";
            if (iter.hasNext()) {
                prevVenues += ", ";
            }
        }
        prevVenues += "";
        return prevVenues;
    }

    /**
     * Enters the last venue visited
     * @return The venue state
     * @throws Exception
     */
    public VenueState enterPreviousVenue() throws Exception {
        String uri = previousVenues.removeLast();
        VenueState state = enterVenue(uri);
        previousVenues.removeLast();
        return state;
    }

    /**
     * Adds an application to the list of available applications
     * @param application The application description
     */
    public void addApplication(ApplicationDescription application) {
        try {
            SharedApplication app = new SharedApplication(application.getUri());
            HashMap<String, String> ids = app.join(null);
            HashMap<String, Object> dataChannel = app.getDataChannel(ids.get("privateId"));
            applicationClients.add(
                new EventClient(
                    new ApplicationEventListener(
                        application, dataChannel.get("address") + ":" + dataChannel.get("port"), agEventServer
                    ), ids.get("publicId"), (String) dataChannel.get("channelId")
                )
            );
            applicationMonitorPrivateTokens.put(application.getId(), ids.get("privateId"));
            Vector <String> appTokens = new Vector<String>();
            applicationPrivateTokens.put(application.getId(), appTokens);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an application from the list of available applications
     * @param application The application description
     */
    public void removeApplication(ApplicationDescription application) {
        int i;
        try {
            SharedApplication app = new SharedApplication(application.getUri());
            Vector<String> appTokens = applicationPrivateTokens.get(application.getId());
            for (i = 0; i < appTokens.size(); i++) {
                app.leave(appTokens.get(i));
            }
            app.leave(applicationMonitorPrivateTokens.get(application.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (i = 0; i < applicationClients.size(); i++) {
            if (applicationClients.get(i).getListenerId().equals(application.getId()))
            {
                applicationClients.get(i).close();
                break;
            }
        }
        applicationClients.remove(i);
    }

    /**
     * Determines if the current venue is the first one
     *
     * @return True if there is a previous venue, false otherwise
     */
    public boolean isPreviousVenue() {
        return previousVenues.size() > 0;
    }

    /**
     * Exits the current venue
     * @return the new venue state (not in a venue)
     */
    public VenueState exitVenue(String venueUrl)  {
        inVenue = false;
        ClientUpdateThread clientUpdateThread = clientUpdateThreads.get(venueUrl);
        Venue currentVenue = venues.get(venueUrl);
        VenueState currentVenueState = venueStates.get(venueUrl);
        String connectionId = venueConnIds.get(venueUrl);
        EventClient eventClient = venueEventClients.get(venueUrl);
        if (clientUpdateThread != null) {
            clientUpdateThread.close();
        }
        if (currentVenue != null) {
            if (currentVenueState != null) {
                Defaults.writeLog("Exit Venue: " + currentVenueState.toLog() + "; Client: " + clientProfile.toLog());
            }
            if (currentVenueUri != null) {
                previousVenues.addLast(currentVenueUri);
            }
            try {
                currentVenue.exit(connectionId);
            } catch (IOException e) {
                // Do Nothing -
                //     this seems to be thrown even though it works!
            } catch (SoapException e) {
                // Do Nothing -
                //     this happens using AG 3.1
            }
        }
        connectionId = null;
        try {
            venueList = new VenueList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentVenueState = null;
        currentVenue = null;
        currentVenueUri = clientProfile.getHomeVenue();
        currentVenueName = NOT_IN_VENUE_STRING;
        currentStreams = new StreamDescription[0];
        agEventServer.setListener(null);
        try {
            JabberClient jc = jabberClients.get(venueUrl);
            if (jc != null) {
                jc.close();
            }
        } catch (Exception e) {
            // Do Nothing -
            //     this seems to be thrown even though it works!
        }
        try {
            if (eventClient != null) {
                eventClient.close();
                for (EventClient applicationClient : applicationClients) {
                    SharedApplication app = new SharedApplication(applicationClient.getListenerUri());
                    for (String appToken : applicationPrivateTokens.get(applicationClient.getListenerId())) {
                        app.leave(appToken);
                    }
                    app.leave(applicationMonitorPrivateTokens.get(applicationClient.getListenerId()));
                    applicationClient.close();
                }
            }
        } catch (Exception e) {
            // Do Nothing -
            //     this seems to be thrown even though it works!
        }
        VenueState venueState = new VenueState();
        venueState.setVenueList(venueList);
        venueState.setName("You are not in a venue");
        venueState.setUri(clientProfile.getHomeVenue());
        return venueState;
    }

    public VenueState exitVenue()  {
        return exitVenue(currentVenueUri);
    }

    /**
     * Determines if the user is in a venue
     * @return True if the user is in a venue, false otherwise
     */
    public boolean isInVenue() {
        return inVenue;
    }

    /**
     * Gets the current list of venues
     * @return The list of venues
     */
    public VenueList getVenueList() {
        return venueList;
    }

    /**
     * Gets the current venue uri
     * @return The uri of the current venue or the home venue if not in one
     */
    public String getCurrentVenueUri() {
        return currentVenueUri;
    }

    /**
     * Gets the current venue name
     * @return The name of the current venue or a default string if not in one
     */
    public String getCurrentVenueName() {
        return currentVenueName;
    }

    /**
     * Gets the profile of the current client
     * @return The current client's profile
     */
    public Vector<VenueServerType> getTrustedServers() {
        log.info("in VenueClientUI.getTrustedServers()");
        return trustedServers;
    }

    public Vector<VOAttribute> getVoAttributes() {
        Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();
        if (credential != null) {
            log.info("getting VO attributes for credential:" + credential.getDN());
            voAttributes = credential.getVoAttributes();
            for (VOAttribute attr : voAttributes) {
                log.info("Attribute: " + attr.toString());
            }
        } else {
            log.info("no credential set");
        }
        return voAttributes;
    }

    public VenueServerType addTrustedServer(String name, String url) {
        VenueServerType venueServer = null;
        try {
            venueServer = getVenueServer(name, url);
            log.info("Add trusted Server:" + venueServer.toLog());
        } catch (Exception e) {
            log.error("Server : " + name + " = " + url + " is not responding ");
            e.printStackTrace();
        }
        return venueServer;
    }

    public ConnectionDescription createVenue(String serverUri, String name, String description,
            Vector<HashMap<String, String>> attributes) {
        log.info("Create New Venue:");
        log.info("Server :" + serverUri);
        log.info("Name: " + name);
        log.info("VOattributes:  " + attributes);
        Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();
        for (HashMap<String, String> map : attributes) {
            voAttributes.add(new VOAttribute(map));
        }
        ConnectionDescription result = null;

        try {
            URL url = new URL(serverUri);
            String serverUrl = url.getProtocol() + "://" + url.getHost()
            + ":" + url.getPort() + "/VenueServer";
            VenueServer venueServer = new VenueServer(serverUrl);
            result = venueServer.createVenues(name, description, clientProfile,
                    voAttributes.toArray(new VOAttribute[]{}));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Vector<String> getVenues(String serverUri, Vector<HashMap<String, String>> attributes) {
        Vector <String> urls = new Vector<String>();
        try {
            VenueServer venueServer = new VenueServer(serverUri);
            Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();
            for (HashMap<String, String> map : attributes) {
                voAttributes.add(new VOAttribute(map));
            }
            ConnectionDescription [] conns = venueServer.getVenues(voAttributes.toArray(new VOAttribute[0]));
            for (ConnectionDescription conn : conns) {
                urls.add(conn.getUri());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SoapException e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Gets the profile of the current client
     * @return The current client's profile
     */
    public ClientProfile getClientProfile() {
        log.info("in VenueClientUI.getClientProfile()");
        return clientProfile;
    }

    /**
     * Changes the current client profile
     * @param clientProfile The new profile
     */
    public void setClientProfile(ClientProfile clientProfile) {
        if (!this.clientProfile.getName().equals(clientProfile.getName())) {
            updateJabber = true;
        }
        this.clientProfile = clientProfile;
        if (!isInVenue()) {
            currentVenueUri = clientProfile.getHomeVenue();
        } else {
            try {
                Venue currentVenue = venues.get(currentVenueUri);
                currentVenue.updateClientProfile(clientProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (updateJabber) {
                for (JabberClient jabber : jabberClients.values()) {
                    try {
                        jabber.setNickname(clientProfile.getName());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
                updateJabber = false;
            }
        }

    }

    /**
     * Changes the current client profile
     * @param name The new name to set
     * @param email The new e-mail to set
     * @param phone The new phone number to set
     * @param location The new location to set
     * @param home The new home venue to set
     * @param type The new profile type to set
     * @return The client profile
     */
    public ClientProfile setClientProfile(String name, String email,
            String phone, String location, String home, String type) {
        if (!clientProfile.getName().equals(name)) {
            updateJabber = true;
        }
        clientProfile.setName(name);
        clientProfile.setEmail(email);
        clientProfile.setPhoneNumber(phone);
        clientProfile.setLocation(location);
        clientProfile.setHomeVenue(home);
        clientProfile.setProfileType(type);
        setClientProfile(clientProfile);
        return clientProfile;
   }

    /**
     * Returns the current set of users in the venue
     * @return An array of ClientProfile instances, one for each user
     */
    public String getCurrentVenueState(String venueURL) {

        VenueState venueState = venueStates.get(venueURL);
        if (venueState == null) {
            venueState = new VenueState();
            venueState.setVenueList(venueList);
            venueState.setName("You are not in a venue");
            venueState.setUri(clientProfile.getHomeVenue());
        }
        return Utils.escapeXmlRpcValue(XMLSerializer.serialize(venueState));
    }

    /**
     * Sets the current venue list mode
     * @param mode The new mode
     * @throws IOException
     * @throws SoapException
     */
    public void setVenueListMode(int mode) throws IOException, SoapException {
///
        Vector<VenueTreeItem> roots = new Vector<VenueTreeItem>();

        if (mode == VenueList.MODE_ALL_VENUES) {
            if (currentVenueUri != null) {
                URL url = new URL(currentVenueUri);
                String serverUrl = url.getProtocol() + "://" + url.getHost()
                    + ":" + url.getPort() + "/VenueServer";
                VenueServer venueServer = new VenueServer(serverUrl);
                ConnectionDescription[] connections = venueServer.getVenues(null);
                for (int i = 0; i < connections.length; i++) {
                    VenueTreeItem item = new VenueTreeItem(connections[i].getName(),
                            connections[i].getUri());
                    roots.add(item);
                    venueList.addVenue(item);
                }
                Collections.sort(roots);
            }
        } else if (mode == VenueList.MODE_EXITS) {
            roots.clear();
            if ((currentVenueUri != null) && (currentVenueName != null)) {
                VenueTreeItem item = new VenueTreeItem(currentVenueName, currentVenueUri);
                roots.add(item);
                item.setExpanded(true);
                venueList.addVenue(item);
                item.setConnections(getConnections(item.getId()));
            }
      } else if (mode == VenueList.MODE_MY_VENUES) {
            Iterator<String> iter = myVenues.keySet().iterator();
            roots.clear();
            while (iter.hasNext()) {
                String url = (String) iter.next();
                VenueTreeItem item = myVenues.get(url);
                roots.add(item);
                venueList.addVenue(item);
            }
            Collections.sort(roots);
        }

        if (venueList != null) {
            venueList.selectMode(mode, roots);
        }


    }

    /**
     * Gets the current mode of the venue list
     * @return The venue list mode
     */
    public int getVenueListMode() {
        return venueList.getMode();
    }

    /**
     * Gets the current connection id
     * @return The current connection id
     */
/*    public String getConnectionId() {
        return connectionId;
    }
*/

    /**
     * Gets the current jabberClient
     * @return The current jabberClient
     */
    public JabberClient getJabberClient(String venueUri) {
        return jabberClients.get(venueUri);
    }

    public JabberClient getJabberClient() {
        return jabberClient;
    }

    /**
     * Gets the list of services
     * @return The list of services
     */
    public Hashtable<String, String> getServicesList() {
        return (new ServicesList()).getServicesList();
    }

    /**
     * Gets the current eventClient
     * @return The current eventClient
     */
    public EventClient getEventClient(String venueUrl) {
        return venueEventClients.get(venueUrl);
    }

    public EventClient getEventClient() {
        return getEventClient(currentVenueUri);
    }

    /**
     * Negotiates the capabilities
     * @param cap The capabilities
     * @return The streams
     * @throws IOException
     * @throws SAXException
     */
    public StreamDescription[] negotiateCapabilities(String cap, String venueUrl)
        throws IOException, SAXException {
        Capability[] deserialize = (Capability[])
            XMLDeserializer.deserialize(StringEscapeUtils.unescapeXml(cap));
        return negotiateCapabilities(deserialize, venueUrl);
    }

    public StreamDescription[] negotiateCapabilities(String cap)
        throws IOException, SAXException {
        return negotiateCapabilities(cap, currentVenueUri);
    }

    public StreamDescription[] negotiateCapabilities(Capability[] cap) {
        return negotiateCapabilities(cap, currentVenueUri);
    }

    /**
     * negotiates the capabilies with services
     * @param cap vector of Capabilities from service
     * @return list with StreamDescriptions
     */
    public StreamDescription[] negotiateCapabilities(Capability[] cap, String venueUrl) {
        Venue currentVenue = venues.get(venueUrl);
        String connectionId = venueConnIds.get(venueUrl);
        if (currentVenue != null) {
            try {
                StreamDescription[] streams =
                    currentVenue.negotiateCapabilities(
                        connectionId, cap);
                currentStreams = streams;
                return streams;
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SoapException e) {
                e.printStackTrace();
            }
        }
        return new StreamDescription[0];
    }

    /**
     * Sends a jabber message
     * @param message The message to send
     * @return true if the message was sent or false otherwise
     */
    public Boolean sendJabberMessage(String venueUri, String message) {
        log.info("sendJabberMessage(" + venueUri + ", " + message + ")");
           log.info("jabberClients : " + jabberClients);

        JabberClient jabber = jabberClients.get(venueUri);
        if (jabber != null) {
            jabber.setMessage(message);
            return true;
        }
        return false;
    }

    public Boolean sendJabberMessage(String message) {
        if (jabberClient != null) {
            jabberClient.setMessage(message);
            return true;
        }
        return false;
    }

    /**
     * Sets the status
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the status
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    // Class to exit the venue when the web server is stopped
    private class DoShutdown extends Thread {

        private VenueClientUI ui = null;

        private DoShutdown(VenueClientUI ui) {
            this.ui = ui;
        }

        /**
         *
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                ui.exitVenue();
            } catch (Exception e) {
                // Do Nothing
            }
        }
    }

    /**
     * Indicates that the object has timed out
     * <dl><dt><b>overrides:</b></dt><dd>{@link pag.xmlrpc.TimeoutListener#timedOut()}</dd></dl>
     */
    public void timedOut() {
        log.error("Timed out");
        try {
            exitVenue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current streams negotiated in the venue
     * @return The current set of streams in the venue
     */
    public StreamDescription[] getCurrentStreams() {
        return currentStreams;
    }

    /**
     *
     * @see javax.servlet.http.HttpSessionBindingListener#valueBound(
     *     javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueBound(HttpSessionBindingEvent event) {
        log.info("VenueClientUI Bound to Session");
    }

    /**
     *
     * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(
     *     javax.servlet.http.HttpSessionBindingEvent)
     */
    public void valueUnbound(HttpSessionBindingEvent arg0) {
        log.info("VenueClientUI Unbound from Session - leaving");
        try {
            exitVenue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a set of bridges from a set of registries
     * @param registryUrlsXml The urls of the registries
     * @return The bridges
     * @throws IOException
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    public Vector<BridgeDescription> getNewBridges(String registryUrlsXml)
            throws IOException, SAXException {
        Vector<String> registryUrls = (Vector<String>)
            XMLDeserializer.deserialize(registryUrlsXml);
        Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();
        for (int i = 0; i < registryUrls.size(); i++) {
            RegistryClient registry = new RegistryClient(registryUrls.get(i));
            bridges.addAll(registry.lookupBridges());
        }
        return bridges;
    }

    /**
     * Sets the position of the venue list scroll bars
     *
     * @param verticalScroll The vertical scroll position
     * @param horizontalScroll The horizontal scroll position
     */
    public void setVenueListScroll(String verticalScroll,
            String horizontalScroll) {
        venueList.setVerticalScrollPosition(Integer.parseInt(verticalScroll));
        venueList.setHorizontalScrollPosition(Integer.parseInt(
                horizontalScroll));
    }

    public Vector<VenueTreeItem> getVenueTree(String venueUri) throws  IOException, SoapException {
        log.info("in GetVenueTree ( " + venueUri + ")");
        Venue venue = new Venue(venueUri, isOneVREVenue(venueUri));
           venue.setGSScredential(credential);
        ConnectionDescription[] connections = venue.getConnections();
        Vector<VenueTreeItem> conns = new Vector<VenueTreeItem>();
        for (ConnectionDescription connection : connections) {
            try {
                log.info("found connection: " + connection.toString());
                URL url = new URL(new URL(venueUri), connection.getUri());
                VenueTreeItem item = new VenueTreeItem(
                        connection.getName(), url.toString());
                conns.add(item);
            } catch (MalformedURLException e) {
                log.error("Error parsing "
                        + connection.getName());
                e.printStackTrace();
            }
        }
        Collections.sort(conns);
        return conns;
    }

    /**
     * Gets the connections from a venue
     * @param venueId The venue id
     * @return The connections
     * @throws IOException
     * @throws SoapException
     */
    private Vector<VenueTreeItem> getConnections(String venueId)
            throws  IOException, SoapException  {
        VenueTreeItem venueTreeItem = venueList.findVenue(venueId);
        if (venueTreeItem == null) {

            log.error("no venueTreeItem for Id: " + venueId + "\n" + venueList.toString());

        }
        Venue venue = new Venue(venueTreeItem.getUri(), isOneVREVenue(venueTreeItem.getUri()));

        venue.setGSScredential(credential);
        ConnectionDescription[] connections = venue.getConnections();
        Vector<VenueTreeItem> conns = new Vector<VenueTreeItem>();
        log.info("get connections for id " + venueId);
        for (ConnectionDescription connection : connections) {
            try {
                URL url = new URL(new URL(venueTreeItem.getUri()),
                        connection.getUri());
               log.info(connection.getName() + " - " + url.toString());
               VenueTreeItem item = new VenueTreeItem(
                        connection.getName(), url.toString());
                conns.add(item);
                venueList.addVenue(item);
            } catch (MalformedURLException e) {
                log.error("Error parsing "
                        + connection.getName());
                e.printStackTrace();
            }
        }
        Collections.sort(conns);

        return conns;
    }

    public void setCredential(CredentialMappings credentialMappings) {
        this.credential = credentialMappings;
    }

}
