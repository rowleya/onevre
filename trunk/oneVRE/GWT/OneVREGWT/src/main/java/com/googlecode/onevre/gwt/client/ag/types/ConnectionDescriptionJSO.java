package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ConnectionDescriptionJSO extends JavaScriptObject {

	protected ConnectionDescriptionJSO (){
	}
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
     * Returns the uri
     * @return this.getthe uri
     */
    public native final String getUri() /*-{
        return this.getUri();
    }-*/;

}
