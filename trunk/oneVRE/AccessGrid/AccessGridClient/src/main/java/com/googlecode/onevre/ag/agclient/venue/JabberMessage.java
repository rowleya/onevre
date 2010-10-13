/*
 * @(#)JabberMessage.java
 * Created: 20 Sep 2007
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

package com.googlecode.onevre.ag.agclient.venue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A message from the Jabber system
 * @author Andrew G D Rowley
 * @version 1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class JabberMessage {

    // Who the message is from
    private String from = "";

    // The message
    private String message = null;

    // The date of the message
    private String date = null;

    /**
     * Creates a new empty Jabber message
     *
     */
    public JabberMessage() {
        // Does Nothing
    }

    /**
     * Creates a new Jabber message
     * @param from Who the message is from
     * @param message The message content
     * @param date The date of the message
     */
    public JabberMessage(String from, String message, String date) {
        this.from = from;
        this.message = message;
        this.date = date;
    }

    /**
     * Sets who the message is from
     * @param from Who the message is from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Sets the message
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the date of the message
     * @param date The date of the message
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets who the message is from
     * @return Who the message is from
     */
    @XmlElement
    public String getFrom() {
        return from;
    }

    /**
     * Gets the message
     * @return The message
     */
    @XmlElement
    public String getMessage() {
        return message;
    }

    /**
     * Gets the message with urls converted to html links
     * @return The message with urls as html links
     */
    @XmlElement
    public String getMessageWithLinks() {
        Pattern url = Pattern.compile("(^|[ \t\r\n])((ftp|http|https|"
                + "gopher|mailto|news|nntp|telnet|wais|file|prospero|"
                + "aim|webcal):(([A-Za-z0-9$_.+!*(),;/?:@&~=-])|%"
                + "[A-Fa-f0-9]{2}){2,}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!"
                + "*(),;/?:@&~=%-]*))?([A-Za-z0-9$_+!*();/?:~-]))");
        Matcher matcher = url.matcher(message);
        String newMessage = "";
        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            if (start != 0) {
                start += 1;
            }
            int end = matcher.end();
            newMessage += message.substring(lastEnd, start);
            lastEnd = end;
            newMessage += "<a href='" + message.substring(start, end)
                           + "' target='_blank'>";
            newMessage += message.substring(start, end);
            newMessage += "</a>";
        }
        newMessage += message.substring(lastEnd);
        return newMessage;
    }

    /**
     * Gets the date of the message
     * @return The date of the message
     */
    @XmlElement
    public String getDate() {
        return date;
    }
}
