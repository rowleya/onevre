package com.googlecode.onevre.ag.agsecurity;

import java.util.Vector;

import org.w3c.dom.Node;

import com.googlecode.onevre.ag.types.VOAttribute;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;
import com.googlecode.onevre.utils.Utils;

public class Subject implements SoapSerializable {
/*
    <xs:complexType name=\"Subject\">
    <xs:sequence>
      <xs:element name=\"name\" type=\"xs:string\"/>
      <xs:element name=\"auth_type\" type=\"xs:string\"/>
      <xs:element name=\"auth_data\" type=\"xs:string\"/>
        <xs:element name=\"id\" type=\"xs:string\"/>
      <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
    </xs:sequence>
    </xs:complexType>
*/

    //the name of the subject
      private String name = null;
    // the type of authentication used for the subject
    private String auth_type = null;
    // opaque authentication specific data
    private String auth_data = null;
    // a globally unique identifier for this object
    private String id = Utils.generateID();

    private Vector<VOAttribute>  voAttributes = new Vector<VOAttribute>();

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "auth_type",
                     "auth_data",
                     "id",
                     "voAttributes" //OneVRE extension
                     };

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     null
                     };

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    public Vector<VOAttribute> getVoAttributes(){
    	return voAttributes;
    }

    public void setVoAttribute (VOAttribute voAttribute){
    	this.voAttributes.add(voAttribute);
    }

    public void setVoAttributes(Vector<String> voAttributes){
    	this.voAttributes.clear();
    	for (String voText : voAttributes)
    	{
    		this.voAttributes.add(new VOAttribute(voText));
    	}
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the authentication type
     * @return the authentication type
     */
    public String getAuth_type() {
        return auth_type;
    }

    /**
     * Sets the authentication type
     * @param auth_type The authentication type
     */
    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    /**
     * Returns the authentication type
     * @return the authentication type
     */
    public String getAuth_data() {
        return auth_data;
    }

    /**
     * Sets the authentication data
     * @param auth_data The authentication data
     */
    public void setAuth_data(String auth_data) {
        this.auth_data = auth_data;
    }

    public boolean equals(Object o){
        if (o == null) {
            return false;
        }
        if (!(o instanceof Subject)) {
            return false;
        }
        Subject c = (Subject) o;
        if ((c.name == null) || (name == null)) {
            return false;
        }
        if (!name.equals(c.name)) {
            return false;
        }
        if ((auth_data == null)){
            if (c.auth_data == null) {
                return true;
            }
            return false;
        }
        return auth_data.equals(c.auth_data);
    }

    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    public String getSoapType() {
        return "Subject";
    }

    public String[] getFields() {
        return SOAP_FIELDS;
    }

    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    public void parseXml(PolicyParser parser, Node node) {
        parser.parse(node);
        name = parser.getAttributes().get("name");
        auth_data = parser.getAttributes().get("auth_data");
        auth_type = parser.getAttributes().get("auth_type");
    }

    public String toString(){
    	return name;
    }

    public String toXml(){
        String xml = "<" +getSoapType();
        xml += " auth_data + \""+ auth_data + "\"";
        xml	+= " auth_type + \""+ auth_type + "\"";
        xml	+= " name=\"" + name + "\"";
        xml	+= "/>";
        return xml;
    }

}
