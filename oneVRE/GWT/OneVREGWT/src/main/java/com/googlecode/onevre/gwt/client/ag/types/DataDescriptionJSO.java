package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class DataDescriptionJSO extends JavaScriptObject {

	protected DataDescriptionJSO (){
	}

    /**
     * Returns the last time the data was modified
     * @return this.getthe last modified time
     */
    public native final String getLastModified() /*-{
        return this.getLastModified();
    }-*/;

    public native final String getExpires() /*-{
	    return this.getExpires();
	}-*/;

    /**
     * Returns the data type
     * @return this.getthe type
     */
    public native final String getType() /*-{
        return this.getType();
    }-*/;

    /**
     * Returns the checksum
     * @return this.getthe checksum
     */
    public native final String getChecksum() /*-{
        return this.getChecksum();
    }-*/;

    /**
     * Returns the description
     * @return this.getthe description
     */
    public native final String getDescription() /*-{
        return this.getDescription();
    }-*/;

    /**
     * Returns the id
     * @return this.getthe id
     */
    public native final String getId() /*-{
        return this.getId();
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public native final String getName() /*-{
        return this.getName();
    }-*/;

    /**
     * Returns the owner
     * @return this.getthe owner
     */
    public native final String getOwner() /*-{
        return this.getOwner();
    }-*/;

    /**
     * Returns the size
     * @return this.getthe size
     */
    public native final int getSize() /*-{
        return this.getSize();
    }-*/;

    /**
     * Returns the status
     * @return this.getthe status
     */
    public native final String getStatus() /*-{
        return this.getStatus();
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public native final String getUri() /*-{
        return this.getUri();
    }-*/;

    /**
     * Returns the objectType
     * @return this.getthe objectType
     */
    public native final String getObjectType()/*-{
    	return this.getObjectType();
    }-*/;

    /**
     * Returns the hierarchyLevel
     * @return this.getthe hierarchyLevel
     */
    public native final int getHierarchyLevel()/*-{
    	return this.getHierarchyLevel();
    }-*/;

    /**
     * Returns the id of the parent item
     * @return this.getthe id of the parent item
     */
   public native final String getParentId()/*-{
    	return this.getParentId();
    }-*/;

}
