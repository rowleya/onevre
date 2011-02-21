/*
 * @(#)AGNetworkService.java
 * Created: 10 Mar 2008
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

package com.googlecode.onevre.ag.common.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;


import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapResponseHash;
import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.exceptions.SoapException;


/**
 * @author Tobias M Schiebeck
 * @version 1.0
 */

public class AGNetworkService {
    private static final String AG_NETWORK_SERVICE_NS =
        "http://www.accessgrid.org/v3.0/agnetworkservice";

    static {
        SoapDeserializer.mapType(StreamDescription.class);
    }

    // The current soap request
    private SoapRequest soapRequest = null;
    // The url of the server
    private String serverUrl = null;

    /**
     * Creates a new ServiceManager interface
     * @param serverUrl
     * @throws MalformedURLException
     */
    public AGNetworkService(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
        this.serverUrl = serverUrl;
    }

    /**
     * Gets the url being used
     * @return The url
     */
    public String getUrl() {
        return serverUrl;
    }

    /**
     * transforms a streamDescription to the remote equivalents
     * Perform the chain of stream transformation.
     * streams -> net service -> net service -> net service -> streams
     *
     * @param stream original streamDescription
     * @return transformed streamDescription
     * @throws IOException
     * @throws SoapException
     */
    public StreamDescription transform(StreamDescription stream)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(AG_NETWORK_SERVICE_NS,
            "Transform", "TransformRequest",
            new String[]{"streamDescription"},
            new Object[]{stream},
            new Object[]{null},
            new SoapResponseHash(
                new String[]{AG_NETWORK_SERVICE_NS + "/streamDescription"},
                new Class[]{StreamDescription.class}));
        Object sd = result.get("streamDescription");
        if ((sd != null) && (sd instanceof StreamDescription)) {
            return (StreamDescription) sd;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * stop transformation of a streamDescription
     *
     * @param stream original streamDescription
     * @return transformed streamDescription
     * @throws IOException
     * @throws SoapException
     */
    public StreamDescription stopTransform(StreamDescription stream)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(AG_NETWORK_SERVICE_NS,
            "StopTransform", "StopTransformRequest",
            new String[]{"streamDescription"},
            new Object[]{stream},
            new Object[]{null},
            new SoapResponseHash(
                new String[]{AG_NETWORK_SERVICE_NS + "/streamDescription"},
                new Class[]{StreamDescription.class}));
        Object sd = result.get("streamDescription");
        if ((sd != null) &&  (sd instanceof StreamDescription)) {
            return (StreamDescription) sd;
        }
        throw new SoapException("Return type not correct");
    }

}
