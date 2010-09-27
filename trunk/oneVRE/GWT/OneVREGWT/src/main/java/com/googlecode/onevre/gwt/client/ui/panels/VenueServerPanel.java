package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;

public class VenueServerPanel extends FlexTable {

	public VenueServerPanel(VenueServerType vst, Panel ServerVenuePanel) {
		this.setWidth("100%");
		this.setWidget(0, 0,new VenueServerHeaderPanel(vst, this));
		this.getFlexCellFormatter().setHeight(0, 0,"50px");
		this.setStyleName("gwt-StackPanel");
		this.setWidget(1, 0, ServerVenuePanel);
	}


	public void toggle () {
		boolean vis = this.getFlexCellFormatter().isVisible(1, 0);
		vis = !vis;
		this.getFlexCellFormatter().setVisible(1,0, vis);
	}


}
