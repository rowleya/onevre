package com.googlecode.onevre.ag.types.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AGBridgeConnectorDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "description",
                     "launchUrl",
                     "serverType",
                     "bridgeClass"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};


    // The name of the service
    private String name = null;

    // The description of the service
    private String description = null;

    // The url used to launch the service
    private String launchUrl = null;

    // The directory holding the service on the server
    private String serverType = null;

    // The class name of the Service to be run
    private String bridgeClass = null;


    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "AGBridgeConnectorDescription"
     */
    public String getSoapType() {
        return "AGBridgeConnectorDescription";
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
     *	<li>"launchUrl"</li>
     *	<li>"packageName"</li>
     *	<li>"serviceClass"</li>
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
     * <ul><li>STRING_TYPE (name)</li><li>STRING_TYPE (description)</li><li>STRING_TYPE (launchUrl)</li><li>STRING_TYPE (packageName)</li><li>STRING_TYPE (serviceClass)</li></ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Gets the name of the service
     * @return The name of the service
     */
    @XmlElement
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the service
     * @param name The name of the service
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Sets the description of the service
     * @param description The description of the service
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Returns the launchUrl
     * @return the launchUrl
     */
    @XmlElement
    public String getLaunchUrl() {
        return launchUrl;
    }

    /**
     * Sets the launchUrl
     * @param launchUrl the launchUrl to set
     */
    public void setLaunchUrl(String launchUrl) {
        this.launchUrl = launchUrl;
    }

    /**
     * Returns the serverType
     * @return the serverType
     */
    @XmlElement
    public String getServerType() {
        return serverType;
    }

    /**
     * Sets the serverType
     * @param serverType the packageName to set
     */
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    /**
     * @param bridgeClass the serviceClass to set
     */
    public void setBridgeClass(String bridgeClass) {
        this.bridgeClass = bridgeClass;
    }

    /**
     * @return the serviceClass
     */
    @XmlElement
    public String getBridgeClass() {
        return bridgeClass;
    }

    public String toString() {
        String out = "\nName: " + name + "\ndescription: " + description
            + "\nlaunchUrl: " + launchUrl + "\n serverType: " + serverType
            + "\nbridgeClass: " + bridgeClass + "\n\n";
        return out;
    }
}
