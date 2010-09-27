package com.googlecode.onevre.gwt.client.ag.types;

import java.util.Vector;

public class VenueState {

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


	public VenueState (VenueStateJSO jso){
		this.dataLocation = jso.getDataLocation();
		this.eventLocation = jso.getEventLocation();
		this.textLocation = jso.getTextLocation();
		this.name = jso.getName();
		this.uri = jso.getUri();
		this.description = jso.getDescription();
		this.uniqueId = jso.getUniqueId();
		this.venueList = jso.getVenueList();

		VectorJSO<DataDescriptionJSO> datajso = jso.getData();
		for (int i = 0; i<datajso.size(); i++) {
			data.add(new DataDescription(datajso.get(i)));
		}
		VectorJSO<ClientProfileJSO> clientsjso = jso.getClients();
		for (int i = 0; i<clientsjso.size(); i++) {
			clients.add(new ClientProfile(clientsjso.get(i)));
		}
		VectorJSO<ConnectionDescriptionJSO> connectionsjso = jso.getConnections();
		for (int i = 0; i<connectionsjso.size(); i++) {
			connections.add(new ConnectionDescription(connectionsjso.get(i)));
		}
		VectorJSO<ApplicationDescriptionJSO> applicationsjso = jso.getApplications();
		for (int i = 0; i<applicationsjso.size(); i++) {
			applications.add(new ApplicationDescription(applicationsjso.get(i)));
		}
		VectorJSO<ServiceDescriptionJSO> servicesjso = jso.getServices();
		for (int i = 0; i<servicesjso.size(); i++) {
			services.add(new ServiceDescription(servicesjso.get(i)));
		}
	}

    /**
     * Returns the applications
     * @return A list of application descriptions
     */
    public Vector<ApplicationDescription> getApplications() {
        return applications;
    }

    /**
     * Returns the clients
     * @return A list of client profiles
     */
    public Vector<ClientProfile> getClients() {
        return clients;
    }

    /**
     * Returns the connections
     * @return A list of connection descriptions
     */
    public Vector<ConnectionDescription> getConnections() {
        return connections;
    }

    /**
     * Returns the data
     * @return A list of data descriptions
     */
    public Vector<DataDescription> getData() {
        return data;
    }

    /**
     * Returns the data store location
     * @return the data location
     */
    public String getDataLocation() {
        return dataLocation;
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the location of the event stream
     * @return the event location
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the venue services
     * @return a list of service descriptions
     */
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
    public String getTextLocation() {
        return textLocation;
    }

    /**
     * Returns the unique id
     * @return the unique id
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the list of venues
     * @return the list of venues
     */
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
    public void updateData(DataDescription datum) {
        data.set(data.indexOf(datum),datum);
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
     * Returns a string to go into the log file
     * @return log file string
     */
    public String toLog(){
        return name + ", " + description;
    }

}
