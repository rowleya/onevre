package com.googlecode.onevre.gwt.client.ag.types;

public class ServiceDescription{

    // The id of the service
    private String id = null;

    // The name of the service
    private String name = null;

    // The description of the service
    private String description = null;

    // The uri of the service
    private String uri = null;

    // The mime type of the service
    private String mimeType = null;

	public ServiceDescription (ServiceDescriptionJSO jso){
		id = jso.getId();
		name = jso.getName();
		description = jso.getDescription();
		uri = jso.getUri();
		mimeType = jso.getMimeType();
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
     * Returns the mime type
     * @return the mime type
     */
    public String getMimeType() {
        return mimeType;
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
     * @return he uri
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
     * Sets the mime type
     * @param mimeType The mime type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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


}
