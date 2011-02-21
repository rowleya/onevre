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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VOAttribute;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
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
public class VenueServer extends SoapServable {

    private Log log = LogFactory.getLog(this.getClass());

    private PrintWriter venueServerLog = null;

    private VenuesServlet venuesServlet = null;

    private HashMap<ConnectionDescription, Vector<VOAttribute>> venues =
        new HashMap<ConnectionDescription, Vector<VOAttribute>>();
/*
    static {
//      ADD_METHOD_RESULT("RegisterNetworkService", "", "");
//      ADD_METHOD_RESULT("UnRegisterNetworkService", "", "");
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
        SoapDeserializer.mapType(VOAttribute.class);
/*        SoapDeserializer.mapType(MulticastNetworkLocation.class);
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
*/
/*        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(TextParameter.class);
        SoapDeserializer.mapType(OptionSetParameter.class);
        SoapDeserializer.mapType(RangeParameter.class);
        SoapDeserializer.mapType(BridgeDescription.class);
*/    }


    /**
     * Creates a new AGNodeService
     * @param venueName
     * @param venueDescription
     * @param eventServer
     * @param log The services that are available to this node service
     */
    public VenueServer(VenuesServlet venuesServlet, PrintWriter log) {
        this.venueServerLog = log;
        this.venuesServlet = venuesServlet;
//        log.info("Venue Server: Create Venue ");
    }

    private ServiceDescription getServiceDescription(String serviceId,
            HashMap<String, HashMap<String, String>> venuesData) {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setId(Utils.generateID());
        String name = ConfigFile.getParameter(venuesData, serviceId, "name", "");
        if ((name == null) || name.trim().equals("")) {
            return null;
        }
        serviceDescription.setName(name);
        String description = ConfigFile.getParameter(venuesData, serviceId, "description", "");
        if (description == null) {
            description = "";
        }
        serviceDescription.setDescription(description);
        String mimeType = ConfigFile.getParameter(venuesData, serviceId, "mimeType", "");
        if ((mimeType == null) || mimeType.trim().equals("")) {
            return null;
        }
        String uri = ConfigFile.getParameter(venuesData, serviceId, "uri", "");
        if ((uri == null) || uri.trim().equals("")) {
            return null;
        }
        serviceDescription.setUri(uri);
        return serviceDescription;
    }


    public StreamDescription getStreamDescription(String streamId,
            HashMap<String, HashMap<String, String>> venuesData) {
        StreamDescription stream = new StreamDescription();
        stream.setId(streamId);
        int encryptionFlag = Integer.valueOf(ConfigFile.getParameter(venuesData, streamId, "encryptionFlag", "0"));
        stream.setEncryptionFlag(encryptionFlag);
        stream.setEncryptionKey(ConfigFile.getParameter(venuesData, streamId, "encryptionKey", null));
        stream.setName(ConfigFile.getParameter(venuesData, streamId, "name", ""));
        String capString = ConfigFile.getParameter(venuesData, streamId, "capability", "");
        Vector<Capability> capabilities = VenueServerConfigParameters.defaultCapablities.get(capString);
        if (capabilities == null) {
            return null;
        }
        stream.setCapabilities(capabilities);
        NetworkLocation networkLocation =  getNetworkLocation(
                ConfigFile.getParameter(venuesData, streamId, "location", "").split(" "));
        if (networkLocation == null) {
            return null;
        }
        stream.setLocation(networkLocation);
        return stream;
    }


    public NetworkLocation getNetworkLocation(String[] locString) {
        NetworkLocation networkLocation = null;
        if (locString.length < 3) {
            return null;
        }
        String locationType = locString[0];
        String host = locString[1];
        int port = Integer.valueOf(locString[2]);
        if (MulticastNetworkLocation.TYPE.equals(locationType.trim())) {
            if (locString.length < 4) {
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

    public void setConnections(String[] connections, HashMap<String,
            HashMap<String, String>>venuesData, String importUri) {
        for (String conn : connections) {
            ConnectionDescription connectionDescription = new ConnectionDescription();
            connectionDescription.setId(conn);
            connectionDescription.setName(ConfigFile.getParameter(venuesData, conn, "name", ""));
            connectionDescription.setDescription(ConfigFile.getParameter(venuesData, conn, "description", ""));
            String uri = ConfigFile.getParameter(venuesData, conn, "uri", "");
            if (uri.startsWith(importUri)) {
                uri = uri.substring(uri.lastIndexOf("/") + 1);
            }
            connectionDescription.setUri(uri);
//            venueState.setConnections(connectionDescription);
        }
    }


    /**
     * Creates a venue
     * @param clientProfile clientProfile of the joining client
     * @return the connectionId that joined the venue
     */
    @SoapReturn(
            name = "connection"
        )
    public ConnectionDescription createVenues(
            @SoapParameter("name") String name,
            @SoapParameter("description") String description,
            @SoapParameter("creator") ClientProfile creator,
            @SoapParameter("voAttributes") VOAttribute[] voAttributes
        ) {
        log.info("Venue Server create Venue " + name + "(" + description + ")");
        Vector<VOAttribute> voAtts = new Vector<VOAttribute>();
        for (VOAttribute voAttribute : voAttributes) {
            voAtts.add(voAttribute);
        }
        log.info("calling venuesServlet.addVenue(" + name + ", " + description + ", "
                + voAtts + ", " + creator + " ...)");
        ConnectionDescription conn = venuesServlet.addVenue(name, description, voAtts, creator, venueServerLog);
        venues.put(conn, voAtts);
        log.info("returned :" + conn);
        return conn;
    }

    @SoapReturn(
            name = "connectionDescriptionList"
        )
    public ConnectionDescription[] getVenues(
            @SoapParameter("voAttributes") VOAttribute[] voAttributes
        ) {
        if (voAttributes == null) {
            voAttributes = new VOAttribute[0];
        }
        log.info("Venue Server: getVenues (" + voAttributes.toString() + ")");
        Vector<ConnectionDescription> connDescs = new Vector<ConnectionDescription>();
        Set<ConnectionDescription> venueConns = venues.keySet();
        if (venueConns.isEmpty()) {
            return new ConnectionDescription[0];
        }
        for (ConnectionDescription conn : venueConns) {
            Vector<VOAttribute> voAtts = venues.get(conn);
            if (voAtts != null) {
                for (VOAttribute voAttribute : voAtts) {
                    boolean matches = false;
                    for (VOAttribute voAtt : voAttributes) {
                        if (voAtt.matches(voAttribute)) {
                            matches = true;
                            connDescs.add(conn);
                        }
                    }
                    if (matches) {
                        break;
                    }
                }
            } else {
                if (voAttributes == null) {
                    connDescs.add(conn);
                }
            }
        }
        return connDescs.toArray(new ConnectionDescription[]{});
    }

    public void addVenue(Venue venue, Vector<VOAttribute> voAttributes) {
        venues.put(venue.getConnectionDescription(), voAttributes);
    }


}

