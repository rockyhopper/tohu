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
import org.tohu.domain.questionnaire.LookupTable;
import org.tohu.domain.questionnaire.conditions.ConditionClause;
import org.tohu.load.spreadsheet.SpreadsheetItem;
import org.tohu.load.spreadsheet.SpreadsheetRow;
import org.tohu.load.spreadsheet.sections.SpreadsheetSection;


// TODO all the validations - removing spaces, checking types etc

/**
 * 
 * @author Derek Rendall
 *
 */
public class ExtractApplication implements SpreadsheetSectionConstants {
	
	private Application application = new Application();
	private SpreadsheetSection applicationSection;
	
	private List<SpreadsheetSection> tableSections = new ArrayList<SpreadsheetSection>();
	private LookupTable currentLookupTable = null; 
	
	public ExtractApplication(List<SpreadsheetSection> sections) {
		super();
		for (Iterator<SpreadsheetSection> iterator = sections.iterator(); iterator.hasNext();) {
			SpreadsheetSection spreadsheetSection = (SpreadsheetSection) iterator.next();
			if (spreadsheetSection.getSectionHeadingString().startsWith(APPLICATION_UPPER)) {
				applicationSection = spreadsheetSection;
			}
			else if (spreadsheetSection.getSectionHeadingString().startsWith(PAGE_LISTS_UPPER)) {
				tableSections.add(spreadsheetSection);
			}
		}
		if (applicationSection == null) {
			throw new IllegalArgumentException("There was no section heading with " + APPLICATION_UPPER + " found");
		}
	}

	public Application processApp() {
		List<SpreadsheetRow> rows = applicationSection.getSectionRows();
		if (rows.isEmpty()) {
			return null;
		}
		for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
			SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
			processApplicationHeadingLine(applicationSection.getHeaderRow(), spreadsheetRow);
		}
		applicationSection.setProcessed(true);
		
		for (Iterator<SpreadsheetSection> i = tableSections.iterator(); i.hasNext();) {
			SpreadsheetSection ts = (SpreadsheetSection) i.next();
			rows = ts.getSectionRows();
			if (rows.isEmpty()) {
				continue;
			}
			for (Iterator<SpreadsheetRow> rowIter = rows.iterator(); rowIter.hasNext();) {
				SpreadsheetRow spreadsheetRow = (SpreadsheetRow) rowIter.next();
				processListLine(ts.getHeaderRow(), spreadsheetRow);
			}
			ts.setProcessed(true);
		}
				
		return application;
	}
	
	protected void processApplicationHeadingLine(SpreadsheetRow headings, SpreadsheetRow row) {
		// TODO handle repeated elements?
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				continue;
			}
			String value = item.toString();
			if (key.startsWith(APPLICATION_UPPER)) {
				if (application.getId() != null) {
					throw new IllegalStateException("You cannot have two rows with an application id!");
				}
				application.setId(value);
			}
			else if (key.startsWith("BASE")) {
				application.setApplicationClass(value);
			}
			else if (key.equals("NAME")) {
				application.setApplicationName(value);
			}
			else if (key.equals("COMPLETION")) {
				application.setCompletionAction(value);
			}
			else if (key.startsWith("NOTE")) {
				application.setNote(value);
			}
			else if (key.startsWith("ACTIVE")) {
				application.setActivePage(value);
			}
			else if (key.startsWith("INCLUDE")) {
				application.addImport(value);
			}
			else {
				System.out.println("Unknown Application key: " + key);
			}
		}
	}
	
	
	protected void processListLine(SpreadsheetRow headings, SpreadsheetRow row) {
		// TODO handle repeated elements?
		String itemValue = null, displayedValue = null;
		String itemName = null, attributeName = null, operation = null, rhs = null;
		for (Iterator<SpreadsheetItem> iterator = row.getRowItems().iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			String key = headings.getHeaderTextForColumnInUpperCase(item.getColumn());
			if (key == null) {
				// Comment item - ignore
				continue;
			}
			String value = item.toString();
			
			if (key.startsWith(PAGE_LISTS_UPPER)) {
				currentLookupTable = new LookupTable(value);
				application.addLookupTable(currentLookupTable);
				continue;
			}
			if (key.startsWith("ACTUAL")) {
				itemValue = value;
				continue;
			}

			if (key.startsWith("DISPLAY")) {
				displayedValue = value;
				continue;
			}
			
			if (key.startsWith("DEPENDS")) {
				itemName = value;
				continue;
			}

			if (key.startsWith("ATTRIBUTE")) {
				attributeName = value;
				continue;
			}

			if (key.startsWith("OP")) {
				operation = value;
				continue;
			}
			
			if (key.startsWith("VALUE")) {
				rhs = value;
				continue;
			}
			
			System.out.println("Unknown List key: " + key);
		}
		ConditionClause cc = null;
		if (itemName != null) {
			if (itemValue == null) {
				// TODO handle multiple lines
				throw new IllegalArgumentException("You cannot (yet) have more than one logic element for a list entry");
			}
			cc = new ConditionClause(itemName, attributeName, operation, rhs);
		}
		if (displayedValue == null) {
			currentLookupTable.addEntry(itemValue, cc);
		}
		else {
			currentLookupTable.addEntry(itemValue, displayedValue, cc);
		}
	}

	
	


}
