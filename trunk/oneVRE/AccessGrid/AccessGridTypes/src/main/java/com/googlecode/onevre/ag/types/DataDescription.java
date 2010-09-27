/*
 * @(#)DataDescription.java
 * Created: 15-Sep-2006
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


import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * The AG3 Data Description
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DataDescription implements SoapSerializable {

    private static final String[] SOAP_FIELDS =
        new String[]{"id",
                     "name",
                     "description",
                     "uri",
                     "status",
                     "size",
                     "checksum",
                     "owner",
                     "type",
                     "lastModified",
                     "objectType",
                     "hierarchyLevel",
                     "parentId",
                     "expires"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     INT_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};

    // The id of the data
    private String id = null;

    // The name of the data
    private String name = null;

    // The description of the data
    private String description = null;

    // The uri of the data
    private String uri = null;

    // The status of the data
    private String status = null;

    // The size of the data in bytes
    private int size = 0;

    // The checksum of the data
    private String checksum = null;

    // The name of the owner of the data
    private String owner = null;

    // The mime type of the data
    private String type = null;

    // The last time the data was modified
    private String lastModified = null;

    // The last time the data was modified
    private String expires = null;

    // The type of the data object
    private String objectType = null;

    // The level of the object in the hierarchy
    private int hierarchyLevel = 0;

    // The id of the item that is above this one in the hierarchy
    private String parentId = null;

    /**
     * Returns the last time the data was modified
     * @return the last modified time
     */
    public String getLastModified() {
        return lastModified;
    }
    public String getExpires() {
        return expires;
    }

    /**
     * Returns the data type
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the last modified time
     * @param lastModified The last Modified time
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Sets the last modified time
     * @param lastModified The last Modified time
     */
    public void setExpires (String expires) {
        this.expires = expires;
    }

    /**
     * Sets the data time
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the checksum
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
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
     * Returns the owner
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the size
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the status
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the uri
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the objectType
     * @return the objectType
     */
    public String getObjectType(){
    	return objectType;
    }

    /**
     * Returns the hierarchyLevel
     * @return the hierarchyLevel
     */
    public int getHierarchyLevel(){
    	return hierarchyLevel;
    }

    /**
     * Checks if it data is a presentation
     * @return true if data is a presentation
     */
    public boolean isPresentation(){
    	if (name.endsWith(".ppt"))
    		return true;
    	return false;
    }

    /**
     * Returns the id of the parent item
     * @return the id of the parent item
     */
   public String getParentId(){
    	return parentId;
    }

    /**
     * Sets the checksum
     * @param checksum The checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
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
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the owner
     * @param owner The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Sets the size
     * @param size The size
     */
    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Sets the size
     * @param size The size
     */
    public void setSize(String size) {
        this.size = Integer.parseInt(size);
    }

    /**
     * Sets the status
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the uri
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Sets the hierarchyLevel
     * @param hierarchyLevel the hierarchyLevel to set
     */
    public void setHierarchyLevel(int hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }
    /**
     * Sets the hierarchyLevel
     * @param hierarchyLevel the hierarchyLevel to set
     */
    public void setHierarchyLevel(String hierarchyLevel) {
        this.hierarchyLevel = Integer.parseInt(hierarchyLevel);
    }

    /**
     * Sets the objectType
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Sets the parentId
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type - "DataDescription"
     */
    public String getSoapType() {
        return "DataDescription";
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
     *	<li>"id"</li>
     *	<li>"name"</li>
     *	<li>"description"</li>
     *	<li>"uri"</li>
     *	<li>"status"</li>
     *	<li>"size"</li>
     *	<li>"checksum"</li>
     *	<li>"owner"</li>
     *	<li>"type"</li>
     *	<li>"lastModified"</li>
     *	<li>"objectType"</li>
     *	<li>"hierarchyLevel"</li>
     *	<li>"parentId"</li>
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
     * <li>STRING_TYPE (id)</li>
     * <li>STRING_TYPE (name)</li>
     * <li>STRING_TYPE (description)</li>
     * <li>STRING_TYPE (uri)</li>
     * <li>STRING_TYPE (status)</li>
     * <li>INT_TYPE (size)</li>
     * <li>STRING_TYPE (checksum)</li>
     * <li>STRING_TYPE (owner)</li>
     * <li>STRING_TYPE (type)</li>
     * <li>STRING_TYPE (lastModified)</li>
     * <li>STRING_TYPE (objectType)</li>
     * <li>INT_TYPE (hierarchyLevel)</li>
     * <li>STRING_TYPE (parentId)</li>
     * </ul>
     */
    public Object[] getTypes() {
        return SOAP_TYPES;
    }

    /**
     * compares two DataDescriptions in [ id ]
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o){
        return this.id.equals(((DataDescription)o).id);
    }

    /**
    *
    * @see java.lang.Object#hashCode()
    */
    public int hashCode(){
        return id.hashCode();
    }

}
