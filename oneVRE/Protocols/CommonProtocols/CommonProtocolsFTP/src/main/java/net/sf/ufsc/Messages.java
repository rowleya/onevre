/*
 * Copyright (c) 2004-2007, Identity Theft 911, LLC.  All rights reserved.
 */
package net.sf.ufsc;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author Paul Ferraro
 */
@SuppressWarnings("nls")
public class Messages
{
	public static final String PROVIDER_FOUND = "provider-found";
	public static final String PROVIDER_NOT_FOUND = "provider-not-found";
	
	private static ResourceBundle resource = ResourceBundle.getBundle(Messages.class.getName());
	
	/**
	 * Returns the localized message using the specified resource key and potential arguments.
	 * @param key a resource key
	 * @param args a variable number of arguments
	 * @return a localized message
	 */
	public static String getMessage(String key, Object... args)
	{
		String message = resource.getString(key);
		
		return (args.length == 0) ? message : MessageFormat.format(message, args);
	}
	
	private Messages()
	{
		// Hide constructor
	}
}
