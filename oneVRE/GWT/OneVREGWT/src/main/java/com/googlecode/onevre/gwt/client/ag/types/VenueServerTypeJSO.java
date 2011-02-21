package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VenueServerTypeJSO extends JavaScriptObject {

    protected VenueServerTypeJSO() {
    }

    public final native String getProtocol() /*-{
        return this.getProtocol();
    }-*/;

    public final native void setProtocol(String protocol) /*-{
        this.setProtocol(protocol);
    }-*/;


    public final native String getName() /*-{
        return this.getName();
    }-*/;

    public final native void setName(String name) /*-{
        this.setName(name);
    }-*/;

    public final native String getUrl() /*-{
        return this.getUrl();
    }-*/;

    public final native void setUrl(String url) /*-{
        this.setUrl(url);
    }-*/;

    public final native int getPortNumber() /*-{
        return this.getPortNumber();
    }-*/;

    public final native void setPortNumber(int portNumber) /*-{
        this.setPortNumber(portNumber);
    }-*/;

    public final native String getDefaultVenue() /*-{
        return this.getDefaultVenue();
    }-*/;

    public final native String getDefaultVenueId() /*-{
        return this.getDefaultVenueId();
    }-*/;

    public final native void setDefaultVenue(String defaultVenue) /*-{
        this.setDefaultVenue(defaultVenue);
    }-*/;

    public final native void setDefaultVenueId(String defaultVenueId) /*-{
        this.setDefaultVenueId(defaultVenueId);
    }-*/;

    public final native String getVersion() /*-{
        return this.getVersion();
    }-*/;

    public final native void setVersion(String version) /*-{
        this.setVersion(version);
    }-*/;
}
