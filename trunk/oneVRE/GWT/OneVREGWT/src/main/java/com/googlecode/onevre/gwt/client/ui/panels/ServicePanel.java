package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.onevre.gwt.client.ag.types.ServiceDescription;
import com.googlecode.onevre.gwt.client.ui.Icons;

public class ServicePanel extends FlexTable {

	Vector<ServiceDescription> serviceDescriptions= new Vector<ServiceDescription>();

	public ServicePanel(Vector<ServiceDescription> serviceDescriptions) {
		this.serviceDescriptions = serviceDescriptions;
		this.setWidth("100%");
		this.setHeight("100%");
		int i = 0;
		for (ServiceDescription serviceDescription : serviceDescriptions){
			this.setWidget(i,0,new Image(Icons.serviceIcon));
			this.setText(i, 1, serviceDescription.getName());
			this.setText(i, 2, serviceDescription.getDescription());
			i++;
		}
	}
}
