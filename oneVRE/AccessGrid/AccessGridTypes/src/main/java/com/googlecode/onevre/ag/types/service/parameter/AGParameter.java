/*
 * @(#)AGParameter.java
 * Created: 06-Nov-2006
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

package com.googlecode.onevre.ag.types.service.parameter;

import java.util.Vector;

import javax.swing.JComponent;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * Represents an AGParameter
 *
 * Note that subclasses MUST also implement getValue, although the return
 * type is defined by the subclass
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class AGParameter implements SoapSerializable {

    private final Vector<String> soapFields = new Vector<String>();

    private final Vector<String> soapTypes = new Vector<String>();

    // The name of the parameter
    private String name = null;

    // The type of the parameter
    private String type = null;

    /**
     * Creates a new AGParameter
     * @param name The name of the parameter
     * @param type The type of the parameter
     * @param valueType The type to be returned by getValue
     */
    public AGParameter(String name, String type, String valueType) {
        addField("name", STRING_TYPE);
        addField("type", STRING_TYPE);
        addField("value", valueType);
        this.name = name;
        this.type = type;
    }

    /**
     * Creates a new AGParameter
     *
     */
    public AGParameter() {
        addField("name", STRING_TYPE);
        addField("type", STRING_TYPE);
        this.name = "";
        this.type = "AGParameter";
    }


    /**
     * Returns the name of the parameter
     * @return The name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the parameter
     * @return The type of the parameter
     */
    public String getType() {
        return type;
    }

    /**
     * Adds a SOAP field to the class
     * @param field The name of the field
     */
    protected void addField(String field, String type) {
        soapFields.add(field);
        soapTypes.add(type);
    }

    /**
     * Returns the SOAP type of the item
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getSoapType()}</dd></dl>
     * @return the SOAP Type
     */
    public String getSoapType() {
        return type;
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
     * @return the fields
     */
    public String[] getFields() {
        return soapFields.toArray(new String[0]);
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
     * @return the types
     */
    public Object[] getTypes() {
        return soapTypes.toArray();
    }

    /**
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof AGParameter) {
            if (((AGParameter) o).getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Sets the parameter value
     * @param value The value to set
     */
    public abstract void setValue(Object value);

    /**
     * Gets the parameter value
     * @return The parameter value
     */
    public abstract Object getValue();

    /**
     * Sets the name of the parameter
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of the parameter
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the component used to modify the parameter
     * @return The component, or null if not modifiable
     */
    public abstract JComponent getVisualComponent();

    /**
     * Sets the value of the parameter to the value of the visual component
     *
     */
    public abstract void setValueFromComponent();
}
