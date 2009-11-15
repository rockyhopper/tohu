/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tohu.domain.questionnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Derek Rendall
 *
 */
public class Application {
	
	private String id;
	private String applicationClass;
	private String applicationName;
	private String completionAction;
	private String activePage = null;
	private String note;
	private List<Page> pageList = new ArrayList<Page>();
	private List<String> imports = new ArrayList<String>();
	
	private List<PageElement> globalElements = new ArrayList<PageElement>();
	
	private Map<String, LookupTable> listTables = new HashMap<String, LookupTable>();
	private List<String> initiatedAlternateConsequences = new ArrayList<String>();

	
	public Application() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public LookupTable getLookupTable(String key) {
		return listTables.get(key);
	}

	public void addLookupTable(LookupTable table) {
		this.listTables.put(table.getId(), table);
	}

	public void addGlobalElement(PageElement element) {
		globalElements.add(element);
	}

	public List<PageElement> getGlobalElements() {
		return globalElements;
	}

	public void setApplicationClass(String applicationClass) {
		this.applicationClass = applicationClass;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setCompletionAction(String completionAction) {
		this.completionAction = completionAction;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getApplicationClass() {
		return applicationClass;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getCompletionAction() {
		return completionAction;
	}
		
	public String getActivePage() {
		return activePage;
	}

	public void setActivePage(String activePage) {
		this.activePage = activePage;
	}

	public void addImport(String name) {
		imports.add(name);
	}
	
	public List<String> getImports() {
		return imports;
	}

	public void addPage(Page thePage) {
		//System.out.println("addPage for " + name + " type: " + thePage.getType() + " getDisplayAfter: " + String.valueOf(thePage.getDisplayAfter()));
		pageList.add(thePage);
	}
	
	public List<Page> getPageList() {
		return pageList;
	}
	
	public String getItemList() {
		if (pageList.isEmpty()) {
			throw new IllegalStateException("You must have at least one page");
		}
		
		boolean found = false;
		List<String> orderedPages = new ArrayList<String>();
		for (int i = 0; i < pageList.size(); i++) {
			Page pg = pageList.get(i);
			String pageName = pg.getId();
			if (!pg.isBranchedPage()) {
				if (pg.getDisplayAfter() != null) {
					//System.out.println("Page " + pageName + " displayed after " + pg.getDisplayAfter());
					int pos = orderedPages.indexOf(pg.getDisplayAfter());
					if ((pos < 0) || (pos == (orderedPages.size() - 1))) {
						orderedPages.add(pageName);
					}
					else {
						orderedPages.add(pos + 1, pageName);
					}
				}
				else {
					orderedPages.add(pageName);
				}
				found = true;
			}
		}
		if (!found) {
			throw new IllegalStateException("You must have at least one non branched page to start with");
		}
		
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < orderedPages.size(); i++) {
			String pageName = orderedPages.get(i);
			str = str.append((i > 0) ? ", \"" : "\"").append(pageName).append("\"");
		}
		
		return str.toString();
	}
	
	public PageElement findPageElement(String id) {
		if ((id == null) || (id.length() == 0)) {
			return null;
		}
		for (Iterator<PageElement> iterator = globalElements.iterator(); iterator.hasNext();) {
			PageElement element = (PageElement) iterator.next();
			if (element.getId().equals(id)) {
				return element;
			}
		}
		for (Iterator<Page> iterator = pageList.iterator(); iterator.hasNext();) {
			Page pg = iterator.next();
			PageElement element = pg.findElementOnThisPage(id);
			if (element != null) {
				return element;
			}
		}
		return null;
	}
	
	public void processTableEntries() {		
		for (Iterator<Page> i = pageList.iterator(); i.hasNext();) {
			Page pg = i.next();
			pg.assignTables(listTables);
		}
		
	}
	

	/**
	 * @param id
	 * @return true if this has not been added before
	 */
	public boolean addNewAlternateConsequence(String id) {
		if (initiatedAlternateConsequences.contains(id)) {
			//System.out.println("Already used AlternateConsequence Id: " + id);
			return false;
		}
		initiatedAlternateConsequences.add(id);
		//System.out.println("Not used AlternateConsequence Id: " + id);
		return true;
	}
		

//	public LookupTable getCurrentTableList() {
//		return currentTableList;
//	}
//
//	public void setCurrentTableList(PageElementValuesLink theEntry) {
//		if (theEntry != null) {
//			listTables.put(theEntry.getId(), theEntry);
//		}
//		this.currentTableList = theEntry;
//	}
//	
//	public Map<String, PageElementValuesLink> getListTables() {
//		return listTables;
//	}

	
	


}
