package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class DataDescriptionEJSO extends JavaScriptObject {

    protected DataDescriptionEJSO() {
    }

    /**
     * Returns the last time the data was modified
     * @return this.getthe last modified time
     */
    public final native String getLastModified() /*-{
        return this.lastModified;
    }-*/;

    public final native String getExpires() /*-{
        return this.expires;
    }-*/;

    /**
     * Returns the data type
     * @return this.getthe type
     */
    public final native String getType() /*-{
        return this.mimeType;
    }-*/;

    /**
     * Returns the checksum
     * @return this.getthe checksum
     */
    public final native String getChecksum() /*-{
        return this.checksum;
    }-*/;

    /**
     * Returns the description
     * @return this.getthe description
     */
    public final native String getDescription() /*-{
        return this.description;
    }-*/;

    /**
     * Returns the id
     * @return this.getthe id
     */
    public final native String getId() /*-{
        return this.id;
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public final native String getName() /*-{
        return this.name;
    }-*/;

    /**
     * Returns the owner
     * @return this.getthe owner
     */
    public final native String getOwner() /*-{
        return this.owner;
    }-*/;

    /**
     * Returns the size
     * @return this.getthe size
     */
    public final native String getSize() /*-{
        return this.size;
    }-*/;

    /**
     * Returns the status
     * @return this.getthe status
     */
    public final native String getStatus() /*-{
        return this.status;
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public final native String getUri() /*-{
        return this.uri;
    }-*/;

    /**
     * Returns the objectType
     * @return this.getthe objectType
     */
    public final native String getObjectType()/*-{
        return this.objectType;
    }-*/;

    /**
     * Returns the hierarchyLevel
     * @return this.getthe hierarchyLevel
     */
    public final native String getHierarchyLevel()/*-{
        return this.hierarchyLevel;
    }-*/;

    /**
     * Returns the id of the parent item
     * @return this.getthe id of the parent item
     */
   public final native String getParentId()/*-{
        return this.parentId;
    }-*/;

}
