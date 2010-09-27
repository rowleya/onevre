package com.googlecode.onevre.gwt.client.ag.types;

public class ApplicationDescription {

	// The id of the application
    private String id = null;

    // The name of the application
    private String name = null;

    // The description of the application
    private String description = null;

    // The uri of the application
    private String uri = null;

    // The mime type that the application opens
    private String mimeType = null;

	public ApplicationDescription (ApplicationDescriptionJSO jso){
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
     * Returns the mime type that the application opens
     * @return the mimeType
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
     * Sets the mime type
     * @param mimeType The mimeType
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
