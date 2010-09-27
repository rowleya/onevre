/*
 * @(#)ClientProfile.java
 * Created: 08-Jun-2006
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.ag.types;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;
import com.googlecode.onevre.web.common.Defaults;


/**
 * A Client of AG3
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientProfile implements SoapSerializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String[] SOAP_FIELDS =
        new String[]{"profileType",
                     "name",
                     "email",
                     "phoneNumber",
                     "publicId",
                     "location",
                     "venueClientURL",
                     "homeVenue",
                     "privateId",
                     "distinguishedName",
                     "techSupportInfo",
                     "connectionId",
                     "gmtoffset",
                     "beacon",
                     "audio",
                     "video",
                     "display",
                     "lat",
                     "long"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     FLOAT_TYPE,
                     FLOAT_TYPE};

    // The type of the profile (e.g. "user" or "service")
    private String profileType = "user";

    // The name of the client
    private String name = "";

    // The e-mail address of the client
    private String email = "";

    // The phone number of the client
    private String phoneNumber = "";

    // The external id of the client (e.g. public key)
    private String publicId = "";

    // The location of the client on the earth
    private String location = "";

    // The url of client for soap requests
    private String venueClientURL = "";

    // The home venue of the client
    private String homeVenue = "";

    // The internal id of the client (e.g. private key)
    private String privateId = "";

    // The DN of the client (i.e. in a certificate)
    private String distinguishedName = "";

    // The technical support information of the client
    private String techSupportInfo = "";

    // The connection id of the client
    private String connectionId = "";

    // The clients offset from gmt time
    private int gmtoffset = 0;

    // 1 if the client is running a beacon or 0 otherwise
    private int beacon = 0;

    // 1 if the client is running audio or 0 otherwise
    private int audio = 0;

    // 1 if the client is running video or 0 otherwise
    private int video = 0;

    // 1 if the client is running display or 0 otherwise
    private int display = 0;

    // The latitude location of the client
    private float lat = 0.0f;

    // The longitude location of the client
    private float longg = 0.0f;

    /**
     * Gets the audio value
     * @return 1 if audio or 0 otherwise
     */
    public int getAudio() {
        return audio;
    }

    /**
     * Sets the audio value
     * @param audio 1 if audio, 0 otherwise
     */
    public void setAudio(int audio) {
        this.audio = audio;
    }

    /**
     * Sets the audio value
     * @param audio 1 if audio, 0 otherwise
     */
    public void setAudio(String audio) {
        this.audio = Integer.parseInt(audio);
    }

    /**
     * Gets the beacon value
     * @return 1 if beacon, 0 otherwise
     */
    public int getBeacon() {
        return beacon;
    }

    /**
     * Sets the beacon value
     * @param beacon 1 if beacon, 0 otherwise
     */
    public void setBeacon(int beacon) {
        this.beacon = beacon;
    }
    /**
     * Sets the beacon value
     * @param beacon 1 if beacon, 0 otherwise
     */
    public void setBeacon(String beacon) {
        this.beacon = Integer.parseInt(beacon);
    }
    /**
     * Gets the connection id
     * @return the connection id
     */
    public String getConnectionId() {
        return connectionId;
    }

    /**
     * Sets the connection id
     * @param connectionId the connection id
     */
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * Gets the display
     * @return 1 if display, 0 otherwise
     */
    public int getDisplay() {
        return display;
    }
    /**
     * Sets the display
     * @param display 1 if display, 0 otherwise
     */
    public void setDisplay(int display) {
        this.display = display;
    }
    /**
     * Sets the display
     * @param display 1 if display, 0 otherwise
     */
    public void setDisplay(String display) {
        this.display = Integer.parseInt(display);
    }
    /**
     * Gets the dn
     * @return the dn
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the dn
     * @param distinguishedName
     */
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    /**
     * Gets the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the beacon value
     * @param email the e-mail
     */
    public void setEmail(String email) {
        if (email!=null) {
            this.email = email;
        }
    }

    /**
     * Gets the gmt offset
     * @return the offset
     */
    public int getGmtoffset() {
        return gmtoffset;
    }

    /**
     * Sets the gmt offset
     * @param gmtoffset The offset
     */
    public void setGmtoffset(int gmtoffset) {
        this.gmtoffset = gmtoffset;
    }
    /**
     * Sets the gmt offset
     * @param gmtoffset The offset
     */
    public void setGmtoffset(String gmtoffset) {
        this.gmtoffset = Integer.parseInt(gmtoffset);
    }
    /**
     * Gets the home venue
     * @return the home venue
     */
    public String getHomeVenue() {
        return homeVenue;
    }

    /**
     * Sets the home venue
     * @param homeVenue the home venue
     */
    public void setHomeVenue(String homeVenue) {
        if (homeVenue != null){
            this.homeVenue = homeVenue;
        }
    }

    /**
     * Gets the latitude
     * @return the lat
     */
    public float getLat() {
        return lat;
    }

    /**
     * Sets the latitude
     * @param lat the lat
     */
    public void setLat(float lat) {
        this.lat = lat;
    }
    /**
     * Sets the latitude
     * @param lat the lat
     */
    public void setLat(String lat) {
        this.lat = Float.parseFloat(lat);
    }
    /**
     * Gets the location
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location
     * @param location the location
     */
    public void setLocation(String location) {
        if (location != null){
            this.location = location;
        }
    }

    /**
     * Gets the longitude
     * @return the long
     */
    public float getLong() {
        return longg;
    }

    /**
     * Sets the longitude
     * @param longg the longitude
     */
    public void setLong(float longg) {
        this.longg = longg;
    }
    /**
     * Sets the longitude
     * @param longg the longitude
     */
    public void setLong(String longg) {
        this.longg = Float.parseFloat(longg);
    }
    /**
     * Gets the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name name
     */
    public void setName(String name) {
        if (name !=null) {
            this.name = name;
        }
    }

    /**
     * Gets the phone number
     * @return the number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number
     * @param phoneNumber the number
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null){
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * Gets the private id
     * @return the id
     */
    public String getPrivateId() {
        return privateId;
    }

    /**
     * Sets the private id
     * @param privateId the id
     */
    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    /**
     * Gets the profile type
     * @return the type
     */
    public String getProfileType() {
        return profileType;
    }

    /**
     * Sets the profile type
     * @param profileType the type
     */
    public void setProfileType(String profileType) {
        if (profileType != null){
            this.profileType = profileType;
        }
    }

    /**
     * Gets the public id
     * @return the id
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * Sets the public id
     * @param publicId the id
     */
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    /**
     * Gets the tech support info
     * @return the info
     */
    public String getTechSupportInfo() {
        return techSupportInfo;
    }

    /**
     * Sets the tech support info
     * @param techSupportInfo the info
     */
    public void setTechSupportInfo(String techSupportInfo) {
        this.techSupportInfo = techSupportInfo;
    }

    /**
     * Gets the venue client url
     * @return the url
     */
    public String getVenueClientURL() {
        return venueClientURL;
    }

    /**
     * Sets the venue client url
     * @param venueClientURL the url
     */
    public void setVenueClientURL(String venueClientURL) {
        this.venueClientURL = venueClientURL;
    }

    /**
     * Gets the video
     * @return 1 if video or 0 otherwise
     */
    public int getVideo() {
        return video;
    }

    /**
     * Sets the video
     * @param video 1 if video, 0 otherwise
     */
    public void setVideo(int video) {
        this.video = video;
    }
    /**
     * Sets the video
     * @param video 1 if video, 0 otherwise
     */
    public void setVideo(String video) {
        this.video = Integer.parseInt(video);
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "ClientProfile"
     */
    public String getSoapType() {
        return "ClientProfile";
    }

    /**
     * Returns the namespace of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getNameSpace()}</dd></dl>
     * @return the namespace - "http://www.accessgrid.org/v3.0"
     */
    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    /**
     * Returns the fields that should be included with the soap Each of the fields should have a getter and a setter with the same name e.g. field is "test" there should be a "getTest" and a "setTest" method (note standard capitalisation)
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getFields()}</dd></dl>
     * @return the fields :
     *	<ul>
     *	<li>"profileType"</li>
     *	<li>"name"</li>
     *	<li>"email"</li>
     *	<li>"phoneNumber"</li>
     *	<li>"publicId"</li>
     *	<li>"location"</li>
     *	<li>"venueClientURL"</li>
     *	<li>"homeVenue"</li>
     *	<li>"privateId"</li>
     *	<li>"distinguishedName"</li>
     *	<li>"techSupportInfo"</li>
     *	<li>"connectionId"</li>
     *	<li>"gmtoffset"</li>
     *	<li>"beacon"</li>
     *	<li>"audio"</li>
     *	<li>"video"</li>
     *	<li>"display"</li>
     *	<li>"lat"</li>
     *	<li>"long"</li>
     *	</ul>
     */
    public String[] getFields() {
        return SOAP_FIELDS;
    }

    /**
     * Returns the types of the fields that should be included with the soap<br>
     * If the field is not a vector or array each of the types must be one of:
     * <ol><li>A fully qualified url</li>
     * <li>A standard XML type starting with xsd:</li>
     * <li>Null if the field is itself SoapSerializable</li></ol>
     * If the return type is an array or vector, the type must be one of:
     * <ol><li>A type as above if all the values have the same type</li>
     * <li>A Vector of types if the field is a vector with different types</li></ol>
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getTypes()}</dd></dl>
     * @return the types :
     * <ul>
     * <li>STRING_TYPE (profileType)</li>
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (email)</li>
     * <li>STRING_TYPE (phoneNumber)</li>
     * <li>STRING_TYPE (publicId)</li>
     * <li>STRING_TYPE (location)</li>
     * <li>STRING_TYPE (venueClientURL)</li>
     * <li>STRING_TYPE (homeVenue)</li>
     * <li>STRING_TYPE (privateId)</li>
     * <li>STRING_TYPE (distinguishedName)</li>
     * <li>STRING_TYPE (techSupportInfo)</li>
     * <li>STRING_TYPE (connectionId)</li>
     * <li>INT_TYPE (gmtoffset)</li>
     * <li>INT_TYPE (beacon)</li>
     * <li>INT_TYPE (audio)</li>
     * <li>INT_TYPE (video)</li>
     * <li>INT_TYPE (display)</li>
     * <li>FLOAT_TYPE (lat)</li>
     * <li>FLOAT_TYPE (long)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two ClientProfiles in [ publicId ]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        return this.publicId.equals(((ClientProfile)o).publicId);
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return publicId.hashCode();
    }

    /**
     * Gets the Icon to represent the ClientProfile as a Group or personal node
     * @return the profile Icon
     */
    public String getProfileIcon(){
        String profileImg=Defaults.PAG_USER_NODE_IMG;
        if (getProfileType().equals("user"))
            profileImg = Defaults.PAG_USER_PARTICIPANT_IMG;
        else
            profileImg = Defaults.PAG_USER_NODE_IMG;
        return profileImg;
     }

    /**
     * Returns a string to go into the log file
     * @return log file string
     */
    public String toLog(){
        return name+", "+email+", "+phoneNumber+", " + location;
    }

}
