/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.docs.example.bookmarksclient;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import com.liferay.bookmarks.service.BookmarksFolderLocalService;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Bookmarks Client Portlet",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class BookmarksClientPortlet extends GenericPortlet {

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();
		printWriter.println("Bookmarks Client Portlet - Hello World!");

		Bundle bundle = FrameworkUtil.getBundle(BookmarksClientPortlet.class);
		BundleContext bundleContext = bundle.getBundleContext();
		
		ServiceReference serviceReference = bundleContext.getServiceReference(BookmarksFolderLocalService.class);
		BookmarksFolderLocalService bookmarksFolderLocalService = (BookmarksFolderLocalService) bundleContext
				.getService(serviceReference);
		
		int bookmarksFoldersCount = bookmarksFolderLocalService.getBookmarksFoldersCount();
		
		printWriter.println("Bookmarks folder count: " + bookmarksFoldersCount);
	}

}