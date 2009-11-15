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
package org.tohu.load.spreadsheet.sections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tohu.load.spreadsheet.SpreadsheetItem;
import org.tohu.load.spreadsheet.SpreadsheetRow;

/**
 * 
 * @author Derek Rendall
 *
 */
public class SpreadsheetSection implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sectionHeadingString = null;
	//private boolean headingStyleExpected = true;	// could be used to check to make sure black background?
	private List<SpreadsheetRow> sectionRows = new ArrayList<SpreadsheetRow>();
	private SpreadsheetRow headerRow = null;
	private Map<Integer, String> columnHeadingMap = new HashMap<Integer, String>();
	private Map<Integer, Integer> columnDepthMap = new HashMap<Integer, Integer>();
	private boolean processed = false;
	private String sheetName = null;
	
	public SpreadsheetSection(String sheetName, String sectionHeadingString, SpreadsheetRow headerRow) {
		super();
		if (headerRow == null) {
			throw new IllegalArgumentException("Null header row for " + sheetName + " " + sectionHeadingString);
		}
		this.sheetName = sheetName;
		this.sectionHeadingString = sectionHeadingString.toUpperCase();
		this.headerRow = headerRow;
		for (Iterator<SpreadsheetItem> heading = headerRow.getRowItems().iterator(); heading.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) heading.next();
			columnHeadingMap.put(new Integer(item.getColumn()), item.toString());
		}
		for (int i = 0; i < headerRow.getRowItems().size(); i++) {
			SpreadsheetItem item = headerRow.getRowItems().get(i);
			int count = 1;
			String tempStr = item.toString().toUpperCase();
			for (int j = 0; j < i; j++) {
				SpreadsheetItem previousItem = headerRow.getRowItems().get(j);
				if (previousItem.toString().toUpperCase().equals(tempStr)) {
					count++;
				}
			}
			columnDepthMap.put(new Integer(item.getColumn()), new Integer(count));
		}
	}

//	public boolean isHeadingStyleExpected() {
//		return headingStyleExpected;
//	}
//
//	public void setHeadingStyleExpected(boolean headingStyleExpected) {
//		this.headingStyleExpected = headingStyleExpected;
//	}

	public String getSectionHeadingString() {
		return sectionHeadingString;
	}

	public List<SpreadsheetRow> getSectionRows() {
		return sectionRows;
	}
	

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public String getSheetName() {
		return sheetName;
	}

	public SpreadsheetRow getHeaderRow() {
		return headerRow;
	}
	
	/**
	 * @param row
	 * @param sectionHeadingStrings
	 * @return
	 */
	public SpreadsheetSection processSectionRow(String sheetName, SpreadsheetRow row, List<String> sectionHeadingStrings) {
		if (row.getRowItems().size() == 0) {
			return null;
		}
		for (Iterator<String> header = sectionHeadingStrings.iterator(); header.hasNext();) {
			String string = (String) header.next();
			if (row.getRowItems().get(0).toString().toUpperCase().startsWith(string)) {
				SpreadsheetSection newSection = new SpreadsheetSection(sheetName, row.getRowItems().get(0).toString().toUpperCase(), row);
				row.setHeaderRow(true);
				return newSection;
			}
		}
		
		sectionRows.add(row);
		return null;
	}
	
	/**
	 * @param column
	 * @return
	 */
	public String getHeaderStringForColumn(int column) {
		return columnHeadingMap.get(new Integer(column));
	}

	/**
	 * @param column
	 * @return Depth of the column, for repeated columns. Starts at 1
	 */
	public Integer getHeaderDepthForColumn(int column) {
		return columnDepthMap.get(new Integer(column));
	}

	@Override
	public String toString() {
		return String.format("headingString: %s, sheetName: %s, row: %d", sectionHeadingString, sheetName, (headerRow == null) ? 0 : headerRow.getRowNumber() + 1);
	}
	
	

}
