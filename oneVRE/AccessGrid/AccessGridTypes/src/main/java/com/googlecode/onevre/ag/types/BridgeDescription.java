/*
 * @(#)BridgeDescription.java
 * Created: 13-Nov-2006
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

import java.util.Comparator;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * Represents a bridge
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeDescription implements SoapSerializable {

    /**
     * The rank of the bridge when it has not got one
     */
    public static final int UNRANKED = Integer.MAX_VALUE;

    /**
     * The ping time of the bridge when it is unreachable
     */
    public static final int UNREACHABLE = Integer.MAX_VALUE;

    private static final String[] SOAP_FIELDS =
        new String[]{"guid",
                     "name",
                     "host",
                     "port",
                     "serverType",
                     "description",
                     "portMin",
                     "portMax",
                     "pingTime",
                     "rank"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     INT_TYPE,
                     INT_TYPE};

    // The id of the bridge
    private String guid = null;

    // The name of the bridge
    private String name = null;

    // The description of the bridge
    private String description = null;

    // The host of the bridge
    private String host = null;

    // The port of the bridge
    private int port = 0;

    // The server type of the bridge
    private String serverType = null;

    // The minimum port of the bridge
    private int portMin = 0;

    // The maximum port of the bridge
    private int portMax = 0;

    // The last ping time of the bridge
    private int pingTime = UNREACHABLE;

    // The order of the bridge in the list of bridges
    private int rank = UNRANKED;

    // True if the bridge is enabled
    private boolean enabled = true;

    // True if the bridge has been pinged
    private boolean wasPinged = false;

    /**
     * Returns the host
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the port
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the portMax
     * @return the portMax
     */
    public int getPortMax() {
        return portMax;
    }

    /**
     * Returns the portMin
     * @return the portMin
     */
    public int getPortMin() {
        return portMin;
    }

    /**
     * Returns the serverType
     * @return the serverType
     */
    public String getServerType() {
        return serverType;
    }

    /**
     * Sets the host
     * @param host The host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port
     * @param port The port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the port as a string
     * @param port The port (String)
     */
    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    /**
     * Sets the portMax
     * @param portMax The portMax
     */
    public void setPortMax(int portMax) {
        this.portMax = portMax;
    }

    /**
     * Sets the portMax as a String
     * @param portMax The portMax (String)
     */
    public void setPortMax(String portMax) {
        this.portMax = Integer.parseInt(portMax);
    }

    /**
     * Sets the portMin
     * @param portMin The portMin
     */
    public void setPortMin(int portMin) {
        this.portMin = portMin;
    }

    /**
     * Sets the portMin as a string
     * @param portMin The portMin (String)
     */
    public void setPortMin(String portMin) {
        this.portMin = Integer.parseInt(portMin);
    }

    /**
     * Sets the serverType
     * @param serverType The serverType
     */
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    /**
     * Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the id
     * @param id The id
     */
    public void setGuid(String id) {
        this.guid = id;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ping time
     * @return The ping time in milliseconds
     */
    public int getPingTime() {
        return pingTime;
    }

    /**
     * Sets the ping time
     * @param pingTime The ping time in milliseconds
     */
    public void setPingTime(int pingTime) {
        this.pingTime = pingTime;
    }

    /**
     * Gets the rank of the bridge
     * @return The rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets the rank of the bridge
     * @param rank The new rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns if the bridge is enabled or not
     * @return True if the bridge is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the bridge
     * @param enabled True if the bridge is enabled, false otherwise
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "BridgeDescription"
     */
    public String getSoapType() {
        return "BridgeDescription";
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
     *	<li>"guid"</li>
     *	<li>"name"</li>
     *	<li>"host"</li>
     *	<li>"port"</li>
     *	<li>"serverType"</li>
     *	<li>"description"</li>
     *	<li>"portMin"</li>
     *	<li>"portMax"</li>
     *	<li>"pingTime"</li>
     *	<li>"rank"</li>
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
     * <li>STRING_TYPE (guid)</li>
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (host)</li>
     * <li>INT_TYPE (port)</li>
     * <li>STRING_TYPE (serverType)</li>
     * <li>STRING_TYPE (description)</li>
     * <li>INT_TYPE (portMin)</li>
     * <li>INT_TYPE (portMax)</li>
     * <li>INT_TYPE (pingTime)</li>
     * <li>INT_TYPE (rank)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two AppDataDescriptions in [name && host && port]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof BridgeDescription) {
            BridgeDescription b = (BridgeDescription) o;
            return new String(b.name + ":" + b.host + ":" + b.port).equals(
                    name + ":" + host + ":" + port);
        }
        return false;
    }

    /**
     * Returns a string representation of the object
     * @return "BridgeDescription: &lt;name&gt;:&lt;host&gt;:&lt;port&gt; (ping = &lt;pingTime&gt;)"
     */
    public String toString() {
        return "BridgeDescription: " + name + ":" + host + ":" + port +":"+ serverType
            + " (ping = " + pingTime + ")";
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new String(name + ":" + host + ":" + port).hashCode();
    }

    /**
     * Determines if the bridge has been pinged
     * @return True if the bridge has been pinged
     */
    public boolean wasPinged() {
        return wasPinged;
    }

    /**
     * Sets the pinged status of the bridge
     * @param pinged True if was pinged, false otherwise
     */
    public void setPinged(boolean pinged) {
        this.wasPinged = pinged;
    }

    /**
     * Compares bridges by rank
     * @author Andrew G D Rowley
     * @version 1.0
     */
    public static class BridgeRankComparator
            implements Comparator<BridgeDescription> {

        /**
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(BridgeDescription bridge1,
                BridgeDescription bridge2) {
            return bridge1.getRank() - bridge2.getRank();
        }
    }

    /**
     * Compares bridges by ping time
     * @author Andrew G D Rowley
     * @version 1.0
     */
    public static class BridgePingComparator
            implements Comparator<BridgeDescription> {

        /**
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(BridgeDescription bridge1,
                BridgeDescription bridge2) {
            return bridge1.getPingTime() - bridge2.getPingTime();
        }
    }

    /**
     * Compares bridges by name
     * @author Andrew G D Rowley
     * @version 1.0
     */
    public static class BridgeNameComparator
            implements Comparator<BridgeDescription> {

        /**
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(BridgeDescription b, BridgeDescription b2) {
            return b.getName().compareTo(b2.getName());
        }
    }

}
