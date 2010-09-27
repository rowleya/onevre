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
package net.sf.ufsc.http;

import java.net.URI;

import net.sf.ufsc.Provider;
import net.sf.ufsc.Session;

/**
 * @author Paul Ferraro
 */
public class HttpProvider implements Provider
{
	/**
	 * @see net.sf.ufsc.Provider#createSession(java.net.URI)
	 */
	public Session createSession(URI uri)
	{
		return new HttpSession(uri);
	}

	/**
	 * @see net.sf.ufsc.Provider#getScheme()
	 */
	public String getScheme()
	{
		return "http"; //$NON-NLS-1$
	}

	/**
	 * @see net.sf.ufsc.Provider#getDefaultPort()
	 */
	public int getDefaultPort()
	{
		return 80;
	}
}
