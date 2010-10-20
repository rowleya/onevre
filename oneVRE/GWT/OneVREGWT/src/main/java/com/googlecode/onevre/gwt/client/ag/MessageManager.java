package com.googlecode.onevre.gwt.client.ag;



import com.googlecode.onevre.gwt.client.ag.types.MessageBox;
import com.googlecode.onevre.gwt.client.ag.types.UploadStatus;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.interfaces.MessageManagerInterface;


public class MessageManager {

	private MessageManagerInterface ui = null;

	public MessageManager(MessageManagerInterface ui) {
		this.ui = ui;
		ui.setMessageManager(this);
	}

	public void showUpload(VenueState state, UploadStatus uploadStatus) {
		ui.showUpload(uploadStatus);

	}

	public void setUploadStatus(VenueState state, UploadStatus uploadStatus) {
		ui.setUploadStatus(uploadStatus);

	}

	public void hideUpload(VenueState state) {
		ui.hideUpload();
	}

	public void addMessage(VenueState state, MessageBox message) {
		ui.showMessage(message);

	}


}
