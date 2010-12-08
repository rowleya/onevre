package com.googlecode.onevre.ag.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VOAttribute implements SoapSerializable {
    private static final long serialVersionUID = 1L;

    private static final String[] SOAP_FIELDS =
        new String[]{"vo",
                     "group",
                     "role",
                     "cap"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};


    // The scheme of the profile (e.g. "user" or "service")
    private String vo = "";

    // The name of the venue server
    private String group = "";

    // The url address of the venue server
    private String role = "";

    // The external id of the client (e.g. public key)
    private String cap = "";

    public VOAttribute(){
    }

    public VOAttribute(String vo, String group, String role, String cap){
    	this.vo = vo;
    	this.group = group;
    	this.role = role;
    	this.cap = cap;
    }

	/**
	 * @param name the name to set
	 */
	public void setVo(String vo) {
		this.vo = vo;
	}

	/**
	 * @return the name
	 */
	@XmlElement
	public String getVo() {
		return vo;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the protocol
	 */
	@XmlElement
	public String getGroup() {
		return group;
	}

	/**
	 * @param url the url to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the url
	 */
	@XmlElement
	public String getRole() {
		return role;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setCap(String cap) {
		this.cap = cap;
	}


	/**
	 * @return the defaultVenue
	 */
	@XmlElement
	public String getCap() {
		return cap;
	}

	public String toString(){
		String out = vo +" G="+ group + " R=" + role + " C=" + cap;
		return out;
	}


    public int hashCode(){
        return toString().hashCode();
    }

    public boolean equals(Object o){
        return this.toString().equals(((VOAttribute)o).toString());
    }
/*
    public String toLog(){
        return name+" = "+ getDefaultVenueUrl() + " (" + version +")";
    }
*/

	public String getSoapType() {
        return "VOAttribute";
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
