package com.googlecode.onevre.gwt.client.ag.types;

public class ConnectionDescription {

    // The id of the connection
    private String id = null;

    // The name of the connection
    private String name = null;

    // The description of the connection
    private String description = null;

    // The uri of the connection
    private String uri = null;

    public ConnectionDescription(ConnectionDescriptionJSO jso) {
        id = jso.getId();
        name = jso.getName();
        description = jso.getDescription();
        uri = jso.getUri();
    }

    public ConnectionDescription(ConnectionDescriptionEJSO jso) {
        id = jso.getId();
        name = jso.getName();
        description = jso.getDescription();
        uri = jso.getUri();
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the id
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String toString() {
        String out = name + "( " + uri + " )";
        return out;
    }

}
