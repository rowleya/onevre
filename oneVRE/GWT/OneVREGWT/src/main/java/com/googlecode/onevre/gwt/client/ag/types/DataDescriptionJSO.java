package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class DataDescriptionJSO extends JavaScriptObject {

    protected DataDescriptionJSO() {
    }

    /**
     * Returns the last time the data was modified
     * @return this.getthe last modified time
     */
    public final native String getLastModified() /*-{
        return this.getLastModified();
    }-*/;

    public final native String getExpires() /*-{
        return this.getExpires();
    }-*/;

    /**
     * Returns the data type
     * @return this.getthe type
     */
    public final native String getType() /*-{
        return this.getType();
    }-*/;

    /**
     * Returns the checksum
     * @return this.getthe checksum
     */
    public final native String getChecksum() /*-{
        return this.getChecksum();
    }-*/;

    /**
     * Returns the description
     * @return this.getthe description
     */
    public final native String getDescription() /*-{
        return this.getDescription();
    }-*/;

    /**
     * Returns the id
     * @return this.getthe id
     */
    public final native String getId() /*-{
        return this.getId();
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public final native String getName() /*-{
        return this.getName();
    }-*/;

    /**
     * Returns the owner
     * @return this.getthe owner
     */
    public final native String getOwner() /*-{
        return this.getOwner();
    }-*/;

    /**
     * Returns the size
     * @return this.getthe size
     */
    public final native int getSize() /*-{
        return this.getSize();
    }-*/;

    /**
     * Returns the status
     * @return this.getthe status
     */
    public final native String getStatus() /*-{
        return this.getStatus();
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public final native String getUri() /*-{
        return this.getUri();
    }-*/;

    /**
     * Returns the objectType
     * @return this.getthe objectType
     */
    public final native String getObjectType()/*-{
        return this.getObjectType();
    }-*/;

    /**
     * Returns the hierarchyLevel
     * @return this.getthe hierarchyLevel
     */
    public final native int getHierarchyLevel()/*-{
        return this.getHierarchyLevel();
    }-*/;

    /**
     * Returns the id of the parent item
     * @return this.getthe id of the parent item
     */
   public final native String getParentId()/*-{
        return this.getParentId();
    }-*/;

}
