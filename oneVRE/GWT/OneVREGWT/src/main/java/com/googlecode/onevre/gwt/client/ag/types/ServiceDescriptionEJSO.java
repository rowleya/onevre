package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ServiceDescriptionEJSO extends JavaScriptObject {

	protected ServiceDescriptionEJSO (){
	}

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
     * Returns the mime type
     * @return this.getthe mime type
     */
    public native final String getMimeType() /*-{
        return this.mimeType;
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public native final String getName() /*-{
        return this.name;
    }-*/;

    /**
     * Returns the uri
     * @return this.gethe uri
     */
    public native final String getUri() /*-{
        return this.uri;
    }-*/;


}
