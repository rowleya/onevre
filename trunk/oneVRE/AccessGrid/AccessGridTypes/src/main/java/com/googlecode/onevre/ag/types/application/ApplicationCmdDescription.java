/*
 * @(#)ApplicationCmdDescription.java
 * Created: 15-Mar-2008
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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Application Command Description
 * @author Andrew G D Rowley
 * @version 1.0
 */
/*
<xs:complexType name=\"ApplicationCmdDescription\">
 <xs:sequence>
 <xs:element name=\"appDesc\" type=\"tns:ApplicationDescription\"/>
 <xs:element name=\"verb\" nillable=\"true\" type=\"xs:string\"/>
 <xs:element name=\"cmd\" nillable=\"true\" type=\"xs:string\"/>
 <xs:element name=\"profile\" type=\"tns:ClientProfile\"/>
 <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
</xs:sequence>
</xs:complexType>
*/

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ApplicationCmdDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"appDesc",
                     "verb",
                     "cmd",
                     "profile"};

    private static final String[] SOAP_TYPES =
        new String[]{null,
                     STRING_TYPE,
                     STRING_TYPE,
                     null};

    // The application the command is issued to
    private ApplicationDescription appDesc = null;

    // a verbose description of the command
    private String verb = null;

    // The command
    private String cmd = null;

    // A client profile of the issuer of the command
    private ClientProfile profile = null;


    /**
     * Returns the description
     * @return the description
     */
    @XmlElement
    public ApplicationDescription getAppDesc() {
        return appDesc;
    }

    /**
     * Returns the id
     * @return the id
     */
    @XmlElement
    public String getVerb() {
        return verb;
    }

    /**
     * Returns the command to execute
     * @return the command to execute
     */
    @XmlElement
    public String getCmd() {
        return cmd;
    }

    /**
     * Returns the Client Profile
     * @return the Client Profile
     */
    @XmlElement
    public ClientProfile getProfile() {
        return profile;
    }

    /**
     * Sets the application description
     * @param appDesc The application description
     */
    public void setAppDesc(ApplicationDescription appDesc) {
        this.appDesc = appDesc;
    }

    /**
     * Sets the verbose description of the command
     * @param verb The verbose description of the command
     */
    public void setVerb(String verb) {
        this.verb = verb;
    }

    /**
     * Sets the command
     * @param cmd The command
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Sets the client profile the command is issued by
     * @param profile The name
     */
    public void setProfile(ClientProfile profile) {
        this.profile = profile;
    }


    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "ApplicationCmdDescription"
     */
    public String getSoapType() {
        return "ApplicationCmdDescription";
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
     *	<li>"appDesc"</li>
     *	<li>"verb"</li>
     *	<li>"cmd"</li>
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
     * <li>null (appDesc) -> {@link ag3.interfaces.com.googlecode.onevre.ag.common.types.application.ApplicationDescription}</li>
     * <li>STRING_TYPE (verb)</li>
     * <li>STRING_TYPE (cmd)</li>
     * <li>null (profile) -> {@link ag3.interfaces.types.ClientProfile}</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two AppDataDescriptions in [appDesc && profile]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        return (this.appDesc.equals(((ApplicationCmdDescription)o).appDesc) &&
        		this.profile.equals(((ApplicationCmdDescription)o).profile)) ;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode(){
        return verb.hashCode();
    }

}
