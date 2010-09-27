/*
 * @(#)SoapSerializable.java
 * Created: 08-Jun-2006
 * Version: 1
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

package com.googlecode.onevre.types.soap.interfaces;

/**
 * Represents an object that can be Serialized using SOAP
 * @author Andrew G D Rowley
 * @version 1
 */
public interface SoapSerializable {

    /**
     * The soap type for a string
     */
    String STRING_TYPE = "xsd:string";
    /**
     * The soap type for a boolean
     */
    String BOOLEAN_TYPE = "xsd:boolean";
    /**
     * The soap type for a double
     */
    String DOUBLE_TYPE = "xsd:double";
    /**
     * The soap type for a float
     */
    String FLOAT_TYPE = "xsd:float";
    /**
     * The soap type for an int
     */
    String INT_TYPE = "xsd:int";
    /**
     * The soap type for an integer
     */
    String INTEGER_TYPE = "xsd:integer";

    /**
     * The start of a get method
     */
    String GET_METHOD_PREFIX = "get";

    /**
     *  The start of a set method
     */
    String SET_METHOD_PREFIX = "set";

    /**
     * Returns the SOAP type of the item
     * @return the type
     */
    String getSoapType();

    /**
     * Returns the namespace of the item
     * @return the namespace
     */
    String getNameSpace();

    /**
     * Returns the fields that should be included with the soap
     *
     * Each of the fields should have a getter and a setter with the same name
     * e.g. field is "test" there should be a "getTest" and a "setTest" method
     * (note standard capitalisation)
     *
     * @return the fields
     */
    String[] getFields();

    /**
     * Returns the types of the fields that should be included with the soap<br>
     *
     * If the field is not a vector or array each of the types must be one of:
     *
     * <ol><li>A fully qualified url</li>
     * <li>A standard XML type starting with xsd:</li>
     * <li>Null if the field is itself SoapSerializable</li></ol>
     *
     * If the return type is an array or vector, the type must be one of:
     * <ol><li>A type as above if all the values have the same type</li>
     * <li>A Vector of types if the field is a vector with different types</li></ol>
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.soap.SoapSerializable#getTypes()}</dd></dl>
     *
     * @return the types
     */

    Object[] getTypes();
}
