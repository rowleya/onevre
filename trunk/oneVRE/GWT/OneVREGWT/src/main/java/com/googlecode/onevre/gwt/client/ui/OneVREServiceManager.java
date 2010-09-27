package com.googlecode.onevre.gwt.client.ui;

import com.googlecode.onevre.gwt.client.ag.ServiceManager;
import com.googlecode.onevre.gwt.client.interfaces.ServiceManagerInterface;

public class OneVREServiceManager implements ServiceManagerInterface {

	ServiceManager serviceManager = null;

	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void updateUI() {
		// TODO Auto-generated method stub

	}

}
