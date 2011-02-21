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

package com.googlecode.onevre.protocols.soap.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.SAXException;

import com.googlecode.onevre.types.soap.SoapObject;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * Deserialises a soap call into an object
 * @author Andrew G D Rowley
 * @version 1
 */
public class SoapDeserializer {

    // The url path separator
    private static final String URL_PATH_SEP = "/";

    // The XSD type
    private static final String XSD =
        "http://www.w3.org/2001/XMLSchema";

    // A map of known types to classes
    private static final HashMap<String, Class<?>> TYPE_MAP =
        new HashMap<String, Class<?>>();
    // A map of known types to classes
    private static final HashMap<Class<?>, String> CLASS_MAP =
        new HashMap<Class<?>, String>();

    static {
        mapType(XSD + "/string", String.class);
        mapType(XSD + "/int", Integer.TYPE);
        mapType(XSD + "/integer", Integer.TYPE);
        mapType(XSD + "/float", Float.TYPE);
        mapType(XSD + "/double", Double.TYPE);
        mapType(XSD + "/boolean", Boolean.TYPE);
        CLASS_MAP.put(Float.class, XSD + "/float");
        CLASS_MAP.put(Integer.class, XSD + "/integer");
        CLASS_MAP.put(Double.class, XSD + "/double");
        CLASS_MAP.put(Boolean.class, XSD + "/boolean");
    }

    // The SOAP parser
    private SoapObjectParser parser = null;


    /**
     * Creates a new SoapDeserializer
     */
    public SoapDeserializer() {
        parser = new SoapObjectParser();
    }

    /**
     * Maps a soap type to a java type
     * @param soapType The soap type (full including namespace)
     * @param javaType The java type
     */
    public static void mapType(String soapType, Class <?> javaType) {
        TYPE_MAP.put(soapType, javaType);
        CLASS_MAP.put(javaType, soapType);
    }

    /**
     * Maps a soap type to a java type
     * @param javaType The java type (must be SoapSerializable)
     */
    public static void mapType(Class<? extends SoapSerializable> javaType) {
        try {
            SoapSerializable soap = javaType.newInstance();
            String soapType = soap.getNameSpace() + URL_PATH_SEP
                + soap.getSoapType();
            TYPE_MAP.put(soapType, javaType);
            CLASS_MAP.put(javaType, soapType);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * looks up the Java class from a SOAP type
     * @param soapType
     * @return the Java class
     */
    public static Class<?> getJavaType(String soapType) {
        return TYPE_MAP.get(soapType);
    }

    /**
     * looks up the SOAP Type from a Java class
     * @param javaType
     * @return the SOAP Type
     */
    public static String getSoapType(Class<?> javaType) {
        return CLASS_MAP.get(javaType);
    }

    /**
     * deserializes a soapObject tree into Java Objects
     * @param soapObject
     * @return the java object that represents the SOAP object tree
     * @throws SoapException
     */
    public Object deserialize(SoapObject soapObject) throws SoapException {
        if (soapObject.isNil()) {
            return null;
        }
        String type = soapObject.getSoapType();
        Class<?> objClass = TYPE_MAP.get(type);

        if (objClass.equals(String.class)) {
            return soapObject.getValue();
        } else if (objClass.equals(Integer.TYPE)
                || objClass.equals(Integer.class)) {
            return Integer.valueOf(soapObject.getValue());
        } else if (objClass.equals(Float.TYPE)
                || objClass.equals(Float.class)) {
            return Float.valueOf(soapObject.getValue());
        } else if (objClass.equals(Double.TYPE)
                || objClass.equals(Double.class)) {
            return Double.valueOf(soapObject.getValue());
        } else if (objClass.equals(Boolean.TYPE)
                || objClass.equals(Boolean.class)) {
            return Boolean.valueOf(soapObject.getValue());
        }
        Object obj;
        try {
            obj = objClass.newInstance();
        } catch (Exception e) {
            throw new SoapException(e);
        }
        for (String name : soapObject.getSubObjectNames()) {
            for (SoapObject subObject : soapObject.getSubObject(name)) {
                String subType = subObject.getSoapType();
                Class<?> subClass = TYPE_MAP.get(subType);
                String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                if (subType == null) {
                    subClass = findParameterType(objClass, methodName);
                    subType = CLASS_MAP.get(subClass);
                    subObject.setSoapType(subType);
                }
                Object subObj = deserialize(subObject);
                if (subObj != null) {
                    Method method = null;
                    while ((method == null) && (subClass != null)) {
                        try {
                            method = objClass.getMethod(methodName, subClass);
                        } catch (NoSuchMethodException e) {
                            subClass = subClass.getSuperclass();
                        }
                    }
                    try {
                        if (method == null) {
                            throw new NoSuchMethodException("method " + methodName
                                    + "(" + subObj.getClass() + ") does not exist in class " + objClass);
                        }
                        method.invoke(obj, subObj);
                    } catch (Exception e) {
                        throw new SoapException(e);
                    }

                }
            }
        }
        return obj;
    }

    /**
     * Deserializes an object
     * @param message The message to deserialize
     * @return The return object from the soap message
     * @throws SoapException
     * @throws SAXException
     */
    public Object deserialize(String message) throws SoapException,
            SAXException {
        SoapObject soapObject;
        try {
            soapObject = parser.parse(message);
        } catch (IOException e) {
            throw new SoapException(e);
        }
        return deserialize(soapObject);
    }

    /**
     * Deserializes an object
     * @param message The message to deserialize
     * @return The return object from the soap message
     * @throws SoapException
     * @throws SAXException
     */
    public Object deserialize(InputStream message) throws SoapException,
            SAXException {
        String text = "";
        BufferedReader input = new BufferedReader(
                new InputStreamReader(message));
        String line = "";
        try {
            while ((line = input.readLine()) != null) {
                text += line;
            }
        } catch (IOException e) {
            throw new SoapException(e);
        }
        return deserialize(text);
    }

    // Returns the return type of the first instance of the method in the class
    private Class <?> findParameterType(Class <?> type, String method) {
        while (type != null) {
            Method[] methods = type.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(method)) {
                    if (!methods[i].getParameterTypes()[0].equals(
                            Vector.class)) {
                        return methods[i].getParameterTypes()[0];
                    }
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

}
