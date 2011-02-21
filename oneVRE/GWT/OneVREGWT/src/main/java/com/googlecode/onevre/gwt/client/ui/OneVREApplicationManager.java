package com.googlecode.onevre.gwt.client.ui;

import com.googlecode.onevre.gwt.client.ag.ApplicationManager;
import com.googlecode.onevre.gwt.client.interfaces.ApplicationManagerInterface;

public class OneVREApplicationManager implements ApplicationManagerInterface {

    private ApplicationManager applicationManager = null;

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public void updateUI() {
        // TODO Auto-generated method stub

    }

}
