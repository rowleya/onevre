/*
 * @(#)Capability.java
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

import java.io.Serializable;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * An AG3 Capability
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class Capability implements SoapSerializable, Serializable {

    private static final long serialVersionUID = 1L;
//	public static final String Capability_TYPE = "Capability";

    /**
     * Audio type
     */
    public static final String AUDIO = "audio";

    /**
     * Video type
     */
    public static final String VIDEO = "video";

    /**
     * Producer role
     */
    public static final String PRODUCER = "producer";

    /**
     * Consumer role
     */
    public static final String CONSUMER = "consumer";

    /**
     * LocationType PREFERRED_UNICAST
     */
    public static final String PREFERRED_UNICAST = "UC";

    /**
     * LocationType MULTICAST
     */
    public static final String MULTICAST = "MC";

   /**
     * The default rate for video
     */
    public static final int VIDEO_RATE = 90000;

    /**
     * The rate for 16KHz audio
     */
    public static final int AUDIO_16KHZ = 16000;

    // The size of the random number
    private static final int RANDOM_SIZE = 100000;

    private static final String[] SOAP_FIELDS =
        new String[]{"role",
                     "type",
                     "codec",
                     "rate",
                     "serviceId",
                     "channels",
                     "locationType",
                     "port",
                     "host"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE};

    // The number of instances
    private static int instanceCount = 0;

    // The role of the capability (usually producer or consumer)
    private String role = "";

    // The type of the capability (e.g. audio, video)
    private String type = "";

    // The codec in use
    private String codec = "";

    // The RTP rate of the codec
    private int rate = 0;

    // The unique id of the service
    private String serviceId = "";

    // The number of channels (e.g. 1 for mono, 2 for stereo)
    private int channels = 0;

    // Specifies whether to use multicast assigned by Venue or Unicast as specified by service
    private String locationType = "";

    // only applicable if used PREFERRED_UNICAST: port of the service to use
    private int port = 0;

    // only applicable if used PFEFERRED_UNICAST: ip of the service to use
    private String host = "";


    /**
     * Creates a new empty Capability
     *
     */
    public Capability() {
        // Does Nothing
    }

    /**
     * Creates a new Capability
     * @param role The role (consumer or producer)
     * @param type The type (audio or video or something else)
     * @param codec The codec
     * @param rate The rate
     * @param channels The channels
     */
    public Capability(String role, String type, String codec, int rate,
            int channels) {
        this.role = role;
        this.type = type;
        this.codec = codec;
        this.rate = rate;
        this.channels = channels;
        this.serviceId = String.valueOf(System.currentTimeMillis())
            + (Math.random() * RANDOM_SIZE) + (instanceCount++);
    }

    /**
     * Creates a new Capability
     * @param role The role (consumer or producer)
     * @param type The type (audio or video or something else)
     * @param codec The codec
     * @param rate The rate
     * @param channels The channels
     * @param locationType Specifies whether to use multicast assigned by Venue or Unicast as specified by service
     * @param port the port of the service to use (only applicable if used PREFERRED_UNICAST)
     * @param host the ip of the service to use (only applicable if used PFEFERRED_UNICAST)
     */
    public Capability(String role, String type, String codec, int rate,
            int channels, String locationType, int port, String host) {
        this.role = role;
        this.type = type;
        this.codec = codec;
        this.rate = rate;
        this.channels = channels;
        this.serviceId = String.valueOf(System.currentTimeMillis())
            + (Math.random() * RANDOM_SIZE) + (instanceCount++);
        this.locationType=locationType;
        this.host=host;
        this.port=port;
    }

    /**
     * Returns the number of channels
     * @return the number of channels
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Returns the codec in use
     * @return the codec name
     */
    public String getCodec() {
        return codec;
    }

    /**
     * Returns the RTP rate
     * @return the rate
     */
    public int getRate() {
        return rate;
    }

    /**
     * Returns the role (e.g. producer or consumer)
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the unique id of the service
     * @return the id
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Returns the data type (e.g. audio or video)
     * @return The data type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the locationType Specifies whether to use multicast assigned by Venue or Unicast as specified by service
     * @return the locationType Specifies whether to use multicast assigned by Venue or Unicast as specified by service
     */
    public String getLocationType(){
        return locationType;
    }

    /**
     * Returns the host ip of the service to use (only applicable if used PFEFERRED_UNICAST)
     * @return the host ip of the service to use (only applicable if used PFEFERRED_UNICAST)
     */
    public String getHost(){
        return host;
    }

    /**
     * Returns the port of the service to use (only applicable if used PREFERRED_UNICAST)
     * @return the port of the service to use (only applicable if used PREFERRED_UNICAST)
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the number of channels
     * @param channels The channels
     */
    public void setChannels(int channels) {
        this.channels = channels;
    }

    /**
     * Sets the channels
     * @param channels The channels
     */
    public void setChannels(String channels) {
        this.channels = Integer.parseInt(channels);
    }
    /**
     * Sets the codec
     * @param codec The codec
     */
    public void setCodec(String codec) {
        this.codec = codec;
    }

    /**
     * Sets the rate
     * @param rate The rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Sets the rate
     * @param rate The rate
     */
    public void setRate(String rate) {
        this.rate = Integer.parseInt(rate);
    }
    /**
     * Sets the role
     * @param role The role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the service id
     * @param serviceId The serviceId
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Sets the type
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets  the locationType Specifies whether to use multicast assigned by Venue or Unicast as specified by service
     * @param locationType Specifies whether to use multicast assigned by Venue or Unicast as specified by service
     */
    public void setLocationType(String locationType){
        this.locationType=locationType;
    }

    /**
     * Sets the host ip of the service to use (only applicable if used PFEFERRED_UNICAST)
     * @param host the host ip of the service to use (only applicable if used PFEFERRED_UNICAST)
     */
    public void setHost(String host){
        this.host=host;
    }

    /**
     * Sets the port of the service to use (only applicable if used PREFERRED_UNICAST)
     * @param port the port of the service to use (only applicable if used PREFERRED_UNICAST)
     */
    public void setPort(int port) {
        this.port=port;
    }

    /**
     * Sets the port of the service to use (only applicable if used PREFERRED_UNICAST)
     * @param port the port of the service to use (only applicable if used PREFERRED_UNICAST)
     */
    public void setPort(String port) {
        this.port=Integer.parseInt(port);
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "Capability"
     */
    public String getSoapType() {
        return "Capability";
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
     *	<li>"role"</li>
     *	<li>"type"</li>
     *	<li>"codec"</li>
     *	<li>"rate"</li>
     *	<li>"serviceId"</li>
     *	<li>"channels"</li>
     *	<li>"locationType"</li>
     *	<li>"port"</li>
     *	<li>"host"</li>
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
     * <li>STRING_TYPE ("role")</li>
     * <li>STRING_TYPE ("type")</li>
     * <li>STRING_TYPE ("codec")</li>
     * <li>INT_TYPE ("rate")</li>
     * <li>STRING_TYPE ("serviceId")</li>
     * <li>INT_TYPE ("channels")</li>
     * <li>STRING_TYPE ("locationType")</li>
     * <li>INT_TYPE ("port")</li>
     * <li>STRING_TYPE ("host")</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * Returns true if the capability matches this one
     * @param capability The capability to check
     * @return True if a match is made, false otherwise
     */
    public boolean matches(Capability capability) {
        if (!capability.type.equals(type) || !capability.codec.equals(codec)
                || (capability.rate != rate)
                || (capability.channels != channels)) {
            return false;
        }
        return true;
    }

    /**
     * compares two Capabilities by matching them
     * @see ag3.interfaces.types.Capability#matches(ag3.interfaces.types.Capability)
     */
    public boolean equals(Object o) {
        if (o instanceof Capability) {
            return matches((Capability) o);
        }
        return false;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (type + ":" + codec + ":" + rate + ":" + channels).hashCode();
    }

    /**
     * Returns a string representation of the object
     * @return "type=&lt;type&gt;, role=&lt;role&gt;, codec=&lt;codec&gt;, channels=&lt;channels&gt;, rate=&lt;rate&gt;"
     */
     public String toString() {
        return "type=" + type + ", role=" + role + ", codec=" + codec +
            ", channels=" + channels + ", rate=" + rate + "\n";
    }

}
