/*
 * @(#)AppDataDescription.java
 * Created: 25-Mar-2008
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

package com.googlecode.onevre.ag.types.application;


import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Application Data Description
 * @author Tobias M Schiebeck
 * @version 1.0
 */
/*
<xs:complexType name=\"AppDataDescription\">
  <xs:sequence>
    <xs:element name=\"appId\" type=\"xs:string\"/>
    <xs:element name=\"key\" type=\"xs:string\"/>
    <xs:element name=\"value\" type=\"xs:string\"/>
    <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
  </xs:sequence>
</xs:complexType>
*/

public class AppDataDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"appId",
                     "key",
                     "value"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};

    // The application Id
    private String appId = null;

    // The application Data Key
    private String key = null;

    // The application Data Value
    private String value = null;

    /**
     * Returns the description
     * @return the description
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Returns the data key
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the data value
     * @return the data value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the shared application id
     * @param appId The shared Application id
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * Sets the data value
     * @param value The data value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the data key
     * @param key The data key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AppDataDescription"
     */
    public String getSoapType() {
        return "AppDataDescription";
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
     *	<li>"appId"</li>
     *	<li>"key"</li>
     *	<li>"value"</li>
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
     * <li>STRING_TYPE (appId)</li>
     * <li>STRING_TYPE (key)</li>
     * <li>STRING_TYPE (value)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two AppDataDescriptions in [appId && key && value]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        return ((this.appId.equals(((AppDataDescription)o).appId) &&
        		this.key.equals(((AppDataDescription)o).key)) &&
        		this.value.equals(((AppDataDescription)o).value));
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return appId.hashCode();
    }

}
