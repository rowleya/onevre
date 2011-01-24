package com.googlecode.onevre.gwt.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.StringJSO;

public class VenueClientController {

	static String vcc = null;

	public static void setVenueClientController(String vcc){
		VenueClientController.vcc = vcc;
	}

	public static JavaScriptObject getObjectDec(String xml){
		GWT.log("getObjectDec:" + xml);
		JavaScriptObject out = getPrivObjectDec(vcc,xml);
		GWT.log("returns:" + out.toString());
		return out;
	}

	public static String getStringDec(String xml){
		return getPrivStringDec(vcc,xml);
	}
	private static native JavaScriptObject getPrivObjectDec (String vcc, String xml)/*-{
		venueClientController = $doc.getElementById(vcc);
		return venueClientController.getObjectDec(xml);
	}-*/;

	public static String getXMLEnc(Object object){
		return getPrivXMLEnc(vcc,object).value();
	}
	private static native StringJSO getPrivXMLEnc (String vcc, Object object)/*-{
		venueClientController = $doc.getElementById(vcc);
		return venueClientController.getXMLEnc(object);
	}-*/;


	private static native String getPrivStringDec (String vcc, String xml)/*-{
		venueClientController = $doc.getElementById(vcc);
		return venueClientController.getObjectDec(xml);
	}-*/;


	public static void setClientProfile(ClientProfile clientProfile){
		setPrivClientProfile(vcc, clientProfile.getName(), clientProfile.getEmail(),
				clientProfile.getPhoneNumber(), clientProfile.getLocation(),
				clientProfile.getHomeVenue(), clientProfile.getProfileType());
	}

	private static native void setPrivClientProfile(String vcc, String name, String email,
			String phone, String location, String home, String type)/*-{
		venueClientController = $doc.getElementById(vcc);
		venueClientController.setClientProfile(type, name, email, phone, location, home);
	}-*/;

}
