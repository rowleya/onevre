package com.googlecode.onevre.bridges.lowbag.common;

import java.io.IOException;

public interface LowBagClientInterface {

	boolean isAutoRatio();

	void addUpdate( String ssrc,  String key,  Object value) throws IOException;

	void setReportSSRC(String ssrc) throws IOException ;

	int getReportRate(Object report) throws IOException ;

	void removeReportSSRC(String ssrc) throws IOException ;

}
