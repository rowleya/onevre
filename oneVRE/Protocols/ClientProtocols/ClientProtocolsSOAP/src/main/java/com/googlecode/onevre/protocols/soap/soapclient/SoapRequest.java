/*
 * @(#)SoapRequest.java
 * Created: 19-Sep-2006
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

package com.googlecode.onevre.protocols.soap.soapclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.SAXException;

import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapObjectParser;
import com.googlecode.onevre.protocols.soap.common.SoapSerializer;
import com.googlecode.onevre.types.soap.SoapObject;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapResponse;
import com.googlecode.onevre.utils.Utils;


/**
 * A Soap Method request
 * @author Andrew G D Rowley
 * @version 1.0
 */
public final class SoapRequest {

    // The default timeout of 60 seconds
    private static final int DEFAULT_TIMEOUT = 60000;

    // The content encoding
    private static final String ENCODING = "UTF-8";

    // The content type header
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    // The soap content type
    private static final String CONTENT_TYPE =
        "application/soap+xml; charset=utf-8";

    // The soap action header
    private static final String SOAP_ACTION_HEADER = "SOAPAction";

    // The HTTP method to use
    private static final String METHOD = "POST";

    // The url of the server
    private URL url = null;

    // The timeout of the connection
    private int timeout = DEFAULT_TIMEOUT;

    // The current connection
    private HttpURLConnection connection = null;

    // The current output stream
    private OutputStream outputstream = null;

    // The current input stream
    private InputStream inputstream = null;

    /**
     * Creates a new SOAP request
     * @param url The url of the request
     * @throws MalformedURLException
     */
    public SoapRequest(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    /**
     * Sets the timeout of future connections
     * @param timeout the timeout in ms
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Calls a method and returns the result
     * @param nameSpace The nameSpace of the method
     * @param action The SoapAction to call
     * @param method The method to call
     * @param argNames The names of the arguments
     * @param args The argument values
     * @param types The SOAP types of the arguments
     * @param response The response parser
     * @return An array of returned objects
     * @throws SoapException
     * @throws IOException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public synchronized HashMap<String, Object> call(String nameSpace,
            String action, String method, String[] argNames, Object[] args,
            Object[] types, SoapResponse response)
            throws SoapException, IOException {
        SoapSerializer serializer = new SoapSerializer();
        String requestString = serializer.serializeMethod(nameSpace,
                method, argNames, args, types);
        byte[] requestData = requestString.getBytes(ENCODING);

        connection = (HttpURLConnection) url.openConnection();
        Utils.addSslConnection(connection);
        connection.setReadTimeout(timeout);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(METHOD);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        connection.setRequestProperty(SOAP_ACTION_HEADER, action);
        connection.connect();

        outputstream = connection.getOutputStream();
        BufferedOutputStream buffered = new BufferedOutputStream(outputstream);
        DataOutputStream output = new DataOutputStream(buffered);
        output.write(requestData);
        output.flush();

        inputstream = connection.getInputStream();
        BufferedInputStream bufferedin = new BufferedInputStream(inputstream);
        SoapObjectParser parser = new SoapObjectParser();
        SoapObject soapObject;
        try {
            soapObject = parser.parse(bufferedin);
        } catch (SAXException e) {
            throw new SoapException(e);
        }
        SoapDeserializer deserializer = new SoapDeserializer();
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (String subObjectName : soapObject.getSubObjectNames()){
            Vector<SoapObject> subObject = soapObject.getSubObject(subObjectName);
            String subObjectNameSpace= subObject.firstElement().getNameSpace();
            String soapType = subObject.firstElement().getSoapType();
            Class<?> subObjectClass = SoapDeserializer.getJavaType(soapType);
            if (soapType==null){
                subObjectClass = response.getType(subObjectNameSpace+"/"+subObjectName);
                soapType=SoapDeserializer.getSoapType(subObjectClass);
    //            System.out.println("NS: " +subObjectNameSpace+ " Cl: " + subObjectClass + " st: " + soapType );
                for (SoapObject so: subObject){
                    if (so.getSoapType()==null){
                        so.setSoapType(soapType);
                    }
                }
            }
   //         System.out.println("NS: " +subObjectNameSpace+ " Cl: " + subObjectClass + " O: " + subObject );
            Object objectArray = Array.newInstance(subObjectClass, subObject.size());
            for (int i = 0; i < subObject.size(); i++) {
                SoapObject element=subObject.get(i);
                element.setSoapType(soapType);
                Object object = deserializer.deserialize(element);
                Array.set(objectArray, i, object);
            }
            if (subObject.size()>1 || response.isArray(subObjectNameSpace+"/"+subObjectName)){
                result.put(subObjectName, objectArray);
            } else{
                result.put(subObjectName, Array.get(objectArray, 0));
            }
        }
//        System.out.println("returning: "+ result.toString());
        return result;
    }

    /**
     * Cancels the most recent call
     *
     */
    public void cancel() {
        try {
            outputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
    }
}
