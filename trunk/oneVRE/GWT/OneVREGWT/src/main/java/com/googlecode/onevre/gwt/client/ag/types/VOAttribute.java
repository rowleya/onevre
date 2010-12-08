package com.googlecode.onevre.gwt.client.ag.types;


public class VOAttribute {

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

    public VOAttribute(VOAttributeJSO jso){
    	this.vo = jso.getVo();
    	this.group = jso.getGroup();
    	this.role = jso.getRole();
    	this.cap = jso.getCap();
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
	public String getCap() {
		return cap;
	}
	
	
	public String toString(){
		String out = vo +" G="+ group + " R=" + role + " C=" + cap;
		return out;
	}

/*
    public int hashCode(){
        return getDefaultVenueUrl().hashCode();
    }

    public boolean equals(Object o){
        return this.getDefaultVenueUrl().equals(((VOAttribute)o).getDefaultVenueUrl());
    }

    public String toLog(){
        return name+" = "+ getDefaultVenueUrl() + " (" + version +")";
    }
*/


}
