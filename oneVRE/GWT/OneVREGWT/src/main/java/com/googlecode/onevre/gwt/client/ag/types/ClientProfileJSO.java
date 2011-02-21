package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ClientProfileJSO extends JavaScriptObject {


    protected ClientProfileJSO() {
    }

    public final native String getPhoneNumber() /*-{
        return this.getPhoneNumber();
    }-*/;

    public final native void setPhoneNumber(String phone) /*-{
        this.setPhoneNumber(phone);
    }-*/;

    public final native String getEmail() /*-{
        return this.getEmail();
    }-*/;

    public final native void setEmail(String email) /*-{
        this.setEmail(email);
    }-*/;

    public final native String getLocation() /*-{
        return this.getLocation();
    }-*/;

    public final native void setLocation(String location) /*-{
        this.setLocation(location);
    }-*/;

    public final native String getName() /*-{
        return this.getName();
    }-*/;

    public final native void setName(String name) /*-{
        this.setName(name);
    }-*/;

    public final native String getPublicId() /*-{
        return this.getPublicId();
    }-*/;

    public final native String getHomeVenue()  /*-{
        return this.getHomeVenue();
    }-*/;

    public final native void setHomeVenue(String homeVenue)/*-{
        this.setHomeVenue(homeVenue);
    }-*/;

    public final native String getProfileType()  /*-{
        return this.getProfileType();
    }-*/;

    public final native void setProfileType(String type)  /*-{
        this.setProfileType(type);
    }-*/;
}
