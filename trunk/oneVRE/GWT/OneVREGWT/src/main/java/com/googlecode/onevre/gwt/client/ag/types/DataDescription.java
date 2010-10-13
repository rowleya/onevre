package com.googlecode.onevre.gwt.client.ag.types;

public class DataDescription {

    // The id of the data
    private String id = null;

    // The name of the data
    private String name = null;

    // The description of the data
    private String description = null;

    // The uri of the data
    private String uri = null;

    // The status of the data
    private String status = null;

    // The size of the data in bytes
    private int size = 0;

    // The checksum of the data
    private String checksum = null;

    // The name of the owner of the data
    private String owner = null;

    // The mime type of the data
    private String type = null;

    // The last time the data was modified
    private String lastModified = null;

    // The last time the data was modified
    private String expires = "";

    // The type of the data object
    private String objectType = null;

    // The level of the object in the hierarchy
    private int hierarchyLevel = 0;

    // The id of the item that is above this one in the hierarchy
    private String parentId = null;

	public DataDescription (String name, String description, String expires){
		this.name = name;
		this.description = description;
		this.expires = expires;
	}

	public DataDescription (DataDescriptionJSO jso){
	    id = jso.getId();
	    name = jso.getName();
	    description = jso.getDescription();
	    uri = jso.getUri();
	    status = jso.getStatus();
	    size = Integer.valueOf(jso.getSize());
	    checksum = jso.getChecksum();
	    owner = jso.getOwner();
	    type = jso.getType();
	    expires = jso.getExpires();
	    lastModified = jso.getLastModified();
	    objectType = jso.getObjectType();
	    hierarchyLevel = Integer.valueOf(jso.getHierarchyLevel());
	    parentId = jso.getParentId();
	}

	public DataDescription (DataDescriptionEJSO jso){
	    id = jso.getId();
	    name = jso.getName();
	    description = jso.getDescription();
	    uri = jso.getUri();
	    status = jso.getStatus();
	    size = Integer.valueOf(jso.getSize());
	    checksum = jso.getChecksum();
	    owner = jso.getOwner();
	    type = jso.getType();
	    expires = jso.getExpires();
	    lastModified = jso.getLastModified();
	    objectType = jso.getObjectType();
	    hierarchyLevel = Integer.valueOf(jso.getHierarchyLevel());
	    parentId = jso.getParentId();
	}

    /**
     * Returns the last time the data was modified
     * @return the last modified time
     */
    public String getLastModified() {
        return lastModified;
    }
    public String getExpires() {
        return expires;
    }

    /**
     * Returns the data type
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the last modified time
     * @param lastModified The last Modified time
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * Sets the data time
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the checksum
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
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
     * Returns the owner
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the size
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the status
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the objectType
     * @return the objectType
     */
    public String getObjectType(){
    	return objectType;
    }

    /**
     * Returns the hierarchyLevel
     * @return the hierarchyLevel
     */
    public int getHierarchyLevel(){
    	return hierarchyLevel;
    }

    /**
     * Checks if it data is a presentation
     * @return true if data is a presentation
     */
    public boolean isPresentation(){
    	if (name.endsWith(".ppt"))
    		return true;
    	return false;
    }

    /**
     * Returns the id of the parent item
     * @return the id of the parent item
     */
   public String getParentId(){
    	return parentId;
    }

    /**
     * Sets the checksum
     * @param checksum The checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
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
     * Sets the owner
     * @param owner The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets the size
     * @param size The size
     */
    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Sets the size
     * @param size The size
     */
    public void setSize(String size) {
        this.size = Integer.parseInt(size);
    }

    /**
     * Sets the status
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the hierarchyLevel
     * @param hierarchyLevel the hierarchyLevel to set
     */
    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }
    /**
     * Sets the hierarchyLevel
     * @param hierarchyLevel the hierarchyLevel to set
     */
    public void setHierarchyLevel(String hierarchyLevel) {
        this.hierarchyLevel = Integer.parseInt(hierarchyLevel);
    }

    /**
     * Sets the objectType
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Sets the parentId
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String toString(){
	    String out = "Id: " +id +"\n";
	    	out += "Name: "+ name +"\n";
	    	out += "Description: "+ description +"\n";
	    	out += "Size: " + size +"\n";
	    	out += "lastModified: " + lastModified +"\n";
	    	out += "expires: " + expires +"\n";
		return out;
    }

    public boolean equals(Object other){
    	if (other.getClass()!=DataDescription.class){
    		return false;
    	}
    	if (id.equals(((DataDescription)other).id)){
    		return true;
    	}
    	return false;
    }

}
