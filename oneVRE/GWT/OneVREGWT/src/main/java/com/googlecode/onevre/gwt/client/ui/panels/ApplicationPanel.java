package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.onevre.gwt.client.ag.types.ApplicationDescription;
import com.googlecode.onevre.gwt.client.ui.Icons;

public class ApplicationPanel extends FlexTable {

//    private Vector<ApplicationDescription> applicationDescriptions = new Vector<ApplicationDescription>();

    public ApplicationPanel(Vector<ApplicationDescription> applicationDescriptions) {
//        this.applicationDescriptions = applicationDescriptions;
        this.setWidth("100%");
        this.setHeight("100%");
        int i = 0;
        for (ApplicationDescription applicationDescription : applicationDescriptions) {
            Image appImg = new Image(Icons.applicationIcon);
            appImg.setTitle(applicationDescription.getName());
            appImg.setHeight("22px");
            this.setWidget(i, 0, appImg);
            this.setText(i, 1, applicationDescription.getName());
            this.setText(i, 2, applicationDescription.getDescription());
            i++;
        }
    }
}
