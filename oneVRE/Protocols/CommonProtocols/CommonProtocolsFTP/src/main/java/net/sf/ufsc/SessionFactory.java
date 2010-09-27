/*
 * UFSC: Universal File Server Connectivity
 * Copyright (c) 2004-2007 Paul Ferraro
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact: ferraro@users.sourceforge.net
 */
package net.sf.ufsc;

import java.io.IOException;
import java.net.URI;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

import net.sf.ufsc.ftp.FtpsProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A factory for obtaining sessions.
 * @author  Paul Ferraro
 * @since   1.0
 */
public class SessionFactory implements URLStreamHandlerFactory
{
	private static final String DEFAULT_SCHEME = "local"; //$NON-NLS-1$

	private static SessionFactory instance = new SessionFactory();

	private Log logger = LogFactory.getLog(this.getClass());

	private Map<String, Provider> providerMap = new HashMap<String, Provider>();

	private SessionFactory()
	{
		Provider provider = new FtpsProvider();
		providerMap.put(provider.getScheme(), provider);
	}

	public Provider getProvider(String scheme) throws IOException
	{
		Provider provider = this.providerMap.get((scheme != null) ? scheme : DEFAULT_SCHEME);

		if (provider == null)
		{
			throw new IOException(Messages.getMessage(Messages.PROVIDER_NOT_FOUND, scheme));
		}

		return provider;
	}

	/**
	 * Returns a session for the file server indicated by the specified URI.
	 * @param uri a URI
	 * @return a file server session
	 * @throws java.io.IOException if uri scheme is invalid, or a session could not be obtained for this file server defined by the specified URI.
	 */
	public static Session getSession(URI uri) throws java.io.IOException
	{
		return instance.getProvider(uri.getScheme()).createSession(uri);
	}

	public static URLStreamHandlerFactory getURLStreamHandlerFactory()
	{
		return instance;
	}

	/**
	 * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
	 */
	public URLStreamHandler createURLStreamHandler(String protocol)
	{
		try
		{
			return new ProviderURLStreamHandler(this.getProvider(protocol));
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
}
