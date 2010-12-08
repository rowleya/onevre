package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import com.googlecode.onevre.gwt.client.ag.types.VOAttribute;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ui.Icons;
import com.googlecode.onevre.gwt.client.ui.VoAttributeRow;
import com.googlecode.onevre.gwt.client.ui.buttons.AddVOButton;

import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class CreateVenuePanel extends ModalPopup<DockPanel> implements ClickHandler {

	private static String DATE_FORMAT = "EEE, MMM d, yyyy, HH:mm:ss";

	Vector<VOAttribute> voAttributes  = null;

	TextBox fileNameInput = new TextBox();
	TextArea descriptionInput = new TextArea();
	DateBox dateInput = new DateBox();
//	TextBox  = new TextBox();
	TextBox locationInput = new TextBox();
	TextBox homeInput = new TextBox();
	ListBox profileInput = new ListBox();
	PushButton cancel = new PushButton("Cancel",this);
	PushButton clear = new PushButton("Clear",this);
	PushButton ok = new PushButton("Upload Data Item",this);
	FlexTable profilepanel = new FlexTable();
//	NamedFrame targetFrame = new NamedFrame("victim");
//	FormPanel form = null;
	Hidden parent = new Hidden("parentId");
	Hidden namespace = new Hidden("namespace", Application.getParam("pag_namespace"));
	Hidden venueUri = new Hidden("venueUri");
	Hidden expiry = new Hidden("expiry");
	Hidden description = new Hidden("description");

	boolean changed = false;

	public boolean hasChanged(){
		return changed;
	}

	public CreateVenuePanel(Vector<VOAttribute> voAttributes) {
		super(new DockPanel());
		this.voAttributes = voAttributes;
		DockPanel panel= getWidget();
		Image img = new Image(Icons.addVenueIcon);
		img.setHeight("64px");
		panel.add(img,DockPanel.WEST);
		panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
//		Grid profilepanel = new Grid (3,3);
		profilepanel.setWidget(1,0, new Image(Icons.fileIcon));
		profilepanel.setText(1,1, "Name: *");
		fileNameInput.setName("file");
		profilepanel.setWidget(1, 2, fileNameInput);
		profilepanel.getFlexCellFormatter().setColSpan(1,2,2);
		Image docInfo = new Image(Icons.docInfoIcon);
		docInfo.setHeight("22px");
		profilepanel.setWidget(2,0, docInfo);
		profilepanel.setText(2,1, "Description: *");
		descriptionInput.setVisibleLines(3);
		//descriptionInput.setName("description");
		profilepanel.setWidget(2, 2, descriptionInput);
		profilepanel.getFlexCellFormatter().setColSpan(2,2,2);
		panel.add(profilepanel,DockPanel.CENTER);

		FlexTable voAttPanel = new FlexTable();
		voAttPanel.setText(0, 0, "Required VO attributes");
		voAttPanel.getFlexCellFormatter().setColSpan(0,0, 3);
		voAttPanel.setText(1,0,"VO");
		voAttPanel.setText(1,1,"Group");
		voAttPanel.setText(1,2,"Role");
		addVOAttribute(voAttPanel);

		AddVOButton addVOButton = new AddVOButton(this, voAttPanel);
		profilepanel.setWidget(3, 0, voAttPanel);
		profilepanel.getFlexCellFormatter().setColSpan(3,0,2);
		profilepanel.setWidget(3, 1, addVOButton.getButton());
		Image timeout = new Image(Icons.timeoutIcon);
		timeout.setHeight("22px");

		DockPanel buttons  = new DockPanel();
		buttons.add(ok, DockPanel.EAST);
		buttons.add(cancel, DockPanel.WEST);
		panel.add(buttons,DockPanel.SOUTH);
        panel.setWidth("100%");
	}

	public void onClick(ClickEvent event) {
		if (event.getSource().equals(clear)){
			dateInput.setValue(null);
			return;
		}
		if (event.getSource().equals(ok)){
			Date date = dateInput.getValue();
			String expString = "";
			if (date!=null){
				expString = DateTimeFormat.getFormat(DATE_FORMAT).format(date);
			}
			expiry.setValue(expString);
			description.setValue(descriptionInput.getValue());
		}
		hide();
	}

	public void addVOAttribute(FlexTable voAttPanel) {
		new VoAttributeRow(voAttPanel);
	}

}
