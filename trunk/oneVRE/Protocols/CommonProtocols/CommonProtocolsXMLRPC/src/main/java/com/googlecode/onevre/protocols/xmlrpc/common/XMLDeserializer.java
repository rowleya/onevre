/*
 * @(#)XMLDeserializer.java
 * Created: 30 Aug 2007
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

package com.googlecode.onevre.protocols.xmlrpc.common;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Used to convert xml back into objects
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XMLDeserializer extends DefaultHandler {

    // The XML parser
    private XMLReader parser = new org.apache.xerces.parsers.SAXParser();

    // The object stack
    private LinkedList<Object> currentObject = new LinkedList<Object>();

    // The type stack
    private LinkedList<Class<?>> currentType = new LinkedList<Class<?>>();

    // The name stack (only used if we are filling an object)
    private LinkedList<String> currentName = new LinkedList<String>();

    // The index stack (only used if we are filling an array)
    private LinkedList<Integer> currentIndex = new LinkedList<Integer>();

    // The key stack (only used if we are filling a map)
    private LinkedList<Object> currentKey = new LinkedList<Object>();

    // The value stack (only used if we are filling a map)
    private LinkedList<Object> currentValue = new LinkedList<Object>();

    // The response (type determined by message)
    private Object response = null;

    // Does the actual deserialization
    private XMLDeserializer(String xml) throws IOException, SAXException {
        parser.setContentHandler(this);
        try {
        	parser.parse(new InputSource(new StringReader(xml)));
        } catch (SAXParseException e) {
        	System.err.println("Parsing  |" + xml + "|" );
        	e.printStackTrace();
        	throw e;
        }

    }

    // Gets the response
    private Object getResponse() {
        return response;
    }

    /**
     * Deserializes an object
     * @param xml The xml of the object
     * @return The deserialized object
     * @throws SAXException
     * @throws IOException
     */
    public static Object deserialize(String xml)
            throws IOException, SAXException {
        XMLDeserializer deserializer = new XMLDeserializer(xml);
        return deserializer.getResponse();
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *     java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {
        String className = atts.getValue("class");
        String value = atts.getValue("value");

        try {
            if (className != null) {
                Class<?> cls = null;
                if (className.equals(Integer.TYPE.getName())) {
                    cls = Integer.TYPE;
                } else if (className.equals(Float.TYPE.getName())) {
                    cls = Float.TYPE;
                } else if (className.equals(Double.TYPE.getName())) {
                    cls = Double.TYPE;
                } else if (className.equals(Boolean.TYPE.getName())) {
                    cls = Boolean.TYPE;
                } else if (className.equals(Byte.TYPE.getName())) {
                    cls = Byte.TYPE;
                } else if (className.equals(Short.TYPE.getName())) {
                    cls = Short.TYPE;
                } else if (className.equals(Long.TYPE.getName())) {
                    cls = Long.TYPE;
                } else {
                    cls = Class.forName(className);
                }
                if (cls.equals(String.class)) {
                    currentObject.addLast(value);
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Integer.TYPE)
                        || cls.equals(Integer.class)) {
                    currentObject.addLast(Integer.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Float.TYPE)
                        || cls.equals(Float.class)) {
                    currentObject.addLast(Float.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Double.TYPE)
                        || cls.equals(Double.class)) {
                    currentObject.addLast(Double.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Boolean.TYPE)
                        || cls.equals(Boolean.class)) {
                    currentObject.addLast(Boolean.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Byte.TYPE)
                        || cls.equals(Byte.class)) {
                    currentObject.addLast(Byte.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Short.TYPE)
                        || cls.equals(Short.class)) {
                    currentObject.addLast(Short.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.equals(Long.TYPE)
                        || cls.equals(Long.class)) {
                    currentObject.addLast(Long.valueOf(value));
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (cls.isArray()) {
                    String length = atts.getValue("length");
                    if (length == null) {
                        throw new SAXException("Array without length");
                    }
                    currentType.addLast(cls);
                    cls = cls.getComponentType();
                    Object instance = Array.newInstance(cls,
                            Integer.parseInt(length));
                    currentObject.addLast(instance);
                    currentIndex.addLast(new Integer(0));
                    currentName.addLast(localName);
                } else if (Collection.class.isAssignableFrom(cls)) {
                    Object instance = cls.newInstance();
                    currentObject.addLast(instance);
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else if (Map.class.isAssignableFrom(cls)) {
                    Object instance = cls.newInstance();
                    currentObject.addLast(instance);
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                } else {
                    Object instance = cls.newInstance();
                    currentObject.addLast(instance);
                    currentType.addLast(cls);
                    currentName.addLast(localName);
                }
            } else if (localName.equals("element")
                    && Map.class.isAssignableFrom(currentType.getLast())) {
                // Do Nothing
            } else {
                throw new SAXException("Class name not found in XML");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *     java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public void endElement(String namespaceURI, String localName,
            String qualifiedName) throws SAXException {
        if (!localName.equals("element")) {
            Object object = currentObject.removeLast();
            Class<?> cls = currentType.removeLast();
            String name = currentName.removeLast();

            Object superObject = null;
            Class<?> superClass = null;
            if (!currentObject.isEmpty()) {
                superObject = currentObject.getLast();
                superClass = currentType.getLast();
                if (superClass.isArray()) {
                    int index = currentIndex.removeLast().intValue();
                    Array.set(superObject, index, object);
                    if (Array.getLength(superObject) > index + 1){
                        currentIndex.addLast(new Integer(index + 1));
                    }
                } else if (Collection.class.isAssignableFrom(superClass)) {
                    ((Collection<Object>) superObject).add(object);
                } else if (Map.class.isAssignableFrom(superClass)) {
                    if (localName.equals("key")) {
                        currentKey.addLast(object);
                    } else if (localName.equals("value")) {
                        currentValue.addLast(object);
                    } else {
                        throw new SAXException("Unknown element in Map: "
                                + localName);
                    }
                } else {
                    name = name.substring(0, 1).toUpperCase()
                        + name.substring(1);
                    String methodName = "set" + name;
                    Method setMethod = null;
                    while ((setMethod == null) && (cls != null)) {
                        try {
                            setMethod = superClass.getMethod(methodName,
                                    new Class[]{cls});
                        } catch (Exception e) {
                            setMethod = null;
                            cls = cls.getSuperclass();
                        }
                    }
                    if (setMethod == null) {
                        throw new SAXException("Set method " + methodName
                                + " not in class " + superClass);
                    }
                    try {
                        setMethod.invoke(superObject, new Object[]{object});
                    } catch (Exception e) {
                        throw new SAXException(e);
                    }
                }

            } else {
                response = object;
            }
        } else {
            Object key = currentKey.removeLast();
            Object value = currentValue.removeLast();
            Object map = currentObject.getLast();
            ((Map) map).put(key, value);
        }
    }
}
