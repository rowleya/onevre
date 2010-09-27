/*
 * @(#)ApplicationEventListener.java
 * Created: 28-March-2008
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

package com.googlecode.onevre.ag.agclient;



import com.googlecode.onevre.ag.common.interfaces.EventListener;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.application.AppDataDescription;
import com.googlecode.onevre.ag.types.application.AppParticipantDescription;
import com.googlecode.onevre.ag.types.application.ApplicationCmdDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;





/**
 * Implements the Listener for the event queue of SharedApplications
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class ApplicationEventListener implements EventListener {

    private String appLocation=null;

    private ApplicationDescription application = null;

    private PagXmlRpcServer xmlRpcServer=null;

    /**
     * @param application The application description of the application the EventListener is responding to
     * @param appLocation The uri of the SharedApplication (including the public id)
     * @param xmlRpcServer The XML-RPC server that responds to the Application Event
     */
    public ApplicationEventListener(ApplicationDescription application, String appLocation, PagXmlRpcServer xmlRpcServer) {
        this.appLocation=appLocation;
        this.application=application;
        this.xmlRpcServer=xmlRpcServer;
        SoapDeserializer.mapType(AppParticipantDescription.class);
        SoapDeserializer.mapType(AppDataDescription.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(ApplicationCmdDescription.class);
    }

    /**
     * Method to return the Application Location
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#getLocation()}</dd></dl>
     * @return The uri of the SharedApplication (including the public id)
     */
    public String getLocation()  {
        return appLocation;
    }

    /**
     * Method to return the URI of the SharedApplication/EventListener
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#getListenerUri()}</dd></dl>
     * @return The uri of the SharedApplication the EventListener is responding to
     */
    public String getListenerUri() {
        return  application.getUri();
    }


    /**
     * Method to return the unique IdURI of the EventListener
     * <dl><dt><b>overrides:</b></dt><dd>{@link  ag3.interfaces.EventListener#getListenerId()}</dd></dl>
     * @return The unique Idof the EventListener (SharedApplication)
     */
    public String getListenerId(){
        return application.getId();
    }

    /**
     * Event Processor: This forwards the event to the XML-RPC server
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#processEvent(ag3.interfaces.types.EventDescription, java.lang.String)}</dd></dl>
     * @param event The event which shall be processed by the Listener
     * @param SOAPmessage The original SOAP message sent to the Event Handler
     */
    public void processEvent(EventDescription event, String SOAPmessage) {
   //     System.err.println("Received application event " + event.getEventType());
        xmlRpcServer.addRequest("ApplicationEvent", new Object[]{application,
                event});
    }


}
