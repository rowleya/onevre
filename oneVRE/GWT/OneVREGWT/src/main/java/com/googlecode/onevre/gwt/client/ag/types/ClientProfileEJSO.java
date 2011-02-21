package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ClientProfileEJSO extends JavaScriptObject {


    protected ClientProfileEJSO() {
    }

    public final native String getPhoneNumber() /*-{
        return this.phoneNumber;
    }-*/;

    public final native void setPhoneNumber(String phone) /*-{
        this.setPhoneNumber(phone);
    }-*/;

    public final native String getEmail() /*-{
        return this.email;
    }-*/;

    public final native void setEmail(String email) /*-{
        this.setEmail(email);
    }-*/;

    public final native String getLocation() /*-{
        return this.location;
    }-*/;

    public final native void setLocation(String location) /*-{
        this.setLocation(location);
    }-*/;

    public final native String getName() /*-{
        return this.name;
    }-*/;

    public final native void setName(String name) /*-{
        this.setName(name);
    }-*/;

    public final native String getPublicId() /*-{
        return this.publicId;
    }-*/;

    public final native String getHomeVenue()  /*-{
        return this.homeVenue;
    }-*/;

    public final native void setHomeVenue(String homeVenue)/*-{
        this.setHomeVenue(homeVenue);
    }-*/;

    public final native String getProfileType()  /*-{
        return this.profileType;
    }-*/;

    public final native void setProfileType(String type)  /*-{
        this.setProfileType(type);
    }-*/;
}
