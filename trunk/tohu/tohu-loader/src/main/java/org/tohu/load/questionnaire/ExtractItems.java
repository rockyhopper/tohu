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
package org.tohu.load.questionnaire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tohu.domain.questionnaire.Application;
import org.tohu.domain.questionnaire.Page;
import org.tohu.domain.questionnaire.PageElement;
import org.tohu.domain.questionnaire.conditions.ConditionClause;
import org.tohu.domain.questionnaire.conditions.PageElementCondition;
import org.tohu.load.spreadsheet.SpreadsheetItem;
import org.tohu.load.spreadsheet.SpreadsheetRow;
import org.tohu.load.spreadsheet.sections.SpreadsheetSection;

/**
 * 
 * @author Derek Rendall
 *
 */
public class ExtractItems implements SpreadsheetSectionConstants {
	
	private Application application = null;
	protected Page currentPage = null;
	protected int currentDepth = 1;
	private List<PageElement> currentElementsAtDepth = new ArrayList<PageElement>();
	private List<Page> currentPageAtDepth = new ArrayList<Page>();
	private String currentSheetName;
	
	public static final String ELEMENT_PAGE_UPPER = "PAGE";
	public static final String ELEMENT_BRANCH_UPPER = "BRANCH";
	
	public ExtractItems(Application application, Page currentPage) {
		super();
		this.application = application;
		this.currentPage = currentPage;
	}
		
	/**
	 * @param element
	 */
	protected void createNormalIntermediateGroup(PageElement element) {
		PageElement currentParent = getElementAtDepth(currentDepth);
		PageElement newGroup = new PageElement();
		String tempStr = "_CHILDREN";
		System.out.println("Warning: creating new Intermediate Group: " + currentParent.getId()+tempStr + " current depth = " + currentDepth);
		newGroup.setId(currentParent.getId() + tempStr, currentDepth, element.getRowNumber());
		newGroup.setType("Group");

		if (!currentParent.isAGroupType()) {
			if (currentDepth == 1) {
				currentParent = currentPage.getParentPageElement();
			} 
			else {
				currentParent = getElementAtDepth(currentDepth - 1);
			}
		}
		currentPage.addElement(newGroup);
		currentParent.addChild(newGroup);
		currentElementsAtDepth.set(currentDepth - 1, newGroup);
	}
	
	/**
	 * @param element
	 */
	protected void setElementAtDepth(PageElement element) {
		
		int depth = element.getDepth();

		if ((depth < 1) || (depth > (currentElementsAtDepth.size() + 1))) {
			throw new IllegalArgumentException("Cannot set an element depth of " + String.valueOf(depth) + " when depth tree size is " + String.valueOf(currentElementsAtDepth.size()));
		}
		
		if (element.isAConsequenceType()) {
			if (currentPage == null) {
				application.addGlobalElement(element);
				return;
			}
			if (depth == 1) {
				currentPage.getParentPageElement().addChild(element);
			}
			else {
				getElementAtDepth(depth - 1).addChild(element);
			}
			currentPage.addElement(element);
			return;
		}
		
		// now check to see if we need to add an automatic group
		// Note: depth == 1 will mean that there is a page group created, therefore no problem
		if ((depth > 1) && (depth > currentDepth) && (!getElementAtDepth(currentDepth).isAGroupType()) && (!element.isABranchedPage())) {
			//System.out.println("Item at current depth: " + getElementAtDepth(currentDepth).getId());
			createNormalIntermediateGroup(element);
		}
		
		if (!element.isAPageElement() && (currentElementsAtDepth.size() == 0)) {
			//System.out.println("About to create a default master page");
			PageElement masterElement = new PageElement();
			masterElement.setId("DefaultPage", 0, 0);
			masterElement.setType("Page");
			currentPage = new Page(currentSheetName, masterElement, currentPage);
			application.addPage(currentPage);
		}
		else if (element.isAPageElement()) {
			currentPage = new Page(currentSheetName, element, currentPage);
			application.addPage(currentPage);
		}
		
		// Add it into the right position on the working lists
		if (depth > currentElementsAtDepth.size()) {
			//System.out.println("Setting element " + element.getId() + " to depth " + currentElementsAtDepth.size() + 1);
			currentElementsAtDepth.add(element);
			currentPageAtDepth.add(currentPage);
		}
		else {
			//System.out.println("Setting element " + element.getId() + " at depth " + depth);
			currentElementsAtDepth.set(depth - 1, element);
			for (int i = (currentElementsAtDepth.size() - 1); i >= depth ; i--) {
				currentElementsAtDepth.remove(i);
				currentPageAtDepth.remove(i);
			}
			if (element.isAPageElement()) {
				currentPageAtDepth.set(depth - 1, currentPage);
			}
			else {
				currentPage = currentPageAtDepth.get(depth - 1);
			}
		}
		
		// Deal with linking elements on the page
		if (!element.isAPageElement()) {
			currentPage.addElement(element);
			PageElement tempElement = (depth == 1) ? currentPage.getParentPageElement() : getElementAtDepth(depth - 1);
			tempElement.addChild(element);
		}
		currentDepth = element.getDepth();
	}
	

	/**
	 * @param depth
	 * @return
	 */
	protected PageElement getElementAtDepth(int depth) {
		if ((depth < 1) || (depth > currentElementsAtDepth.size())) {
			return null;
		}
		return currentElementsAtDepth.get(depth - 1);
	}
	
	
	
	/**
	 * @param section
	 * @return
	 */
	public Page processSectionData(SpreadsheetSection section) {
		List<SpreadsheetRow> rows = section.getSectionRows();
		currentSheetName = section.getSheetName();
		PageElement lastRealElement = null;
		
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			if (spreadsheetRow.getRowItems().size() == 0) {
				continue;
			}
			
			//System.out.println("Processing row " + spreadsheetRow.getRowNumber());
			
			PageElement element = extractPageElement(section, spreadsheetRow);
			
			//System.out.println("Processing line " + spreadsheetRow.getRowNumber() + " item id " + element.getId() + " depth " + String.valueOf(element.getDepth()));
			
			if (element.getId() != null){
				// ie not a display fact or consequence extension
				setElementAtDepth(element);
				lastRealElement = element;
			}
			
			// Now deal with display facts
			if ((element.getLogicElement() != null) && (!element.getLogicElement().isProcessed())) {
				if (lastRealElement.isAConsequenceType()) {
					if (element.getId() != null) {
						if (element.getLogicElement() == null) {
							throw new IllegalArgumentException("You must specify a logic clause on a Consequence " + element.getId());
						}
						// Will turn this into a global by not requiring the parent to be visible
						element.setRequired("Yes");
					}					
					processDisplayFactLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
				else if (lastRealElement.isAValidationElement()) {
					processValidationLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
				else {
					processDisplayFactLine(lastRealElement, element, spreadsheetRow.getRowNumber());
				}
			}
		}
		
		return currentPage;
	}
	
	
	protected PageElement extractPageElement(SpreadsheetSection section, SpreadsheetRow row) {
		SpreadsheetRow headings = section.getHeaderRow();
		PageElement element = new PageElement();
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				System.out.println("Ignoring value: " + item);
				continue;
			}
			String value = item.toString();
			if (key.startsWith(PAGE_ITEMS_UPPER)) {
				element.setId(value, section.getHeaderDepthForColumn(item.getColumn()), item.getRow());
				continue;
			}
			if (key.startsWith("SET")) {
				element.setDefaultValueStr(value);
				continue;
			}
			if (key.startsWith("STYLE")) {
				element.addStyle(value);
				continue;
			}
			if (key.startsWith("TYPE")) {
				element.setType(value);
				continue;
			}
			if (key.startsWith("REQUIRE")) {
				element.setRequired(value);
				continue;
			}
			if (key.startsWith("DATA")) {
				element.setFieldType(value);
				continue;
			}
			if (key.startsWith("PRE")) {
				element.setPreLabel(value);
				continue;
			}
			if (key.startsWith("POST")) {
				element.setPostLabel(value);
				continue;
			}
			if (key.startsWith("SELECTION")) {
				element.setLookupTableId(value);
				continue;
			}
			if (key.startsWith("CATEGORY")) {
				element.setCategory(value);
				continue;
			}
			if (key.startsWith("DEPENDS")) {
				element.setLogicDependsOnItemId(value);
				continue;
			}
			if (key.startsWith("ATTRIBUTE")) {
				element.setLogicAttribute(value);
				continue;
			}
			if (key.startsWith("OPERATION")) {
				element.setLogicOperation(value);
				continue;
			}
			if (key.startsWith("VALUE")) {
				element.setLogicValue(value);
				continue;
			}
			System.out.println("Unknown Section key: " + key);
		}
		if ((element.getId() != null) && (element.getType() == null)) {
			throw new IllegalArgumentException("Row " + String.valueOf(row.getRowNumber() + 1) + " has no type!");
		}
		if ((element.getType() != null) && (element.isAConsequenceType()) && (element.getLogicElement() == null) && (currentPage != null)) {
			int depth = currentDepth;
			try {
				while (depth > 0) {
					PageElement temp = getElementAtDepth(depth);
					if ((temp != null) && (temp.getLogicElement() != null)) {
						element.setDisplayCondition((PageElementCondition)temp.getDisplayCondition().clone());
						element.setLogicElement(temp.getLogicElement());
						depth = 0;
					}
					depth--;
				}
				if (element.getLogicElement() == null) {
					if (currentPage.getParentPageElement().getLogicElement() != null) {
						element.setDisplayCondition((PageElementCondition)currentPage.getParentPageElement().getDisplayCondition().clone());
						element.setLogicElement(currentPage.getParentPageElement().getLogicElement());
					}
					else {
						throw new IllegalArgumentException("Row " + String.valueOf(row.getRowNumber() + 1) + " has a consequence with no condition or parent with a condition!");
					}
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				throw new IllegalStateException(e.getMessage());
			}
		}
		
		return element;
	}
	
	
	protected void processValidationLine(PageElement masterElement, PageElement element, int row) {
		// TODO handle repeated elements?
		ConditionClause le = element.getLogicElement();
		if (element.getId() != null) {
			// first line 
			String type = PageElementCondition.TYPE_VALIDATION;
			masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row));
		}
		masterElement.getDisplayCondition().addElement(le);
		le.setProcessed(true);
	}

	protected void processDisplayFactLine(PageElement masterElement, PageElement element, int row) {
		// TODO handle repeated elements?
		ConditionClause le = element.getLogicElement();
		if (element.getId() != null) {
			// first line 
			String type = PageElementCondition.TYPE_INCLUSION;
			if (masterElement.isAPageElement()) {
				//System.out.println("Processing page displayFact: " + value);
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row, currentPage.getId(), currentPage.isBranchedPage(), currentPage.getDisplayAfter()));
			}
			else if (masterElement.isAnAlternateConsequenceItem()) {
				le.setExplanation(element.getPostLabel());
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId() + String.valueOf(row), row));
			}
			else {
				masterElement.setDisplayCondition(new PageElementCondition(type, masterElement.getId(), row));
			}
		}
		masterElement.getDisplayCondition().addElement(le);
		le.setProcessed(true);
	}


}
