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
package org.tohu;

import static org.junit.Assert.*;

/**
 * Test Case for the basic JQuery Client.
 *
 * @author John Bebbington
 */
public class JQueryClientTest extends AbstractSeleniumTest {

	@org.junit.BeforeClass
	public static void oneTimeSetUp() throws Exception {
		DEBUG = true;
		debug("JQueryClientTest.oneTimeSetUp()");
		basicSetUp(
			BrowserType.FIREFOX,
			"http://localhost:9999/unittest-web",
			"isGUIBusy",
			"/html/body/div[@id='bodyContent']");
	}

	@org.junit.AfterClass
	public static void oneTimeTearDown() throws Exception {
		debug("JQueryClientTest.oneTimeTearDown()");
		basicTearDown();
	}

	@org.junit.Before
	public void setUp() throws Exception {
		debug("JQueryClientTest.setUp()");
	}

	@org.junit.After
	public void tearDown() throws Exception {
		debug("JQueryClientTest.tearDown()");
	}

	// Test all HTML elements of the initial page are present and correct.
	@org.junit.Test
	public void testInitialPage() throws Exception {
		debug("JQueryClientTest.testInitialPage()");
		try {
			openURL(new String[] { "ajaxCall=initial" });
			checkQuestionnaire(
				"questionnaire1",
				new String[] { "questionnaire1Style" },
				"Questionnaire 1",
				new String[] { "group1", "group2", "group3" });
			checkGroup(
				"/form/div[@id='questionnaire1']/div",
				"group1",
				new String[] { "group1Style" },
				"Group 1",
				new String[] { "item_1_1", "item_1_2", "group4", "item_1_3" });
			checkGroup(
				"/form/div[@id='questionnaire1']/div",
				"group2",
				new String[] { "group2Style" },
				"Group 2",
				new String[] { "item_2_1", "item_2_2", "item_2_3" });
			checkGroup(
				"/form/div[@id='questionnaire1']/div",
				"group3",
				new String[] { "group3Style" },
				"Group 3",
				new String[] { "item_3_1", "item_3_2", "item_3_3" });
			checkGroup(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				"group4",
				new String[] { "group4Style" },
				"Group 4",
				new String[] { "item_4_1", "item_4_3" });
			checkNote(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.IMAGE,
				"item_1_1",
				new String[] { "image", "item_1_1Style1", "item_1_1Style2" },
				"images/earth.jpg");
			checkNote(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.PARAGRAPH,
				"item_1_2",
				new String[] { "item_1_2Style" },
				"Item 1.2");
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.FIELD,
				"item_1_3",
				new String[] { "datepicker", "item_1_3Style" },
				"Item 1.3",
				"Date Field with Picker",
				true,
				AnswerType.DATE,
				"16/06/2009",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.FIELD,
				"item_2_1",
				new String[] { "item_2_1Style" },
				"Item 2.1",
				"Simple Text Field",
				true,
				AnswerType.TEXT,
				"Item 2.1 Text",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.TEXTAREA,
				"item_2_2",
				new String[] { "textarea", "item_2_2Style" },
				"Item 2.2",
				"Text Area",
				false,
				AnswerType.TEXT,
				"Item 2.2 Text",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.RADIO,
				"item_2_3",
				new String[] { "item_2_3Style", "radio" },
				"Item 2.3",
				"Radio Buttons",
				true,
				AnswerType.TEXT,
				"1",
				new String[][] {
						new String[] { "0", "Item 2.3.0" },
						new String[] { "1", "Item 2.3.1" },
						new String[] { "2", "Item 2.3.2" } },
				new String[][] { new String[] { "ErrorType1", "Item 2.3 Error" } });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.SELECT,
				"item_3_1",
				new String[] { "item_3_1Style" },
				"Item 3.1",
				"Decimal Dropdown List",
				false,
				AnswerType.DECIMAL,
				"0.3",
				new String[][] {
						new String[] { "0.1", "Item 3.1.0.1" },
						new String[] { "0.2", "Item 3.1.0.2" },
						new String[] { "0.3", "Item 3.1.0.3" }, },
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.SELECT,
				"item_3_2",
				new String[] { "item_3_2Style" },
				"Item 3.2",
				"Text Dropdown List",
				false,
				AnswerType.TEXT,
				"",
				new String[][] {
						new String[] { "", "Please specify..." },
						new String[] { "first", "Item 3.2.1" },
						new String[] { "second", "Item 3.2.2" },
						new String[] { "third", "Item 3.2.3" } },
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.FIELD,
				"item_3_3",
				new String[] { "item_3_3Style" },
				"Item 3.3",
				"Date Field without Picker",
				true,
				AnswerType.DATE,
				"",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.CHECKBOX,
				"item_4_1",
				new String[] { "item_4_1Style" },
				"Item 4.1",
				"Boolean Checkbox",
				true,
				AnswerType.BOOLEAN,
				"false",
				null,
				new String[][] {});
			assertFalse("placeholder displayed", isItemPresent("item_4_2"));
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.RADIO,
				"item_4_3",
				new String[] { "item_4_3Style" },
				"Item 4.3",
				"Boolean Radio Buttons",
				true,
				AnswerType.BOOLEAN,
				"true",
				null,
				new String[][] {
						new String[] { "ErrorType1", "Item 4.3 Error 1" },
						new String[] { "ErrorType2", "Item 4.3 Error 2" } });
			checkControl(
				"/form/div[@id='questionnaire1_controls']",
				WidgetType.LINK,
				"questionnaire1_action_0",
				new String[] { "previous" },
				"Previous");
			checkControl(
				"/form/div[@id='questionnaire1_controls']",
				WidgetType.LINK,
				"questionnaire1_action_1",
				new String[] { "next" },
				"Next");
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testInitialPage()", ex);
		}
	}

	// Start with initial page and drive it around.
	@org.junit.Test
	public void testSetQuestionAnswer() throws Exception {
		debug("JQueryClientTest.testSetQuestionAnswer()");
		try {
			openURL(new String[] { "ajaxCall=initial" });
			selectDropDownAnswer("item_3_1", "0.2");
			assertFalse("item_2_1 not deleted", isItemPresent("item_2_1"));
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.RADIO,
				"item_4_3",
				new String[] { "item_4_3Style" },
				"Item 4.3",
				"Boolean Radio Buttons",
				true,
				AnswerType.BOOLEAN,
				"false",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.SELECT,
				"item_3_1",
				new String[] { "item_3_1Style" },
				"Item 3.1",
				"Decimal Dropdown List",
				false,
				AnswerType.DECIMAL,
				"0.2",
				new String[][] {
						new String[] { "0.1", "Item 3.1.0.1" },
						new String[] { "0.2", "Item 3.1.0.2" },
						new String[] { "0.3", "Item 3.1.0.3" }, },
				new String[][] {
						new String[] { "ErrorType1", "Item 3.1 Error 1" } });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.FIELD,
				"item_3_1_1",
				new String[] { "item_3_1_1Style" },
				"Item 3.1.1",
				"Another Simple Text Field",
				false,
				AnswerType.TEXT,
				"Item 3.1.1 Text",
				null,
				new String[][] {});
			checkGroup(
				"/form/div[@id='questionnaire1']/div",
				"group3",
				new String[] { "group3Style" },
				"Group 3",
				new String[] { "item_3_1", "item_3_1_1", "item_3_2", "item_3_3" });
			
			//setTextAnswer("passport", "A1234");
			// Check if field value has changed.
			//assertEquals("Passport Number text has not changed", "B4321", getTextAnswer("passport"));
			//assertEquals("Passport Number text has changed", "A1234", getTextAnswer("passport"));

			//setTextAnswer("dob", "01/01/99");

			//clickCheckboxAnswer("stayingOnEarth");
			// Check the "planetNext" question has been removed.
			//assertFalse("planetNext question not removed", isItemPresent("planetNext"));

			//clickRadioButtonAnswer("liveOnEarth", "true");
			// Check the "tripDurationUnits" question has been added.
			//assertTrue("tripDurationUnits question not added", isItemPresent("tripDurationUnits"));

			//selectDropDownAnswer("tripDurationUnits", "M");

			// Add variety of create, update and/or delete combinations covering all fact types
			// and all question/note/style/data types.
		}
		catch (Exception ex) {
			//Thread.sleep(60000);
			handleException("JQueryClientTest.testSetQuestionAnswer()", ex);
		}
	}

	@org.junit.Test
	public void testJavaScriptHooks() throws Exception {
		debug("JQueryClientTest.testJavaScriptHooks()");
		try {
			// Test that all of the javascript hooks work properly.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testJavaScriptHooks()", ex);
		}
	}

	@org.junit.Test
	public void testDateField() throws Exception {
		debug("JQueryClientTest.testDateField()");
		try {
			// Test display and entry of dates.
			// Test datepicker.
			// Test invalid date entry.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testDateField()", ex);
		}
	}

	@org.junit.Test
	public void testChangedQuestionnaire() throws Exception {
		debug("JQueryClientTest.testChangedQuestionnaire()");
		// Test changes to questionnaire.
		try {
			// Test changes to actions.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testChangedQuestionnaire()", ex);
		}
	}

	@org.junit.Test
	public void testNewQuestionnaire() throws Exception {
		debug("JQueryClientTest.testNewQuestionnaire()");
		// Test a entirely new questionnaire.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testNewQuestionnaire()", ex);
		}
	}

	@org.junit.Test
	public void testSetActiveItem() throws Exception {
		debug("JQueryClientTest.testSetActiveItem()");
		// Test page change.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testSetActiveItem()", ex);
		}
	}

	@org.junit.Test
	public void testActions() throws Exception {
		debug("JQueryClientTest.testActions()");
		try {
			// Check correct buttons are generated.
			// Override default actions with onGetQuestionnaireActions function.
			// Test the various types of action.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testActions()", ex);
		}
	}

	@org.junit.Test
	public void testReadOnly() throws Exception {
		debug("JQueryClientTest.testReadOnly()");
		try {
			// Test readonly can be set at various levels and question types.
			// Test cannot answer readonly question.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testReadOnly()", ex);
		}
	}

	@org.junit.Test
	public void testTabOrder() throws Exception {
		debug("JQueryClientTest.testTabOrder()");
		try {
			// Test the correct field receives focus when tabbing.
			// Both directions.
			// Add & remove fields.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testTabOrder()", ex);
		}
	}

	@org.junit.Test
	public void testFileUpload() throws Exception {
		debug("JQueryClientTest.testFileUpload()");
		try {
			// Test can upload a file.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testFileUpload()", ex);
		}
	}

	@org.junit.Test
	public void testInvalidFact() throws Exception {
		debug("JQueryClientTest.testInvalidFact()");
		// Return invalid XML fact from AJAX call.
		try {
			// Invalid Type.
			// No ID.
			// Invalid question datatype.
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testInvalidFact()", ex);
		}
	}

	@org.junit.Test
	public void testInvalidXML() throws Exception {
		debug("JQueryClientTest.testInvalidXML()");
		// Return invalid XML from AJAX call.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testInvalidXML()", ex);
		}
	}

	@org.junit.Test
	public void testAjaxError() throws Exception {
		debug("JQueryClientTest.testAjaxError()");
		// Return error code from AJAX call.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testAjaxError()", ex);
		}
	}

	@org.junit.Test
	public void testDuplicateError() throws Exception {
		debug("JQueryClientTest.testDuplicateError()");
		// Return duplicate of an already displayed fact.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testDuplicateError()", ex);
		}
	}

	@org.junit.Test
	public void testNoParentError() throws Exception {
		debug("JQueryClientTest.testNoParentError()");
		// Return an orphan fact.
		try {}
		catch (Exception ex) {
			handleException("JQueryClientTest.testNoParentError()", ex);
		}
	}
}
