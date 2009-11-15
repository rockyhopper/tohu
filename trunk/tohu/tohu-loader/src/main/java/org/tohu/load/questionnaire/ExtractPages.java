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

import java.util.Iterator;
import java.util.List;

import org.tohu.domain.questionnaire.Application;
import org.tohu.domain.questionnaire.Page;
import org.tohu.load.spreadsheet.SpreadsheetRow;
import org.tohu.load.spreadsheet.sections.SpreadsheetSection;


// TODO all the validations - removing spaces, checking types etc

/**
 * 
 * @author Derek Rendall
 *
 */
public class ExtractPages implements SpreadsheetSectionConstants {
	

	private Application application = null;
	private List<SpreadsheetSection> data;
	protected Page currentPage = null;
	protected String currentSheetName;
	
//	private Map<String, Page> pages = new HashMap<String, Page>();

	
	public ExtractPages(List<SpreadsheetSection> theData, Application theApplication) {
		super();
		data = theData;
		application = theApplication;
	}
	

	public boolean processPages() {
		for (Iterator<SpreadsheetSection> iterator = data.iterator(); iterator.hasNext();) {
			SpreadsheetSection section = (SpreadsheetSection) iterator.next();
			if (section.isProcessed()) {
				continue;
			}
			
//			currentPage = null;
			if (!processSectionData(section)) {
				System.out.println("Failed to process section " + section.getSectionHeadingString() + " for sheet " + section.getSheetName());
				return false;
			}
		}
		
		return true;
	}

	protected boolean processSectionData(SpreadsheetSection section) {
		List<SpreadsheetRow> rows = section.getSectionRows();
		currentSheetName = section.getSheetName();
		
		if (section.getSectionHeadingString().startsWith(PAGE_ITEMS_UPPER)) {
			currentPage = new ExtractItems(application, currentPage).processSectionData(section);
			if (currentPage == null) {
				System.out.println("Warning: no current page returned for section. " + section.toString());
				return true;
			}
			return true;
		}
		
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			if (spreadsheetRow.getRowItems().size() == 0) {
				continue;
			}
			if (section.getSectionHeadingString().startsWith(TohuSpreadsheetLoader.SHEET_END)) {
				// ignore the rest of the rows, although should never have got here
				System.out.println("Did not expect to be processing a Sheet End section - ignoring!");
				break;
			}
			
		}
		
		return true;
	}
		


}
