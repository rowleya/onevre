package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ApplicationDescriptionJSO extends JavaScriptObject {

    protected ApplicationDescriptionJSO() {
    }

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
     * Returns the mime type that the application opens
     * @return this.getthe mimeType
     */
    public final native String getMimeType() /*-{
        return this.getMimeType();
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public final native String getName() /*-{
        return this.getName();
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public final native String getUri() /*-{
        return this.getUri();
    }-*/;


}
