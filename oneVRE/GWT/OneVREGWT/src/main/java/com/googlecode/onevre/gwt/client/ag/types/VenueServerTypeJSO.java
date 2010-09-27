package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.JavaScriptObject;

public class VenueServerTypeJSO extends JavaScriptObject {

	protected VenueServerTypeJSO (){
	}

	public native final String getProtocol() /*-{
		return this.getProtocol();
	}-*/;

	public native final void setProtocol(String protocol) /*-{
		this.setProtocol(protocol);
	}-*/;


	public native final String getName() /*-{
		return this.getName();
	}-*/;

	public native final void setName(String name) /*-{
		this.setName(name);
	}-*/;

	public native final String getUrl() /*-{
		return this.getUrl();
	}-*/;

	public native final void setUrl(String url) /*-{
		this.setUrl(url);
	}-*/;

	public native final int getPortNumber() /*-{
		return this.getPortNumber();
	}-*/;

	public native final void setPortNumber(int portNumber) /*-{
		this.setPortNumber(portNumber);
	}-*/;

	public native final String getDefaultVenue() /*-{
		return this.getDefaultVenue();
	}-*/;

	public native final String getDefaultVenueId() /*-{
		return this.getDefaultVenueId();
	}-*/;

	public native final void setDefaultVenue(String defaultVenue)/*-{
		this.setDefaultVenue(defaultVenue);
	}-*/;

	public native final void setDefaultVenueId(String defaultVenueId)/*-{
		this.setDefaultVenueId(defaultVenueId);
	}-*/;

	public native final String getVersion()  /*-{
		return this.getVersion();
	}-*/;

	public native final void setVersion(String version)  /*-{
		this.setVersion(version);
	}-*/;
}
