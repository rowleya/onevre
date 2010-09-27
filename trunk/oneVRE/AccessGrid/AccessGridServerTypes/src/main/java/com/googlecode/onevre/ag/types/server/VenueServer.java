/*
 * @(#)VenueServer.java
 * Created: 14-May-2007
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
import com.googlecode.onevre.types.soap.exceptions.SoapException;



/**
 * A proxy for a venue server
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueServer {

    private static final String VENUESERVER_NS =
        "http://www.accessgrid.org/v3.0/venueserver";

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
    }

    /**
     * Creates a new VenueServer proxy
     * @param serverUrl The url of the venue server
     * @throws MalformedURLException
     */
    public VenueServer(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
    }

    /**
     * Gets all the venues on the server
     * @return An array of connection description objects
     * @throws IOException
     * @throws SoapException
     */
    public ConnectionDescription[] getVenues()
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(VENUESERVER_NS,
                "GetVenues", "GetVenuesRequest",
                new String[0], new Object[0], new Object[0],
                new SoapResponseHash(
                    new String[]{VENUESERVER_NS + "/connectionDescriptionList"},
                    new Class[]{ConnectionDescription.class}));
        Object connections = result.get("connectionDescriptionList");
        if ((connections != null) && ConnectionDescription.class.equals(connections.getClass().getComponentType())){
            return (ConnectionDescription[])connections;
        }
        throw new SoapException("Return type not correct");
    }
}
