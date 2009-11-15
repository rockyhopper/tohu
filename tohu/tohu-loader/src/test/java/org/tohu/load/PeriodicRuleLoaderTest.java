package org.tohu.load;

import static org.junit.Assert.*;

import org.junit.Test;

public class PeriodicRuleLoaderTest {
	@Test
	public void createPeriodicRuleLoader() {
		String ruleFile = "src/test/resources/SampleDecisionTreeSimple.xls";
		String outputDir = "target/temp-rules";
		String importDir = "src/test/resources/";
		String droolsDir = "target/simulated-drools-directory";
		PeriodicRuleLoader periodic = new PeriodicRuleLoader(ruleFile, outputDir, importDir, droolsDir);
		periodic.setPeriodSeconds(1);
		assertTrue("Conversion from spreadsheet to DRL files should succeed", periodic.loadRules());
	}
}
