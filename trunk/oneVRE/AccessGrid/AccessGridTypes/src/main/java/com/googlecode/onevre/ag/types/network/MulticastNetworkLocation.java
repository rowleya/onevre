/*
 * @(#)MulticastNetworkLocation.java
 * Created: 25-Sep-2006
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

/**
 * An AG3 MulticastNetworkLocation
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class MulticastNetworkLocation extends NetworkLocation {

    public static String TYPE = "multicast";

    // The default TTL value
    private static final int DEFAULT_TTL = 127;

    private static final String[] SOAP_FIELDS =
        new String[]{"ttl"};

    private static final String[] SOAP_TYPES =
        new String[]{INT_TYPE};

    // The ttl of the connection
    private int ttl = DEFAULT_TTL;

    /**
     * Creates a new MulticastNetworkLocation
     */
    public MulticastNetworkLocation() {
        super.setType("Multicast");
    }

    /**
     * Returns the ttl
     * @return the ttl
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * Sets the ttl
     * @param ttl The ttl
     */
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    /**
     * Sets the TTL
     * @param ttl The ttl
     */
    public void setTtl(String ttl) {
        this.ttl = Integer.parseInt(ttl);
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "MulticastNetworkLocation"
     */
    public String getSoapType() {
        return "MulticastNetworkLocation";
    }

    /**
     *
     * @see ag3.soap.SoapSerializable#getFields()
     */
    /**
     * Returns the fields that should be included with the soap Each of the fields should have a getter and a setter with the same name e.g. field is "test" there should be a "getTest" and a "setTest" method (note standard capitalisation)
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getFields()}</dd></dl>
     * @return the fields :
     *	<ul>
     *  <li>{@link ag3.interfaces.types.NetworkLocation#getFields()}</li>
     *	<li>"ttl"</li>
     *	</ul>
     */
    public String[] getFields() {
        String[] superFields = super.getFields();
        String[] fields = new String[superFields.length + SOAP_FIELDS.length];
        System.arraycopy(superFields, 0, fields, 0, superFields.length);
        System.arraycopy(SOAP_FIELDS, 0, fields, superFields.length,
                SOAP_FIELDS.length);
        return fields;
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
     * <li>{@link ag3.interfaces.types.NetworkLocation#getTypes()}</li>
     * <li>INT_TYPE (ttl)</li>
     * </ul>
     */
    public Object[] getTypes() {
        Object[] superTypes = super.getTypes();
        Object[] types = new String[superTypes.length + SOAP_FIELDS.length];
        System.arraycopy(superTypes, 0, types, 0, superTypes.length);
        System.arraycopy(SOAP_TYPES, 0, types, superTypes.length,
                SOAP_TYPES.length);
        return types;
    }

    /**
     * compares two MulticastNetworkLocations in [ {@link ag3.interfaces.types.NetworkLocation#equals(java.lang.Object)} && ttl ]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if (object instanceof MulticastNetworkLocation) {
            MulticastNetworkLocation location =
                (MulticastNetworkLocation) object;
            if (location.ttl != ttl) {
                return false;
            }
        }
        return super.equals(object);
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (getHost() + ":" + getPort() + ":" + ttl).hashCode();
    }

    /**
     * Returns a string representation of the object
     * @return {@link ag3.interfaces.types.NetworkLocation#toString()} :&lt;ttl&gt;
     */
    public String toString() {
        return super.toString() + ":" + ttl;
    }
}
