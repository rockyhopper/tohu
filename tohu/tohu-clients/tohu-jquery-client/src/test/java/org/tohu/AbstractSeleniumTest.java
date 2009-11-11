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

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.lang.StringUtils;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
//import org.mortbay.http.NCSARequestLog;
//import org.mortbay.http.RequestLog;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.URI;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * This is the parent class of all Selenium Unit Tests.
 * 
 * @author John Bebbington
 */
public abstract class AbstractSeleniumTest {
	/** Supported browser types */
	public static enum BrowserType {
		FIREFOX, IEXPLORER, SAFARI
	};

	/** Supported Answer types */
	public static enum AnswerType {
		TEXT, NUMBER, DECIMAL, DATE, BOOLEAN, LIST
	};

	/** Supported HTML widget types */
	public static enum WidgetType {
		FIELD, TEXTAREA, SELECT, RADIO, DATEPICKER, BUTTON, LINK, IMAGE, CHECKBOX, PARAGRAPH, FILE
	};

	/** The selenium browser handle */
	protected static DefaultSelenium selenium = null;
	/** The maximum number of milliseconds to wait for the AJAX call and subsequent processing to complete */
	protected static String processingTimeout = "10000";
	/** URL of the page currently being tested */
	protected static String testURL = null;
	/** ID of hidden field which stores a flag indicating the JQuery client is processing, see dynamicUI_main.js */
	protected static String isGUIBusyID = null;
	/** System time at which the test case started for debugging purposes */
	protected static long startTime = System.currentTimeMillis();
	/** Background thread to run the Selenium Server */
	protected static SeleniumServerThread seleniumThread = null;
	/** Background thread to run the Jerry Server */
	protected static JettyServerThread jettyThread = null;
	/** Flag indicating if debug messages should be printed to the console */
	protected static boolean DEBUG = false;
	/** The test browser type */
	protected static BrowserType browserType = null;
	/** The XPath to the content div */
	protected static String baseXPath = null;

	/**
	 * Starts up the Selenium Server and/or Jetty Server if required and starts the specified
	 * browser.
	 * 
	 * @param browserType Type of browser the test is to be run in, one of: FIREFOX, IEXPLORER or SAFARI.
	 * @param testURL URL of the web page to be tested.
	 * @param isGUIBusyID ID of hidden field which stores a flag indicating the JQuery client is processing.
	 * @param baseXPath The XPath to the content div.
	 * @throws Exception
	 */
	public static void basicSetUp(
			BrowserType browserType,
			String testURL,
			String isGUIBusyID,
			String baseXPath) throws Exception {
		debug("AbstractSeleniumTest.basicSetUp() browserType="
				+ browserType
				+ " testURL="
				+ testURL);

		AbstractSeleniumTest.testURL = testURL;
		AbstractSeleniumTest.isGUIBusyID = isGUIBusyID;
		AbstractSeleniumTest.baseXPath = "xpath=" + baseXPath;
		AbstractSeleniumTest.browserType = browserType;
		String browserString = null;
		switch (browserType) {
			case FIREFOX:
				browserString = "*firefoxproxy";
				//browserString = "*chrome";
				break;
			case IEXPLORER:
				browserString = "*iexploreproxy";
				break;
			case SAFARI:
				browserString = "*safariproxy";
				break;
		}

		if (!isHTTPServerRunning("localhost", 4444)) {
			//  Selenium Server not running on localhost:4444, start it as a separate thread.
			debug("AbstractSeleniumTest.basicSetUp() Starting Selenium Server");
			seleniumThread = new SeleniumServerThread(Thread.currentThread());
			seleniumThread.start();
			debug("AbstractSeleniumTest.basicSetUp() Waiting for Selenium Server");
			LockSupport.parkNanos(60000000000l);
			debug("AbstractSeleniumTest.basicSetUp() Selenium Server Started");
		}

		// Determine the Selenium base URL and if we need to start a Jetty Server.
		URI uri = new URI(testURL);
		// TODO Replace the above line with this one if Selenium ever allows a Jetty version
		//      more recent then 5.1, see http://jira.openqa.org/browse/SRC-176
		//HttpURI uri = new HttpURI(testURL);

		String baseURL = "http://localhost:8080/";
		if (uri.getScheme().equals("http")) {
			baseURL = "http://" + uri.getHost() + ":" + uri.getPort() + "/";
			if (!isHTTPServerRunning(uri.getHost(), uri.getPort())) {
				// HTTP Server not running there.
				if (uri.getHost().equals("localhost")) {
					// Start Jetty Server as a separate thread.
					debug("AbstractSeleniumTest.basicSetUp() Starting Jetty Server");
					jettyThread = new JettyServerThread(Thread.currentThread(), uri.getPort());
					jettyThread.start();
					debug("AbstractSeleniumTest.basicSetUp() Waiting for Jetty Server");
					LockSupport.parkNanos(60000000000l);
					debug("AbstractSeleniumTest.basicSetUp() Jetty Server Started");
				}
				else {
					// Nothing we can do Jetty Server only runs locally.
					fail("No HTTP server running at " + baseURL);
				}
			}
		}
		debug("AbstractSeleniumTest.basicSetUp() baseURL=" + baseURL);

		debug("AbstractSeleniumTest.basicSetUp() Starting browser");
		selenium = new DefaultSelenium("localhost", 4444, browserString, baseURL);
		//selenium.setSpeed("100");
		// Uncomment for debugging.
		//selenium.setBrowserLogLevel("debug");
		try {
			selenium.start();
		}
		catch (Exception ex) {
			debug("AbstractSeleniumTest.basicSetUp() exception=" + ex);
			if (((browserType == BrowserType.SAFARI) && ex.toString().contains(
				"java.lang.RuntimeException: Safari could not be found in the path!"))
					|| ((browserType == BrowserType.FIREFOX) && ex.toString().contains(
						"java.lang.RuntimeException: Firefox could not be found in the path!"))
					|| ((browserType == BrowserType.IEXPLORER) && ex.toString().contains(
						"java.lang.RuntimeException: Internet Explorer could not be found in the path!"))) {
				// Browser not installed, treat this as a successful test.
				debug("AbstractSeleniumTest.basicSetUp() browser not installed.");
				assumeTrue(false);
			}
			throw ex;
		}
		debug("AbstractSeleniumTest.basicSetUp() done");
	}

	/**
	 * Opens the test URL with the specified parameters.
	 * 
	 * @param testParams Array of parameters to be passed to the test page.
	 * @throws Exception
	 */
	public static void openURL(String[] testParams) throws Exception {
		// Add parameters.
		String fullURL = testURL + "?isGUIBusyID=" + isGUIBusyID;
		for (int i = 0; i < testParams.length; i++) {
			fullURL += "&" + testParams[i];
		}
		debug("AbstractSeleniumTest.openURL() fullURL=" + fullURL);
		selenium.open(fullURL);
		// Wait up to 30 seconds for the initial page to load.
		selenium.waitForPageToLoad("30000");
		// Make sure we have focus on the main browser window.
		selenium.selectWindow("null");
		debug("AbstractSeleniumTest.openURL() done");
	}

	/**
	 * Closes the browser and stops any background threads which were started in basicSetUp.
	 * 
	 * @throws Exception
	 */
	public static void basicTearDown() throws Exception {
		selenium.stop();
		if (jettyThread != null) {
			debug("AbstractSeleniumTest.basicTearDown() Stopping Jetty Server");
			jettyThread.stopServer();
			jettyThread.join();
		}
		if (seleniumThread != null) {
			debug("AbstractSeleniumTest.basicTearDown() Stopping Selenium Server");
			seleniumThread.stopServer();
			seleniumThread.join();
		}
		debug("AbstractSeleniumTest.basicTearDown() Done");
	}

	/**
	 * Waits up to 10 seconds for the AJAX call and subsequent processing to complete.
	 * @param step Description of where we are for debugging purposes.
	 */
	protected static void waitWhileGUIIsBusy(String step) {
		debug("AbstractWebDriverTest.waitWhileGUIIsBusy() step=" + step);
		selenium.waitForCondition("(selenium.getValue(\"//input[@id='"
				+ isGUIBusyID
				+ "']\") == 'false')", processingTimeout);
		debug("AbstractWebDriverTest.waitWhileGUIIsBusy() done");
	}

	/**
	 * Set the value in the text field for the specified question to the specified answer.
	 * @param quesionID
	 * @param answer
	 */
	protected void setTextAnswer(String questionID, String answer) {
		// Set field value.
		selenium.type(questionID + "_input", answer);
		waitWhileGUIIsBusy("setTextAnswer: questionID=" + questionID);
	}

	/**
	 * Returns the value in the text field for the specified question.
	 * @param quesionID
	 * @return String
	 */
	protected String getTextAnswer(String questionID) {
		return selenium.getValue(questionID + "_input");
	}

	/**
	 * Activates the specified action.
	 * @param quesionID
	 */
	protected void clickControl(String actionID) {
		selenium.click(actionID);
		waitWhileGUIIsBusy("clickControl: actionID=" + actionID);
	}

	/**
	 * Toggles the checkbox for the specified question.
	 * @param quesionID
	 */
	protected void clickCheckboxAnswer(String questionID) {
		try {
			selenium.click(questionID + "_input");
		}
		catch (RuntimeException ex) {
			if ((browserType == BrowserType.IEXPLORER)
					&& (jettyThread != null)
					&& ex.getMessage().equals(
						"ERROR: Command execution failure. Please search the forum at http://clearspace.openqa.org for error details from the log window.  The error message is: Unspecified error.")) {
				// For some reason Selenium throws this exception when simulating a click event in
				// IE running against the Jetty Server/Test Harness, but seems to continue fine if
				// you ignore it.
				debug("AbstractSeleniumTest.clickCheckboxAnswer() Ignoring exception: " + ex);
			}
			else {
				throw ex;
			}
		}
		waitWhileGUIIsBusy("clickCheckboxAnswer: questionID=" + questionID);
	}

	/**
	 * Returns the current checkbox value for the specified question.
	 * @param quesionID
	 * @return boolean
	 */
	protected boolean getCheckboxAnswer(String questionID) {
		return selenium.isChecked(questionID + "_input");
	}

	/**
	 * Sets the radio button group for the specified question to the specified answer value.
	 * @param quesionID
	 * @param answer
	 */
	protected void clickRadioButtonAnswer(String questionID, String answer) {
		try {
			selenium.click(questionID + "_input_" + answer);
		}
		catch (RuntimeException ex) {
			if ((browserType == BrowserType.IEXPLORER)
					&& (jettyThread != null)
					&& ex.getMessage().equals(
						"ERROR: Command execution failure. Please search the forum at http://clearspace.openqa.org for error details from the log window.  The error message is: Unspecified error.")) {
				// For some reason Selenium throws this exception when simulating a click event in
				// IE running against the Jetty Server/Test Harness, but seems to continue fine if
				// you ignore it.
				debug("AbstractSeleniumTest.clickRadioButtonAnswer() Ignoring exception: " + ex);
			}
			else {
				throw ex;
			}
		}
		waitWhileGUIIsBusy("clickRadioButtonAnswer: questionID=" + questionID);
	}

	/**
	 * Gets the current value of the radio button group for the specified question.
	 * @param quesionID
	 * @return The answer
	 */
	protected String getRadioButtonAnswer(String questionID) {
		if (browserType == BrowserType.IEXPLORER) {
			return selenium.getAttribute("//span[@id='"
					+ questionID
					+ "_input']/input[@type='radio' and @checked='true']/@value");
		}
		else {
			return selenium.getAttribute("//span[@id='"
					+ questionID
					+ "_input']/input[@type='radio' and @checked='']/@value");
		}
	}

	/**
	 * Sets the drop-down list for the specified question to the specified answer value.
	 * @param quesionID
	 * @param answer
	 */
	protected void selectDropDownAnswer(String questionID, String answer) {
		selenium.select(questionID + "_input", "value=" + answer);
		waitWhileGUIIsBusy("selectDropDownAnswer: questionID=" + questionID);
	}

	/**
	 * Gets the selected drop-down list value for the specified question.
	 * @param quesionID
	 * @return the Answer
	 */
	protected String getDropDownAnswer(String questionID) {
		return selenium.getSelectedValue(questionID + "_input");
	}

	/**
	 * Checks that all properties of the Question HTML match those specified.
	 * 
	 * @param path XPath to the question's primary div.
	 * @param widget Type of HTML widget.
	 * @param id The question ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param preLabel The question pre-label text to check for (null means don't check). 
	 * @param postLabel The question post-label text to check for (null means don't check).
	 * @param required Flag indicating if the question is required (null means don't check).
	 * @param readonly Flag indicating if the question is readonly (null means don't check).
	 * @param answerType The question answerType to check for (null means don't check). 
	 * @param possibleAnswers List of possible answers, 1st element Value, 2nd element Label (null means don't check). 
	 * @param errors List of possible errors, 1st element Type, 2nd element Reason (null means don't check). 
	 */
	protected void checkQuestion(
			String path,
			WidgetType widget,
			String id,
			String[] presentationStyles,
			String preLabel,
			String postLabel,
			Boolean required,
			Boolean readonly,
			AnswerType answerType,
			String answer,
			String[][] possibleAnswers,
			String[][] errors) {
		String fullPath = baseXPath + StringUtils.defaultString(path) + "/div[@id='" + id + "']";
		debug("AbstractSeleniumTest.checkQuestion() fullPath=" + fullPath);
		assertTrue(
			"Question " + id + " not found at " + fullPath,
			selenium.isElementPresent(fullPath));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute(fullPath + "/@class");
			debug("AbstractSeleniumTest.checkQuestion() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Question", true);
			if (required != null) {
				checkStyle(id, classAttr, "required", required.booleanValue());
			}
			if (errors != null) {
				checkStyle(id, classAttr, "error", (errors.length > 0));
			}
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (preLabel != null) {
			assertEquals(id + " wrong preLabel", preLabel, selenium.getText(fullPath
					+ "/span[@id='"
					+ id
					+ "_preLabel']"));
		}
		if (postLabel != null) {
			assertEquals(id + " wrong postLabel", postLabel, selenium.getText(fullPath
					+ "/span[@id='"
					+ id
					+ "_postLabel']"));
		}
		if (answerType == AnswerType.BOOLEAN) {
			switch (widget) {
				case CHECKBOX:
					assertTrue(id + " no checkbox", selenium.isElementPresent(fullPath
							+ "/input[@id='"
							+ id
							+ "_input' and @type='checkbox' and @name='"
							+ id
							+ "']"));
					if (answer != null) {
						assertEquals(
							id + " wrong check",
							Boolean.parseBoolean(answer),
							selenium.isChecked(fullPath + "/input[@id='" + id + "_input']"));
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath + "/div[@class='readonly_overlay']"));
					}
					break;
				case RADIO:
					assertTrue(id + " no radio group", selenium.isElementPresent(fullPath
							+ "/span[@id='"
							+ id
							+ "_input']"));
					assertTrue(id + " wrong true button", selenium.isElementPresent(fullPath
							+ "/span/input[@id='"
							+ id
							+ "_input_true' and @type='radio' and @value='true' and @name='"
							+ id
							+ "']"));
					assertTrue(id + " wrong false button", selenium.isElementPresent(fullPath
							+ "/span/input[@id='"
							+ id
							+ "_input_false' and @type='radio' and @value='false' and @name='"
							+ id
							+ "']"));
					assertEquals(id + " wrong true button label", "Yes", selenium.getText(fullPath
							+ "/span[@id='"
							+ id
							+ "_input']/label[1]"));
					assertEquals(id + " wrong false button label", "No", selenium.getText(fullPath
							+ "/span[@id='"
							+ id
							+ "_input']/label[2]"));
					if (answer != null) {
						assertTrue(id + " wrong radio button selected", selenium.isChecked(fullPath
								+ "/span/input[@id='"
								+ id
								+ "_input_"
								+ answer.toLowerCase()
								+ "']"));
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly true button",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath
									+ "/span/div[@class='readonly_overlay'][1]"));
						assertEquals(
							id + " wrong readonly false button",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath
									+ "/span/div[@class='readonly_overlay'][2]"));
					}
					break;
				default:
					fail(id + " Invalid widget/answerType for Question");
			}
		}
		else {
			switch (widget) {
				case FIELD:
					assertTrue(id + " no input", selenium.isElementPresent(fullPath
							+ "/input[@id='"
							+ id
							+ "_input' and @name='"
							+ id
							+ "']"));
					if (answer != null) {
						assertEquals(id + " wrong answer", answer, selenium.getValue(fullPath
								+ "/input[@id='"
								+ id
								+ "_input']"));
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath + "/div[@class='readonly_overlay']"));
					}
					break;
				case TEXTAREA:
					assertTrue(id + " no textarea", selenium.isElementPresent(fullPath
							+ "/textarea[@id='"
							+ id
							+ "_input' and @name='"
							+ id
							+ "']"));
					if (answer != null) {
						assertEquals(id + " wrong answer", answer, selenium.getText(fullPath
								+ "/textarea[@id='"
								+ id
								+ "_input']"));
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath + "/div[@class='readonly_overlay']"));
					}
					break;
				case RADIO:
					assertTrue(id + " no radio group", selenium.isElementPresent(fullPath
							+ "/span[@id='"
							+ id
							+ "_input']"));
					if (possibleAnswers != null) {
						for (int i = 0; i < possibleAnswers.length; i++) {
							assertTrue(
								id + " wrong radio button " + i,
								selenium.isElementPresent(fullPath
										+ "/span/input[@id='"
										+ id
										+ "_input_"
										+ i
										+ "' and @type='radio' and @value='"
										+ possibleAnswers[i][0]
										+ "' and @name='"
										+ id
										+ "']"));
							assertEquals(
								id + " wrong radio button " + i + " label",
								possibleAnswers[i][1],
								selenium.getText(fullPath
										+ "/span[@id='"
										+ id
										+ "_input']/label["
										+ (i + 1)
										+ "]"));
							if (answer != null) {
								assertEquals(
									id + " wrong radio button selected",
									answer.equals(possibleAnswers[i][0]),
									selenium.isChecked(fullPath
											+ "/span/input[@id='"
											+ id
											+ "_input_"
											+ i
											+ "']"));
							}
							if (readonly != null) {
								assertEquals(
									id + " wrong readonly radio button " + i,
									readonly.booleanValue(),
									selenium.isElementPresent(fullPath
											+ "/span/div[@class='readonly_overlay']["
											+ (i + 1)
											+ "]"));
							}
						}
					}
					break;
				case SELECT:
					assertTrue(id + " no dropdown", selenium.isElementPresent(fullPath
							+ "/select[@id='"
							+ id
							+ "_input' and @name='"
							+ id
							+ "']"));
					if (possibleAnswers != null) {
						for (int i = 0; i < possibleAnswers.length; i++) {
							assertEquals(
								id + " wrong list item " + i,
								possibleAnswers[i][1],
								selenium.getText(fullPath
										+ "/select/option[@value='"
										+ possibleAnswers[i][0]
										+ "']"));
						}
						if (answer != null) {
							assertEquals(
								id + " wrong list item selected",
								answer,
								selenium.getSelectedValue(fullPath
										+ "/select[@id='"
										+ id
										+ "_input']"));
						}
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath + "/div[@class='readonly_overlay']"));
					}
					break;
				case DATEPICKER:
					assertTrue(id + " no input", selenium.isElementPresent(fullPath
							+ "/input[@id='"
							+ id
							+ "_input' and @name='"
							+ id
							+ "']"));
					assertTrue(id + " no datepicker", selenium.isElementPresent(fullPath
							+ "/img[@class='ui-datepicker-trigger']"));
					if (answer != null) {
						assertEquals(id + " wrong answer", answer, selenium.getValue(fullPath
								+ "/input[@id='"
								+ id
								+ "_input']"));
					}
					if (readonly != null) {
						assertEquals(
							id + " wrong readonly",
							readonly.booleanValue(),
							selenium.isElementPresent(fullPath + "/div[@class='readonly_overlay']"));
					}
					break;
				case FILE:
					assertTrue(id + " no input", selenium.isElementPresent(fullPath
							+ "/input[@id='"
							+ id
							+ "_input' and @name='"
							+ id
							+ "' and @type='file']"));
					break;
				default:
					fail(id + " Invalid widget/answerType for Question");
			}
		}

		if (errors != null) {
			assertTrue(id + " missing error div", selenium.isElementPresent(fullPath
					+ "/div[@id='"
					+ id
					+ "_errors']"));
			for (int i = 0; i < errors.length; i++) {
				assertTrue(
					id + " missing error '" + errors[i][1] + "'",
					selenium.isElementPresent(fullPath
							+ "/div[@id='"
							+ id
							+ "_errors']/div[@class='InvalidAnswer "
							+ errors[i][0]
							+ "' and text()='"
							+ errors[i][1]
							+ "']"));
			}
		}

	}

	/**
	 * Checks that all properties of the Note HTML match those specified.
	 * 
	 * @param path XPath to the note's primary div.
	 * @param widget Type of HTML widget.
	 * @param id The note ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The note label text to check for (null means don't check). 
	 */
	protected void checkNote(
			String path,
			WidgetType widget,
			String id,
			String[] presentationStyles,
			String label) {
		String fullPath = baseXPath + StringUtils.defaultString(path) + "/div[@id='" + id + "']";
		assertTrue("Note " + id + " not found at " + fullPath, selenium.isElementPresent(fullPath));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute(fullPath + "/@class");
			debug("AbstractSeleniumTest.checkNote() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Note", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			if (widget == WidgetType.IMAGE) {
				if (browserType == BrowserType.IEXPLORER) {
					assertTrue(id + " no image", selenium.isElementPresent(fullPath
							+ "/img[@id='"
							+ id
							+ "_image']"));
				}
				else {
					assertTrue(id + " no image", selenium.isElementPresent(fullPath
							+ "/img[@id='"
							+ id
							+ "_image' and @src='"
							+ label
							+ "']"));
				}
			}
			else {
				assertEquals(id + " wrong label", label, selenium.getText(fullPath
						+ "/p[@id='"
						+ id
						+ "_label']"));
			}
		}

	}

	/**
	 * Checks that all properties of a Control HTML match those specified.
	 * 
	 * @param path XPath to the control's primary div.
	 * @param widget Type of HTML widget.
	 * @param id The control ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The control label text to check for (null means don't check). 
	 */
	protected void checkControl(
			String path,
			WidgetType widget,
			String id,
			String[] presentationStyles,
			String label) {
		String fullPath = baseXPath + StringUtils.defaultString(path);
		if (widget == WidgetType.BUTTON) {
			fullPath += "/input[@id='" + id + "' and @type='button']";
			assertTrue("Button control " + id + " not found", selenium.isElementPresent(fullPath));
			if (label != null) {
				assertEquals("Button " + id + " wrong label", label, selenium.getAttribute(fullPath
						+ "/@value"));
			}
		}
		else {
			fullPath += "/a[@id='" + id + "']";
			assertTrue("Link control " + id + " not found", selenium.isElementPresent(fullPath));
			if (label != null) {
				assertEquals("Link " + id + " wrong label", label, selenium.getText(fullPath));
			}
		}
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute(fullPath + "/@class");
			debug("AbstractSeleniumTest.checkControl() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Control", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
	}

	/**
	 * Checks that all properties of a Group HTML match those specified.
	 * 
	 * @param path XPath to the group's primary div.
	 * @param id The group ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The group label text to check for (null means don't check).
	 * @param items List of item IDs to check for, in order (null means don't check). 
	 */
	protected void checkGroup(
			String path,
			String id,
			String[] presentationStyles,
			String label,
			String[] items) {
		String fullPath = baseXPath + StringUtils.defaultString(path) + "/div[@id='" + id + "']";
		assertTrue("Group " + id + " not found at " + fullPath, selenium.isElementPresent(fullPath));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute(fullPath + "/@class");
			debug("AbstractSeleniumTest.checkGroup() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Group", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			assertEquals(id + " wrong label", label, selenium.getText(fullPath
					+ "/p[@id='"
					+ id
					+ "_label']"));
		}
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				assertEquals(
					id + " item " + i + " missing",
					items[i],
					selenium.getAttribute(fullPath
							+ "/div[@id='"
							+ id
							+ "_items']/div["
							+ (i + 1)
							+ "]/@id"));
			}
		}
	}

	/**
	 * Checks that all properties of a Questionnaire HTML match those specified.
	 * 
	 * @param path XPath to the questionnaire's primary div.
	 * @param id The questionnaire ID.
	 * @param presentationStyles List of styles to check for (null means don't check).
	 * @param label The questionnaire label text to check for (null means don't check).
	 * @param items List of item IDs to check for, in order (null means don't check). 
	 */
	protected void checkQuestionnaire(
			String id,
			String[] presentationStyles,
			String label,
			String[] items) {
		String fullPath = baseXPath + "/form[@id='" + id + "_form']";
		assertTrue(
			"Questionnaire form " + id + " not found at " + fullPath,
			selenium.isElementPresent(fullPath));
		fullPath += "/div[@id='" + id + "']";
		assertTrue(
			"Questionnaire group " + id + " not found at " + fullPath,
			selenium.isElementPresent(fullPath));
		if (presentationStyles != null) {
			String classAttr = selenium.getAttribute(fullPath + "/@class");
			debug("AbstractSeleniumTest.checkGroup() classAttr=" + classAttr);
			checkStyle(id, classAttr, "Questionnaire", true);
			for (int i = 0; i < presentationStyles.length; i++) {
				checkStyle(id, classAttr, presentationStyles[i], true);
			}
		}
		if (label != null) {
			assertEquals(id + " wrong label", label, selenium.getText(fullPath
					+ "/p[@id='"
					+ id
					+ "_label']"));
		}
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				assertEquals(
					id + " item " + i + " missing",
					items[i],
					selenium.getAttribute(fullPath
							+ "/div[@id='"
							+ id
							+ "_items']/div["
							+ (i + 1)
							+ "]/@id"));
			}
		}
	}

	/**
	 * Checks the specified style is present or not present.
	 * 
	 * @param id ID of the HTML element.
	 * @param classAttr The class attribute of the HTML element.
	 * @param className The name of the class to check for.
	 * @param present The presence to check for.
	 */
	protected void checkStyle(String id, String classAttr, String className, boolean present) {
		if (present) {
			assertTrue(
				id + " does not have style '" + className + "'",
				(" " + classAttr + " ").contains(" " + className + " "));
		}
		else {
			assertFalse(id + " has style '" + className + "'", (" " + classAttr + " ").contains(" "
					+ className
					+ " "));
		}
	}

	/**
	 * Checks that the specified message is displayed in an alert box.
	 * 
	 * @param message The message text.
	 */
	protected void checkAlert(String message) {
		assertTrue("Alert '" + message + "' not displayed", selenium.isAlertPresent());
		assertEquals("Alert '" + message + "' not displayed", message, selenium.getAlert());
	}

	/**
	 * Returns true if an item with the specified ID is present on the page.
	 * @param itemID
	 * @return boolean
	 */
	protected boolean isItemPresent(String itemID) {
		return selenium.isElementPresent("//div[@id='" + itemID + "']");
	}

	/**
	 * Handles an exception thrown by the test case.
	 */
	protected static void handleException(String msg, Exception ex) throws Exception {
		error(msg, ex);
		System.err.println(selenium.retrieveLastRemoteControlLogs());
		//Thread.sleep(60000);
		throw ex;
	}

	/**
	 * Displays debugging messages to the stdout with the time in milliseconds since the test case started.
	 * @param msg The message.
	 */
	protected static void debug(String msg) {
		if (DEBUG) {
			System.out.println("time=" + (System.currentTimeMillis() - startTime) + ": " + msg);
		}
	}

	/**
	 * Displays error messages to the stderr with the time in milliseconds since the test case started.
	 * @param msg The message.
	 * @param ex Optional exception.
	 */
	protected static void error(String msg, Exception ex) {
		System.err.println("time=" + (System.currentTimeMillis() - startTime) + ": " + msg);
		if (ex != null) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns true if there is an HTTP server running on the specified host and port.
	 * @param host Host name.
	 * @param port Port number.
	 * @return boolean
	 */
	protected static boolean isHTTPServerRunning(String host, int port) {
		boolean retVal = false;
		try {
			HttpConnection connection = new HttpConnection(host, port);
			connection.open();
			retVal = true;
			connection.close();
		}
		catch (IOException ioe) {
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Thread class for running the Selenium Server on localhost:4444.
	 */
	protected static class SeleniumServerThread extends Thread {
		private SeleniumServer server = null;
		private Thread parentThread = null;

		public SeleniumServerThread(Thread parentThread) {
			this.parentThread = parentThread;
		}

		public void run() {
			try {
				RemoteControlConfiguration rcc = new RemoteControlConfiguration();
				// Uncomment for debugging.
				//rcc.setBrowserSideLogEnabled(true);
				//rcc.setDebugMode(true);
				//rcc.setLogOutFileName("SeleniumServer.log");
				server = new SeleniumServer(rcc);
				server.start();
			}
			catch (Exception ex) {
				error("SeleniumServerThread.run()", ex);
			}
			finally {
				// Notify the parent thread that the server has finished starting up.
				LockSupport.unpark(parentThread);
			}
		}

		public void stopServer() throws Exception {
			server.stop();
		}
	}

	/**
	 * Thread class for running a Jetty HTTP Server which serves up the test page and the
	 * dummy xml files for the AJAX calls.
	 */
	protected static class JettyServerThread extends Thread {
		private HttpServer server = null;
		//private Server server = null;
		private Thread parentThread = null;
		private int port = 0;

		public JettyServerThread(Thread parentThread, int port) {
			this.parentThread = parentThread;
			this.port = port;
			debug("JettyServerThread() port=" + port);
		}

		public void run() {
			try {
				server = new HttpServer();
				SocketListener listener = new SocketListener();
				listener.setPort(port);
				listener.setMinThreads(1);
				listener.setMaxThreads(5);
				server.addListener(listener);
				HttpContext context = server.addContext("/");
				context.setResourceBase(this.getClass().getResource("../../../test-classes/").toExternalForm());
				context.setRedirectNullPath(true);
				context.addWelcomeFile("test.html");
				debug("JettyServerThread.run() serving " + context.getBaseResource());
				ResourceHandler handler = new ResourceHandler();
				handler.setAcceptRanges(true);
				handler.setDirAllowed(true);
				context.addHandler(handler);
				//context.addHandler(new NotFoundHandler());
				//server.setStopGracefully(true);
				// Uncomment for debugging.
				//server.setTrace(true);
				//NCSARequestLog log = new NCSARequestLog("JettyServer.log");
				//log.setExtended(true);
				//log.setLogCookies(true);
				//log.setLogLatency(true);
				//server.setRequestLog(log);
				server.start();

				// TODO Replace the above code with this if Selenium ever allows a Jetty version
				//      more recent then 5.1, see http://jira.openqa.org/browse/SRC-176
				//ResourceHandler handler = new ResourceHandler();
				//handler.setWelcomeFiles(new String[] { "test.html" });
				//handler.setResourceBase(this.getClass().getResource("../../../").toExternalForm());
				//debug("JettyServerThread.run() serving " + handler.getBaseResource());
				//HandlerList handlers = new HandlerList();
				//handlers.setHandlers(new Handler[] { handler, new DefaultHandler() });
				//server = new Server(port);
				//server.setHandler(handlers);
				//server.start();
			}
			catch (Exception ex) {
				error("JettyHandlerThread.run()", ex);
			}
			finally {
				// Notify the parent thread that the server has finished starting up.
				LockSupport.unpark(parentThread);
			}
		}

		public void stopServer() throws Exception {
			server.stop();
			server.destroy();
		}
	}

}
