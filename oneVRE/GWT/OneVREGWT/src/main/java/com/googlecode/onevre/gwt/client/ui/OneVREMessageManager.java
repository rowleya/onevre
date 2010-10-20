package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.googlecode.onevre.gwt.client.ag.MessageManager;
import com.googlecode.onevre.gwt.client.ag.types.MessageBox;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatus;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.MessageManagerInterface;
import com.googlecode.onevre.gwt.client.ui.panels.JabberPanel;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.ProgressPopup;

public class OneVREMessageManager implements MessageManagerInterface {

	ProgressPopup progress = null;

	MessageManager messageManager = null;

	HashMap<VenueState, JabberPanel> jabberPanels = new HashMap<VenueState, JabberPanel>();

	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	@Override
	public void showUpload(UploadStatus uploadStatus) {
		progress = new ProgressPopup("Uploading file: "+ uploadStatus.getFileName(), false);
		progress.setMax(uploadStatus.getSize());
		progress.show();
	}

	@Override
	public void setUploadStatus(UploadStatus uploadStatus) {
		progress.setValue(uploadStatus.getCurrent());
	}

	@Override
	public void hideUpload() {
		progress.hide();
	}

	@Override
	public void showMessage(MessageBox message) {
		String messageType = MessagePopup.INFO;
		if (message.getType().equals("error")){
			messageType = MessagePopup.ERROR;
		}
		MessagePopup box = new MessagePopup(message.getMessage(), null, messageType, 3);
		box.show();
	}

}
