/*
 * @(#)VenueDescription.java
 * Created: 21-May-2009
 * Version: 1.0
 * Copyright (c) 2005-2009, University of Manchester All rights reserved.
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

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AGTk AG Network Service Description
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"id",
                     "uri",
                     "name",
                     "description",
                     "encryptMedia",
                     "encryptionKey",
                     "connections",
                     "streams"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     null,
                     null};

    // The id of the venue
    private String id = null;

    // The uri of the venue
    private String uri = null;

    // The name of the venue
    private String name = null;

    // The description of the venue
    private String description = null;

    // Flag to define if Media is encrypted
    private int encryptMedia = 0;

    // The encryption key
    private String encryptionKey = null;

    // The connections of the Venue
    private Vector<ConnectionDescription> connections = new Vector<ConnectionDescription>();

    // The streams of the Venue
    private Vector<StreamDescription> streams = new Vector<StreamDescription>();


    /**
     * Default Constructor
     */
    public VenueDescription () {
        // do nothing
    }

    /**
     * @param id
     * @param name
     * @param description
     * @param encryptMedia
     * @param encryptionKey
     * @param connections
     * @param streams
     */
    public VenueDescription(String id, String name, String description, int encryptMedia, String encryptionKey, Vector<ConnectionDescription> connections, Vector<StreamDescription> streams ) {
        this.id=id;
        this.name=name;
        this.description=description;
        this.streams=streams;
        this.encryptMedia=encryptMedia;
        this.encryptionKey=encryptionKey;
        this.connections=connections;
    }

    /**
     * Returns the connections
     * @return the connections
     */
    public Vector<ConnectionDescription> getConnections() {
        return connections;
    }

    /**
     * Returns the streams
     * @return the streams
     */
    public Vector<StreamDescription> getStreams() {
        return streams;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Adds a connection
     * @param connection The connection to add
     */
    public void setConnections(ConnectionDescription connection) {
        this.connections.add(connection);
    }

    /**
     * Sets the connections
     * @param connections the connections
     */
    public void setConnections(Vector<ConnectionDescription> connections) {
        this.connections = connections;
    }

    /**
     * Adds a stream
     * @param stream The stream to add
     */
    public void setStreams (StreamDescription stream) {
        this.streams.add(stream);
    }

    /**
     * Sets the streams
     * @param streams the streams
     */
    public void setStreams (Vector<StreamDescription> streams) {
        this.streams = streams;
    }
   /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the uri
     * @param uri The service uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the id
     * @param id The id of the venue
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the package name
     * @return The package name
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "VenueDescription"
     */
    public String getSoapType() {
        return "VenueDescription";
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
     *	<ul><li>"name"</li><li>"uri"</li><li>"capabilities"</li><li>"packageName"</li></ul>
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
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (uri)</li>
     * <li>null (capabilities) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.application.Capability}</li>
     * <li>STRING_TYPE (packageName)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Returns a string representation of the object
     * @return "AGNetworkServiceDescription(&lt;ServiceName&gt;, &lt;ServiceURI&gt;)"
     */
    public String toString() {
        return "VenueDescription(" + name + ", " + uri + ")";
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object other){
        VenueDescription onsd = (VenueDescription)other;
        return uri.equals(onsd.uri);
    }

    public void setEncryptMedia(int encryptMedia) {
        this.encryptMedia = encryptMedia;
    }

    public int getEncryptMedia() {
        return encryptMedia;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

}
