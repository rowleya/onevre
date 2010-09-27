/*
 * @(#)EventClient.java
 * Created: 31-May-2006
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

package com.googlecode.onevre.ag.agclient.venue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Vector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.googlecode.onevre.ag.common.interfaces.EventListener;
import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.security.AcceptAllTrustManager;
import com.googlecode.onevre.types.soap.exceptions.SoapException;


/**
 * Represents a client connection to the event server
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class EventClient {

	Log log = LogFactory.getLog(this.getClass());

    private EventListener listener;

    private boolean done=false;

    private String connectionId = null;

    // Vector to store all non SOAP messages in the message queue
    private Vector<String> nonSOAP = null;

    private int waitTime = 10000;

    private LinkedList<String> queue = new LinkedList<String>();

 //   private Vector<String> history = new Vector<String>();

    private String hostname=null;
    private int port;

    private SSLSocket sslsocket = null;

    private boolean firstMessageReceived = false;

    private void sendString(DataOutputStream output, String string)
    throws IOException {
//        System.err.println("Sending " + string);
        output.writeInt(string.length());
        output.write(string.getBytes("UTF-8"));
        output.flush();
    }

    static{
        SoapDeserializer.mapType(EventDescription.class);
    }
    // Gets all the messages in the queue
    private String getMessages() {
        String messages = "<concerns>Exit</concerns><eventType>bye</eventType>";
        synchronized (queue) {
            if (!done && queue.isEmpty()) {
                try {
  //                  System.err.println("Waiting for messages");
                    queue.wait(waitTime);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
            if (!queue.isEmpty()) {
                String message = queue.removeFirst();
                messages = message;
            } else if (done == false) {
                messages = "<concerns>Noone</concerns><eventType>empty</eventType>";
            }
        }
        return messages;
    }

    /**
     * Creates and EventClient with a specified Listener
     * @param eventListener Listener to process the events
     * @param connectionId
     * @param groupId
     */
    public EventClient(EventListener eventListener, final String connectionId, String groupId ) {
    	log.info ("starting EventClient");
        this.listener=eventListener;
        this.connectionId = connectionId;
        String [] location=listener.getLocation().split(":");
        hostname=location[0];
        port=Integer.parseInt(location[1]);
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new AcceptAllTrustManager()}, new SecureRandom());
            SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
            final SoapDeserializer soap = new SoapDeserializer();
            InputStream inputstream = sslsocket.getInputStream();
            BufferedInputStream bufferedin = new BufferedInputStream(inputstream);
            final DataInputStream input = new DataInputStream(bufferedin);
            nonSOAP=new Vector<String>();

            Thread receive = new Thread() {
                public void run() {
                    String message = null;
                    EventDescription event = null;
                    while (!done) {
                        try {
                            int length = input.readInt();
                            byte[] stringarray = new byte[length];
                            input.read(stringarray);
                            if (!firstMessageReceived) {
                                firstMessageReceived = true;
/*                              int version = Integer.parseInt(
                                        new String(stringarray, 0, 1, "UTF-8"));
                                String connId = new String(stringarray, 2,
                                        length - 2, "UTF-8");
                                System.err.println("Version = " + version + " connection Id = " + connId + "; " + connectionId);
*/                            } else {
                                message = new String(stringarray, "UTF-8");
                                event = (EventDescription)
                                    soap.deserialize(message);
                                listener.processEvent(event, message);
                            }
                        } catch (SAXException e) {
                            nonSOAP.add(message);
                            System.err.println("SAX - Error parsing message "
                                    + message);
                        } catch (SoapException e) {
                            nonSOAP.add(message);
                            System.err.println("SOAP - Error parsing message "
                                    + message);
                        } catch (IOException e) {
                             e.printStackTrace();
                        }
                    }
                }
            };
            receive.start();

            // create a monitoring event client if connectionId is null
            if (connectionId!=null)
            {
	            OutputStream outputstream = sslsocket.getOutputStream();
	            BufferedOutputStream buffered = new BufferedOutputStream(outputstream);
	            DataOutputStream output = new DataOutputStream(buffered);
	            NumberFormat format = NumberFormat.getInstance();
	            format.setMaximumFractionDigits(0);
	            format.setMinimumIntegerDigits(2);
	            String data = format.format(groupId.length()) + groupId;
	            data += format.format(connectionId.length()) + connectionId;
	            sendString(output, data);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Waits until the messages are available and then gets them
     * @return The messages in the queue
     */
    public String getMessage() {
        return getMessages();
    }

    /**
     * Get the EventListener id
     * @return the id of the EventListener
     */
    public String getListenerId(){
        return listener.getListenerId();
    }

    /**
     * Get the EventListeners URL
     * @return the URL of the eventListener
     */
    public String getListenerUri(){
        return listener.getListenerUri();
    }
    public String getConnectionId(){
        return connectionId;
    }

    /**
     * Closes the connection to the server
     */
    public void close() {
        done=true;
        try {
            sslsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
