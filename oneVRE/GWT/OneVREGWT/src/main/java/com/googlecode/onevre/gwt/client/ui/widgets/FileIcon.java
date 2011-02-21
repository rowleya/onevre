package com.googlecode.onevre.gwt.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ui.Icons;

public class FileIcon extends Image implements ErrorHandler {

    private String type = DataDescription.TYPE_FILE;

    private String mimeType = null;

    private boolean open = false;

    private boolean hadError = false;

    public FileIcon() {
        super(Icons.fileIcon);
    }

    public static String iconFromMimeType(String mimeType) {
        if (mimeType != null) {
            String icon = mimeType.replaceAll("/", "-");
            return GWT.getModuleBaseURL() + "images/icons/mimetypes/" + icon + ".png";
        }
        return Icons.fileIcon;
    }

    public FileIcon(String type, String mimeType) {
        addErrorHandler(this);
        this.mimeType = mimeType;
        this.type = type;
        if (type.equals(DataDescription.TYPE_DIR)) {
            setUrl(Icons.folderIcon);
        } else {
            setUrl(iconFromMimeType(mimeType));
        }
    }

    public void setMimeType(String mimeType) {
        if (!type.equals(DataDescription.TYPE_DIR)) {
            this.mimeType = mimeType;
            setUrl(iconFromMimeType(mimeType));
        }
    }

    public boolean toggle() {
        if (type.equals(DataDescription.TYPE_DIR)) {
            open = !open;
            if (open) {
                setUrl(Icons.openfolderIcon);
            } else {
                setUrl(Icons.folderIcon);
            }
        }
        return open;
    }

    public boolean isOpen() {
        return open;
    }

    public String getType() {
        return type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void onError(ErrorEvent paramErrorEvent) {
        if (!hadError) {
            setUrl(Icons.fileIcon);
            hadError = true;
        }
    }

}
