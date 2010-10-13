package com.googlecode.onevre.ag.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VenueServerType implements SoapSerializable {
    private static final long serialVersionUID = 1L;

    private static final String[] SOAP_FIELDS =
        new String[]{"protocol",
                     "name",
                     "url",
                     "port",
                     "defaultVenue",
                     "version"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};


    // The scheme of the profile (e.g. "user" or "service")
    private String protocol = "https";

    // The name of the venue server
    private String name = "";

    // The url address of the venue server
    private String url = "";

    // The port number of the venue server
    private int portNumber = 8000;

    // The external id of the client (e.g. public key)
    private String defaultVenue = "/Venues/default";

    // The external id of the client (e.g. public key)
    private String defaultVenueId = "";

    // The location of the client on the earth
    private String version = "";

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	@XmlElement
	public String getName() {
		return name;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the protocol
	 */
	@XmlElement
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the url
	 */
	@XmlElement
	public String getUrl() {
		return url;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * @return the portNumber
	 */
	@XmlElement
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * @param defaultVenue the defaultVenue to set
	 */
	public void setDefaultVenue(String defaultVenue) {
		this.defaultVenue = defaultVenue;
	}

	public void setDefaultVenueId(String uniqueId) {
		defaultVenueId = uniqueId;
	}

	/**
	 * @return the defaultVenue
	 */
	@XmlElement
	public String getDefaultVenue() {
		return defaultVenue;
	}

	@XmlElement
	public String getDefaultVenueId() {
		return defaultVenueId;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the version
	 */
	@XmlElement
	public String getVersion() {
		return version;
	}

	@XmlElement
	public String getDefaultVenueUrl() {
		String out = protocol + "://" + url + ":" + portNumber + defaultVenue;
		return out;
	}

	public String getBaseUrl() {
		String out = protocol + "://" + url + ":" + portNumber;
		return out;

	}

    public int hashCode(){
        return getDefaultVenueUrl().hashCode();
    }

    public boolean equals(Object o){
        return this.getDefaultVenueUrl().equals(((VenueServerType)o).getDefaultVenueUrl());
    }

    public String toLog(){
        return name+" = "+ getDefaultVenueUrl() + " (" + version +")";
    }

	public String getSoapType() {
        return "VenueServerType";
	}

	public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
	}

	public String[] getFields() {
        return SOAP_FIELDS;
	}

	public Object[] getTypes() {
        return SOAP_TYPES;
	}



}
