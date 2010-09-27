/*
 * @(#)SharedOOImpress.java
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

import java.io.File;
import java.util.Vector;

import ooo.connector.BootstrapSocketConnector;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.drawing.XDrawView;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * An interface to Open Office
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class SharedOOImpress implements SharedPresInterface {

	private String OO_locations[] = { "/usr/lib64", "/usr/lib", "/opt",
			"/usr/local" };

	private String OO_names[] = { "openoffice", "ooo-2" };

	// private XPresentation xPresentation = null;

	private XComponent xComponent = null;

	private XController xController = null;
	private XComponentLoader xCLoader;

	private int nSlides = 0;
	private XDesktop xDesktop = null;

	/**
	 * Searches for Open Office locations on the local file system
	 *
	 * @return list of possible OpenOffice locations
	 */
	public Vector<String> findOpenOffice() {
		Vector<String> hopefully = new Vector<String>();
		String[] files = null;
		for (int i = 0; i < OO_locations.length; i++) {
			File file = new File(OO_locations[i]);
			files = file.list();
			if (files == null)
				continue;
			for (int j = 0; j < files.length; j++) {
				for (int k = 0; k < OO_names.length; k++) {
					if (files[j].startsWith(OO_names[k])) {
						File f = new File(file.getPath() + "/" + files[j]
								+ "/program");
						if (f.exists()) {
							System.out.println(f.getPath());
							hopefully.add(f.getPath());
						}
					}
				}
			}
		}
		return hopefully;
	}

	/**
	 * tries to start up OpenOffice
	 *
	 * @return the Component context necessary to communicate with OpenOffice
	 * @throws BootstrapException
	 */
	public XComponentContext bootstrapOpenOffice() throws BootstrapException {
		XComponentContext xContext = null;
		// get the remote office component context
		Vector<String> ooLocations = findOpenOffice();
		BootstrapException bse = null;
		String search = "OpenOffice not found in :\n";
		for (int i = 0; i < ooLocations.size(); i++) {
			try {
				xContext = BootstrapSocketConnector.bootstrap(ooLocations
						.get(i));
				System.out.println("Connected to a running office ...");
			} catch (BootstrapException e) {
				search += ooLocations.get(i) + "\n";
				bse = e;
			}
		}
		if (xContext == null) {
			throw (new BootstrapException(search, bse));
		}
		return xContext;
	}

	/**
	 * go to the given slide in the presentation
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#GotoSlide(int)}</dd>
	 * </dl>
	 *
	 * @param slideNo
	 *            slide number to go to [ 1 .. number of slides ]
	 * @return the slide number of the active slide
	 */
	public int GotoSlide(int slideNo) {
		XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
				XDrawView.class, xController);
		XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) UnoRuntime
				.queryInterface(XDrawPagesSupplier.class, xController
						.getModel());
		XDrawPage xDrawPage = null;
		try {
			xDrawPage = (XDrawPage) UnoRuntime.queryInterface(XDrawPage.class,
					xDrawPagesSupplier.getDrawPages().getByIndex(slideNo - 1));
			xDrawView.setCurrentPage(xDrawPage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getCurrentSlide();
	}

	/**
	 * go to the next visible slide in the presentation (jumping across hidden
	 * slides)
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#NextSlide()}</dd>
	 * </dl>
	 *
	 * @return the slide number of the active slide
	 */
	public int NextSlide() {
		return GotoSlide(getCurrentSlide() + 1);
	}

	/**
	 * go to the previous visible slide in the presentation (jumping across
	 * hidden slides)
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#PreviousSlide()}</dd>
	 * </dl>
	 *
	 * @return the slide number of the active slide
	 */
	public int PreviousSlide() {
		return GotoSlide(getCurrentSlide() - 1);
	}

	/**
	 * close the current presentation and the presentation tool if possible
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#closePresentation()}</dd>
	 * </dl>
	 */
	public void closePresentation() {
		xComponent.dispose();
		// xPresentation.end();
	}

	/**
	 * get the current slide
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#getCurrentSlide()}</dd>
	 * </dl>
	 *
	 * @return the currently active slide
	 */
	public int getCurrentSlide() {
		int cSlide = 0;
		XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
				XDrawView.class, xController);
		XDrawPage xDrawPage = xDrawView.getCurrentPage();
		XPropertySet xProperty = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, xDrawPage);
		try {
			cSlide = (Short) xProperty.getPropertyValue("Number");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cSlide;
	}

	/**
	 * Get the number of slides in the current presentation
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>{@link sharedPresentation.SharedPresInterface#getNSlides()}</dd>
	 * </dl>
	 *
	 * @return the number of slides in the presentation
	 */
	public int getNSlides() {
		return nSlides;
	}

	/**
	 * Opens a Presentation file opening the presentation tool if necessary
	 * <dl>
	 * <dt><b>overrides:</b></dt>
	 * <dd>
	 * {@link sharedPresentation.SharedPresInterface#openPresentation(java.lang.String)}
	 * </dd>
	 * </dl>
	 *
	 * @param name
	 *            name of the local file to open
	 */
	public void openPresentation(String name) {
		try {
			PropertyValue[] szEmptyArgs = new PropertyValue[0];

			xComponent = xCLoader.loadComponentFromURL("file://" + name,
					"_blank", 0, szEmptyArgs);
			XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) UnoRuntime
					.queryInterface(XDrawPagesSupplier.class, xComponent);
			nSlides = xDrawPagesSupplier.getDrawPages().getCount();
			xController = xDesktop.getCurrentFrame().getController().getModel()
					.getCurrentController();

			// Does not work as OpenOffice API is broken and doesn't support
			// slide show changes

			// XPresentationSupplier xPresentationSupplier
			// =(XPresentationSupplier
			// )UnoRuntime.queryInterface(XPresentationSupplier.class,
			// xComponent);
			// xPresentation = xPresentationSupplier.getPresentation();
			// XPropertySet xPresPropSet =
			// (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
			// xPresentation);
			// xPresPropSet.setPropertyValue("IsFullScreen", new Boolean(true));
			// xPresPropSet.getPropertyValue("")
			// xPresentation.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a Shared OpenOffice instance opening the OpenOffice Desktop
	 */
	public SharedOOImpress() {
		try {
			XComponentContext xContext = bootstrapOpenOffice();
			// get the service manager from the office
			XMultiComponentFactory xMCF = xContext.getServiceManager();
			// create a new instance of the the desktop
			Object oDesktop = xMCF.createInstanceWithContext(
					"com.sun.star.frame.Desktop", xContext);
			xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class,
					oDesktop);
			// query the desktop object for the XComponentLoader
			xCLoader = (XComponentLoader) UnoRuntime.queryInterface(
					XComponentLoader.class, oDesktop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test routine for Shared OpenOffice
	 *
	 * @param argv
	 */
	public static void main(String[] argv) {
		SharedPresInterface presentation = new SharedOOImpress();
		presentation.openPresentation("/home/ts23/oneVRE/oneVRE-2009-10-28.ppt");
		SharedPresentationUI presUI = new SharedPresentationUI(presentation);
		presUI.setPresentation("oneVRE-2009-10-28.ppt");
		presentation.GotoSlide(1);
		presentation.GotoSlide(5);
		while (true) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
