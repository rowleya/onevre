/*
 * @(#)SoapObjectParser.java
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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.googlecode.onevre.types.soap.SoapObject;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * Parses a SOAP call into a SoapObject
 * @author Andrew G D Rowley
 * @version 1
 */
public class SoapObjectParser extends DefaultHandler {

    private Log log = LogFactory.getLog(this.getClass());

    // The url path separator
    private static final String URL_PATH_SEP = "/";

    // The level that is the root i.e. outside all tags
    private static final int ROOT_LEVEL = 0;

    // The level that is in the SOAP_ENV tag
    private static final int IN_SOAP_ENV = 1;

    // The level that is in the SOAP_HEADER or SOAP_BODY tag
    private static final int IN_SOAP_HEADER_OR_BODY = 2;

    // The level that is inside the soap message tag
    private static final int IN_MESSAGE = 3;

    // The SOAP_ENV type
    private static final String SOAP_ENV =
        "http://schemas.xmlsoap.org/soap/envelope/";

    // The SOAP_ENV tag
    private static final String ENVELOPE = "Envelope";

    // The SOAP_HEADER tag
    private static final String HEADER = "Header";

    // The SOAP_BODY tag
    private static final String BODY = "Body";

    // The XSI type
    private static final String XSI =
        "http://www.w3.org/2001/XMLSchema-instance";

    // The XSD type
    private static final String XSD =
        "http://www.w3.org/2001/XMLSchema";

    // A map of known types to classes
    private static final HashMap<String, Class<?>> TYPE_MAP =
        new HashMap<String, Class<?>>();

    static {
        TYPE_MAP.put(XSD + "/string", String.class);
        TYPE_MAP.put(XSD + "/int", Integer.TYPE);
        TYPE_MAP.put(XSD + "/integer", Integer.TYPE);
        TYPE_MAP.put(XSD + "/float", Float.TYPE);
        TYPE_MAP.put(XSD + "/double", Double.TYPE);
        TYPE_MAP.put(XSD + "/boolean", Boolean.TYPE);
    }

    // The name of the method being called if in method mode
    private String methodName = null;

    // The namespace of the method being called
    private String methodNameSpace = null;

    // The XML parser
    private XMLReader parser = null;

    // The response (type determined by message)
    private SoapObject response = null;

    // The current level in the XML
    private int level = ROOT_LEVEL;

    // A stack of elements
    private LinkedList<String> currentElement = new LinkedList<String>();

    // A stack of objects
    private LinkedList<SoapObject> currentObject = new LinkedList<SoapObject>();

    // The chars in the current tag
    private String currentChars = "";

    // The currently known namespaces
    private HashMap<String, String> namespaces = new HashMap<String, String>();

    // A list of the types of the finished objects
    private Vector<Class<?>> finishedObjectTypes = new Vector<Class<?>>();

    /**
     * Creates a new SoapDeserializer
     */
    public SoapObjectParser() {
        parser = new org.apache.xerces.parsers.SAXParser();
        parser.setContentHandler(this);
    }

    /**
     * Maps a soap type to a java type
     * @param soapType The soap type (full including namespace)
     * @param javaType The java type
     */
    public static void mapType(String soapType, Class<?> javaType) {
        TYPE_MAP.put(soapType, javaType);
    }

    /**
     * Maps a soap type to a java type
     * @param javaType The java type (must be SoapSerializable)
     */
    public static void mapType(Class<?> javaType) {
        try {
            SoapSerializable soap = (SoapSerializable) javaType.newInstance();
            String soapType = soap.getNameSpace() + URL_PATH_SEP
                + soap.getSoapType();
            TYPE_MAP.put(soapType, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the name of the method being called
     * @return The name of the method being called
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets the namespace of the method
     * @return The namespace of the method
     */
    public String getMethodNamespace() {
        return methodNameSpace;
    }

    /**
     * Gets the types of the arguments deserialized
     * @return The types
     */
    public Class<?>[] getArgumentTypes() {

        return finishedObjectTypes.toArray(new Class[0]);
    }

    // Replaces the namespace prefixes with the actual namespace
    private String deprefix(String string) {
        if (string == null) {
            return null;
        }
        Iterator<String> iter = namespaces.keySet().iterator();
        while (iter.hasNext()) {
            String prefix = (String) iter.next();
            String uri = namespaces.get(prefix);
            string = string.replaceAll(prefix + ":", uri + URL_PATH_SEP);
        }
        return string;
    }

    /**
     * parses a SOAP-Message into a SoapObject
     * @param message The SOAP-message to parse
     * @return The SoapObject represented in the soap message
     * @throws IOException
     * @throws SAXException
     */
    public SoapObject parse(String message) throws IOException,
            SAXException {
        InputSource source = new InputSource(new StringReader(message));
        parser.parse(source);
        return response;
    }

    /**
     * parses a message received on the InputStream into a SoapObject
     * @param inStream The input stream the message is received on
     * @return The return object from the soap message
     * @throws IOException
     * @throws SAXException
     */
    public SoapObject parse(InputStream inStream) throws IOException,
            SAXException {
        String text = "";
        BufferedReader input = new BufferedReader(
                new InputStreamReader(inStream));
        String line = "";
        while ((line = input.readLine()) != null) {
            text += line;
        }
        log.info("parsing: \n" + text);
        return parse(text);
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *     java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {
        log.debug("startElement: localName = " + localName + " qualifiedName = " + qualifiedName + " level = " + level);
        currentChars = "";
        String type = "";
        SoapObject soapObject = null;
        try {
            boolean ok = false;
            switch (level) {
            case ROOT_LEVEL:
                if (namespaceURI.equals(SOAP_ENV)) {
                    if (localName.equals(ENVELOPE)) {
                        ok = true;
                        level = IN_SOAP_ENV;
                    }
                }
                break;

            case IN_SOAP_ENV:
                if (namespaceURI.equals(SOAP_ENV)) {
                    if (localName.equals(HEADER)) {
                        ok = true;
                        level = IN_SOAP_HEADER_OR_BODY;
                    } else if (localName.equals(BODY)) {
                        ok = true;
                        level = IN_SOAP_HEADER_OR_BODY;
                    }
                }
                break;

            case IN_SOAP_HEADER_OR_BODY:
                level = IN_MESSAGE;
                type = atts.getValue(XSI, "type");
                log.debug("IN_SOAP_HEADER_OR_BODY: " + localName + " type: " + type);
                soapObject = new SoapObject(localName, deprefix(type), namespaceURI);
                response = soapObject;
                currentObject.addLast(soapObject);
                ok = true;
                break;
            case IN_MESSAGE:

                // Falls Through
                log.debug("IN_MESSAGE: " + localName);
            default:
                level++;

                type = atts.getValue(XSI, "type");
                String nil = atts.getValue(XSI, "nil");
                log.debug("default level " + level + " : " +  localName + " type:" + type);

                soapObject = new SoapObject(localName, deprefix(type), namespaceURI);
                if ((nil != null) && nil.equals("true")) {
                    soapObject.setNil(true);
                }
                currentObject.addLast(soapObject);
                ok = true;
                break;
            }
            if (!ok) {
                throw new SAXException("Error parsing message at start of "
                        + qualifiedName);
            }
            currentElement.addLast(qualifiedName);
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
    public void endElement(String namespaceURI, String localName,
            String qualifiedName) throws SAXException {

        log.debug("endElement: localName = " + localName + " qualifiedName = " + qualifiedName + " level = " + level);

        try {
            String last = currentElement.removeLast();
            boolean ok = false;
            if (last.equals(qualifiedName)) {
                ok = true;
            }
            switch (level) {
            case IN_SOAP_ENV:
                level = ROOT_LEVEL;
                break;

            case IN_SOAP_HEADER_OR_BODY:
                level = IN_SOAP_ENV;
                break;

            case IN_MESSAGE:
                level = IN_SOAP_HEADER_OR_BODY;
                break;
            default:
                level--;
                SoapObject object = currentObject.removeLast();
                object.setValue(StringEscapeUtils.unescapeXml(currentChars));
                log.debug("adding object:" + object.toString());
                currentObject.getLast().addSubObject(object);
            break;
            }
            if (!ok) {
                throw new SAXException("Error parsing message at "
                        + qualifiedName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length);
        currentChars += value;
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
     *     java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }

    /**
     *
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) {
        namespaces.remove(prefix);
    }

}
