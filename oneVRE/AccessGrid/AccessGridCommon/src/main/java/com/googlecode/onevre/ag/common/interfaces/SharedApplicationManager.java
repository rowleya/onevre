/*
 * @(#)SharedApplicationManager.java
 * Created: 06-Nov-2006
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


import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * Interface to an AG3 ServiceManager
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SharedApplicationManager {

    // The service manager namespace
    private static final String SHARED_APPLICATION_MANAGER_NS =
        "http://www.accessgrid.org/v3.0/sharedappmanager";

    static {
//        SoapDeserializer.mapType(AGServiceDescription.class);
//        SoapDeserializer.mapType(AGServiceManagerDescription.class);
//        SoapDeserializer.mapType(Capability.class);
//        SoapDeserializer.mapType(StreamDescription.class);
//        SoapDeserializer.mapType(ClientProfile.class);
//        SoapDeserializer.mapType(StreamDescription.class);
//        SoapDeserializer.mapType(ValueParameter.class);
//        SoapDeserializer.mapType(AGServicePackageDescription.class);
    }

    // The current soap request
    private SoapRequest soapRequest = null;

    /**
     * Creates a new ServiceManager interface
     * @param serverUrl
     * @throws MalformedURLException
     */
    public SharedApplicationManager(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
    }

    /**
     * registers an application with shared application manager on the server
     * @param token private token to access the shared application
     * @param url URL to access the application
     * @throws IOException
     * @throws SoapException
     */
    public void registerApplication(String token, String url)
           throws IOException, SoapException {
           soapRequest.call(SHARED_APPLICATION_MANAGER_NS,
               "RegisterApplication", "RegisterApplicationRequest",
               new String[]{"token", "url"},
               new Object[]{token, url},
               new Object[]{SoapSerializable.STRING_TYPE,
                            SoapSerializable.STRING_TYPE},
               null);
    }

    /**
     * Downloads the data for the shared application
     * @param venueFilename filename of the data file in the venue
     * @param applicationUrl URL of the application the data is downloaded for
     * @throws IOException
     * @throws SoapException
     */
    public void downloadData(String venueFilename, String applicationUrl)
            throws IOException, SoapException {
        soapRequest.call(SHARED_APPLICATION_MANAGER_NS,
           "downloadData", "downloadDataRequest",
           new String[]{"venueFilename", "applicationUrl"},
           new Object[]{venueFilename, applicationUrl},
           new Object[]{SoapSerializable.STRING_TYPE, SoapSerializable.STRING_TYPE},
           null);
    }

    /**
     * registers a shutdown of a shared application with the shared application manager on the server
     * @throws IOException
     * @throws SoapException
     */
    public void shutdown() throws IOException, SoapException {
        soapRequest.call(SHARED_APPLICATION_MANAGER_NS,
            "Shutdown", "ShutdownRequest",
            new String[]{},
            new Object[]{},
            new Object[]{},
            null);
    }


    /**
     * @param url Sets the point of reference server URL for the shared application manager
     * @throws IOException
     * @throws SoapException
     */
    public void setPointOfReference(String url)
            throws IOException, SoapException {
        soapRequest.call(SHARED_APPLICATION_MANAGER_NS,
                "SetPointOfReference", "SetPointOfReferenceRequest",
                new String[]{"url"},
                new Object[]{url},
                new Object[]{SoapSerializable.STRING_TYPE},
                null);
    }

    /**
     * sets the venues encrytpion key in the shared application manager
     * @param encryption the encryption key
     * @throws IOException
     * @throws SoapException
     */
    public void setEncryption(String encryption)
            throws IOException, SoapException {
        soapRequest.call(SHARED_APPLICATION_MANAGER_NS,
                "SetEncryption", "SetEncryptionRequest",
                new String[]{"encryption"},
                new Object[]{encryption},
                new Object[]{SoapSerializable.STRING_TYPE},
                null);
    }

}
