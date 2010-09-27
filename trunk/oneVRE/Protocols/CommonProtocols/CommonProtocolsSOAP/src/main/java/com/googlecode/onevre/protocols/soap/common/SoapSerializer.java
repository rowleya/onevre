/*
 * @(#)SoapSerializer.java
 * Created: 16-Sep-2006
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

package com.googlecode.onevre.protocols.soap.common;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * A class that serializes a soap call
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SoapSerializer {

    // The start of a header key
    private static final String END_OF_KEY = "\" ";

    // The end of a header key
    private static final String START_OF_KEY = "=\"";

    // The end of the XSI_TYPE
    private static final String EXSI_TYPE = "\"";

    // The start of the XSI_TYPE
    private static final String XSI_TYPE = " xsi:type=\"";

    // The start of the end of a tag
    private static final String SEOT = "/";

    // The separator between a namespace
    private static final String NS_SEP = ":";

    // The start of a get method
    private static final String GET_METHOD_PREFIX = "get";

    // The start of the SOAP envelope
    @SuppressWarnings("unused")
	private static final String START_SOAP_ENVELOPE = "<?xml "
        + "version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<SOAP-ENV:Envelope "
        + "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ";

    // The start of the SOAP envelope without xml intro
    private static final String START_SOAP_ENVELOPE_SHORT = "<SOAP-ENV:Envelope "
        + "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" ";

    // The end of tag character
    private static final String EOT = ">";

    // The SOAP header
    private static final String SOAP_HEADER =
        "<SOAP-ENV:Header></SOAP-ENV:Header>";

    // The start of the SOAP body
    private static final String START_SOAP_BODY = "<SOAP-ENV:Body>";

    // The end of the SOAP body
    private static final String END_SOAP_BODY = "</SOAP-ENV:Body>";

    // The end of the SOAP envelope
    private static final String END_SOAP_ENVELOPE = "</SOAP-ENV:Envelope>";

    // The namespace tag
    private static final String NS = "ns";

    // The XMLNS namespace tag
    private static final String XMLNS = "xmlns:";

    // The start of a tag
    private static final String SOT = "<";

    // A map of the namespaces in use
    private HashMap<String, String> namespaces = new HashMap<String, String>();

    // The current number of namespaces defined
    private int currentLevel = 1;

    // Gets a namespace string for the given namespace
    private String getNs(String space) {
        String ns = namespaces.get(space);
        if (ns == null) {
            ns = NS + currentLevel;
            currentLevel += 1;
            namespaces.put(space, ns);
        }
        return ns;
    }

    // Gets the SOAP value of the given field
    private String getValue(Class<?> fieldType, Object value) {
        String soapFieldValue = null;
        if (fieldType.equals(Integer.TYPE)
                || fieldType.equals(Integer.class)
                || fieldType.equals(Float.TYPE)
                || fieldType.equals(Float.class)
                || fieldType.equals(Double.TYPE)
                || fieldType.equals(Double.class)
                || fieldType.equals(String.class)) {
            soapFieldValue = StringEscapeUtils.escapeXml(value.toString());
        } else if (fieldType.equals(Boolean.TYPE)
                || fieldType.equals(Boolean.class)) {
            if ((Boolean) value) {
                soapFieldValue = "1";
            } else {
                soapFieldValue = "0";
            }
        }
        return soapFieldValue;
    }

    @SuppressWarnings("unchecked")
	private String getParameterString(String nameSpace, String name,
            Object value, Object type) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String message = "";
        String ns = getNs(nameSpace);
        if (value == null) {
            message += SOT + ns + NS_SEP + name;
            message += " xsi:nil=\"true\"";
            message += SEOT + EOT;
        } else if (value instanceof SoapSerializable) {
            String namespace = getNs(((SoapSerializable) value).getNameSpace());
            String soapFieldType = namespace + NS_SEP
                + ((SoapSerializable) value).getSoapType();
            message += SOT + ns + NS_SEP + name;
            message += XSI_TYPE + soapFieldType + EXSI_TYPE;
            message += EOT;
            message += getParameterString((SoapSerializable) value);
            message += SOT + SEOT + ns + NS_SEP + name + EOT;
        } else if ((value instanceof Vector<?>) || value.getClass().isArray()) {
            Vector<Object> values = null;
            if (value.getClass().isArray()) {
                values = new Vector<Object>();
                for (int i = 0; i < Array.getLength(value); i++) {
                    values.add(Array.get(value, i));
                }
            } else {
                values = (Vector) value;
            }
            for (int j = 0; j < values.size(); j++) {
                Object val = values.get(j);
                if (val instanceof SoapSerializable) {
                    String namespace =
                        getNs(((SoapSerializable) val).getNameSpace());
                    String soapFieldType = namespace + NS_SEP
                        + ((SoapSerializable) val).getSoapType();
                    message += SOT + ns + NS_SEP + name;
                    message += XSI_TYPE + soapFieldType + EXSI_TYPE;
                    message += EOT;
                    message += getParameterString((SoapSerializable) val);
                    message += SOT + SEOT + ns + NS_SEP
                        + name + EOT;
                } else {
                    Class <?> fieldType = val.getClass();
                    String soapFieldType = null;
                    if (type instanceof Vector<?>) {
                        soapFieldType = (String) ((Vector<?>) type).get(j);
                    } else if (type instanceof String[]) {
                        soapFieldType = ((String[]) type)[j];
                    } else {
                        soapFieldType = (String) type;
                    }
                    message += SOT + ns + NS_SEP + name;
                    message += XSI_TYPE + soapFieldType + EXSI_TYPE;
                    message += EOT;
                    message += getValue(fieldType, val);
                    message += SOT + SEOT + ns + NS_SEP
                        + name + EOT;
                }
            }
        } else {
            Class <?> fieldType = value.getClass();
            String soapFieldType = (String) type;
            message += SOT + ns + NS_SEP + name;
            message += XSI_TYPE + soapFieldType + EXSI_TYPE + EOT;
            message += getValue(fieldType, value);
            message += SOT + SEOT + ns + NS_SEP + name + EOT;
        }
        return message;
    }

    // Converts the parameters of a message or object to a string
    private String getParameterString(SoapSerializable method)
            throws NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException {
        String message = "";
        String[] parameters = method.getFields();
        Object[] types = method.getTypes();
        Class <?> cls = method.getClass();
        for (int i = 0; i < parameters.length; i++) {
            String getMethod = GET_METHOD_PREFIX
                + parameters[i].substring(0, 1).toUpperCase()
                + parameters[i].substring(1);
            Method getter = cls.getMethod(getMethod, new Class[0]);
            Object value = getter.invoke(method, (Object[]) new Class[0]);
            message += getParameterString(method.getNameSpace(), parameters[i],
                    value, types[i]);
        }
        return message;
    }

    /**
     * Serializes a method as a soap call
     * @param method The method to serialize
     * @return The string to send as a request
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public String serialize(SoapSerializable method)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String header = "";
        String message = "";
        String space = method.getNameSpace();
        String ns = getNs(space);
        Iterator <String> iterator = null;
        message += SOAP_HEADER;
        message += START_SOAP_BODY;
        message += SOT + ns + NS_SEP + method.getSoapType() + EOT;
        message += getParameterString(method);
        message += SOT + SEOT + ns + NS_SEP + method.getSoapType() + EOT;
        message += END_SOAP_BODY;
        message += END_SOAP_ENVELOPE;

        header += START_SOAP_ENVELOPE_SHORT;
        iterator = namespaces.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = namespaces.get(key);
            header += XMLNS + value + START_OF_KEY + key + END_OF_KEY;
        }
        header += EOT;
        return header + message;
    }

    /**
     * Serializes a method as a soap call
     * @param method The method to serialize
     * @return The string to send as a request
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public String serialize(SoapSerializable event, String name)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String header = "";
        String message = "";
        String space = event.getNameSpace();
        String ns = getNs(space);
        Iterator<String> iterator = null;
        message += SOAP_HEADER;
        message += START_SOAP_BODY;
        message += SOT + name + " " + XSI_TYPE + ns + NS_SEP + event.getSoapType() + EXSI_TYPE + EOT;
        message += getParameterString(event);
        message += SOT + SEOT + name + EOT;
        message += END_SOAP_BODY;
        message += END_SOAP_ENVELOPE;

        header += START_SOAP_ENVELOPE_SHORT;
        iterator = namespaces.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = namespaces.get(key);
            header += XMLNS + value + START_OF_KEY + key + END_OF_KEY;
        }
        header += EOT;
        return header + message;
    }

    /**
     * Serializes a method call
     * @param nameSpace The namespace of the method
     * @param soapType The name of the method
     * @param paramNames The names of the parameters
     * @param objects The values of the parameters
     * @param types The SOAP types of the parameters
     * @return The serialized method call
     * @throws SoapException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    public String serializeMethod(String nameSpace, String soapType,
            String[] paramNames, Object[] objects, Object[] types)
            throws SoapException {
        String header = "";
        String message = "";
        String ns = getNs(nameSpace);
        Iterator<String> iterator = null;
        message += SOAP_HEADER;
        message += START_SOAP_BODY;
        message += SOT + ns + NS_SEP + soapType + EOT;
        for (int i = 0; i < paramNames.length; i++) {
            try {
                message += getParameterString(nameSpace, paramNames[i], objects[i],
                        types[i]);
            } catch (Exception e) {
                throw new SoapException(e);
            }
        }
        message += SOT + SEOT + ns + NS_SEP + soapType + EOT;
        message += END_SOAP_BODY;
        message += END_SOAP_ENVELOPE;

        header += START_SOAP_ENVELOPE_SHORT;
        iterator = namespaces.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = namespaces.get(key);
            header += XMLNS + value + START_OF_KEY + key + END_OF_KEY;
        }
        header += EOT;

        return header + message;
    }
}
