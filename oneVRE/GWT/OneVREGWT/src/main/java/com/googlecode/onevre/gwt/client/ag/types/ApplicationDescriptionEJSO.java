package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ApplicationDescriptionEJSO  extends JavaScriptObject {

    protected ApplicationDescriptionEJSO() {
    }

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
     * Returns the mime type that the application opens
     * @return this.getthe mimeType
     */
    public final native String getMimeType() /*-{
        return this.mimeType;
    }-*/;

    /**
     * Returns the name
     * @return this.getthe name
     */
    public final native String getName() /*-{
        return this.name;
    }-*/;

    /**
     * Returns the uri
     * @return this.getthe uri
     */
    public final native String getUri() /*-{
        return this.uri;
    }-*/;


}
