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
package org.tohu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.tohu.test.element.Element;
import org.tohu.test.element.data.DynamicPageData;
import org.tohu.test.element.data.FileUploadData;
import org.tohu.test.element.data.InitialPageData;
import org.tohu.test.element.data.ReadOnlyPageData;

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

	// Test all HTML elements of the initial page are present and correct.
	@org.junit.Test
	public void testInitialPage() throws Exception {
		debug("JQueryClientTest.testInitialPage()");
		Element element;
		try {
			openURL(new String[] { "ajaxCall=initial" });
			checkQuestionnaire(
				"questionnaire1",
				new String[] { "questionnaire1Style" },
				"Questionnaire 1",
				new String[][] { new String[] {"group1", "Group 1"}, 
				                 new String[] {"group2", "Group 2"}, 
				                 new String[] {"group3", "Group 3"} });
			checkGroup(
				"group1",
				new String[] { "group1Style" },
				"Group 1",
				new String[] { "item_1_1", "item_1_2", "group4", "item_1_3" });
			checkGroup(
				"group2",
				new String[] { "group2Style" },
				"Group 2",
				new String[] { "item_2_1", "item_2_2", "item_2_3" });
			checkGroup(
				"group3",
				new String[] { "group3Style" },
				"Group 3",
				new String[] { "item_3_1", "item_3_2", "item_3_3" });
			checkGroup(
				"group4",
				new String[] { "group4Style" },
				"Group 4",
				new String[] { "item_4_1", "item_4_3", "item_4_4", "item_4_5" });
			checkNote(
				WidgetType.IMAGE,
				"item_1_1",
				new String[] { "image", "item_1_1Style1", "item_1_1Style2" },
				"images/earth.jpg");
			checkNote(
				WidgetType.PARAGRAPH,
				"item_1_2",
				new String[] { "item_1_2Style" },
				"Item 1.2");
			
			element = elementMap.get(WidgetType.TEXT);
			InitialPageData.populateTestDataForInitialPageQuestion(element, "item_1_3");
			element.checkQuestion();
			
			element = elementMap.get(WidgetType.TEXT);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_2_1");
            element.checkQuestion();			
			
			element = elementMap.get(WidgetType.TEXTAREA);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_2_2");
            element.checkQuestion();
            
			element = elementMap.get(WidgetType.RADIO_GROUP);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_2_3");
            element.checkQuestion();
            			
			element = elementMap.get(WidgetType.DROPDOWN);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_3_1");
            element.checkQuestion();
            			
			element = elementMap.get(WidgetType.DROPDOWN);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_3_2");
            element.checkQuestion();
            			
			element = elementMap.get(WidgetType.TEXT);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_3_3");
            element.checkQuestion();
            		
			element = elementMap.get(WidgetType.CHECKBOX);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_4_1");
            element.checkQuestion();
            
			assertFalse("placeholder displayed", isItemPresent("item_4_2"));
			
			element = elementMap.get(WidgetType.RADIO_BOOLEAN);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_4_3");
            element.checkQuestion();
            		
			element = elementMap.get(WidgetType.DROPDOWN);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_4_4");
            element.checkQuestion();
            
			element = elementMap.get(WidgetType.TEXT);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_4_5");
            element.checkQuestion();
            			
			element = elementMap.get(WidgetType.DROPDOWN);
            InitialPageData.populateTestDataForInitialPageQuestion(element, "item_4_6");
            element.checkQuestion();
            
			checkControl(
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
	        Element element;
			selectDropDownAnswer("item_3_1", "0.2");
			assertFalse("item_2_1 not deleted", isItemPresent("item_2_1"));
			
			element = elementMap.get(WidgetType.RADIO_BOOLEAN);
	        DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_4_3", false);
	        element.checkQuestion();
	        
			element = elementMap.get(WidgetType.DROPDOWN);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_3_1", false);
            element.checkQuestion();          

			element = elementMap.get(WidgetType.TEXT);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_3_1_1", false);
            element.checkQuestion();
            
			element = elementMap.get(WidgetType.DROPDOWN);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_3_2", false);
            element.checkQuestion();
            
			checkGroup(
				"group3",
				new String[] { "group3Style" },
				"Group 3",
				new String[] { "item_3_1", "item_3_1_1", "item_3_2", "item_3_3" });
							
			clickCheckboxAnswer("item_4_1");
			
			element = elementMap.get(WidgetType.CHECKBOX);
	        DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_4_1", true);
	        element.checkQuestion();
	            
			clickCheckboxAnswer("item_4_1");
			
			element = elementMap.get(WidgetType.CHECKBOX);
			DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_4_1", false);
			element.checkQuestion();		
							
			setTextAnswer("item_2_2", "Test Text");
            
			element = elementMap.get(WidgetType.TEXTAREA);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_2_2", false);
            element.checkQuestion();          

			setTextAnswer("item_2_1", "Test Text");
			
			element = elementMap.get(WidgetType.TEXT);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_2_1", false);
            element.checkQuestion();
            
			clickRadioButtonAnswer("item_2_3", "2");
			
			checkQuestionnaire(
				"questionnaire1",
				new String[] { "questionnaire1Style1", "questionnaire1Style2" },
				"Questionnaire 1a",
				new String[][] { 
				                new String[] {"group1", "Group 1"}, 
				                new String[] {"group2", "Group 2a"},
				                new String[] {"group3", "Group 3"} });
			checkControl(
				WidgetType.LINK,
				"questionnaire1_action_0",
				new String[] { "previous" },
				"Previous");
			
			checkControl(
				WidgetType.LINK,
				"questionnaire1_action_1",
				new String[] { "next" },
				"Next");
			
			assertFalse("item_2_1 not deleted", isItemPresent("item_2_1"));
			
			element = elementMap.get(WidgetType.RADIO_GROUP);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_5_2", false);
            element.checkQuestion();
            
			element = elementMap.get(WidgetType.TEXT);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_5_1", false);
            element.checkQuestion();
            		
			checkGroup(
				"group5",
				new String[] { "group5Style1", "group5Style2" },
				"Group 5",
				new String[] { "item_5_1", "item_5_2" });
			
			checkGroup("group2", 
			        new String[] {
					"group2Style1",
					"group2Style2",
					"group2Style3" }, "Group 2a", new String[] { "group5", "item_2_2", "item_2_3" });
			
			element = elementMap.get(WidgetType.DROPDOWN);
            DynamicPageData.populateTestDataForDynamicPageQuestion(element, "item_2_3", false);
            element.checkQuestion();
            
			mouseDownControl("questionnaire1_action_1");
			checkQuestionnaire(
				"questionnaire2",
				new String[] { "questionnaire2Style" },
				"Questionnaire 2",
				new String[][] { new String[] {"group6", "Group 6"} });

			mouseDownControl("questionnaire2_action_1");
			checkAlert("You must fix all errors first.");

			setTextAnswer("item_6_1", "Test Text");
			mouseDownControl("questionnaire2_action_1");
			checkQuestionnaire(
				"questionnaire3",
				null,
				"All Done",
				new String[][] {});
			
			assertHooks();			
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
	public void testReadOnlyPage() throws Exception {
		debug("JQueryClientTest.testReadOnly()");
		Element element;
		try {
			openURL(new String[] { "ajaxCall=readonly" });
			element = elementMap.get(WidgetType.TEXT);
			ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_1");
			element.checkQuestion();

            element = elementMap.get(WidgetType.CHECKBOX);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_2");
            element.checkQuestion();			

            element = elementMap.get(WidgetType.TEXT);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_3");
            element.checkQuestion();			

            element = elementMap.get(WidgetType.TEXTAREA);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_4");
            element.checkQuestion();			

            element = elementMap.get(WidgetType.RADIO_GROUP);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_5");
            element.checkQuestion();			

            element = elementMap.get(WidgetType.DROPDOWN);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_6");
            element.checkQuestion();			

            element = elementMap.get(WidgetType.RADIO_BOOLEAN);
            ReadOnlyPageData.populateTestDataForReadOnlyPageQuestion(element, "item_1_7");
            element.checkQuestion();			

		}
		catch (Exception ex) {
			handleException("JQueryClientTest.testReadOnly()", ex);
		}
	}

	// Test can upload a file.
	@org.junit.Test
	public void testFileUpload() throws Exception {
		debug("JQueryClientTest.testFileUpload()");
		Element element;
		try {
			openURL(new String[] { "ajaxCall=fileupload" });
            element = elementMap.get(WidgetType.FILE);
            FileUploadData.populateTestDataForFileUploadPage(element, "item_1_1");
            element.checkQuestion();						
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
    @org.junit.Test
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
                    new String[][] { new String[] {"group1", "Group 1"}, 
                                     new String[] {"group2", "Group 2"}, 
                                     new String[] {"group3", "Group 3"} });			
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
    @org.junit.Test
	public void testDynamicallyGeneratedErrors() throws Exception {
		debug("JQueryClientTest.testDynamicallyGeneratedErrors()");
		try {
			openURL(new String[] { "ajaxCall=initialCleanPage" });
			// check we are on quesionaire 1
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[][] { new String[] {"group1", "Group 1"}});
			// navigate forward, as there are no errors
			mouseDownControl("questionnaire1_action_1");			
			// check we are now on page for quesionaire 2
			checkQuestionnaire(
					"questionnaire2",
					new String[] { "questionnaire2Style" },
					"Questionnaire 2",
					new String[][] { new String[] {"group6", "Group 6"} });
			// navigate back to previous page. The generated xml payload will include an error			
			mouseDownControl("questionnaire2_action_0");
			// check we are back to quesionaire 1
			checkQuestionnaire(
					"questionnaire1",
					new String[] { "questionnaire1Style" },
					"Questionnaire 1",
					new String[][] { new String[] {"group1", "Group 1"}});			
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
					new String[][] { new String[] {"group1", "Group 1"} });			

		}
		catch (Exception ex) {
			handleException("BaseJQueryClientTest.testDynamicallyGeneratedErrors()", ex);
		}		
	}	
}
