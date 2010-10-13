package com.googlecode.onevre.gwt.client.ag.types;

public class ClientProfile {

	private String phoneNumber;
	private String name;
	private String email;
	private String location;
	private String publicId;
	private String homeVenue;
	private String profileType;

	public ClientProfile (ClientProfileJSO jso) {
		name = jso.getName();
		email = jso.getEmail();
		phoneNumber = jso.getPhoneNumber();
		location = jso.getLocation();
		publicId = jso.getPublicId();
		homeVenue = jso.getHomeVenue();
		profileType = jso.getProfileType();
	};

	public ClientProfile (ClientProfileEJSO jso) {
		name = jso.getName();
		email = jso.getEmail();
		phoneNumber = jso.getPhoneNumber();
		location = jso.getLocation();
		publicId = jso.getPublicId();
		homeVenue = jso.getHomeVenue();
		profileType = jso.getProfileType();
	};

	public void setPhoneNumber(String phone) {
		this.phoneNumber = phone;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	};

	public void setEmail(String email) {
		this.email = email;
	};

	public String getLocation() {
		return location;
	};

	public void setLocation(String location) {
		this.location = location;
	};

	public String getName() {
		return name;
	};

	public void setName(String name) {
		this.name = name;
	};

	public String getPublicId() {
		return publicId;
	};

	public String getHomeVenue() {
		return homeVenue;
	};

	public void setHomeVenue(String homeVenue){
		this.homeVenue = homeVenue;
	};

	public String getProfileType() {
		return profileType;
	};

	public void setProfileType(String type) {
		this.profileType = type;
	};

    public boolean equals(Object o){
        return this.getPublicId().equals(((ClientProfile)o).getPublicId());
    }

    public int hashCode(){
        return publicId.hashCode();
    }
}
