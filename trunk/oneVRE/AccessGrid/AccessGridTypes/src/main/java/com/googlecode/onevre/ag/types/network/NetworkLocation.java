/*
 * @(#)NetworkLocation.java
 * Created: 23-Sep-2006
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

package com.googlecode.onevre.ag.types.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.googlecode.onevre.ag.types.ProviderProfile;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * An AG3 Network Location
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class NetworkLocation implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"id",
                     "privateId",
                     "host",
                     "port",
                     "type",
                     "profile"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     null };
                     //                ProviderProfile.ProviderProfile_TYPE };

    // The id
    private String id = "";

    // The private id
    private String privateId = "";

    // The host
    private String host = null;

    // The port
    private int port = 0;

    // The connection type (e.g. unicast, multicast)
    private String type = null;

    // The profile of the provider of the address (e.g. a bridge)
    private ProviderProfile profile = new ProviderProfile();

    /**
     * Returns the host
     * @return the host
     */
    @XmlElement
    public String getHost() {
        return host;
    }

    /**
     * Returns the id
     * @return the id
     */
    @XmlElement
    public String getId() {
        return id;
    }

    /**
     * Returns the port
     * @return the port
     */
    @XmlElement
    public int getPort() {
        return port;
    }

    /**
     * Returns the private id
     * @return the privateId
     */
    @XmlElement
    public String getPrivateId() {
        return privateId;
    }

    /**
     * Returns the profile of the provider
     * @return the profile
     */
    @XmlElement
    public ProviderProfile getProfile() {
        return profile;
    }

    /**
     * Returns the type of the connection
     * @return the type
     */
    @XmlElement
    public String getType() {
        return type;
    }

    /**
     * Sets the host
     * @param host The host
     */
    public void setHost(String host) {
        try {
            InetAddress hostAdd = InetAddress.getByName(host);
            this.host = hostAdd.getHostAddress();
        } catch (UnknownHostException e) {
            this.host = host;
        }
    }

    /**
     * Sets the id
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the port
     * @param port The port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the port
     * @param port The port
     */
    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }
    /**
     * Sets the private id
     * @param privateId The privateId
     */
    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    /**
     * Sets the profile
     * @param profile The profile
     */
    public void setProfile(ProviderProfile profile) {
        this.profile = profile;
    }

    /**
     * Sets the type
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "NetworkLocation"
     */
    public String getSoapType() {
        return "NetworkLocation";
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
     *	<li>"id"</li>
     *	<li>"privateId"</li>
     *	<li>"host"</li>
     *	<li>"port"</li>
     *	<li>"type"</li>
     *	<li>"profile"</li>
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
     * <li>STRING_TYPE (id)</li>
     * <li>STRING_TYPE (privateId)</li>
     * <li>STRING_TYPE (host)</li>
     * <li>INT_TYPE (port)</li>
     * <li>STRING_TYPE (type)</li>
     * <li>null (profile) -> {@link  ag3.interfaces.types.ProviderProfile}</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two NetworkLocations in [host && port]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if (object instanceof NetworkLocation) {
            NetworkLocation location = (NetworkLocation) object;
            if (this.host.equals(location.host)
                    && (this.port == location.port)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (host + ":" + port).hashCode();
    }

    /**
     * Returns a string representation of the object
     * @return "&lt;host&gt; : &lt;port&gt;"
     */
    public String toString() {
        return host + ":" + port;
    }
}
