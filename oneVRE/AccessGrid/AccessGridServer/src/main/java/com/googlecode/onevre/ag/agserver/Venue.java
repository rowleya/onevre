/*
 * @(#)Venue.java
 * Created: 19-May-2009
 * Version: 1.0
 * Copyright (c) 2005-2009, University of Manchester All rights reserved.
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;


import com.googlecode.onevre.ag.agclient.venue.GroupClient;
import com.googlecode.onevre.ag.common.Event;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VenueDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.ag.types.server.AGNetworkServicesManager;
import com.googlecode.onevre.ag.types.server.AGStreamDescriptionList;
import com.googlecode.onevre.ag.types.service.AGNetworkServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceManagerDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Utils;

/**
 * The Venue implements the full Access Grid Venue.
 * This is the place where all the services a AG venue provides are registered.
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class Venue extends SoapServable {

    private Vector<AGNetworkServiceDescription> networkServices = new Vector<AGNetworkServiceDescription>();

    public static final long MAX_TIMEOUT = 30;

    public static final int DEFAULT_TTL = 127;

    public static final int evenPortFlag = Utils.PAG_PORT_EVEN;

    private HashMap<String, VenueClientState> clients = new HashMap<String, VenueClientState>();

    private PrintWriter venueServerLog = null;

    private VenueState venueState = null;

    private AGNetworkServicesManager networkServicesManager = new AGNetworkServicesManager();

    private HashMap<String,ClientProfile> monitoringClients = new HashMap<String, ClientProfile>();

    private VenueEventServer eventServer= null;

    private GroupClient eventClient = null;

    // The system node config directory
    private String sysNodeConfigDir = null;

    // The uri of the node service
    private String uri = "";

    // The current client profile
    private ClientProfile clientProfile = null;

    // The current set of services
    private HashMap<String, AGServicePackageDescription> allServices =
        new HashMap<String, AGServicePackageDescription>();

    private Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();

    private BridgeDescription currentBridge = null;

    private AGStreamDescriptionList streamList = new AGStreamDescriptionList();

    private String encryption = null;

    private String pointOfReference = null;

    private String dataLocation;

    private int dataPort;

    private DataStore dataStore = null;
/*
    static {
//    	ADD_METHOD_RESULT("RegisterNetworkService", "", "");
//    	ADD_METHOD_RESULT("UnRegisterNetworkService", "", "");
        // no result: Exit
        // no result: DestroyApplication
        // no result: RecycleMulticastLocation
        ADD_METHOD_RESULT("addNetworkLocationToStream", "id", STRING_TYPE);
        // no result: Shutdown
        ADD_METHOD_RESULT("regenerateEncryptionKeys", "key", STRING_TYPE);
        // no result: ImportPolicy
        ADD_METHOD_RESULT("exportPolicy", "policy", COMPLEX_TYPE);
//      ADD_METHOD_RESULT("setConnections", "", "");
        ADD_METHOD_RESULT("getConnections", "connections", COMPLEX_TYPE);
        ADD_METHOD_RESULT("getUploadDescriptor", "uploadUrl", STRING_TYPE);
        ADD_METHOD_RESULT("updateData", "dataDesc", COMPLEX_TYPE);
        ADD_METHOD_RESULT("removeData", "dataDesc", COMPLEX_TYPE);
        ADD_METHOD_RESULT("getDataDescriptions", "dataDescriptions", COMPLEX_TYPE);
        ADD_METHOD_RESULT("addService", "serviceDesc", COMPLEX_TYPE);
        ADD_METHOD_RESULT("removeService", "serviceDesc", COMPLEX_TYPE);
        ADD_METHOD_RESULT("updateService", "serviceDesc", COMPLEX_TYPE);
//        ADD_METHOD_RESULT("UpdateClientProfile", "", "");
        ADD_METHOD_RESULT("getDataStoreInformation", "dataStoreInformation", STRING_TYPE);
        ADD_METHOD_RESULT("addDir", "data", COMPLEX_TYPE);
        ADD_METHOD_RESULT("getVersion", "version", STRING_TYPE);
        ADD_METHOD_RESULT("getStreams", "streams", COMPLEX_TYPE);
//        ADD_METHOD_RESULT("HDDump", "", "");
        ADD_METHOD_RESULT("getDescById", "DirectoryDescription", COMPLEX_TYPE);
//        ADD_METHOD_RESULT("removeDir", "", "");
        ADD_METHOD_RESULT("getDataSize", "size", INT_TYPE);
    }
*/
    static {
        SoapDeserializer.mapType(ClientProfile.class);
//        SoapDeserializer.mapType("http://www.accessgrid.org/v3.0/venue/clientProfile",ClientProfile.class);
        SoapDeserializer.mapType(VenueState.class);
        SoapDeserializer.mapType(MulticastNetworkLocation.class);
        SoapDeserializer.mapType(AGNetworkServiceDescription.class);
        SoapDeserializer.mapType(VenueDescription.class);
        SoapDeserializer.mapType(Capability.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(ServiceDescription.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(EventDescription.class);
        SoapDeserializer.mapType(AGServicePackageDescription.class);
        SoapDeserializer.mapType(AGServiceDescription.class);
        SoapDeserializer.mapType(AGServiceManagerDescription.class);
        SoapDeserializer.mapType(DataDescription.class);
/*        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(TextParameter.class);
        SoapDeserializer.mapType(OptionSetParameter.class);
        SoapDeserializer.mapType(RangeParameter.class);
*/        SoapDeserializer.mapType(BridgeDescription.class);
    }


    /**
     * Creates a new AGNodeService
     * @param venueName
     * @param venueDescription
     * @param eventServer
     * @param log The services that are available to this node service
     */
    public Venue(String venueId, HashMap<String,HashMap<String, String>>venuesData, VenueEventServer eventServer, DataStore dataStore, PrintWriter log) {
        this.venueServerLog=log;
        System.out.println("Venue Server: Create Venue");
        this.venueState = new VenueState();
        venueState.setName(ConfigFile.getParameter(venuesData, venueId, "name", ""));
        venueState.setUniqueId(venueId);
        venueState.setDescription(ConfigFile.getParameter(venuesData, venueId, "description", ""));
//        venueState.setConnections(connection)
        this.eventServer = eventServer;
        venueState.setEventLocation(eventServer.getLocation());
        String streams = ConfigFile.getParameter(venuesData, venueId, "streams", "");
        for (String streamId: streams.split(":")){
            StreamDescription streamDescription = getStreamDescription(streamId,venuesData);
            if (streamDescription != null){
                streamList.addStream(streamDescription);
            }
        }
        String applications = ConfigFile.getParameter(venuesData, venueId, "applications", "");
        for (String applicationId : applications.split(":")){
            addApplication(applicationId,venuesData);
        }
        String services = ConfigFile.getParameter(venuesData, venueId, "services","");
        for (String serviceId : services.split(":")){
            ServiceDescription serviceDescription = getServiceDescription(serviceId,venuesData);
            if (serviceDescription!=null){
                addService(serviceDescription);
            }
        }
        this.dataStore = dataStore;
        dataStore.addVenue(venueId,this);
    }

    private ServiceDescription getServiceDescription(String serviceId, HashMap<String, HashMap<String, String>> venuesData) {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setId(Utils.generateID());
        String name = ConfigFile.getParameter(venuesData, serviceId, "name", "");
        if ((name==null) || name.trim().equals("")){
            return null;
        }
        serviceDescription.setName(name);
        String description = ConfigFile.getParameter(venuesData, serviceId, "description", "");
        if (description==null) {
            description ="";
        }
        serviceDescription.setDescription(description);
        String mimeType = ConfigFile.getParameter(venuesData, serviceId, "mimeType", "");
        if ((mimeType==null) || mimeType.trim().equals("")){
            return null;
        }
        String uri = ConfigFile.getParameter(venuesData, serviceId, "uri", "");
        if ((uri==null) || uri.trim().equals("")){
            return null;
        }
        serviceDescription.setUri(uri);
        return serviceDescription;
    }

    private void addApplication(String applicationId, HashMap<String, HashMap<String, String>> venuesData) {


    }

    public StreamDescription getStreamDescription(String streamId,HashMap<String,HashMap<String, String>> venuesData){
        StreamDescription stream = new StreamDescription();
        stream.setId(streamId);
        int encryptionFlag = Integer.valueOf(ConfigFile.getParameter(venuesData, streamId, "encryptionFlag", "0"));
        stream.setEncryptionFlag(encryptionFlag);
        stream.setEncryptionKey(ConfigFile.getParameter(venuesData, streamId, "encryptionKey", null));
        stream.setName(ConfigFile.getParameter(venuesData, streamId, "name", ""));
        String capString = ConfigFile.getParameter(venuesData, streamId, "capability", "");
        Vector<Capability> capabilities = VenueServerConfigParameters.defaultCapablities.get(capString);
        if (capabilities==null){
            return null;
        }
        stream.setCapabilities(capabilities);
        NetworkLocation networkLocation =  getNetworkLocation(
                ConfigFile.getParameter(venuesData, streamId, "location", "").split(" "));
        if (networkLocation == null){
            return null;
        }
        stream.setLocation(networkLocation);
        return stream;
    }


    public NetworkLocation getNetworkLocation(String[] locString){
        NetworkLocation networkLocation=null;
        if (locString.length<3){
            return null;
        }
        String locationType = locString[0];
        String host = locString[1];
        int port = Integer.valueOf(locString[2]);
        if (MulticastNetworkLocation.TYPE.equals(locationType.trim())){
            if (locString.length<4){
                return null;
            }
            int ttl = Integer.valueOf(locString[3]);
            networkLocation = new MulticastNetworkLocation();
            ((MulticastNetworkLocation) networkLocation).setTtl(ttl);
        } else {
            networkLocation = new UnicastNetworkLocation();
        }
        networkLocation.setHost(host);
        networkLocation.setPort(port);
        return networkLocation;
    }

    public void setConnections(String[] connections, HashMap<String,HashMap<String, String>>venuesData, String importUri){
        for (String conn:connections){
            ConnectionDescription connectionDescription = new ConnectionDescription();
            connectionDescription.setId(conn);
            connectionDescription.setName(ConfigFile.getParameter(venuesData, conn, "name", ""));
            connectionDescription.setDescription(ConfigFile.getParameter(venuesData, conn, "description", ""));
            String uri= ConfigFile.getParameter(venuesData, conn, "uri", "");
            if (uri.startsWith(importUri)){
                uri = uri.substring(uri.lastIndexOf("/")+1);
            }
            connectionDescription.setUri(uri);
            venueState.setConnections(connectionDescription);
        }
    }


    /**
     * Enters a venue
     * @param clientProfile clientProfile of the joining client
     * @return the connectionId that joined the venue
     */
    @SoapReturn (
        name = "connectionId"
    )
    public String enter (
            @SoapParameter("clientProfile") ClientProfile clientProfile){
        String connectionId = Utils.generateID();
        System.out.println("Venue Server enter Venue " + clientProfile.toLog());
        clientProfile.setConnectionId(connectionId);
        venueServerLog.println("\"Enter\", \"" + clientProfile.getDistinguishedName()+ "\", \"" + venueState.getName() +  "\", \""+ venueState.getUniqueId() +"\"");
        venueServerLog.flush();
        clients.put(connectionId, new VenueClientState(this, MAX_TIMEOUT, clientProfile));
        venueState.setClients(clientProfile);
        sendEvent(Event.ENTER, clientProfile);
        System.out.println("Venue Server enter Venue return: " + connectionId);
        dataStore.addUser(venueState.getUniqueId(), connectionId);
        return connectionId;
    }

    /**
     * Enters a venue
     * @param clientProfile clientProfile of the joining client
     * @return the connectionId that joined the venue
     */
    @SoapReturn (
        name = "connectionId"
    )
    public String monitor (
            @SoapParameter("clientProfile") ClientProfile clientProfile){
        String connectionId = Utils.generateID();
        System.out.println("Venue Server monitor Venue " + clientProfile.toLog());
        clientProfile.setConnectionId(connectionId);
        venueServerLog.println("\"Monitor\", \"" + clientProfile.getDistinguishedName()+ "\", \"" + venueState.getName() +  "\", \""+ venueState.getUniqueId() +"\"");
        venueServerLog.flush();
        clients.put(connectionId, new VenueClientState(this, MAX_TIMEOUT, clientProfile));
        monitoringClients.put(connectionId, clientProfile);
        sendEvent(Event.MONITOR, clientProfile);
        System.out.println("Venue Server enter Venue return: " + connectionId);
        dataStore.addUser(venueState.getUniqueId(), connectionId);
        return connectionId;
    }

    public void addData( @SoapParameter("dataDescription") DataDescription dataDescription){
        venueState.setData(dataDescription);
        dataStore.storeDescription(venueState.getUniqueId(), dataDescription.getName(), dataDescription);
        sendEvent(Event.ADD_DATA, dataDescription);
    }

    private void sendEvent(String eventType, Object eventData){
        EventDescription event = new EventDescription();
        event.setEventType(eventType);
        event.setData(eventData);
        event.setChannelId(venueState.getUniqueId());
        event.setSenderId(venueState.getUniqueId());
        eventServer.addEvent(event,venueState.getUniqueId());
    }

    /**
     * @param connectionId The id of the client connection with
     * @param requestedTimeout
     * @return The timeout until the next heart beat is expected
     */
    @SoapReturn (
        name = "timeout"
    )
    public float updateLifetime(
            @SoapParameter("connectionId") String connectionId,
            @SoapParameter("requestedTimeout")  float requestedTimeout){
        VenueClientState vcs = clients.get(connectionId);
        if (vcs!=null){
            vcs.setTimeout(new Date().getTime()+MAX_TIMEOUT);
            float clientNextHeartbeat = ((Long)MAX_TIMEOUT).floatValue() * (float)0.3;
            return clientNextHeartbeat;
        }
        clients.put(connectionId, new VenueClientState(this, MAX_TIMEOUT, clientProfile));
        for (String connId: clients.keySet()){
        	vcs = clients.get(connId);
        	if (vcs.hasTimedOut()){
        		exit(connId);
        		leaveMonitoringClient(vcs.getClientProfile());
        	}
        }
        return -1;
    }

    /**
     * Exits a venue
     * @param connectionId connectionId of the connection to terminate
     */
    public void exit (
            @SoapParameter("connectionId") String connectionId) {
        VenueClientState vcstate = clients.remove(connectionId);
        monitoringClients.remove(connectionId);
        if (vcstate==null){
            venueServerLog.println("User not in Venue");
            venueServerLog.flush();
        } else {
            sendEvent(Event.EXIT, vcstate.getClientProfile());
            venueState.removeClient(vcstate.getClientProfile());
            venueServerLog.println("\"Exit\", \"" + vcstate.getClientProfile().getDistinguishedName()+ "\", \"" + venueState.getName() +  "\", \""+ venueState.getUniqueId() +"\"");
            venueServerLog.flush();
            dataStore.delUser(venueState.getUniqueId(), connectionId);
        }
    }

    /**
     * Registers a network service with the venue.
     *
     * @param netDescription The network service description of the service to register
     */
    public void registerNetworkService(@SoapParameter("netDescription") AGNetworkServiceDescription netDescription){
        if (!networkServices.contains(netDescription)){
            networkServices.add(netDescription);
        }
    }

    /**
     * Removes a network service from the venue
     *
     * @param netDescription The network service description to remove
     */
    public void unRegisterNetworkService(
            @SoapParameter("netDescription") AGNetworkServiceDescription netDescription){
        networkServices.remove(netDescription);
    }

    /**
     * @return The state of the Venue
     */

    @SoapReturn (
		name = "version"
    )
    public String getVersion(){
        return VenueServerDefaults.serverVersion;
    }

    /**
     * @return The state of the Venue
     */
    @SoapReturn (
        name = "state"
    )
    public VenueState getState(){
        return venueState;
    }

    /**
     * This creates a Venue Description filled in with the data from this venue.
     * @return the venueDescription
     */
    @SoapReturn (
        name = "venueDescription"
    )
    public VenueDescription asVenueDescription(){
        int enc=0;
        if (encryption!=null){
            enc=1;
        }
        VenueDescription venueDescription = new VenueDescription(venueState.getUniqueId(),
                venueState.getName(), venueState.getDescription(),
                enc, encryption, venueState.getConnections(), streamList.getStaticStreams());
        venueDescription.setUri(venueState.getUri());
        return venueDescription;
    }

    private StreamDescription getMatchingStream(Capability[] capabilities){
        Vector<Capability> serviceProducerCaps = new Vector<Capability>();
        Vector<Capability> serviceConsumerCaps = new Vector<Capability>();
        for (Capability c: capabilities){
            if (c.getRole().equals(Capability.PRODUCER))
                serviceProducerCaps.add(c);
            if (c.getRole().equals(Capability.CONSUMER))
                serviceConsumerCaps.add(c);
        }
        Vector<StreamDescription> matchingStreams = new Vector<StreamDescription>();
        for (StreamDescription s : streamList.getStreams()){
            if (s.getCapability().firstElement().getType().equals(capabilities[0].getType())){
                Vector<Capability> streamProducerCaps=new Vector<Capability>();
                Vector<Capability> streamConsumerCaps=new Vector<Capability>();
                for (Capability c : s.getCapability()){
                    if (c.getRole().equals(Capability.PRODUCER)) {
                        streamProducerCaps.add(c);
                    }
                    if (c.getRole().equals(Capability.CONSUMER)) {
                        streamConsumerCaps.add(c);
                    }
                }
                boolean streamConsumerMatch = false;
                boolean serviceConsumerMatch = false;

                // Compare stream producer capabilities to
                // service consumer capabilities
                for (Capability cap : streamProducerCaps){
                    for (Capability ccap : serviceConsumerCaps){
                        if (ccap.matches(cap)){
                            serviceConsumerMatch = true;
                        }
                    }
                }
                if ((streamProducerCaps.size()==0)||(serviceConsumerCaps.size()==0)){
                    serviceConsumerMatch = true;
                }

                // Compare service producer capabilities
                // to stream consumer capabilities
                for (Capability cap : serviceProducerCaps){
                    for (Capability ccap : streamConsumerCaps){
                        if (ccap.matches(cap)){
                            streamConsumerMatch = true;
                        }
                    }
                }
                if ((serviceProducerCaps.size()==0)||(streamConsumerCaps.size()==0)){
                    streamConsumerMatch = true;
                }

                if (serviceConsumerMatch && streamConsumerMatch){
                    matchingStreams.add(s);
                }
            }
        }
        if (matchingStreams.size()>0){
            return matchingStreams.firstElement();
        }
        return null;
    }

    private void addCapabilitiesToStream(StreamDescription stream, Capability[] capabilities){
        for (Capability cap: capabilities){
            boolean match = false;
            for (Capability c: stream.getCapability()){
                if (c.matches(cap)){
                    match = true;
                }
            }
            if (!match){
                stream.setCapability(cap);
            }
        }
    }

    /**
     * This method takes node capabilities and finds a set of streams that
     * matches those capabilities.  This method uses the network services
     *  manager to find The Best Match of all the network services, the
     *  existing streams, and all the client capabilities.
     *
     * @param connectionId Private id for the node
     * @param capabilities Node capabilities
     * @return A vector of stream descriptions
     */
     @SoapReturn (
         name = "streams"
     )
     public StreamDescription[] negotiateCapabilities(
             @SoapParameter("connectionId") String connectionId,
             @SoapParameter("capabilities") Capability[] capabilities){

         Vector<StreamDescription> streams = null;
         Vector<Capability> producers = new Vector<Capability>();
         VenueClientState clientState = clients.get(connectionId);
         if (clientState==null){
             throw new RuntimeException("Client Not found");
         }
         HashMap <String,Vector<Capability>> services = new HashMap<String, Vector<Capability>>();
         for (Capability cap :capabilities){
             if (!services.containsKey(cap.getServiceId())){
                 services.put(cap.getServiceId(), new Vector<Capability>());
             }
             services.get(cap.getServiceId()).add(cap);
             if (cap.getRole().equals(Capability.PRODUCER)) {
                 producers.add(cap);
             }

         }
         // For each service, find a stream that matches
         for (String serviceId : services.keySet()){
             StreamDescription stream = getMatchingStream(services.get(serviceId).toArray(new Capability[0]));
             if (stream!=null){
                 addCapabilitiesToStream(stream,services.get(serviceId).toArray(new Capability[0]));
                 if (producers.size()>0){
                     streamList.addStreamProducer(connectionId, stream);
                 }
             } else{
                 streams = streamList.getStreams();
                 HashMap<StreamDescription, String> matchingStreams = new HashMap<StreamDescription, String>();
                 if (streams.size() > 0){
                     matchingStreams = networkServicesManager.resolveMismatch(streams, services.get(serviceId));
                 }
                 if (!matchingStreams.isEmpty()){
                     for (StreamDescription streamDescription : matchingStreams.keySet()){
                         if (!streams.contains(streamDescription)){
                             streamList.addStream(streamDescription);
                             streamList.addStreamProducer(matchingStreams.get(streamDescription), streamDescription);
                         }
                     }
                 } else {
                     Vector<Capability> locCap = services.get(serviceId);
                     NetworkLocation addr = null;
                     if (locCap.firstElement().getLocationType().equals(Capability.PREFERRED_UNICAST)){
                         addr = allocateUnicastLocation(locCap.firstElement());
                     } else {
                         try {
                             addr = allocateMulticastLocation();
                         } catch (SocketException e) {
                             e.printStackTrace();
                         }
                     }
                     StreamDescription streamDesc = new StreamDescription();
                     streamDesc.setId(Utils.generateID());
                     streamDesc.setName(venueState.getName());
                     streamDesc.setLocation(addr);
                     streamDesc.setNetworkLocations(addr);
                     streamDesc.setCapabilities(services.get(serviceId));
                     streamDesc.setEncryptionKey(encryption);
                     if (encryption!=null){
                         streamDesc.setEncryptionFlag(1);
                     }
                     streamList.addStreamProducer(connectionId, streamDesc);
                     sendEvent(Event.ADD_STREAM, streamDesc);
                 }
             }


         }
         return streamList.getStreams().toArray(new StreamDescription[0]);
     }

    /**
     * This method creates a new Unicast Network Location based
     * on the service preferred IP in the capability
     *
     * @param capability service capability
     * @return A new unicast network location object.
     */
    public UnicastNetworkLocation allocateUnicastLocation(
            @SoapParameter("capability") Capability capability) {
        UnicastNetworkLocation networkLocation = new UnicastNetworkLocation();
        networkLocation.setHost(capability.getHost());
        networkLocation.setPort(capability.getPort());
        return networkLocation;
    }

    /**
     * @param appName
     * @param appDescription
     * @param appMimeType
     * @return The Application Description
     */
    @SoapReturn (
        name = "appDescription"
    )
    public ApplicationDescription createApplication(
            @SoapParameter("capability") String appName,
            @SoapParameter("appDescription") String appDescription,
            @SoapParameter("appMimeType") String appMimeType){
        ApplicationDescription applicationDescription = new ApplicationDescription();

        // create shared app

        return applicationDescription;
    }


    /**
     *  Add a transport to an existing stream
     *
     * @param streamId The id of the stream to which to add the transport
     * @param location The network location (transport) to add
     * @return The network location
     */
    @SoapReturn (
            name = "id"
        )
    public String addNetworkLocationToStream(
            @SoapParameter("streamId") String streamId,
            @SoapParameter("location") NetworkLocation location){
        String nid = null;
        Vector<StreamDescription> streamList = this.streamList.getStreams();
        for (StreamDescription streamDescription : streamList){
            if (streamDescription.getId().equals(streamId)){
                streamDescription.setLocation(location);
                nid=location.getId();
                sendEvent(Event.MODIFY_STREAM, streamDescription);
            }
        }
        return nid;
    }

    /**
     * Update application.
     * @param appDescription Application Description describing the application.
     * @return Application Description describing the application.
     */
    @SoapReturn (
        name = "appDescription"
    )
    public ApplicationDescription updateApplication (
            @SoapParameter("appDescription") ApplicationDescription appDescription){
        Vector<ApplicationDescription> applicationDescriptions = venueState.getApplications();
        if (!applicationDescriptions.contains(appDescription)){
            throw new RuntimeException("Application Not Found");
        }
        ApplicationDescription applicationDescription = applicationDescriptions.get(applicationDescriptions.indexOf(appDescription));
        applicationDescription.setName(appDescription.getName());
        applicationDescription.setDescription(appDescription.getDescription());
        sendEvent(Event.UPDATE_APPLICATION, appDescription);
        venueServerLog.println("Update Application: id=" + applicationDescription.getId() + " handle=" + applicationDescription.getUri());
        venueServerLog.flush();
        return applicationDescription;
    }

    /**
     * Destroy an application object.
     * @param appId The id of the application object to be destroyed.
     */
    public void destroyApplication(
            @SoapParameter("appId") String appId){
        Vector<ApplicationDescription> applicationDescriptions = venueState.getApplications();
        ApplicationDescription app = new ApplicationDescription();
        app.setId(appId);
        int index = applicationDescriptions.indexOf(app);
        ApplicationDescription applicationDescription = applicationDescriptions.get(index);
        sendEvent(Event.REMOVE_APPLICATION, applicationDescription);
        applicationDescriptions.remove(applicationDescription);
        venueServerLog.println("destroy Application: id=" + applicationDescription.getId() + " handle=" + applicationDescription.getUri());
        venueServerLog.flush();
    }

    /**
     * @return the connections of the current venue
     */
    @SoapReturn (
            name = "connections"
        )
    public ConnectionDescription[] getConnections(){
        Vector<ConnectionDescription> connections = venueState.getConnections();
        return connections.toArray(new ConnectionDescription[0]);
    }

    /**
     * @param connections
     */
    public void setConnections(
            @SoapParameter("connections") ConnectionDescription[] connections){
        for (ConnectionDescription connection : connections){
            venueState.setConnections(connection);
            venueServerLog.println("adding connection: id=" + connection.getId() + "name="+ connection.getName() + " handle=" + connection.getUri() );
            venueServerLog.flush();
        }
    }


    /**
     * This method creates a new Multicast Network Location.
     * @return A new multicast network location object.
     * @throws SocketException
     */
    @SoapReturn (
        name = "multicastLocation"
    )
    public MulticastNetworkLocation allocateMulticastLocation() throws SocketException{
        MulticastNetworkLocation multicastNetworkLocation = new MulticastNetworkLocation();
        multicastNetworkLocation.setHost(MulticastAddressAllocator.allocateAddress());
        multicastNetworkLocation.setPort(Utils.searchPort(1, evenPortFlag, false));
        multicastNetworkLocation.setTtl(DEFAULT_TTL);
        return multicastNetworkLocation;
    }

    /**
     * This method allows a Multicast Network Location to be reused.
     * @param multicastLocation the multicast network location object.
     */
    public void recycleMulticastLocation(
            @SoapParameter("multicastLocation") MulticastNetworkLocation multicastLocation
            ){
        MulticastAddressAllocator.recycleAddress(multicastLocation.getHost());
    }

    /**
     * The addService method enables VenuesClients to put services in
     * the Virtual Venue. Service put in the Virtual Venue through
     * AddService is persistently stored.
     *
     * @param serviceDesc a real service description.
     * @return the serviceDescription of the successfully added service
     */
    @SoapReturn (
            name = "serviceDesc"
        )
    public ServiceDescription addService(
            @SoapParameter("serviceDesc") ServiceDescription serviceDesc
            ){
        Vector<ServiceDescription> serviceDescriptions = venueState.getServices();
        if (serviceDescriptions.contains(serviceDesc)){
            venueServerLog.println("service " + serviceDesc.getDescription() + " (id="+ serviceDesc.getId()+") already present in Venue " + venueState.getName());
            venueServerLog.flush();
            return null;
        }
        venueServerLog.println("adding Service:" + serviceDesc.getDescription() + " (id=" + serviceDesc.getId() + ") to venue "+ venueState.getName());
        venueServerLog.flush();
        venueState.setServices(serviceDesc);
        sendEvent(Event.ADD_SERVICE, serviceDesc);
        return serviceDesc;
    }

    /**
     * RemoveService removes persistent service from the Virtual Venue.
     * @param serviceDesc a real service description.
     * @return the ServiceDescription of the successfully removed service.
     */
    @SoapReturn(
            name = "serviceDesc"
        )
    public ServiceDescription removeService(
            @SoapParameter("serviceDesc") ServiceDescription serviceDesc
            ){
        Vector<ServiceDescription> serviceDescriptions = venueState.getServices();
        if (!serviceDescriptions.contains(serviceDesc)){
            venueServerLog.println("service " + serviceDesc.getDescription() + " (id="+ serviceDesc.getId()+") not found in Venue " + venueState.getName());
            venueServerLog.flush();
            return null;
        }
        ServiceDescription service = serviceDescriptions.get(serviceDescriptions.indexOf(serviceDesc));
        venueServerLog.println("removing Service:" + serviceDesc.getDescription() + " (id=" + serviceDesc.getId() + ") to venue "+ venueState.getName());
        venueServerLog.flush();
        venueState.removeService(service);
        sendEvent(Event.REMOVE_SERVICE, service);
        return service;
    }

    /**
     * The updateService method enables VenuesClients to modify
     * a service description
     *
     * @param serviceDesc a real service description.
     * @return the serviceDescription of the successfully updated service
     */
    @SoapReturn (
            name = "serviceDesc"
        )
    public ServiceDescription updateService(
            @SoapParameter("serviceDesc") ServiceDescription serviceDesc){
        Vector<ServiceDescription> serviceDescriptions = venueState.getServices();
        if (!serviceDescriptions.contains(serviceDesc)){
            venueServerLog.println("service " + serviceDesc.getDescription() + " (id="+ serviceDesc.getId()+") not found in Venue " + venueState.getName());
            venueServerLog.flush();
            return null;
        }
        venueServerLog.println("updating Service:" + serviceDesc.getDescription() + " (id=" + serviceDesc.getId() + ") in venue "+ venueState.getName());
        venueServerLog.flush();
        venueState.updateService(serviceDesc);
        sendEvent(Event.UPDATE_SERVICE, serviceDesc);
        return serviceDesc;
    }

    /**
     * Retrieve the upload descriptor from the Venue's datastore.
     *
     * @return the upload descriptor for the data store.
     */
    @SoapReturn (
            name = "uploadUrl"
        )
    public String getUploadDescriptor(){
        String uploadUrl = venueState.getDataLocation();
        return uploadUrl;
    }


    /**
     * Replace the current description for dataDescription.name with this one.
     * @param dataDesc the DataDescription
     * @return the updated dataDescription
     */
    @SoapReturn (
            name = "dataDesc"
        )
    public DataDescription updateData(
            @SoapParameter("dataDesc") DataDescription dataDesc){
        String oldfilename = venueState.updateData(dataDesc);
        dataStore.storeDescription(venueState.getUniqueId(), oldfilename, dataDesc);
        sendEvent(Event.UPDATE_DATA, dataDesc);
        return dataDesc;
    }

    @SoapReturn (
            name = "dataDesc"
        )
    public DataDescription removeData(
            @SoapParameter("dataDesc") DataDescription dataDesc){
        DataDescription dataItem = dataDesc;
        try {
            dataItem = dataStore.removeData(venueState.getUniqueId(),dataDesc);
            venueState.removeData(dataDesc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendEvent(Event.REMOVE_DATA, dataItem);
        return dataItem;
    }

    /**
     * @return the list of dataDescription in the Venue
     */
    @SoapReturn (
            name = "dataDescriptions"
        )
    public DataDescription[] getDataDescriptions(){
        Vector<DataDescription> dataDescriptions = venueState.getData();
        return dataDescriptions.toArray(new DataDescription[0]);
    }

    /**
     * UpdateClientProfile allows a VenueClient to update/modify the client
     * profile that is stored by the Virtual Venue that they gave to the Venue
     * when they called the Enter method.
     *
     * @param clientProfile A client profile.
     */
    public void updateClientProfile(
            @SoapParameter("clientProfile") ClientProfile clientProfile){
        boolean found = false;
        for (VenueClientState clientState : clients.values()){
            if (clientState.updateClientProfile(clientProfile)){
                found = true;
                break;
            }
        }
        if (found){
            venueState.updateClient(clientProfile);
            sendEvent(Event.MODIFY_USER, clientProfile);
        } else {
        	for (String connID : monitoringClients.keySet()){
        		if (monitoringClients.get(connID).equals(clientProfile)){
        			monitoringClients.put(connID,clientProfile);
        			return;
        		}
        	}
        	venueServerLog.println("Error updating client profile");
            venueServerLog.flush();
        }
    }

    /**
     * GetStreams returns a list of stream descriptions to the caller.
     * @return list of stream descriptions in the Venue
     */
    @SoapReturn (
            name="streams"
        )
    public StreamDescription[] getStreams(){
        Vector<StreamDescription> streams = streamList.getStreams();
        return streams.toArray(new StreamDescription[0]);
    }

    /**
     * Restart the application services after a server restart.
     *
     * For each app impl, awaken the app, and create a new
     * web service binding for it.
     */
    public void startApplications(){
//    for appImpl in self.applications.values():
//        app = SharedApplicationI(impl=appImpl, auth_method_name="authorize")
//        hostObj = self.server.hostingEnvironment.BindService(app)
//        appHandle = hostObj.GetHandle()
//        appImpl.SetHandle(appHandle)
//        log.debug("Restarted app id=%s handle=%s",
//                  appImpl.GetId(), appHandle)
    }

    /**
     * @param textHost
     * @param textPort
     */
    public void setTextLocation(String textHost, int textPort) {
        venueState.setTextLocation(textHost+":"+textPort);
    }

    // extensions for oneVRE

    public void addMonitoringClient(
            @SoapParameter("clientProfile") ClientProfile clientProfile){
    	//monitoringClients.add(clientProfile);
        sendEvent(Event.MONITOR, clientProfile);
    }

    public void leaveMonitoringClient(
    		@SoapParameter("clientProfile") ClientProfile clientProfile){
    	sendEvent(Event.END_MONITORING, clientProfile);
    	monitoringClients.remove(clientProfile);
    }

    @SoapReturn (
            name="clients"
        )
    public  ClientProfile[] getAllClients (){
    	Vector<ClientProfile> clients = venueState.getClients();
    	clients.addAll(monitoringClients.values());
    	return clients.toArray(new ClientProfile[0]);
    }
}

