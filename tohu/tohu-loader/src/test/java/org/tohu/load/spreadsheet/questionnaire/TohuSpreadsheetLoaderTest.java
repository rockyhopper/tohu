package org.tohu.load.spreadsheet.questionnaire;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.tohu.load.questionnaire.TohuSpreadsheetLoader;

public class TohuSpreadsheetLoaderTest {

	@Test
	@Ignore
	public void testProcessSimpleFile() {
		TohuSpreadsheetLoader loader = new TohuSpreadsheetLoader();
		if (!loader.processFile("./src/test/resources/SampleDecisionTreeSimple.xls", "./src/test/resources/DecisionTreeSimpleOutput", "./src/test/resources")) {
			fail("File Not Processed");
		}
		
	}
	
	@Test
	@Ignore
	public void testProcessComplexFile() {
		TohuSpreadsheetLoader loader = new TohuSpreadsheetLoader();

		if (!loader.processFile("./src/test/resources/SampleDecisionTreeComplex.xls", "./src/test/resources/DecisionTreeComplexOutput", "./src/test/resources")) {
			fail("File Not Processed");
		}
		
	}

}
