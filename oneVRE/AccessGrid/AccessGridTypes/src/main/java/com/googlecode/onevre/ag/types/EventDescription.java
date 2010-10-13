package com.googlecode.onevre.ag.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;


/**
 * Represents an event from soap
 * @author Andrew G D Rowley
 * @version 1
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class EventDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[] {"channelId",
            "senderId",
            "data",
            "eventType"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};

    private String channelId = "";

    private String senderId = "";

    private Object data = null;

    private String eventType = "";

    /**
     * Gets the channel id
     * @return the channel id
     */
    @XmlElement
    public String getChannelId() {
        return channelId;
    }

    /**
     * Sets the channel id
     * @param channelId the channel id
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Gets the data
     * @return the data
     */
    @XmlElement
    public Object getData() {
        return data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(float data) {
        this.data = data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(double data) {
        this.data = data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(boolean data) {
        this.data = data;
    }

    /**
     * Gets the event type
     * @return the event type
     */
    @XmlElement
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the event type
     * @param eventType the event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the sender id
     * @return the sender id
     */
    @XmlElement
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender id
     * @param senderId the sender id
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
     *	<li>"channelId"</li>
     *	<li>"senderId"</li>
     *	<li>"data"</li>
     *	<li>"eventType"</li>
     *	</ul>
     */
    public String[] getFields() {
        return SOAP_FIELDS;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "EventDescription"
     */
    public String getSoapType() {
        return "EventDescription";
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
     * <li>STRING_TYPE (channelId)</li>
     * <li>STRING_TYPE (senderId)</li>
     * <li>STRING_TYPE (data)</li>
     * <li>STRING_TYPE (eventType)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

}
