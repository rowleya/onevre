/*
 * @(#)SharedPresentationInterface.java
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

/**
 * Interface definition for a presentation tool used by the shared presentation
 * @author Tobias M Schiebeck
 * @version 1.0
 */

public interface SharedPresInterface {

	/**
	 * Opens a Presentation file opening the presentation tool if necessary
	 * @param fName name of the local file to open
	 */
	public void openPresentation(String fName);

	/**
	 * go to the next visible slide in the presentation (jumping across hidden slides)
	 * @return the slide number of the active slide
	 */
	public int NextSlide();

	/**
	 * go to the previous visible slide in the presentation (jumping across hidden slides)
	 * @return the slide number of the active slide
	 */
	public int PreviousSlide();

	/**
	 * go to the given slide in the presentation
	 * @param slideNo slide number to go to [ 1 .. number of slides ]
	 * @return the slide number of the active slide
	 */
	public int GotoSlide(int slideNo);

	/**
	 * Get the number of slides in the current presentation
	 * @return the number of slides in the presentation
	 */
	public int getNSlides();

	/**
	 * get the current slide
	 * @return the currently active slide
	 */
	public int getCurrentSlide();

	/**
	 * close the current presentation and the presentation tool if possible
	 */
	public void closePresentation();
}
