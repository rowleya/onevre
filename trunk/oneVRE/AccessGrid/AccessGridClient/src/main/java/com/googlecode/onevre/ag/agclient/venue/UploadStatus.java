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
public class UploadStatus {

    // Who the message is from
    private String fileName = "";

    // The message
    private long size = 0;

    // The date of the message
    private long done = 0;

    /**
     * Creates a new empty Jabber message
     *
     */
    public UploadStatus() {
        // Does Nothing
    }

    /**
     * Creates a new Jabber message
     * @param from Who the message is from
     * @param message The message content
     * @param date The date of the message
     */
    public UploadStatus(String fileName, long size, long done) {
        this.fileName = fileName;
        this.size = size;
        this.done = done;
    }

    /**
     * Sets who the message is from
     * @param from Who the message is from
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the message
     * @param message The message
     */
    public void setSize(String size) {
        this.size = Long.getLong(size);
    }

    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Sets the date of the message
     * @param date The date of the message
     */
    public void setDone(String done) {
        this.done = Long.getLong(done);
    }

    public void setDone(long done) {
        this.done = done;
    }

    /**
     * Gets who the message is from
     * @return Who the message is from
     */
    @XmlElement
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the message
     * @return The message
     */
    @XmlElement
    public String getSize() {
        return "" + size;
    }

    /**
     * Gets the message with urls converted to html links
     * @return The message with urls as html links
     */
    @XmlElement
    public String getPercentage() {
        float percent = (float) done / (float) size;
        return "" + percent;
    }

    /**
     * Gets the date of the message
     * @return The date of the message
     */
    @XmlElement
    public String getDone() {
        return "" + done;
    }
}
