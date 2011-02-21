package com.googlecode.onevre.gwt.client.ag.types;


import com.google.gwt.core.client.JavaScriptObject;

public class VenueStateJSO extends JavaScriptObject {

    protected VenueStateJSO() {
    }
    /**
     * Returns the applications
     * @return A list of application descriptions
     */
    public final native VectorJSO<ApplicationDescriptionJSO> getApplications() /*-{
        return this.getApplications();
    }-*/;

    /**
     * Returns the clients
     * @return A list of client profiles
     */
    public final native VectorJSO<ClientProfileJSO> getClients() /*-{
        return this.getClients();
    }-*/;


    /**
     * Returns the connections
     * @return A list of connection descriptions
     */
    public final native VectorJSO<ConnectionDescriptionJSO> getConnections() /*-{
        return this.getConnections();
    }-*/;


    /**
     * Returns the data
     * @return A list of data descriptions
     */
    public final native VectorJSO<DataDescriptionJSO> getData() /*-{
        return this.getData();
    }-*/;


    /**
     * Returns the data store location
     * @return the data location
     */
    public final native String getDataLocation() /*-{
        return this.getDataLocation();
    }-*/;

    /**
     * Returns the description
     * @return the description
     */
    public final native String getDescription() /*-{
        return this.getDescription();
    }-*/;

    /**
     * Returns the location of the event stream
     * @return the event location
     */
    public final native String getEventLocation() /*-{
        return this.getEventLocation();
    }-*/;

    /**
     * Returns the name
     * @return the name
     */
    public final native String getName() /*-{
        return this.getName();
    }-*/;

    /**
     * Returns the venue services
     * @return a list of service descriptions
     */
    public final native VectorJSO<ServiceDescriptionJSO> getServices() /*-{
        return this.getServices();
    }-*/;

    /**
     * Returns the location of the jabber service
     * @return the text location
     */
    public final native String getTextLocation() /*-{
        return this.getTextLocation();
    }-*/;

    /**
     * Returns the unique id
     * @return the unique id
     */
    public final native String getUniqueId() /*-{
        return this.getUniqueId();
    }-*/;

    /**
     * Returns the uri
     * @return the uri
     */
    public final native String getUri() /*-{
        return this.getUri();
    }-*/;

    public final native VenueList getVenueList()/*-{
        return this.getVenueList();
    }-*/;


}
