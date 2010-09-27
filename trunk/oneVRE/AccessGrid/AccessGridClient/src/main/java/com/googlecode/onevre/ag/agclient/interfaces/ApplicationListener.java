package com.googlecode.onevre.ag.agclient.interfaces;

import com.googlecode.onevre.ag.types.application.ApplicationDescription;

public interface ApplicationListener {
	void addApplication (ApplicationDescription application);
	void removeApplication (ApplicationDescription application);
}
