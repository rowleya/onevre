/*
 * @(#)SharedPowerPoint.java
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

import jp.ne.so_net.ga2.no_ji.jcom.IDispatch;
import jp.ne.so_net.ga2.no_ji.jcom.JComException;
import jp.ne.so_net.ga2.no_ji.jcom.ReleaseManager;

/**
 * An interface to powerpoint
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class SharedPowerPoint implements SharedPresInterface {

    private IDispatch ppt;
    private IDispatch presentations;
    private IDispatch presentation;
    private IDispatch view;
    private IDispatch slideShowWindow;
    private ReleaseManager rm;
    private int nSlides;

    /**
     * Opens a Presentation in PowerPoint opening the application if necessary
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#openPresentation(java.lang.String)}</dd></dl>
     * @param fName name of the presentation
     */
    public void openPresentation(String fName) {
        try {
            if (view != null) {
                view.method("Exit", new Object[0]);
            }
            if (presentation != null) {
                presentation.method("Close", new Object[0]);
            }
            System.err.println("Opening presentation " + fName);
            rm = new ReleaseManager();
            ppt = new IDispatch(rm, "PowerPoint.Application");
            ppt.put("Visible", new Boolean(true));
            presentations = (IDispatch) ppt.get("Presentations");
            presentation = (IDispatch) presentations.method("Open",
                    new Object[]{fName});
            IDispatch slides = (IDispatch) presentation.get("Slides");
            nSlides = (Integer) slides.get("Count");
            IDispatch slideShowSettings = (IDispatch) presentation.get(
                    "SlideShowSettings");

            // ppShowTypeWindow
            slideShowSettings.put("ShowType", new Integer(0x2));
            slideShowWindow = (IDispatch) slideShowSettings.method(
                    "Run", null);
            view = (IDispatch) slideShowWindow.get("View");
        } catch (JComException e) {
            e.printStackTrace();
        }
    }

    /**
     * go to the next visible slide in the presentation (jumping across hidden slides)
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#NextSlide()}</dd></dl>
     * @return the slide number of the active slide
     */
    public int NextSlide() {
        int current = -1;
        try {
            current = (Integer) view.get("CurrentShowPosition");
            System.err.println("Current = " + current);
            if (current != nSlides) {
                System.err.println("Sending next");
                view.method("Next", null);
                current = (Integer) view.get("CurrentShowPosition");
                System.err.println("Current now = " + current);
            }
        } catch (JComException e) {
                e.printStackTrace();
        }
        return current;
    }

    /**
     * go to the previous visible slide in the presentation (jumping across hidden slides)
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#PreviousSlide()}</dd></dl>
     * @return the slide number of the active slide
     */
    public int PreviousSlide() {
        int current = -1;
        try {
            current = (Integer) view.get("CurrentShowPosition");
            if (current != 1) {
                view.method("Previous", null);
                current = (Integer) view.get("CurrentShowPosition");
            }
        } catch (JComException e) {
                e.printStackTrace();
        }
        return current;
    }

    /**
     * go to the given slide in the presentation
    * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#GotoSlide(int)}</dd></dl>
     * @param slideNo slide number to go to [ 1 .. number of slides ]
     * @return the slide number of the active slide
     */
    public int GotoSlide(int slideNo) {
        Object [] args = new Object[1];
        args[0] = slideNo;
        int current = -1;
        try {
            current = (Integer) view.get("CurrentShowPosition");
            System.err.println("Current = " + current);
            if ((slideNo > 0) && (slideNo < nSlides)) {
                System.err.println("Going to slide " + slideNo);
                view.method("GotoSlide", args);
                current = (Integer) view.get("CurrentShowPosition");
                System.err.println("Current now " + current);
            }
        } catch (JComException e) {
                e.printStackTrace();
        }
        return current;
    }

    /**
     * Get the number of slides in the current presentation
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#getNSlides()}</dd></dl>
     * @return the number of slides in the presentation
     */
    public int getNSlides() {
        return nSlides;
    }

    /**
     * get the current slide
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#getCurrentSlide()}</dd></dl>
     * @return the currently active slide
     */
    public int getCurrentSlide() {
        int current = -1;
        try {
            current = (Integer) view.get("CurrentShowPosition");
        } catch (JComException e) {
            e.printStackTrace();
        }
        return current;
    };

    /**
     * close the current presentation and the presentation tool if possible
     * <dl><dt><b>overrides:</b></dt><dd>{@link sharedPresentation.SharedPresInterface#closePresentation()}</dd></dl>
     */
    public void closePresentation() {
        try {
            view.method("Exit", new Object[0]);
            presentation.method("Close", new Object[0]);
        } catch (JComException e) {
            e.printStackTrace();
        }
    }
}
