package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.client.ui.buttons.CreateVenueButton;
import com.googlecode.onevre.gwt.client.ui.buttons.VenueTreeButton;

public class VenueServerHeaderPanel extends FlexTable implements ClickHandler {

	VenueServerPanel vsp = null;

	public VenueServerHeaderPanel(VenueServerType vs, VenueServerPanel vsp) {
		this.vsp = vsp;
		HorizontalPanel imgPanel = new HorizontalPanel();
		Image image = vs.getImage();
		image.setHeight("30px");
		imgPanel.add(image);
		imgPanel.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_CENTER);
		imgPanel.setCellVerticalAlignment(image, HorizontalPanel.ALIGN_MIDDLE);
		imgPanel.setWidth("75px");
		imgPanel.setHeight("40px");
		HorizontalPanel lblPanel = new HorizontalPanel();
		Label title = new Label(vs.getDescription());
		title.setStyleName("portlet-title");
		lblPanel.add(title);
		this.setStyleName("gwt-StackPanelItem");
		this.setWidth("100%");
		this.setWidget(0, 0, imgPanel);
		this.getFlexCellFormatter().setWidth(0, 0, "80px");
		this.setWidget(0, 1, lblPanel);
		VenueTreeButton vtButton = new VenueTreeButton(vs);
		this.addClickHandler(this);
		HorizontalPanel buttonpanel = new HorizontalPanel();
		if (vs.isManagable()){
			CreateVenueButton cvButton = new CreateVenueButton(vs.toUrl());
			buttonpanel.add(cvButton.getButton());
		}
		buttonpanel.add(vtButton.getButton());
		this.setWidget(0,2,buttonpanel);
		this.getFlexCellFormatter().setWidth(0, 2, "40px");
	}

	public void onClick(ClickEvent event) {
		Cell cell = getCellForEvent(event);
		if (cell.getCellIndex()!=getCellCount(cell.getRowIndex())-1){
			vsp.toggle();
		}
	}

}
