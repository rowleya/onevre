/*
 * @(#)AGSharedApplication.java
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


import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.exceptions.SoapException;


/**
 * @author Tobias M Schiebeck
 * @version 1.0
 */

public class AGSharedApplication {
    private static final String AG_SHARED_APPLICATION_NS =
        "http://www.accessgrid.org/v3.0/agsharedapp";
//    static {
//        SoapDeserializer.mapType(EventDescription.class);
////        SoapDeserializer.mapType(AppDataDescription.class);
////        SoapDeserializer.mapType(SharedAppState.class);
////      SoapDeserializer.mapType(AGServiceManagerDescription.class);
////      SoapDeserializer.mapType(Capability.class);
////      SoapDeserializer.mapType(StreamDescription.class);
////      SoapDeserializer.mapType(ClientProfile.class);
////      SoapDeserializer.mapType(StreamDescription.class);
////      SoapDeserializer.mapType(ValueParameter.class);
////      SoapDeserializer.mapType(AGServicePackageDescription.class);
//    }

    // The current soap request
    private SoapRequest soapRequest = null;

    /**
     * Creates a new ServiceManager interface
     * @param serverUrl
     * @throws MalformedURLException
     */
    public AGSharedApplication(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
    }


    /**
     * Passes an event to the Shared application SOAP interface
     * @param event The event to send on
     * @throws IOException
     * @throws SoapException
     */
    public void handleEvent(EventDescription event)
    throws IOException, SoapException {
        soapRequest.call(AG_SHARED_APPLICATION_NS,
            "HandleEvent", "HandleEventRequest",
            new String[]{"event"},
            new Object[]{event},
            new Object[]{null},
            null);
    }

    /**
     * Sets the state of a shared application through the SOAP interfaces
     * @param appState State of the shared Application
     * @throws IOException
     * @throws SoapException
     */
    public void setState(SharedAppState appState)
    throws IOException, SoapException {
        soapRequest.call(AG_SHARED_APPLICATION_NS,
            "SetState", "SetStateRequest",
            new String[]{"appState"},
            new Object[]{appState},
            new Object[]{null},
            null);
    }

}
