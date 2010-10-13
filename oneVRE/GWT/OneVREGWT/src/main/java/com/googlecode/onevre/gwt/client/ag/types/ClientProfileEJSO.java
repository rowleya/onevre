package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ClientProfileEJSO extends JavaScriptObject {


	protected ClientProfileEJSO (){
	}

	public native final String getPhoneNumber() /*-{
		return this.phoneNumber;
	}-*/;

	public native final void setPhoneNumber(String phone) /*-{
		this.setPhoneNumber(phone);
	}-*/;

	public native final String getEmail() /*-{
		return this.email;
	}-*/;

	public native final void setEmail(String email) /*-{
		this.setEmail(email);
	}-*/;

	public native final String getLocation() /*-{
		return this.location;
	}-*/;

	public native final void setLocation(String location) /*-{
		this.setLocation(location);
	}-*/;

	public native final String getName() /*-{
		return this.name;
	}-*/;

	public native final void setName(String name) /*-{
		this.setName(name);
	}-*/;

	public native final String getPublicId() /*-{
		return this.publicId;
	}-*/;

	public native final String getHomeVenue()  /*-{
		return this.homeVenue;
	}-*/;

	public native final void setHomeVenue(String homeVenue)/*-{
		this.setHomeVenue(homeVenue);
	}-*/;

	public native final String getProfileType()  /*-{
		return this.profileType;
	}-*/;

	public native final void setProfileType(String type)  /*-{
		this.setProfileType(type);
	}-*/;
}
