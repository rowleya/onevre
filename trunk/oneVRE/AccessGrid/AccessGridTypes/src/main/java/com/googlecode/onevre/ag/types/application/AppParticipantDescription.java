/*
 * @(#)AppParticipantDescription.java
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



import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Application Participant Description
 * @author Tobias M Schiebeck
 * @version 1.0
 */

/*
<xs:complexType name=\"AppParticipantDescription\">
<xs:sequence>
  <xs:element name=\"appId\" type=\"xs:string\"/>
  <xs:element name=\"clientProfile\" nillable=\"true\" type=\"tns:ClientProfile\"/>
  <xs:element name=\"status\" type=\"xs:string\"/>
  <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
</xs:sequence>
</xs:complexType>
*/

public class AppParticipantDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"appId",
                     "clientProfile",
                     "status"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     null,
                     STRING_TYPE};

    // The application Id
    private String appId = null;

    // The client profile of the participant
    private ClientProfile clientProfile = null;

    // The status of the Participant (connected / master)
    private String status = null;

    /**
     * Returns the application id
     * @return the application id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Returns the client profile of the participant
     * @return the client profile of the participant
     */
    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    /**
     * Returns the participants status (connected / master)
     * @return the participants status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the application id
     * @param appId The application id
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * Sets the clientProfile of the participant
     * @param clientProfile The clientProfile
     */
    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    /**
     * Sets the participants status
     * @param status The participants status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AppParticipantDescription"
     */
    public String getSoapType() {
        return "AppParticipantDescription";
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
     *	<li>"clientProfile"</li>
     *	<li>"status"</li>
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
     * <li>null (clientProfile) -> {@link ag3.interfaces.types.ClientProfile}</li>
     * <li>STRING_TYPE (status)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two AppDataDescriptions in [appId && clientProfile]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        return (this.appId.equals(((AppParticipantDescription)o).appId) &&
        		this.clientProfile.equals(((AppParticipantDescription)o).clientProfile));
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return appId.hashCode();
    }

}
