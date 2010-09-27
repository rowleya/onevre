/*
 * @(#)SharedPresentation.java
 * Created: 22-Mar-2008
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

package com.googlecode.onevre.web.sharedPresentation;

import java.util.Vector;

import com.googlecode.onevre.ag.types.EventDescription;
import com.googlecode.onevre.ag.types.application.AppDataDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.ag.agsharedapplication.AGSharedApplication;
import com.googlecode.onevre.utils.Download;
import com.googlecode.onevre.utils.OS;


/**
 * The Shared Presentation shared application
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class SharedPresentation extends AGSharedApplication {

    private SharedPresInterface pres = null;
    private SharedPresentationUI presUI=null;
    private String currentPresentation = null;
//    private String currentMaster = null;
    private int currentSlide = 1;
    private int nSlides;
    private boolean presStarted = false;
    private boolean presInitied = false;
    private boolean presLoaded = false;
    private Integer presSync = new Integer(0);

    private void initPresentationTool() {

        Thread t = new Thread() {
            public void run() {
                if (OS.IS_WINDOWS) {
                    pres = new SharedPowerPoint();
                    System.err.println("Tool initialised");
                } else if (OS.IS_LINUX) {
                    pres = new SharedOOImpress();
                } else if (OS.IS_OSX) {
                    // Nothing so far
                }
                synchronized (presSync) {
                    presStarted = true;
                    presSync.notifyAll();
                    presUI=new SharedPresentationUI(pres);
                }
            }
        };
        t.start();
    }

    private void waitForPres() {
        synchronized (presSync) {
            if (!presInitied) {
                initPresentationTool();
                presInitied = true;
            }
            while (!presStarted) {
                try {
                    presSync.wait();
                } catch (InterruptedException e) {
                    // Do Nothing
                }
            }
        }
    }

    /**
     * The main method
     * @param argv The arguments
     */
    public static void main(String[] argv) {
        SharedPresentation presentation = new SharedPresentation();
        presentation.run(argv);
    }

    private void setKey(String key, String value) {
        if (key.equals("slideurl")) {
            System.out.println("Set Presentation: " + value);
            Download.startDownloading();
            String url = downloadData(value);
            Download.stopDownloading();
            currentPresentation = url;
            System.out.println("done download");
            System.out.println("Open Presentation: " + url);
            pres.openPresentation(currentPresentation);
            pres.GotoSlide(currentSlide);
            presUI.setPresentation(value);
            nSlides = pres.getNSlides();
            presUI.setNoSlides(nSlides);
            presLoaded = true;
        } else if (key.equals("slidenum")) {
            currentSlide = Integer.valueOf(value);
            if (presLoaded){
            	currentSlide=pres.GotoSlide(currentSlide);
            }
            presUI.updateUI(currentSlide);
        } else {
            System.out.println("Unused Application Key: " + key +" Value:" + value);
        }
    }

    /**
     *
     * @see ag3.AGSharedApplication#setState(ag3.interfaces.types.SharedAppState)
     */
    public void setState(SharedAppState appState) {
    	System.out.println("Setting SharedPresentation state");
        waitForPres();
        presUI.setSharedPresentation(this);
        Vector<AppDataDescription> appData = appState.getData();
        System.out.println(appState.getDescription());
        for (int i = 0; i < appData.size(); i++) {
        	System.out.println("addkey:" + appData.get(i).getKey() +" = "+ appData.get(i).getValue());
            setKey(appData.get(i).getKey(), appData.get(i).getValue());
        }
    }

    /**
     * Issues an event to the Shared Application Manager
     * @param eventType
     */
    public void sendEvent(String eventType){
        System.out.println(eventType);
    }

    /**
     *
     * @see ag3.AGSharedApplication#handleEvent(
     *     ag3.interfaces.types.EventDescription)
     */
    public void handleEvent(EventDescription event) {
        waitForPres();
        String eventType = event.getEventType();
        AppDataDescription appData;
        System.out.println("EventType: " + eventType);
        if (eventType.equals("Set application data")) {
            appData = (AppDataDescription) event.getData();
            if (appData.getKey().equals("slideurl")) {
                setKey(appData.getKey(), appData.getValue());
            }
        } else if (eventType.equals("next")) {
            currentSlide = pres.NextSlide();
        } else if (eventType.equals("prev")) {
            currentSlide = pres.PreviousSlide();
        } else if (eventType.equals("goto")) {
            currentSlide = pres.GotoSlide((Integer) event.getData());
        } else if (eventType.equals("Leave application")) {
            shutdown();
        } else {
            System.out.println("Unhandled Event: " + eventType);
        }
        presUI.updateUI(currentSlide);
    }

    /**
     *
     * @see ag3.AGSharedApplication#stop()
     */
    public void stop() {
        pres.closePresentation();
    }

}
