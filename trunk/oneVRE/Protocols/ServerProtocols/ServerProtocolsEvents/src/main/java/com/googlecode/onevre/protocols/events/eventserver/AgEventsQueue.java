/*
 * @(#)XmlRpcResponseQueue.java
 * Created: 15 Sep 2007
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

package com.googlecode.onevre.protocols.events.eventserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.util.LinkedList;

import javax.swing.Timer;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.common.Event;
import com.googlecode.onevre.utils.Download;
import com.googlecode.onevre.utils.TimeoutListener;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;

/**
 * A queue of XML-RPC responses
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class AgEventsQueue implements ActionListener {

	Log log = LogFactory.getLog(this.getClass());


    private static final int TIMEOUT_TIME = 20000;

    private static final int WAIT_TIME = 10000;

    private boolean done = false;

    private LinkedList<String> queue = new LinkedList<String>();

    private TimeoutListener listener = null;

    private Timer timer = null;

    /**
     * Sets the listener
     * @param listener The listener
     */
    public void setListener(TimeoutListener listener) {
        this.listener = listener;
        if (listener == null) {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        } else {
            if (timer == null) {
                timer = new Timer(TIMEOUT_TIME, this);
                timer.start();
            }
        }
    }

   public String getEventString(AgEvent event){
		try {
			Object evObject = event.getEventObject();
			Class<?> [] classes = new Class<?>[]{AgEvent.class};
			if (evObject!=null){
				log.info("evOBJ class: " + evObject.getClass());
				classes = new Class<?>[]{AgEvent.class,evObject.getClass()};
			}
			JSONJAXBContext context = new JSONJAXBContext(JSONConfiguration.natural().build(),classes);
			JSONMarshaller marshaller = context.createJSONMarshaller();
	        StringWriter writer = new StringWriter();
	        marshaller.marshallToJSON(event, writer);
	        return (writer.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return ("");
    }
    /**
     * Adds a response to the queue
     * @param response The response to add
     */
    public void addResponse(String response) {
        synchronized (queue) {
            queue.addLast(response);
            queue.notifyAll();
        }
    }

    /**
     * Gets the next available response from the queue
     * @return The next response if there is one after 10 seconds
     */
    public String getNextResponse() {
        if (timer != null) {
            timer.restart();
        }
        synchronized (queue) {
            if (queue.isEmpty() && !done) {
                try {
                    queue.wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!queue.isEmpty()) {
                return queue.removeFirst();
            } else if (!done) {
                return getEventString(new AgEvent("",Event.NONE,null));
            } else {
                return getEventString(new AgEvent("",Event.DONE,null));
            }
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *     java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            if (!Download.isDownloading()) {
                listener.timedOut();
            } else {
                timer.stop();
                Download.waitForDownloadsToComplete();
                timer.start();
            }
        }
    }
}
