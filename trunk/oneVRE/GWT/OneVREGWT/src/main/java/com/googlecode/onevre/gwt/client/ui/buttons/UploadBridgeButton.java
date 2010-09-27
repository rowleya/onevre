package com.googlecode.onevre.gwt.client.ui.buttons;

import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class UploadBridgeButton extends ActionButton {

	public UploadBridgeButton() {
		setImageUrl("images/uploadbr.png");
		setName("Upload Bridge");
	}

	@Override
	public void action() {
		MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
	}

}
