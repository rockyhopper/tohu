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

import java.util.List;

import org.tohu.domain.questionnaire.Application;
import org.tohu.load.spreadsheet.sections.SpreadsheetSection;
import org.tohu.load.spreadsheet.sections.SpreadsheetSectionSplitter;
import org.tohu.load.spreadsheet.WorkbookData;
import org.tohu.write.questionnaire.ApplicationTemplate;

/**
 * 
 * @author Derek Rendall
 *
 */
public class TohuSpreadsheetLoader implements SpreadsheetSectionConstants {
	
	public static final String SHEET_END ="END";
		
	private WorkbookData wbData;
	private Application application = null;
	
	private String outputDirectory = null;
	private String importDirectory = null;

	private boolean seperatePageDirectories = true;
	
	public TohuSpreadsheetLoader() {
		super();
	}

	public boolean processFile(String filename, String outputDirectory, String importDirectory) {
		return processFile(filename, outputDirectory, importDirectory, true);
	}
	
	public boolean processFile(String filename, String outputDirectory, String importDirectory, boolean seperatePageDirectories) {
		wbData = new WorkbookData();
		this.outputDirectory = outputDirectory;
		this.importDirectory = importDirectory;
		this.seperatePageDirectories = seperatePageDirectories;
		
		if (!wbData.loadWorkbook(filename)) {
			System.out.println("Data not loaded from workbook");
			return false;
		}
		
		return processData(PAGE_SECTION_HEADINGS);
	}
	
	protected boolean processData(String[] sectionHeadingNames) {
		List<SpreadsheetSection> sections = new SpreadsheetSectionSplitter(sectionHeadingNames).splitIntoSections(wbData);
		
		application = new ExtractApplication(sections).processApp();
		if (application == null) {
			System.out.println("No Application Object Created");
			return false;
		}
		
		if (!new ExtractPages(sections, application).processPages()) {
			System.out.println("Page Extraction failed");
			return false;
		}
		
		application.processTableEntries();
		
		return createRuleFiles();
	}
	
	protected boolean createRuleFiles() {
		boolean processed = new ApplicationTemplate(application).generateDRLFile(outputDirectory, importDirectory, seperatePageDirectories);
		if (!processed) {
			System.out.println("Failed to create rule files");
		}
		return processed;
	}
	

}
