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
public class Page {
	
	public static String PAGE_TYPE_NORMAL = "Normal";
	public static String PAGE_TYPE_BRANCH = "Branch";
	
	private String id;
	private String initialState;
	private String type;
	private String sheetName;
	private String label;
	private String displayAfter;
	
	// processing variables
	private PageElement parentPageElement = null;
	
	private List<PageElement> elements = new ArrayList<PageElement>();
	private Map<String, Integer> elementLookup = new HashMap<String, Integer>();
	

	public Page(String sheetName, PageElement element, Page currentPage) {
		super();
		this.sheetName = sheetName;
		id = element.getId();
		initialState = (element.getLogicElement() == null) ? "Visible" : "Hidden";
		type = element.getPageType();
		parentPageElement = element;
		displayAfter = element.getPostLabel();
		element.setPostLabel(null);
		//System.out.println("Creating page " + id + " for sheet: " + sheetName + " with initialState " +initialState);
	    addElement(element);
		if ((getDisplayAfter() == null) && (!isVisible()) && (currentPage != null)) {
			setDisplayAfter(currentPage.getId());
		}
	}

	public String getId() {
		return id;
	}
	
	public String getSheetName() {
		return sheetName;
	}

	public String getInitialState() {
		return initialState;
	}

	public List<PageElement> getElements() {
		return elements;
	}

	public boolean isVisible() {
		if (initialState.toUpperCase().startsWith("V")) {
			return true;
		}
		return false;
	}

	public String getType() {
		return type;
	}

	public PageElement getParentPageElement() {
		return parentPageElement;
	}

	public String getDisplayAfter() {
		return displayAfter;
	}

	public void setDisplayAfter(String displayAfter) {
		this.displayAfter = displayAfter;
	}

	public boolean isBranchedPage () {
		return getType().equals(PAGE_TYPE_BRANCH);
	}
	
	protected void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addElement(PageElement element) {
		if (element.isARepeatingElement()) {
			System.out.println("Info: ignoring request to add repeating element " + getId() + " to page");
			return;
		}
		//System.out.println("Adding element for " + element.getId() + element.getDepth());
		if (element.getLookupTableId() != null) {
			//System.out.println("Adding lookup for " + element.getValuesListId());
			elementLookup.put(element.getLookupTableId(), new Integer(elements.size()));
		}
		elementLookup.put(element.getId(), new Integer(elements.size()));
		elements.add(element);
	}

	public PageElement findElementOnThisPage(String id) {
		Integer key = elementLookup.get(id);
		if (key == null) {
			// not on this page - maybe on another
			return null;
		}
		return elements.get(key.intValue());
	}
	
	public void assignTables(Map<String, LookupTable> tables) {
		for (Iterator<PageElement> i = elements.iterator(); i.hasNext();) {
			PageElement pageElement = (PageElement) i.next();
			if ((pageElement.getLookupTableId() != null) && (pageElement.getLookupTable() == null)) {
				LookupTable table = tables.get(pageElement.getLookupTableId());
				if (table != null) {
					pageElement.setLookupTable(table);
				}
				else {
					throw new IllegalArgumentException("No known table for " + pageElement.getLookupTableId());
				}
			}
		}
	}
	


}
