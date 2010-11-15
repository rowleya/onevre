package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class DataDescriptionEJSO extends JavaScriptObject {

	protected DataDescriptionEJSO (){
	}

    /**
     * Returns the last time the data was modified
     * @return this.getthe last modified time
     */
    public native final String getLastModified() /*-{
        return this.lastModified;
    }-*/;

    public native final String getExpires() /*-{
	    return this.expires;
	}-*/;

    /**
     * Returns the data type
     * @return this.getthe type
     */
    public native final String getType() /*-{
        return this.mimeType;
    }-*/;

    /**
     * Returns the checksum
     * @return this.getthe checksum
     */
    public native final String getChecksum() /*-{
        return this.checksum;
    }-*/;

    /**
     * Returns the description
     * @return this.getthe description
     */
    public native final String getDescription() /*-{
        return this.description;
    }-*/;

    /**
     * Returns the id
     * @return this.getthe id
     */
    public native final String getId() /*-{
        return this.id;
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public native final String getName() /*-{
        return this.name;
    }-*/;

    /**
     * Returns the owner
     * @return this.getthe owner
     */
    public native final String getOwner() /*-{
        return this.owner;
    }-*/;

    /**
     * Returns the size
     * @return this.getthe size
     */
    public native final String getSize() /*-{
        return this.size;
    }-*/;

    /**
     * Returns the status
     * @return this.getthe status
     */
    public native final String getStatus() /*-{
        return this.status;
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public native final String getUri() /*-{
        return this.uri;
    }-*/;

    /**
     * Returns the objectType
     * @return this.getthe objectType
     */
    public native final String getObjectType()/*-{
    	return this.objectType;
    }-*/;

    /**
     * Returns the hierarchyLevel
     * @return this.getthe hierarchyLevel
     */
    public native final String getHierarchyLevel()/*-{
    	return this.hierarchyLevel;
    }-*/;

    /**
     * Returns the id of the parent item
     * @return this.getthe id of the parent item
     */
   public native final String getParentId()/*-{
    	return this.parentId;
    }-*/;

}
