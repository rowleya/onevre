/*
 * @(#)StreamDescription.java
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

package com.googlecode.onevre.ag.types;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.network.UnicastNetworkLocation;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * An AG3 StreamDescription
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class StreamDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"id",
                     "name",
                     "location",
                     "description",
                     "capability",
                     "encryptionFlag",
                     "encryptionKey",
                     "static",
                     "networkLocations"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     null,
                     STRING_TYPE,
                     null,
                     INT_TYPE,
                     STRING_TYPE,
                     BOOLEAN_TYPE,
                     null};

    // The id of the stream
    private String id = "";

    // The name of the stream
    private String name = "";

    // The network location of the stream
    private NetworkLocation location = null;

    // The description of the stream
    private String description = "";

    // The capabilities of the stream
    private Vector<Capability> capability = new Vector<Capability>();

    // The encryption status of the stream (1 for encrypted, 0 for not)
    private int encryptionFlag = 0;

    // The encryption key
    private String encryptionKey = "";

    // True if the stream has statically assigned addresses
    private boolean sstatic = false;

    // The network locations of the stream
    private Vector<NetworkLocation> networkLocations =
        new Vector<NetworkLocation>();

    /**
     * Returns the static addressability of the stream
     * @return true if the stream is statically addressed
     */
    @XmlElement
    public boolean getStatic() {
        return sstatic;
    }

    /**
     * Returns the capabilities of the stream (name must be singular for soap)
     * @return A vector of capabilities
     */
    @XmlElement
    public Vector<Capability> getCapability() {
        return capability;
    }

    /**
     * Returns the description
     * @return the description
     */
    @XmlElement
    public String getDescription() {
        return description;
    }

    /**
     * Returns the encryption flag
     * @return 1 if encrypted or 0 otherwise
     */
    @XmlElement
    public int getEncryptionFlag() {
        return encryptionFlag;
    }

    /**
     * Returns the encryption key
     * @return the encryption key
     */
    @XmlElement
    public String getEncryptionKey() {
        return encryptionKey;
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
     * Returns the network location of the stream
     * @return a NetworkLocation object
     */
    @XmlElement
    public NetworkLocation getLocation() {
        return location;
    }

    /**
     * Returns the name
     * @return the name
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Returns all the network locations of the stream
     * @return Returns the network locations
     */
    @XmlElement
    public Vector<NetworkLocation> getNetworkLocations() {
        return networkLocations;
    }

    /**
     * Sets the static state of the addressing of the stream
     * @param sstatic The _static
     */
    public void setStatic(boolean sstatic) {
        this.sstatic = sstatic;
    }

   /**
     * Sets the static state of the addressing of the stream
     * @param sstatic The _static
     */
    public void setStatic(String sstatic) {
        this.sstatic = Boolean.parseBoolean(sstatic);
    }

    /**
     * Adds a capability to the stream (name is for soap deserializer)
     * @param capability The capability to add
     */
    public void setCapability(Capability capability) {
        this.capability.add(capability);
    }

    /**
     * Sets the capabilities of the stream
     * @param capability The capabilities to add
     */
    public void setCapability(Vector<Capability> capability) {
        this.capability = capability;
    }

    /**
     * Sets the capabilities
     * @param capabilities The capabilities to set
     */
    public void setCapabilities(Vector<Capability> capabilities) {
        this.capability = capabilities;
    }
    /**
     * Sets the capabilities
     * @param capability The capability to set
     */
    public void setCapabilities(Capability capability) {
        if (this.capability == null) {
            this.capability = new Vector<Capability>();
        }
        this.capability.add(capability);
    }

    /**
     * Sets the description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the encryption flag
     * @param encryptionFlag The encryption
     */
    public void setEncryptionFlag(int encryptionFlag) {
        this.encryptionFlag = encryptionFlag;
    }

    /**
     * Sets the encryption flag
     * @param encryptionFlag The encryption
     */
    public void setEncryptionFlag(String encryptionFlag) {
        this.encryptionFlag = Integer.parseInt(encryptionFlag);
    }

    /**
     * Sets the encryption key
     * @param encryptionKey The encryption key
     */
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Sets the id
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the location
     * @param location The location
     */
    public void setLocation(NetworkLocation location) {
        this.location = location;
    }
    /**
     * Sets the location
     * @param location The location
     */
    public void setLocation(MulticastNetworkLocation location) {
        this.location = location;
    }
    /**
     * Sets the location
     * @param location The location
     */
    public void setLocation(UnicastNetworkLocation location) {
        this.location = location;
    }
   /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a network location (name is for soap deserializer)
     * @param networkLocations The networkLocations
     */
    public void setNetworkLocations(NetworkLocation networkLocations) {
        if (!this.networkLocations.contains(networkLocations)) {
            this.networkLocations.add(networkLocations);
        }
    }

    /**
     * Sets the network locations
     * @param networkLocations The networkLocations
     */
    public void setNetworkLocations(Vector<NetworkLocation> networkLocations) {
        this.networkLocations = networkLocations;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "StreamDescription"
     */
    public String getSoapType() {
        return "StreamDescription";
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
     *	<li>"name"</li>
     *	<li>"location"</li>
     *	<li>"description"</li>
     *	<li>"capability"</li>
     *	<li>"encryptionFlag"</li>
     *	<li>"encryptionKey"</li>
     *	<li>"static"</li>
     *	<li>"networkLocations"</li>
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
     * <li>STRING_TYPE (name)</li>
     * <li>null (location) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.network.NetworkLocation}</li>
     * <li>STRING_TYPE (description)</li>
     * <li>null (capability) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.application.Capability}</li>
     * <li>INT_TYPE (encryptionFlag)</li>
     * <li>STRING_TYPE (encryptionKey)</li>
     * <li>BOOLEAN_TYPE (static)</li>
     * <li>null (networkLocations) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.network.NetworkLocation}</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two StreamDescriptions in [ id ]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof StreamDescription) {
            return this.id.equals(((StreamDescription) o).id);
        }
        return false;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Determines if the two streams contain the same set of capabilities
     * @param s The stream to compare with
     * @return True if the streams have the same capabilities, false otherwise
     */
    public boolean hasSameCapabilitiesAs(StreamDescription s) {
        if (s.capability.size() != capability.size()) {
            return false;
        }
        return s.capability.containsAll(capability);
    }

    /**
     * Determines if a stream contains at least the capabilities of this stream
     * @param s The stream to compare with
     * @return True if all the capabilities of this stream are in s
     */
    public boolean isCapabilitySubsetOf(StreamDescription s) {
        return s.capability.containsAll(capability);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String out = "StreamDescription: {" + id + ", ";
        out +=  name + ", ";
        out +=  location + ", ";
        out +=  description + ", ";
        out +=  capability + ", ";
        out +=  encryptionFlag + ", ";
        out +=  encryptionKey + ", ";
        out +=  sstatic + ", ";
        out +=  "(" + networkLocations + ") } ";
        return out;
    }
}
