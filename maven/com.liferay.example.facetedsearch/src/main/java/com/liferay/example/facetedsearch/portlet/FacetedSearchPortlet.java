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
package com.liferay.example.facetedsearch.portlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import com.liferay.bookmarks.service.BookmarksEntryService;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermQueryFactoryUtil;
import com.liferay.portal.kernel.search.facet.AssetEntriesFacet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.ScopeFacet;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.util.PortalUtil;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Faceted Search Portlet",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class FacetedSearchPortlet extends GenericPortlet {

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();

		printWriter.println("FacetedSearch Portlet - Hello World!");
		
		// Begin 6.2 faceted search test
		printWriter.println("Conducting faceted search.");
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		SearchContext searchContext = SearchContextFactory.getInstance(httpServletRequest);
		
		Facet assetEntriesFacet = new AssetEntriesFacet(searchContext);
		assetEntriesFacet.setStatic(true);
		searchContext.addFacet(assetEntriesFacet);
		
		String[] entryClassNames = { "com.liferay.portlet.journal.model.JournalArticle" };
        searchContext.setEntryClassNames(entryClassNames);

        Facet scopeFacet = new ScopeFacet(searchContext);
        scopeFacet.setStatic(true);            
        searchContext.addFacet(scopeFacet);

        long[] groupIds = { 20177 };
        searchContext.setGroupIds(groupIds);
        
        long companyId = 20148;
        searchContext.setCompanyId(companyId);
        
        TermQuery termQuery = TermQueryFactoryUtil.create(searchContext, "title", "bacon");
        
        Hits hits = null;
        
        try {
			hits = SearchEngineUtil.search(searchContext, termQuery);
		}
        catch (SearchException se) {
			se.printStackTrace();
		}
        
		if (hits != null) {
			Document[] docs = hits.getDocs();

			printWriter.println("Number of hits (6.2 way): " + docs.length);
		}
		else {
			printWriter.println("No hits (6.2 way)");
		}
		// End 6.2 faceted search test
		
		// Begin 7.0 faceted search test
		TermQuery termQuery7 = new TermQueryImpl("title", "bacon");
		
        try {
			hits = SearchEngineUtil.search(searchContext, termQuery7);
		}
        catch (SearchException se) {
			se.printStackTrace();
		}
        
		if (hits != null) {
			Document[] docs = hits.getDocs();

			printWriter.println("Number of hits (7.0 way): " + docs.length);
		}
		else {
			printWriter.println("No hits (7.0 way)");
		}
		// End 7.0 faceted search test
		
		// Begin 7.0 services experiment
		Bundle bundle = FrameworkUtil.getBundle(FacetedSearchPortlet.class);
		BundleContext bundleContext = bundle.getBundleContext();
		
		String clazz = null;
		String filter = null;
		
		ServiceReference<?>[] serviceReferences = null;
		
		try {
			serviceReferences = bundleContext.getServiceReferences(clazz, filter);
		}
		catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}
		
		if (serviceReferences != null) {
			for (ServiceReference<?> serviceReference : serviceReferences) {
				System.out.println("serviceReference bundle symbolic name: " + serviceReference.getBundle().getSymbolicName());
			}
		}
		
		ServiceReference serviceReference = bundleContext.getServiceReference(
				BookmarksEntryService.class);
		
		BookmarksEntryService bookmarksService = (BookmarksEntryService) bundleContext.getService(serviceReference);
		
		int bookmarksCount = bookmarksService.getEntriesCount(20177, 20701);
		
		printWriter.println("Bookmarks folder has " + bookmarksCount + " entries.");
		// End 7.0 services experiment
	}

}