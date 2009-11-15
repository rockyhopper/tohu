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
package org.tohu.load.spreadsheet;

import java.io.Serializable;
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
public class SpreadsheetRow implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int rowNumber;
	private boolean headerRow = false;
	private Map<Integer, Integer> headerRowColumns = null;
	
	private List<SpreadsheetItem> rowItems = new ArrayList<SpreadsheetItem>();
	
	public SpreadsheetRow(int row) {
		super();
		this.rowNumber = row;
	}
	
	public void addRowItem(SpreadsheetItem item) {
		if (headerRow) {
			headerRowColumns.put(new Integer(item.getColumn()), new Integer(rowItems.size()));
		}
		rowItems.add(item);
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public List<SpreadsheetItem> getRowItems() {
		return rowItems;
	}

	public boolean isHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(boolean headerRow) {
		if (this.headerRow && headerRow) {
			return;
		}
		this.headerRow = headerRow;
		if (!this.headerRow) {
			headerRowColumns = null;
		}
		else {
			headerRowColumns = new HashMap<Integer, Integer>(rowItems.size());
			int count = 0;
			for (Iterator<SpreadsheetItem> iterator = rowItems.iterator(); iterator.hasNext();) {
				SpreadsheetItem item = (SpreadsheetItem) iterator.next();
				headerRowColumns.put(new Integer(item.getColumn()), new Integer(count));
				count++;
			}
		}
	}
	
	public SpreadsheetItem getHeaderEntryForColumn(int column) {
		if (!headerRow) {
			throw new UnsupportedOperationException("Cannot access a header column when the record is not a header column");
		}
		Integer i = headerRowColumns.get(new Integer(column));
		if (i == null) {
			return null;
		}
		return rowItems.get(i.intValue());
	}
	
	public String getHeaderTextForColumnInUpperCase(int column) {
		SpreadsheetItem item = getHeaderEntryForColumn(column);
		return item.getSpreadsheetCell().toString().toUpperCase();
	}
	
	public int firstColumnWithAnEntry() {
		if (rowItems.size() == 0) {
			return -1;
		}
		return rowItems.get(0).getColumn();
	}
	
	public SpreadsheetItem itemForFirstHeaderColumn(SpreadsheetRow headerRow) {
		int firstColumn = headerRow.firstColumnWithAnEntry();
		for (Iterator<SpreadsheetItem> iterator = rowItems.iterator(); iterator.hasNext();) {
			SpreadsheetItem item = (SpreadsheetItem) iterator.next();
			if (item.getColumn() == firstColumn) {
				return item;
			}
		}
		return null;
	}
	
	

}
