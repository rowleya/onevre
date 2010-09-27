package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class ClientProfileJSO extends JavaScriptObject {


	protected ClientProfileJSO (){
	}

	public native final String getPhoneNumber() /*-{
		return this.getPhoneNumber();
	}-*/;

	public native final void setPhoneNumber(String phone) /*-{
		this.setPhoneNumber(phone);
	}-*/;

	public native final String getEmail() /*-{
		return this.getEmail();
	}-*/;

	public native final void setEmail(String email) /*-{
		this.setEmail(email);
	}-*/;

	public native final String getLocation() /*-{
		return this.getLocation();
	}-*/;

	public native final void setLocation(String location) /*-{
		this.setLocation(location);
	}-*/;

	public native final String getName() /*-{
		return this.getName();
	}-*/;

	public native final void setName(String name) /*-{
		this.setName(name);
	}-*/;

	public native final String getPublicId() /*-{
		return this.getPublicId();
	}-*/;

	public native final String getHomeVenue()  /*-{
		return this.getHomeVenue();
	}-*/;

	public native final void setHomeVenue(String homeVenue)/*-{
		this.setHomeVenue(homeVenue);
	}-*/;

	public native final String getProfileType()  /*-{
		return this.getProfileType();
	}-*/;

	public native final void setProfileType(String type)  /*-{
		this.setProfileType(type);
	}-*/;
}
