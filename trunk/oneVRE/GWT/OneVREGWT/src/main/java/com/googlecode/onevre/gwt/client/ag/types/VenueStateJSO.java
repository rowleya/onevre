package com.googlecode.onevre.gwt.client.ag.types;


import com.google.gwt.core.client.JavaScriptObject;

public class VenueStateJSO extends JavaScriptObject {

	protected VenueStateJSO (){
	}
    /**
     * Returns the applications
     * @return A list of application descriptions
     */
    public native final VectorJSO<ApplicationDescriptionJSO> getApplications() /*-{
        return this.getApplications();
    }-*/;

    /**
     * Returns the clients
     * @return A list of client profiles
     */
    public native final VectorJSO<ClientProfileJSO> getClients() /*-{
        return this.getClients();
    }-*/;


    /**
     * Returns the connections
     * @return A list of connection descriptions
     */
    public native final VectorJSO<ConnectionDescriptionJSO> getConnections() /*-{
        return this.getConnections();
    }-*/;


    /**
     * Returns the data
     * @return A list of data descriptions
     */
    public native final VectorJSO<DataDescriptionJSO> getData() /*-{
        return this.getData();
    }-*/;


    /**
     * Returns the data store location
     * @return the data location
     */
    public native final String getDataLocation() /*-{
        return this.getDataLocation();
    }-*/;

    /**
     * Returns the description
     * @return the description
     */
    public native final String getDescription() /*-{
        return this.getDescription();
    }-*/;

    /**
     * Returns the location of the event stream
     * @return the event location
     */
    public native final String getEventLocation() /*-{
        return this.getEventLocation();
    }-*/;

    /**
     * Returns the name
     * @return the name
     */
    public native final String getName() /*-{
        return this.getName();
    }-*/;

    /**
     * Returns the venue services
     * @return a list of service descriptions
     */
    public native final VectorJSO<ServiceDescriptionJSO> getServices() /*-{
        return this.getServices();
    }-*/;

    /**
     * Returns the location of the jabber service
     * @return the text location
     */
    public native final String getTextLocation() /*-{
        return this.getTextLocation();
    }-*/;

    /**
     * Returns the unique id
     * @return the unique id
     */
    public native final String getUniqueId() /*-{
        return this.getUniqueId();
    }-*/;

    /**
     * Returns the uri
     * @return the uri
     */
    public native final String getUri() /*-{
        return this.getUri();
    }-*/;

    public native final VenueList getVenueList()/*-{
        return this.getVenueList();
    }-*/;


}
