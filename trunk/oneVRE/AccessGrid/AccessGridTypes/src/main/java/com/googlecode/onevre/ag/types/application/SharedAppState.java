/*
 * @(#)SharedAppState.java
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

import java.util.Vector;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Shared Application State
 * @author Tobias M Schiebeck
 * @version 1.0
 */

/*
<xs:complexType name=\"SharedAppState\">
<xs:sequence>
<xs:element maxOccurs=\"1\" minOccurs=\"0\" name=\"name\" type=\"xs:string\"/>
<xs:element maxOccurs=\"1\" minOccurs=\"0\" name=\"description\" type=\"xs:string\"/>
<xs:element maxOccurs=\"1\" minOccurs=\"0\" name=\"id\" type=\"xs:string\"/>
<xs:element maxOccurs=\"1\" minOccurs=\"0\" name=\"mimeType\" type=\"xs:string\"/>
<xs:element maxOccurs=\"1\" minOccurs=\"0\" name=\"uri\" type=\"xs:string\"/>
<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"data\" type=\"tns:AppDataDescription\"/>
<xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
</xs:sequence>
</xs:complexType>
*/

public class SharedAppState implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "description",
                     "id",
                     "mimeType",
                     "uri",
                     "data"
                     };

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     null};

    // The id of the Shared Application
    private String id = null;

    // The name of the Shared Application
    private String name = null;

    // The Shared Application description
    private String description = null;

    // The uri of the Shared Application
    private String uri = null;

    // The mimeType of the Shared Application
    private String mimeType = null;

    // A list of Application Data descriptions
    private Vector<AppDataDescription> data= new Vector<AppDataDescription>();

    /**
     * Returns the applications
     * @return A list of application descriptions
     */
    public Vector<AppDataDescription> getData() {
        return data;
    }

    /**
     * Returns the data store location
     * @return the data location
     */
    public String getId() {
        return id;
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
     * Returns the mimeType
     * @return the mimeType
     */
    public String getMimeType(){
        return mimeType;
    }

    /**
     * Adds some data
     * @param datum The data to add
     */
    public void setData(AppDataDescription datum) {
        data.add(datum);
    }

    /**
     * Adds a list of data items
     * @param data The list of data items to add
     */
    public void setData(Vector<AppDataDescription> data) {
        this.data=data;
    }

    /**
     * Updates some data
     * @param datum The data to update
     */
    public void updateData(AppDataDescription datum) {
        data.set(data.indexOf(datum),datum);
    }

    /**
     * removes some data
     * @param datum The data to remove
     */
    public void removeData(AppDataDescription datum) {
        data.remove(datum);
    }

    /**
     * Sets the description
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the mimeType
     * @param mimeType The mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the id
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "SharedAppState"
     */
    public String getSoapType() {
        return "SharedAppState";
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
     *	<li>"name"</li>
     *	<li>"description"</li>
     *	<li>"id"</li>
     *	<li>"mimeType"</li>
     *	<li>"uri"</li>
     *	<li>"data"</li>
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
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (description)</li>
     * <li>STRING_TYPE (id)</li>
     * <li>STRING_TYPE (mimeType)</li>
     * <li>STRING_TYPE (uri)</li>
     * <li>null (data) -> {@link ag3.interfaces.types.AppDataDescription}</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

}
