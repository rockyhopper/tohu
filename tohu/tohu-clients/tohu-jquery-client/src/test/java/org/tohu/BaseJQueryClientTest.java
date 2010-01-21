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

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Base test case for the JQuery Client in all browsers.
 *
 * @author John Bebbington
 */
public abstract class BaseJQueryClientTest extends AbstractSeleniumTest {

	@org.junit.AfterClass
	public static void oneTimeTearDown() throws Exception {
		debug("JQueryClientTest.oneTimeTearDown()");
		basicTearDown();
	}

	@org.junit.Before
	public void setUp() throws Exception {
		//debug("JQueryClientTest.setUp()");
	}

	@org.junit.After
	public void tearDown() throws Exception {
		//debug("JQueryClientTest.tearDown()");
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
				new String[] { "item_4_1", "item_4_3", "item_4_4", "item_4_5" });
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
				false,
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
				false,
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
				false,
				AnswerType.NUMBER,
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
				false,
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
				false,
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
				false,
				AnswerType.BOOLEAN,
				"true",
				null,
				new String[][] {
						new String[] { "ErrorType1", "Item 4.3 Error 1" },
						new String[] { "ErrorType2", "Item 4.3 Error 2" } });
			checkQuestion(
					"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
					WidgetType.SELECT,
					"item_4_4",
					new String[] { "item_4_4Style" },
					"Item 4.4",
					"Multiple answer select",
					false,
					false,
					AnswerType.LIST,
					null,
					new String[][] {
							new String[] { "foo", "foo" },
							new String[] { "bar", "bar" },
							new String[] { "baz", "baz" },
							new String[] { "qux", "qux" },
							new String[] { "quux", "quux" }},
					new String[][] {});
			checkQuestion(
					"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
					WidgetType.FIELD,
					"item_4_5",
					new String[] { "item_4_5Style" },
					"Item 4.5",
					"Text Field with special characters",
					false,
					false,
					AnswerType.TEXT,
					"Text <>&'\"!@#$%^*()_-+={}[]:;?/,.`\\<>&'\"!@#$%^*()_-+={}[]:;?/,.`\\",
					null,
					new String[][] {});
			checkQuestion(
					"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
					WidgetType.SELECT,
					"item_4_6",
					new String[] { "item_4_6Style" },
					"Item 4.6",
					"Text Dropdown List with no null entry",
					false,
					false,
					AnswerType.TEXT,
					"",
					new String[][] {
							new String[] {"", "Please select..."},
							new String[] {"first", "Item 4.6.1"},
							new String[] {"second", "Item 4.6.2"},
							new String[] {"third", "Item 4.6.3"}},
					new String[][] {});
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
	public void testDynamicPage() throws Exception {
		debug("JQueryClientTest.testDynamicPage()");
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
				false,
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
				false,
				AnswerType.DECIMAL,
				"0.2",
				new String[][] {
						new String[] { "0.1", "Item 3.1.0.1" },
						new String[] { "0.2", "Item 3.1.0.2" },
						new String[] { "0.3", "Item 3.1.0.3" }, },
				new String[][] { new String[] { "ErrorType1", "Item 3.1 Error 1" } });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.FIELD,
				"item_3_1_1",
				new String[] { "item_3_1_1Style" },
				"Item 3.1.1",
				"Another Simple Text Field",
				false,
				false,
				AnswerType.TEXT,
				"Item 3.1.1 Text",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.RADIO,
				"item_4_3",
				new String[] { "item_4_3Style" },
				"Item 4.3",
				"Boolean Radio Buttons",
				true,
				false,
				AnswerType.BOOLEAN,
				"false",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group3']/div",
				WidgetType.SELECT,
				"item_3_2",
				new String[] { "item_3_2Style1", "item_3_2Style2" },
				"Item 3.2a",
				"Text Dropdown List",
				true,
				false,
				AnswerType.TEXT,
				"second",
				new String[][] {
						new String[] { "first", "Item 3.2.1" },
						new String[] { "second", "Item 3.2.2" },
						new String[] { "third", "Item 3.2.3" } },
				new String[][] {});
			checkGroup(
				"/form/div[@id='questionnaire1']/div",
				"group3",
				new String[] { "group3Style" },
				"Group 3",
				new String[] { "item_3_1", "item_3_1_1", "item_3_2", "item_3_3" });

			clickCheckboxAnswer("item_4_1");
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.CHECKBOX,
				"item_4_1",
				new String[] { "item_4_1Style" },
				"Item 4.1a",
				"A Boolean Checkbox",
				false,
				false,
				AnswerType.BOOLEAN,
				"true",
				null,
				new String[][] {});

			clickCheckboxAnswer("item_4_1");
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div/div[@id='group4']/div",
				WidgetType.CHECKBOX,
				"item_4_1",
				new String[] { "item_4_1Style" },
				"Item 4.1",
				"Boolean Checkbox",
				true,
				false,
				AnswerType.BOOLEAN,
				"false",
				null,
				new String[][] {});			
			setTextAnswer("item_2_2", "Test Text");
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.TEXTAREA,
				"item_2_2",
				new String[] { "textarea", "item_2_2Style" },
				"Item 2.2",
				"Text Area",
				false,
				false,
				AnswerType.TEXT,
				"More Test Text",
				null,
				new String[][] {
						new String[] { "ErrorType1", "Item 2.2 Error 1" },
						new String[] { "ErrorType2", "Item 2.2 Error 2" },
						new String[] { "ErrorType3", "Item 2.2 Error 3" } });

			setTextAnswer("item_2_1", "Test Text");
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.FIELD,
				"item_2_1",
				new String[] { "item_2_1Style" },
				"Item 2.1a",
				"A Simple Text Field",
				true,
				false,
				AnswerType.TEXT,
				"Still More Test Text",
				null,
				new String[][] {});

			clickRadioButtonAnswer("item_2_3", "2");
			checkQuestionnaire(
				"questionnaire1",
				new String[] { "questionnaire1Style1", "questionnaire1Style2" },
				"Questionnaire 1a",
				new String[] { "group1", "group2", "group3" });
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
			assertFalse("item_2_1 not deleted", isItemPresent("item_2_1"));
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div/div[@id='group5']/div",
				WidgetType.RADIO,
				"item_5_2",
				new String[] { "item_5_2Style1", "item_5_2Style2", "radio" },
				"Item 5.2",
				"More Radio Buttons",
				false,
				false,
				AnswerType.NUMBER,
				"",
				new String[][] {
						new String[] { "0", "Item 5.2.0" },
						new String[] { "1", "Item 5.2.1" },
						new String[] { "2", "Item 5.2.2" } },
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div/div[@id='group5']/div",
				WidgetType.FIELD,
				"item_5_1",
				new String[] { "item_5_1Style" },
				"Item 5.1",
				"Simple Text Field",
				true,
				false,
				AnswerType.TEXT,
				"",
				null,
				new String[][] {});
			checkGroup(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				"group5",
				new String[] { "group5Style1", "group5Style2" },
				"Group 5",
				new String[] { "item_5_1", "item_5_2" });
			checkGroup("/form/div[@id='questionnaire1']/div", "group2", new String[] {
					"group2Style1",
					"group2Style2",
					"group2Style3" }, "Group 2a", new String[] { "group5", "item_2_2", "item_2_3" });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group2']/div",
				WidgetType.SELECT,
				"item_2_3",
				new String[] { "item_2_3Style" },
				"Item 2.3",
				"Radio Buttons?",
				true,
				false,
				AnswerType.NUMBER,
				"",
				new String[][] {
						new String[] { "0", "Item 2.3.0" },
						new String[] { "1", "Item 2.3.1" },
						new String[] { "2", "Item 2.3.2" } },
				new String[][] { new String[] { "ErrorType1", "Item 2.3 Error" } });

			mouseDownControl("questionnaire1_action_1");
			checkQuestionnaire(
				"questionnaire2",
				new String[] { "questionnaire2Style" },
				"Questionnaire 2",
				new String[] { "group6" });

			mouseDownControl("questionnaire2_action_1");
			checkAlert("You must fix all errors first.");

			setTextAnswer("item_6_1", "Test Text");
			mouseDownControl("questionnaire2_action_1");
			checkQuestionnaire(
				"questionnaire3",
				null,
				"All Done",
				new String[] {});

			// Check all the JavaScript hooks have been called.
			assertEquals(
				"preQuestionnaireLoad hook not called",
				"true",
				selenium.getEval("window.calledPreQuestionnaireLoad"));
			assertEquals(
				"preRefreshScreen hook not called",
				"true",
				selenium.getEval("window.calledPreRefreshScreen"));
			assertEquals(
				"postRefreshScreen hook not called",
				"true",
				selenium.getEval("window.calledPostRefreshScreen"));
			assertEquals(
				"preProcessDelete hook not called",
				"true",
				selenium.getEval("window.calledPreProcessDelete"));
			assertEquals(
				"postProcessDelete hook not called",
				"true",
				selenium.getEval("window.calledPostProcessDelete"));
			assertEquals(
				"preProcessCreate hook not called",
				"true",
				selenium.getEval("window.calledPreProcessCreate"));
			assertEquals(
				"postProcessCreate hook not called",
				"true",
				selenium.getEval("window.calledPostProcessCreate"));
			assertEquals(
				"preProcessUpdate hook not called",
				"true",
				selenium.getEval("window.calledPreProcessUpdate"));
			assertEquals(
				"postProcessUpdate hook not called",
				"true",
				selenium.getEval("window.calledPostProcessUpdate"));
			assertEquals(
				"preChangeEvent hook not called",
				"true",
				selenium.getEval("window.calledPreChangeEvent"));
			assertEquals(
				"postChangeEvent hook not called",
				"true",
				selenium.getEval("window.calledPostChangeEvent"));
			assertEquals(
				"onChangeEvent hook not called",
				"true",
				selenium.getEval("window.calledOnChangeEvent"));
			assertEquals(
				"preActionEvent hook not called",
				"true",
				selenium.getEval("window.calledPreActionEvent"));
			assertEquals(
				"postActionEvent hook not called",
				"true",
				selenium.getEval("window.calledPostActionEvent"));
			assertEquals(
				"onActionEvent hook not called",
				"true",
				selenium.getEval("window.calledOnActionEvent"));
			assertEquals(
				"onGetQuestionnaireActions hook not called",
				"true",
				selenium.getEval("window.calledOnGetQuestionnaireActions"));
			assertEquals(
				"onGetObjectClass hook not called",
				"true",
				selenium.getEval("window.calledOnGetObjectClass"));
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testDynamicPage()", ex);
		}
	}

	// Test date field.
	@org.junit.Test
	public void testDateField() throws Exception {
		debug("JQueryClientTest.testDateField()");
		try {
			openURL(new String[] { "ajaxCall=initial" });
			setTextAnswer("item_1_3", "11111111");
			checkAlert("11111111 is not a valid date, please re-enter.");
			
			selenium.click("//div[@id='item_1_3']/img[@class='ui-datepicker-trigger']");
			assertTrue("item_1_3 date picker not visible", selenium.isVisible("//div[@id='ui-datepicker-div']/div"));
			
			setTextAnswer("item_1_3", "01/01/1990");
			assertEquals("item_1_3 wrong value", "02/01/1990", getTextAnswer("item_1_3"));
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testDateField()", ex);
		}
	}

	// Test readonly questions.
	@org.junit.Test
	public void testReadOnly() throws Exception {
		debug("JQueryClientTest.testReadOnly()");
		try {
			openURL(new String[] { "ajaxCall=readonly" });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.FIELD,
				"item_1_1",
				new String[] { "item_1_1Style", "readonly" },
				"Item 1.1",
				"Simple Text Field",
				true,
				true,
				AnswerType.TEXT,
				"Item 1.1 Text",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.CHECKBOX,
				"item_1_2",
				new String[] { "item_1_2Style", "readonly-inherited" },
				"Item 1.2",
				"Boolean Checkbox",
				true,
				true,
				AnswerType.BOOLEAN,
				"true",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.FIELD,
				"item_1_3",
				new String[] { "item_1_3Style", "readonly", "datepicker" },
				"Item 1.3",
				"Date Field with Picker",
				true,
				true,
				AnswerType.DATE,
				"16/06/2009",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.TEXTAREA,
				"item_1_4",
				new String[] { "textarea", "item_1_4Style", "readonly" },
				"Item 1.4",
				"Text Area",
				false,
				true,
				AnswerType.TEXT,
				"Item 1.4 Text",
				null,
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.RADIO,
				"item_1_5",
				new String[] { "readonly", "item_1_5Style", "radio" },
				"Item 1.5",
				"Radio Buttons",
				true,
				true,
				AnswerType.NUMBER,
				"1",
				new String[][] {
						new String[] { "0", "Item 1.5.0" },
						new String[] { "1", "Item 1.5.1" },
						new String[] { "2", "Item 1.5.2" } },
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.SELECT,
				"item_1_6",
				new String[] { "item_1_6Style", "readonly-inherited" },
				"Item 1.6",
				"Decimal Dropdown List",
				false,
				true,
				AnswerType.DECIMAL,
				"0.3",
				new String[][] {
						new String[] { "0.1", "Item 1.6.0.1" },
						new String[] { "0.2", "Item 1.6.0.2" },
						new String[] { "0.3", "Item 1.6.0.3" }, },
				new String[][] {});
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.RADIO,
				"item_1_7",
				new String[] { "item_1_7Style", "readonly" },
				"Item 1.7",
				"Boolean Radio",
				true,
				true,
				AnswerType.BOOLEAN,
				"true",
				null,
				new String[][] {});
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testReadOnly()", ex);
		}
	}

	// Test can upload a file.
	@org.junit.Test
	public void testFileUpload() throws Exception {
		debug("JQueryClientTest.testFileUpload()");
		try {
			openURL(new String[] { "ajaxCall=fileupload" });
			checkQuestion(
				"/form/div[@id='questionnaire1']/div/div[@id='group1']/div",
				WidgetType.FILE,
				"item_1_1",
				new String[] { "item_1_1Style", "file" },
				"Item 1.1",
				"File Upload",
				false,
				false,
				AnswerType.TEXT,
				"",
				null,
				new String[][] {});
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testFileUpload()", ex);
		}
	}

	// Test handling of errors.
	@org.junit.Test
	public void testErrorHandling() throws Exception {
		debug("JQueryClientTest.testErrorHandling()");
		try {
			openURL(new String[] { "ajaxCall=errors" });
			setTextAnswer("error_type", "No ID");
			checkAlert("Fact undefined in the rule base is invalid: no id, revise the rule base.");
			setTextAnswer("error_type", "Garbage");
			checkAlert("The rules server has returned invalid XML, contact support.");
			setTextAnswer("error_type", "HTTP Error");
			checkAlert("An error has occured calling the rules server, refresh the page and try again.");
			setTextAnswer("error_type", "Invalid Fact Type");
			checkAlert("An internal error has occured: unknown tagName: org.tohu.Whatsit, contact support.");
			setTextAnswer("error_type", "Invalid Answer Type");
			checkAlert("Fact item_1_1 in the rule base is invalid: unknown answerType: movie, revise the rule base.");
			setTextAnswer("error_type", "Duplicate");
			checkAlert("A duplicate of the error_type fact has been sent by the rules server, revise the rule base.");
			setTextAnswer("error_type", "No Parent");
			checkAlert("The item_1_1 fact does not have a parent, revise the rule base.");
		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testErrorHandling()", ex);
		}
	}
	
	/**
	 * Basic test to ensure validation kicks in straight away
	 * on page load for the first page
	 * @throws Exception
	 */
	@Test
	public void testInitialPageWithErrors() throws Exception {
		debug("JQueryClientTest.testInitialPageWithErrors()");
		try {
			openURL(new String[] { "ajaxCall=initialErrorPage" });		
			// attempt to navigate to next page
			mouseDownControl("questionnaire1_action_1");
			// check an error alert was generated
			checkAlert("Not all mandatory questions have being answered");
			// check we are still on the same page of the app
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[] { "group1","group2","group3" });						
		}
		catch (Exception ex) {
			handleException("BaseJQueryClientTest.testInitialPageWithErrors()", ex);
		}		
	}
	
	/**
	 * test to ensure that any errors generated by the rules engine are picked up by the
	 * app. and prevent forward navigation
	 *  
	 * @throws Exception
	 */
	@Test
	public void testDynamicallyGeneratedErrors() throws Exception {
		debug("JQueryClientTest.testDynamicallyGeneratedErrors()");
		try {
			openURL(new String[] { "ajaxCall=initialCleanPage" });
			// check we are on quesionaire 1
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[] { "group1"});
			// navigate forward, as there are no errors
			mouseDownControl("questionnaire1_action_1");			
			// check we are now on page for quesionaire 2
			checkQuestionnaire(
					"questionnaire2",
					new String[] { "questionnaire2Style" },
					"Questionnaire 2",
					new String[] { "group6" });
			// navigate back to previous page. The generated xml payload will include an error			
			mouseDownControl("questionnaire2_action_0");
			// check we are back to quesionaire 1
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[] { "group1"});			
			// try to navigate forward again
			mouseDownControl("questionnaire1_action_1");
			// check an error alert was generated
			checkAlert("Not all mandatory questions have being answered");			
			// check we are still on the same questionaire
			// because of an existing error
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[] { "group1" });			

		}
		catch (Exception ex) {
			handleException("BaseJQueryClientTest.testDynamicallyGeneratedErrors()", ex);
		}		
	}
}
