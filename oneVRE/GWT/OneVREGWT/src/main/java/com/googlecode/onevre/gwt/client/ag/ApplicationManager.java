package com.googlecode.onevre.gwt.client.ag;

import com.googlecode.onevre.gwt.client.interfaces.ApplicationManagerInterface;

public class ApplicationManager {

	private ApplicationManagerInterface ui = null;

	public ApplicationManager(ApplicationManagerInterface ui) {
		this.ui = ui;
		ui.setApplicationManager(this);
	}

}
