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

import java.net.URI;

/**
 * @author  Paul Ferraro
 * @since   1.0
 */
public class UserInfo
{
	private String user;
	private String password;
	
	public UserInfo(URI uri)
	{
		String userInfo = uri.getUserInfo();
		
		if (userInfo != null)
		{
			String[] parts = userInfo.split(":"); //$NON-NLS-1$
			
			this.user = parts[0];
			
			if (parts.length > 1)
			{
				this.password = parts[1];
			}
		}
	}
	
	public String getUser()
	{
		return this.user;
	}
	
	public String getPassword()
	{
		return this.password;
	}
}
