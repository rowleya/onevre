/*
 * @(#)VenueClientEventListener.java
 * Created: 31-May-2006
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

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.agclient.interfaces.ApplicationListener;
import com.googlecode.onevre.ag.common.Event;
import com.googlecode.onevre.ag.common.interfaces.EventListener;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.ConnectionDescription;
import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.ServiceDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.ag.types.application.ApplicationCmdDescription;
import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.protocols.events.eventserver.AgEvent;
import com.googlecode.onevre.protocols.events.eventserver.AgEventServer;

/**
 * Implements the Listener for the VenueClient (the top level events)
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class VenueClientEventListener implements EventListener {

    private Log log = LogFactory.getLog(this.getClass());

 //   private Vector<String> history = new Vector<String>();

    private String eventLocation = null;
    private String venueId = null;
//    private String connectionId = null;
    private VenueState venueState = null;
    private AgEventServer agEventServer = null;
    private ApplicationListener venueClientUI = null;

    /**
     * Creates the EventListener to be used by the Venue-EventClient to respond to the general events
     * @param venueState
     * @param eventLocation
     * @param venueClientUI
     * @param xmlRpcServer
     */
    public VenueClientEventListener(VenueState venueState, String eventLocation,
            ApplicationListener venueClientUI, AgEventServer agEventServer) {
        this.eventLocation = eventLocation;
        this.venueState = venueState;
        this.venueId = venueState.getUri();
        this.venueClientUI = venueClientUI;
        this.agEventServer = agEventServer;
    }

    /**
     * get the EventLocation
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#getLocation()}</dd></dl>
     * @return eventLocation of the venue
     */
    public String getLocation() {
        return eventLocation;
    }

    /**
     * get the id of the eventListener
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#getListenerId()}</dd></dl>
     * @return a unique id for the Eventlistener
     */
    public String getListenerId() {
        return String.valueOf(hashCode());
    }

    /**
     * Method to return the URI of the VenueClientEventListener
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#getListenerUri()}</dd></dl>
     * @return The uri of the venue the EventListener is responding to
     */
    public String getListenerUri() {
        return venueState.getUri();
    }


    /**
     * Event Processor for the following events:<dl>
     * <dh><b>ENTER = "Enter"</b></dh><dd>a new participant joins the venue</dd>
     * <dh><b>EXIT = "Exit"</b></dh><dd>a participant leaves the venue</dd>
     * <dh><b>MODIFY_USER = "Modify user"</b></dh><dd>a participant description changes</dd>
     * <dh><b>ADD_DATA = "Add data"</b></dh><dd></dd>
     * <dh><b>ADD_DIR = "Add directory"</b> (# Added new event for client messaging NA2-HPCE)</dh><dd> not fully implemented</dd>
     * <dh><b>UPDATE_DATA = "Update data"</b></dh><dd></dd>
     * <dh><b>REMOVE_DATA = "Remove data"</b></dh><dd></dd>
     * <dh><b>REMOVE_DIR = "Remove directory"</b> (# Added new event for client messaging NA2-HPCE)</dh><dd> not fully implemented</dd>
     * <dh><b>ADD_SERVICE = "Add service"</b></dh><dd></dd>
     * <dh><b>UPDATE_SERVICE = "Update service"</b></dh><dd></dd>
     * <dh><b>REMOVE_SERVICE = "Remove service"</b></dh><dd></dd>
     * <dh><b>ADD_APPLICATION = "Add application"</b></dh><dd></dd>
     * <dh><b>UPDATE_APPLICATION = "Update application"</b></dh><dd></dd>
     * <dh><b>REMOVE_APPLICATION = "Remove application"</b></dh><dd></dd>
     * <dh><b>ADD_CONNECTION = "Add connection"</b></dh><dd></dd>
     * <dh><b>REMOVE_CONNECTION = "Remove connection"</b></dh><dd></dd>
     * <dh><b>SET_CONNECTIONS = "Set connections"</b></dh><dd></dd>
     * <dh><b>UPDATE_VENUE_STATE = "Update venue state"</b></dh><dd></dd>
     * <dh><b>ADD_STREAM = "Add stream"</b></dh><dd></dd>
     * <dh><b>MODIFY_STREAM = "Modify stream"</b></dh><dd></dd>
     * <dh><b>REMOVE_STREAM = "Remove stream"</b></dh><dd></dd>
     * <dh><b>OPEN_APP = "Start application"</b></dh><dd></dd>
     * </dl>
     * The events are passed on to the XML-RPC server to be reflected in the Portlet user interface
     *
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.interfaces.EventListener#processEvent(ag3.interfaces.types.EventDescription, java.lang.String)}</dd></dl>
     * @param event The event which shall be processed by the Listener
     * @param SOAPmessage The original SOAP message sent to the Event Handler
     */
    public void processEvent(EventDescription event, String SOAPmessage) {

        String eventType = event.getEventType();
        ClientProfile client;
        DataDescription data;
        ServiceDescription service;
        ApplicationDescription application;
        ApplicationCmdDescription appCmd;
        ConnectionDescription connection;
        StreamDescription stream;

        log.info("-------------------------------------------------------------");
        log.info("Event: " + eventType + " data: " + event.getData().toString());
        log.info("-------------------------------------------------------------");
        // user events
        if (eventType.equals(Event.ENTER)) {
            client = (ClientProfile) event.getData();
            venueState.setClients(client);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventEnterVenue", client));
       } else
        if (eventType.equals(Event.EXIT)) {
            client = (ClientProfile) event.getData();
            venueState.removeClient(client);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventExitVenue", client));
        } else
        if (eventType.equals(Event.MODIFY_USER)) {
            client = (ClientProfile) event.getData();
            venueState.updateClient(client);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventModifyUser", client));
        } else
        // data events
        if (eventType.equals(Event.ADD_DATA)) {
            data = (DataDescription) event.getData();
            venueState.setData(data);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddData", data));
        } else
        if (eventType.equals(Event.REMOVE_DATA)) {
            data = (DataDescription) event.getData();
            venueState.removeData(data);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveData", data));
        } else
        if (eventType.equals(Event.UPDATE_DATA)) {
            data = (DataDescription) event.getData();
            venueState.updateData(data);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventUpdateData", data));
        } else
        if (eventType.equals(Event.ADD_DIR)) {
            data = (DataDescription) event.getData();
            venueState.updateData(data);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddDirectory", data));
        } else
        if (eventType.equals(Event.REMOVE_DIR)) {
            data = (DataDescription) event.getData();
            venueState.updateData(data);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveDirectory", data));
        } else
        // service events
        if (eventType.equals(Event.ADD_SERVICE)) {
            service = (ServiceDescription) event.getData();
            venueState.setServices(service);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddService", service));
        } else
        if (eventType.equals(Event.REMOVE_SERVICE)) {
            service = (ServiceDescription) event.getData();
            venueState.removeService(service);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveService", service));
        } else
        if (eventType.equals(Event.UPDATE_SERVICE)) {
            service = (ServiceDescription) event.getData();
            venueState.updateService(service);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventUpdateService", service));
        } else
        // application events
        if (eventType.equals(Event.ADD_APPLICATION)) {
            application = (ApplicationDescription) event.getData();
            log.info("Add application: " + application.getName());
            venueState.setApplications(application);
            venueClientUI.addApplication(application);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddApplication", application));
        } else
        if (eventType.equals(Event.REMOVE_APPLICATION)) {
            application = (ApplicationDescription) event.getData();
            venueState.removeApplication(application);
            venueClientUI.removeApplication(application);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveApplication", application));
        } else
        if (eventType.equals(Event.UPDATE_APPLICATION)) {
            application = (ApplicationDescription) event.getData();
            venueState.updateApplication(application);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventUpdateApplication", application));
        } else
        // connection events
        if (eventType.equals(Event.ADD_CONNECTION)) {
            connection = (ConnectionDescription) event.getData();
            venueState.setConnections(connection);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddConnection", connection));
        } else
        if (eventType.equals(Event.REMOVE_CONNECTION)) {
            connection = (ConnectionDescription) event.getData();
            venueState.removeConnection(connection);
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveConnection", connection));
        } else
        if (eventType.equals(Event.ADD_STREAM)) {
            stream = (StreamDescription) event.getData();
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventAddStream", stream));
        } else
        if (eventType.equals(Event.MODIFY_STREAM)) {
            stream = (StreamDescription) event.getData();
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventModifyStream", stream));
        } else
        if (eventType.equals(Event.REMOVE_STREAM)) {
            stream = (StreamDescription) event.getData();
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoveStream", stream));
        } else
        if (eventType.equals(Event.OPEN_APP)) {
            appCmd = (ApplicationCmdDescription) event.getData();
            log.info("Start application: cmd=" + appCmd.getCmd() + " verb=" + appCmd.getVerb());
            Vector<Object> ov = new Vector<Object>();
            ov.add(appCmd.getAppDesc());
            ov.add(appCmd.getProfile());
            agEventServer.addEvent(new AgEvent(getListenerUri(), "eventRemoteStartApplication", ov));
        } else {
            log.info("Event Client: ");
            log.info("unhandled Event: " + eventType);
            log.info("SenderId:  " + event.getSenderId());
            log.info("ChannelId: " + event.getChannelId());
        }
    }
}
