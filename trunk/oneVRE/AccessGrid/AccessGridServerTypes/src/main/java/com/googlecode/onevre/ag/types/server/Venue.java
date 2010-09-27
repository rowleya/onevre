/*
 * @(#)Venue.java
 * Created: 19-Sep-2006
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

package com.googlecode.onevre.ag.types.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;


import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.ProviderProfile;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.application.ApplicationCmdDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapResponseHash;
import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;

/**
 * A proxy for a venue
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Venue extends SoapServable {

    // The string that is the connection id parameter
    private static final String CONNECTION_ID = "connectionId";

    private static final String VENUE_NS =
        "http://www.accessgrid.org/v3.0/venue";

    // The soap request
    private SoapRequest soapRequest = null;

    static {
        SoapDeserializer.mapType(Capability.class);
        SoapDeserializer.mapType(VenueState.class);
        SoapDeserializer.mapType(ClientProfile.class);
        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(ApplicationCmdDescription.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(DataDescription.class);
        SoapDeserializer.mapType(ServiceDescription.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(MulticastNetworkLocation.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(EventDescription.class);
    }

    /**
     * Creates a new Venue
     * @param serverUrl The url of the venue
     * @throws MalformedURLException
     */
    public Venue(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
    }

    /**
     * Gets the state of the venue
     * @return The response
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "state"
        )
    public VenueState getState() throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS, "GetState",
                "GetStateRequest", new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/state"},
                         new Class[]{VenueState.class}));
        Object state = result.get("state");
        if ((state != null) && (state instanceof VenueState)) {
            return (VenueState) state;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Gets the connections to other venues
     * @return An array of connection description objects
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "connections"
        )
    public ConnectionDescription[] getConnections()
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "GetConnections", "GetConnectionsRequest",
                new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/connections"},
                        new Class[]{ConnectionDescription.class},
                        new boolean[]{true}));
        Object connections = result.get("connections");
        if (connections==null){
            return new ConnectionDescription[0];
        }
        if (ConnectionDescription.class.equals(connections.getClass().getComponentType())){
            return (ConnectionDescription[])connections;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Enters a venue
     * @param clientProfile The client entering the venue
     * @return The connection id
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "connectionId"
        )
    public String enter(
            @SoapParameter("clientProfile") ClientProfile clientProfile
            ) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS, "Enter",
                "EnterRequest", new String[]{"clientProfile"},
                new Object[]{clientProfile}, new Object[]{null},
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/connectionId"},
                        new Class[]{String.class}));
        Object connectionId = result.get("connectionId");
        if ((connectionId != null) && (connectionId instanceof String)) {
            return (String) connectionId;
        }
        throw new SoapException("Return type not correct");
    }
    /**
     * Monitors a venue
     * @param clientProfile The client entering the venue
     * @return The connection id
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "connectionId"
        )
    public String monitor(
            @SoapParameter("clientProfile") ClientProfile clientProfile
            ) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS, "Monitor",
                "MonitorRequest", new String[]{"clientProfile"},
                new Object[]{clientProfile}, new Object[]{null},
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/connectionId"},
                        new Class[]{String.class}));
        Object connectionId = result.get("connectionId");
        if ((connectionId != null) && (connectionId instanceof String)) {
            return (String) connectionId;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Updates the lifetime of the client
     * @param connectionId The id of the connection
     * @param requestedTimeout The timeout value to request
     * @return The time before this should be called again
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name="timeout"
        )
    public float updateLifetime(
            @SoapParameter("connectionId") String connectionId,
            @SoapParameter("requestedTimeout") float requestedTimeout
            ) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "UpdateLifetime", "UpdateLifetimeRequest",
                new String[]{CONNECTION_ID, "requestedTimeout"},
                new Object[]{connectionId, new Float(requestedTimeout)},
                new Object[]{SoapSerializable.STRING_TYPE,
                             SoapSerializable.FLOAT_TYPE},
                new SoapResponseHash(
                    new String[]{VENUE_NS + "/timeout"},
                    new Class[]{Float.class}));
        Object timeout = result.get("timeout");
        if ((timeout != null) && (timeout instanceof Float)) {
            return ((Float) timeout).floatValue();
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Negotiates the capabilities of the venue and node
     * @param connectionId The id of the connection
     * @param capabilities The capabilities of this node
     * @return A set of stream descriptions where streams can be sent
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name="streams"
        )
    public StreamDescription[] negotiateCapabilities(
            @SoapParameter("connectionId") String connectionId,
            @SoapParameter("capabilities") Capability[] capabilities
        ) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "NegotiateCapabilities", "NegotiateCapabilitiesRequest",
                new String[]{CONNECTION_ID, "capabilities"},
                new Object[]{connectionId, capabilities},
                new Object[]{SoapSerializable.STRING_TYPE, null},
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/streams"},
                        new Class[]{StreamDescription.class},
                        new boolean[]{true}));
        Object streams = result.get("streams");
        if ((streams != null) && StreamDescription.class.equals(streams.getClass().getComponentType())){
            return (StreamDescription[])streams;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Exits the venue
     * @param connectionId The id of the connection
     * @throws IOException
     * @throws SoapException
     */
    public void exit(
            @SoapParameter("connectionId" )String connectionId
        ) throws IOException, SoapException {
        soapRequest.call(VENUE_NS, "Exit", "ExitRequest",
                new String[]{CONNECTION_ID}, new Object[]{connectionId},
                new Object[]{SoapSerializable.STRING_TYPE},
                null);
    }

    /**
     * Updates the users client profile
     * @param clientProfile
     * @throws IOException
     * @throws SoapException
     */
    public void updateClientProfile(
            @SoapParameter("clientProfile" ) ClientProfile clientProfile
        ) throws IOException, SoapException {
        soapRequest.call(VENUE_NS, "UpdateClientProfile", "UpdateClientProfileRequest",new String[]{"clientProfile"},
                new Object[]{clientProfile}, new Object[]{null},
                null);
    }

    /**
     * Gets the streams of the venue
     * @return An array of StreamDescriptions
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn (
            name = "streams"
        )
    public StreamDescription[] getStreams() throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "GetStreams", "GetStreamsRequest",
                new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                    new String[]{VENUE_NS + "/streams"},
                    new Class[]{StreamDescription.class},
                    new boolean[]{true}));
        Object streams = result.get("streams");
        if ((streams != null) && StreamDescription.class.equals(streams.getClass().getComponentType())){
            return (StreamDescription[])streams;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Removes data from a venue
     * @param dataDescription The data being removed
     * @return The removed data
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "dataDesc"
        )
    public DataDescription removeData(
            @SoapParameter("dataDesc") DataDescription dataDescription
        ) throws IOException, SoapException {
           dataDescription.setType(null);
        HashMap<String, Object> result = soapRequest.call(VENUE_NS, "RemoveData",
                "RemoveDataRequest", new String[]{"dataDesc"},
                new Object[]{dataDescription}, new Object[]{null},
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/dataDesc"},
                         new Class[]{DataDescription.class}));
        Object dataDesc = result.get("dataDesc");
        if ((dataDesc != null) && (dataDesc instanceof DataDescription)) {
            return (DataDescription) dataDesc;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Get the upload descriptor from the Venue's datastore.
     * @return the upload descriptor for the data store
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "uploadUrl"
        )
    public String getUploadDescriptor() throws IOException, SoapException{
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "GetUploadDescriptor", "GetUploadDescriptorRequest",
                new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/uploadUrl"},
                        new Class[]{String.class}));
        Object uploadUrl = result.get("uploadUrl");
        if ((uploadUrl != null) && (uploadUrl instanceof String)) {
            return (String) uploadUrl;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * creates a shared application in the venue
     * @param appName The name of the appliction
     * @param appDescription XML string of an ApplicationDescription
     * @param appMimeType the MimeType the application handles
     * @return the ApplicationDescription of the registered application
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn(
            name = "appDescription"
        )
    public ApplicationDescription createApplication (
            @SoapParameter("appName") String appName,
            @SoapParameter("appDescription") String appDescription,
            @SoapParameter("appMimeType") String appMimeType
        ) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS, "CreateApplication",
                "CreateApplicationRequest", new String[]{"appName","appDescription", "appMimeType"},
                new Object[]{appName,appDescription, appMimeType}, new Object[]{SoapSerializable.STRING_TYPE,SoapSerializable.STRING_TYPE,SoapSerializable.STRING_TYPE},
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/appDescription"},
                         new Class[]{ApplicationDescription.class}));
        Object appDesc = result.get("appDescription");
        if ((appDesc != null) && (appDesc instanceof ApplicationDescription)) {
            return (ApplicationDescription) appDesc;
        }
        throw new SoapException("Return type not correct");

    }

    @SoapReturn(
    		name="version"
		)
    public String  getVersion () throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUE_NS,
                "GetVersion", "GetVersionRequest",
                new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                        new String[]{VENUE_NS + "/version"},
                        new Class[]{String.class}));
        Object version = result.get("version");
        if ((version != null) && (version instanceof String)) {
            return (String) version;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * Cancels the most recent request
     *
     */
    public void cancel() {
        soapRequest.cancel();
    }
}
