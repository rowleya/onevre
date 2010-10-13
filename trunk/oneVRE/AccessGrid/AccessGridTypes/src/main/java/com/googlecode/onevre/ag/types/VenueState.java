/*
 * @(#)VenueState.java
 * Created: 15-Sep-2006
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

package com.googlecode.onevre.ag.types;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.googlecode.onevre.ag.types.application.ApplicationDescription;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Venue State
 * @author Andrew G D Rowley
 * @version 1.0
 */

/*
 <xs:complexType name=\"VenueState\">
 <xs:sequence>
 <xs:element name=\"uniqueId\" type=\"xs:string\"/>
 <xs:element name=\"name\" type=\"xs:string\"/>
 <xs:element name=\"description\" type=\"xs:string\"/>
 <xs:element name=\"uri\" type=\"xs:string\"/>
 <xs:element name=\"eventLocation\" nillable=\"true\" type=\"xs:string\"/>
 <xs:element name=\"textLocation\" nillable=\"true\" type=\"xs:string\"/>
<xs:element name=\"dataLocation\" nillable=\"true\" type=\"xs:string\"/>
 <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"services\" type=\"tns:ServiceDescription\"/>
 <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"connections\" type=\"tns:ConnectionDescription\"/>
 <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"clients\" type=\"tns:ClientProfile\"/>
 <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"data\" type=\"tns:DataDescription\"/>
 <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"applications\" type=\"tns:ApplicationDescription\"/>
 <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
</xs:sequence>
</xs:complexType>
*/

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VenueState implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"uniqueId",
                     "name",
                     "description",
                     "uri",
                     "eventLocation",
                     "textLocation",
                     "dataLocation",
                     "services",
                     "connections",
                     "clients",
                     "data",
                     "applications"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     null,
                     null,
                     null,
                     null,
                     null};

    // The id of the venue
    private String uniqueId = null;

    // The name of the venue
    private String name = null;

    // The venue description
    private String description = null;

    // The uri of the venue
    private String uri = null;

    // The location of the event stream
    private String eventLocation = null;

    // The location of the jabber room
    private String textLocation = null;

    // The location of the data store
    private String dataLocation = null;

    // A list of service descriptions
    private Vector<ServiceDescription> services = new Vector<ServiceDescription>();

    // A list of connection descriptions
    private Vector<ConnectionDescription> connections = new Vector<ConnectionDescription>();

    // A list of client profiles
    private Vector<ClientProfile> clients = new Vector<ClientProfile>();

    // A list of data descriptions
    private Vector<DataDescription> data = new Vector<DataDescription>();

    // A list of application descriptions
    private Vector<ApplicationDescription> applications = new Vector<ApplicationDescription>();

    // The parent venue in a tree of venues
//    private VenueState parent = null;

    // The list of venues we are connected with
    private VenueList venueList = null;


    /**
     * Returns the applications
     * @return A list of application descriptions
     */
    @XmlElement
    public Vector<ApplicationDescription> getApplications() {
        return applications;
    }

    /**
     * Returns the clients
     * @return A list of client profiles
     */
    @XmlElement
    public Vector<ClientProfile> getClients() {
        return clients;
    }

    /**
     * Returns the connections
     * @return A list of connection descriptions
     */
    @XmlElement
    public Vector<ConnectionDescription> getConnections() {
        return connections;
    }

    /**
     * Returns the data
     * @return A list of data descriptions
     */
    @XmlElement
    public Vector<DataDescription> getData() {
        return data;
    }

    /**
     * Returns the data store location
     * @return the data location
     */
    @XmlElement
    public String getDataLocation() {
        return dataLocation;
    }

    /**
     * Returns the description
     * @return the description
     */
    @XmlElement
    public String getDescription() {
        return description;
    }

    /**
     * Returns the location of the event stream
     * @return the event location
     */
    @XmlElement
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Returns the name
     * @return the name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Returns the venue services
     * @return a list of service descriptions
     */
    @XmlElement
    public Vector<ServiceDescription> getServices() {
        return services;
    }

    /**
     * adds a list of services
     * @param services services to add
     */
    public void setServices(Vector<ServiceDescription> services) {
        this.services=services;
    }

    /**
     * Returns the location of the jabber service
     * @return the text location
     */
    @XmlElement
    public String getTextLocation() {
        return textLocation;
    }

    /**
     * Returns the unique id
     * @return the unique id
     */
    @XmlElement
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    @XmlElement
    public String getUri() {
        return uri;
    }

    /**
     * Returns the list of venues
     * @return the list of venues
     */
    @XmlElement
    public VenueList getVenueList(){
        return venueList;
    }


    /**
     * Adds an application
     * @param application The application to add
     */
    public void setApplications(ApplicationDescription application) {
        applications.add(application);
    }

    /**
     * Adds a list of applications
     * @param applications The list of applications to add
     */
    public void setApplications(Vector<ApplicationDescription> applications) {
        this.applications=applications;
    }

    /**
     * updates an application
     * @param application The application to update
     */
    public void updateApplication(ApplicationDescription application) {
        applications.set(applications.indexOf(application),application);
    }

    /**
     * removes an application
     * @param application The application to remove
     */
    public void removeApplication(ApplicationDescription application) {
        applications.remove(application);
    }

    /**
     * Adds a client
     * @param client The client to add
     */
    public void setClients(ClientProfile client) {
        clients.add(client);
    }

    /**
     * Adds a list of clients
     * @param clients The list of clients to add
     */
    public void setClients(Vector<ClientProfile> clients) {
        this.clients=clients;
    }

    /**
     * updates a client
     * @param client The client to update
     */
    public void updateClient(ClientProfile client) {
        clients.set(clients.indexOf(client),client);
    }

    /**
     * removes a client
     * @param client The client to remove
     */
    public void removeClient(ClientProfile client) {
        clients.remove(client);
    }

    /**
     * Adds a connection
     * @param connection The connection to add
     */
    public void setConnections(ConnectionDescription connection) {
        connections.add(connection);
    }

    /**
     * Adds a list of connections
     * @param connections The list of connections to add
     */
    public void setConnections(Vector<ConnectionDescription> connections) {
        this.connections=connections;
    }

    /**
     * Adds the venueList
     * @param venueList The venueList to add
     */

    public void setVenueList(VenueList venueList) {
        this.venueList=venueList;
    }

    /**
     * removes a connection
     * @param connection The connection to remove
     */
    public void removeConnection(ConnectionDescription connection) {
        connections.remove(connection);
    }

    /**
     * Adds some data
     * @param datum The data to add
     */
    public void setData(DataDescription datum) {
        data.add(datum);
    }

    /**
     * Adds some data
     * @param data The data to add
     */
    public void setData(Vector<DataDescription> data) {
        this.data=data;
    }

    /**
     * Updates some data
     * @param datum The data to update
     */
    public String updateData(DataDescription datum) {
        int index = data.indexOf(datum);
        String filename = datum.getName();
        if (index!=-1){
	    	filename = data.get(index).getName();
	    	data.set(index,datum);
        } else {
        	data.add(datum);
        }
    	return filename;
    }

    /**
     * removes some data
     * @param datum The data to remove
     */
    public void removeData(DataDescription datum) {
        data.remove(datum);
    }

    /**
     * Sets the data location
     * @param dataLocation The data location
     */
    public void setDataLocation(String dataLocation) {
        this.dataLocation = dataLocation;
    }

    /**
     * Sets the description
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the event location
     * @param eventLocation The event location
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a service
     * @param service The service to add
     */
    public void setServices(ServiceDescription service) {
        services.add(service);
    }

    /**
     * Updates a service
     * @param service The service to update
     */
    public void updateService(ServiceDescription service) {
        services.set(services.indexOf(service),service);
    }

    /**
     * Removes a service
     * @param service The service to remove
     */
    public void removeService(ServiceDescription service) {
        services.remove(service);
    }

    /**
     * Sets the text location
     * @param textLocation The text location
     */
    public void setTextLocation(String textLocation) {
        this.textLocation = textLocation;
    }

    /**
     * Sets the unique id
     * @param uniqueId The unique id
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "VenueState"
     */
    public String getSoapType() {
        return "VenueState";
    }

    /**
     * Returns the namespace of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getNameSpace()}</dd></dl>
     * @return the namespace - "http://www.accessgrid.org/v3.0"
     */
    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    /**
     * Returns the fields that should be included with the soap Each of the fields should have a getter and a setter with the same name e.g. field is "test" there should be a "getTest" and a "setTest" method (note standard capitalisation)
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getFields()}</dd></dl>
     * @return the fields :
     *	<ul>
     *	<li>"uniqueId"</li>
     *	<li>"name"</li>
     *	<li>"description"</li>
     *	<li>"uri"</li>
     *	<li>"eventLocation"</li>
     *	<li>"textLocation"</li>
     *	<li>"dataLocation"</li>
     *	<li>"services"</li>
     *	<li>"connections"</li>
     *	<li>"clients"</li>
     *	<li>"data"</li>
     *	<li>"applications"</li>
     *	</ul>
     */
    public String[] getFields() {
        return SOAP_FIELDS;
    }

    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String out = "VenueState : \n";
        out += "uniqueId:" + uniqueId + "\n";
        out += "name:" + name + "\n";
        out += "description:" + description + "\n";
        out += "uri:" + uri + "\n";
        out += "eventlocation:" + eventLocation + "\n";
        out += "textlocation:" + textLocation + "\n";
        out += "dataLocation:" + dataLocation + "\n";
        out += "services: " + services.size() + "\n" ;
        for (ServiceDescription service : services){
               out += "    " + service.toString() + "\n";
        }
        out += "connections: " + connections.size() + "\n" ;
        for (ConnectionDescription conn: connections){
               out += "    " + conn.toString() + "\n";
        }
        out += "clients:" + clients.size() + "\n";
        for (ClientProfile client : clients){
               out += "    " + client.toString() + "\n";
        }
        out += "data:" + data.size() + "\n";
        for (DataDescription datadesc : data){
               out += "    " + datadesc.toString() + "\n";
        }
        out += "applications:" + applications.size()+ "\n";
        for (ApplicationDescription application:applications){
               out += "    " + application.toString() + "\n";
        }
        return out;
    }

    /**
     * Returns the types of the fields that should be included with the soap<br>
     * If the field is not a vector or array each of the types must be one of:
     * <ol><li>A fully qualified url</li>
     * <li>A standard XML type starting with xsd:</li>
     * <li>Null if the field is itself SoapSerializable</li></ol>
     * If the return type is an array or vector, the type must be one of:
     * <ol><li>A type as above if all the values have the same type</li>
     * <li>A Vector of types if the field is a vector with different types</li></ol>
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getTypes()}</dd></dl>
     * @return the types :
     * <ul>
     * <li>STRING_TYPE (uniqueId)</li>
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (description)</li>
     * <li>STRING_TYPE (uri)</li>
     * <li>STRING_TYPE (eventLocation)</li>
     * <li>STRING_TYPE (textLocation)</li>
     * <li>STRING_TYPE (dataLocation)</li>
     * <li>null (services) -> {@link com.googlecode.onevre.ag.types.com.googlecode.onevre.ag.common.types.service.ServiceDescription}</li>
     * <li>null (connections) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.ConnectionDescription}</li>
     * <li>null (clients) -> {@link ag3.interfaces.ClientProfileJSO.googlecode.onevre.ag.common.types.ClientProfile}</li>
     * <li>null (data) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.DataDescription}</li>
     * <li>null (applications) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.application.ApplicationDescription}</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Returns a string to go into the log file
     * @return log file string
     */
    public String toLog(){
        return name + ", " + description;
    }

}
